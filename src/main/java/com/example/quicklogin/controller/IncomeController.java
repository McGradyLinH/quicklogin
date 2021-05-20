package com.example.quicklogin.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.fastjson.JSON;
import com.example.quicklogin.domain.Income;
import com.example.quicklogin.domain.IncomeData;
import com.example.quicklogin.listener.IncomeListener;
import com.example.quicklogin.mapper.IncomeDao;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author LX
 * @date 2021/5/14
 */
@Controller
public class IncomeController {
    @Resource
    private IncomeDao incomeDao;

    @GetMapping("/goUpload")
    public String goUpload() {
        return "uploadData";
    }

    @PostMapping("/upload")
    public String upload(MultipartFile income, Map<String, Object> rs) {
        ExcelReader excelReader = null;
        try {
            excelReader = EasyExcel.read(income.getInputStream(), IncomeData.class, new IncomeListener()).build();
            ReadSheet readSheet = EasyExcel.readSheet(0).build();
            excelReader.read(readSheet);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (excelReader != null) {
                // 这里千万别忘记关闭，读的时候会创建临时文件，到时磁盘会崩的
                excelReader.finish();
            }
        }
        List<IncomeData> incomeDatas = IncomeListener.list;
        incomeDatas = incomeDatas.stream().filter(incomeData -> incomeData.getCompany().equals("其他公司")
                && incomeData.getStatus().equals("已完成")).collect(Collectors.toList());
        Map<String, Float> resultMap = new HashMap<>(4);
        Map<String, HashSet<String>> peopleMap = new HashMap<>(4);
        incomeDatas.forEach(incomeData -> {
            resultMap.merge(incomeData.getEatTime(), incomeData.getPrice(), Float::sum);
            HashSet<String> names = peopleMap.get(incomeData.getEatTime());
            if (!Objects.isNull(names)) {
                names.add(incomeData.getName());
            } else {
                names = new HashSet<>();
                names.add(incomeData.getName());
                peopleMap.put(incomeData.getEatTime(), names);
            }
        });

        Income income1 = new Income();
        income1.setBreakfast_income(resultMap.get("早餐"));
        income1.setBreakfast_num(peopleMap.get("早餐").size());
        income1.setLunch_income(resultMap.get("午餐"));
        income1.setLunch_num(peopleMap.get("午餐").size());
        income1.setDinner_income(resultMap.get("晚餐"));
        income1.setDinner_num(peopleMap.get("晚餐").size());
        income1.setIncome_date(new Date());
        incomeDao.recordIncome(income1);

        double totalValue = income1.getBreakfast_income() + income1.getLunch_income() + income1.getDinner_income();
        LocalDate localDate = LocalDate.now();
        income1.setDayOfMonth(localDate.getDayOfMonth());
        rs.put("income", income1);
        rs.put("totalValue", totalValue);
        //前一天的数据
        Income income2 = incomeDao.queryIncome();
        System.err.println(income1);
        System.err.println(income2);
        diffBigDecimal(income2.getBreakfast_income(), income1.getBreakfast_income(), rs, "diffBreakfast");
        diffBigDecimal(income2.getLunch_income(), income1.getLunch_income(), rs, "diffLunch");
        diffBigDecimal(income2.getDinner_income(), income1.getDinner_income(), rs, "diffDinner");

        return "result";
    }

    private void diffBigDecimal(double d1, double d2, Map<String, Object> map, String key) {
        BigDecimal subtract = new BigDecimal(d1).subtract(new BigDecimal(d2));
        String s1 = subtract.toString();
        if (s1.startsWith("-")) {
            map.put(key, "增加" + s1.substring(1) + "元");
        } else {
            map.put(key, "减少" + s1 + "元");
        }
    }


}
