package com.example.quicklogin.domain;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author LX
 * @date 2021/5/14
 */
@Data
public class Income {
    private int id;
    private double breakfast_income;
    private int breakfast_num;
    private double lunch_income;
    private int lunch_num;
    private double dinner_income;
    private int dinner_num;
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private Date income_date;
    private int dayOfMonth;
}
