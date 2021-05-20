package com.example.quicklogin.controller;

import com.example.quicklogin.util.HttpClientUtils;
import org.apache.coyote.Request;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author LX
 * @date 2021/4/9
 */
@RestController
public class LoginController {
    private static final String GITHUB_CLIENT_ID = "70d61922b7e24e4880d5";
    private static final String GITHUB_CLIENT_SECRET = "6c31fc838db4244bca605c0a8b5e8a960f83d5d4";
    private static final String GITHUB_REDIRECT_URL = "http://127.0.0.1:8080/githubCallback";

    @RequestMapping("/githubLogin")
    public void githubLogin(HttpServletResponse response) throws IOException {
        // Github认证服务器地址
        String url = "https://github.com/login/oauth/authorize";
        // 生成并保存state，忽略该参数有可能导致CSRF攻击
        String state = "oauthService.genState";
        // 传递参数response_type、client_id、state、redirect_uri
        String param = "response_type=code&" + "client_id=" + GITHUB_CLIENT_ID + "&state=" + state
                + "&redirect_uri=" + GITHUB_REDIRECT_URL;

        // 1、请求Github认证服务器
        response.sendRedirect(url + "?" + param);
    }

    @GetMapping("/login")
    public ModelAndView index() {
        return new ModelAndView("login");
    }

    @GetMapping("/githubCallback")
    public void githubCallback(String code, String state, HttpServletResponse response) throws Exception {
        // 验证state，如果不一致，可能被CSRF攻击
        if(!state.equals("oauthService.genState")) {
            throw new Exception("State验证失败");
        }

        // 2、向GitHub认证服务器申请令牌
        String url = "https://github.com/login/oauth/access_token";
        // 传递参数grant_type、code、redirect_uri、client_id
        String param = "grant_type=authorization_code&code=" + code + "&redirect_uri=" +
                GITHUB_REDIRECT_URL + "&client_id=" + GITHUB_CLIENT_ID + "&client_secret=" + GITHUB_CLIENT_SECRET;

        // 申请令牌，注意此处为post请求
        String result = HttpClientUtils.sendPostRequest(url, param);

        /*
         * result示例：
         * 失败：error=incorrect_client_credentials&error_description=The+client_id+and%2For+client_secret+passed+are+incorrect.&
         * error_uri=https%3A%2F%2Fdeveloper.github.com%2Fapps%2Fmanaging-oauth-apps%2Ftroubleshooting-oauth-app-access-token-request-errors%2F%23incorrect-client-credentials
         * 成功：access_token=7c76186067e20d6309654c2bcc1545e41bac9c61&scope=&token_type=bearer
         */
        Map<String, String> resultMap = HttpClientUtils.params2Map(result);
        // 如果返回的map中包含error，表示失败，错误原因存储在error_description
        if(resultMap.containsKey("error")) {
            throw  new Exception(resultMap.get("error_description"));
        }

        // 如果返回结果中包含access_token，表示成功
        if(!resultMap.containsKey("access_token")) {
            throw  new Exception("获取token失败");
        }

        // 得到token和token_type
        String accessToken = resultMap.get("access_token");
        String tokenType = resultMap.get("token_type");

        // 3、向资源服务器请求用户信息，携带access_token和tokenType
        String userUrl = "https://api.github.com/user";
        String userParam = "access_token=" + accessToken + "&token_type=" + tokenType;
        // 申请资源
        String userResult = HttpClientUtils.sendGetRequest(userUrl, accessToken);

        // 4、输出用户信息
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().write(userResult);
    }

}
