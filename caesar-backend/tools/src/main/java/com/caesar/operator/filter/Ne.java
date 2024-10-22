package com.caesar.operator.filter;

/**
 * 不等于运算
 *  !=
 * @param <L>
 */
public class Ne<L> implements BoolCondition<L,L>{
    @Override
    public Boolean apply(L left, L right) {
        return (left == null ? right == null : !left.equals(right));
    }

}
