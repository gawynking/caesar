package com.caesar.operator.filter;

import java.io.Serializable;

/**
 * 对象集合接口
 *
 * @param <T>
 */
public interface ObjectCollection<T> extends Condition, Serializable {

    default Boolean filter(Callback<T> callback) {
        try {
            return callback.filter();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
