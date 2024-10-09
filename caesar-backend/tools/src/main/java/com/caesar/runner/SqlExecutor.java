package com.caesar.runner;

/**
 * 执行接口
 * @param <T>
 */
public interface SqlExecutor<T> {

    void execute(String[] args, HandlerCallback<T> handler);

    interface HandlerCallback<T> {
        void process(T t,String sql) throws Exception;
    }

}
