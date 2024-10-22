package com.caesar.operator.filter;

import java.util.Comparator;

public class Lt<L> implements BoolCondition<L,L>{

    private final Comparator<L> comparator;

    public Lt(Comparator<L> comparator) {
        this.comparator = comparator;
    }

    @Override
    public Boolean apply(L left, L right) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("Both arguments must be non-null");
        }
        // 使用 compareTo 方法进行比较
        return comparator.compare(left, right) < 0;
    }
}
