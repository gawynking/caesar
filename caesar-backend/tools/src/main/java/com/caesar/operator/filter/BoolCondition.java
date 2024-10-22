package com.caesar.operator.filter;

/**
 * 布尔运算条件接口
 *
 * @param <L>
 * @param <R>
 */
public interface BoolCondition<L,R> extends Condition<L,R>{

    Boolean apply(L left,R right);

}
