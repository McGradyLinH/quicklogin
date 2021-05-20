package com.example.quicklogin.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author LX
 * @date 2021/5/14
 */
@Data
public class IncomeData {
    @ExcelProperty(index = 2)
    private String name;

    @ExcelProperty(index = 3)
    private String company;

    @ExcelProperty(index = 7)
    private float price;

    @ExcelProperty(index = 8)
    private String status;

    @ExcelProperty(index = 9)
    private String eatTime;



}
