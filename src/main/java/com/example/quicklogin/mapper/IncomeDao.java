package com.example.quicklogin.mapper;

import com.example.quicklogin.domain.Income;

/**
 * @author LX
 * @date 2021/5/14
 */
public interface IncomeDao {
    int recordIncome(Income income);

    Income queryIncome();
}
