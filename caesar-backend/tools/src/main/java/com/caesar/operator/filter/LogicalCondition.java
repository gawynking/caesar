package com.caesar.operator.filter;

/**
 * 逻辑运算条件接口
 *
 * @param <L>
 * @param <R>
 */
public interface LogicalCondition<L,R> extends Condition<L,R>{

    Boolean apply(Boolean... items);

}
