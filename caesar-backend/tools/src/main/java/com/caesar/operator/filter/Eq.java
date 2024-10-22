package com.caesar.operator.filter;

/**
 * 相等运算
 *  ==
 */
public class Eq<L> implements BoolCondition<L,L>{

    @Override
    public Boolean apply(L left, L right) {
        return (left == null ? right == null : left.equals(right));
    }

}
