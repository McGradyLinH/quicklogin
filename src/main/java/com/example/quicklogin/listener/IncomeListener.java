package com.example.quicklogin.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.example.quicklogin.domain.IncomeData;


import java.util.ArrayList;
import java.util.List;

/**
 * @author LX
 * @date 2021/5/14
 */
public class IncomeListener extends AnalysisEventListener<IncomeData> {
    public static List<IncomeData> list = new ArrayList<>();

    @Override
    public synchronized void invoke(IncomeData data, AnalysisContext context) {
        list.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }
}
