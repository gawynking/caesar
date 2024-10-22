package com.caesar.operator.filter;

/**
 * 非运算
 */
public class Not implements LogicalCondition{
    @Override
    public Boolean apply(Boolean... items) {
        if (items.length != 1) {
            throw new IllegalArgumentException("Exactly one item is required, but " + items.length + " were provided.");
        }
        return !items[0];
    }

}
