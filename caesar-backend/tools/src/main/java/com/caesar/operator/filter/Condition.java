package com.caesar.operator.filter;

/**
 * 顶层条件接口
 *
 * @param <L>
 * @param <R>
 */
public interface Condition<L,R> {

    interface Callback<T> {
        Boolean filter() throws Exception;
    }

}
