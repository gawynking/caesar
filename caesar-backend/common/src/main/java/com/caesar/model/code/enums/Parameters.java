package com.caesar.model.code.enums;


import com.caesar.model.code.func.Functions;

import java.util.Date;
import java.util.function.BiFunction;


public enum Parameters {

    ELT_DATE("etl_date", Functions::getEtlDate),
    START_DATE("start_date", Functions::getStartDate),
    END_DATE("end_date", Functions::getEndDate);

    private final String parameter;
    private final BiFunction<DatePeriod,Date,String> expression;

    // 构造函数
    Parameters(String parameter, BiFunction<DatePeriod,Date,String> expression) {
        this.parameter = parameter;
        this.expression = expression;
    }

    // 获取参数名
    public String getParameter() {
        return parameter;
    }

    // 应用表达式并获取结果
    public String applyExpression(DatePeriod period,Date date) {
        return expression.apply(period,date);
    }

    // 从参数名获取枚举实例的静态方法（可选）
    public static Parameters fromParameter(String parameter) {
        for (Parameters p : values()) {
            if (p.getParameter().equals(parameter)) {
                return p;
            }
        }
        throw new IllegalArgumentException("Invalid parameter: " + parameter);
    }
}