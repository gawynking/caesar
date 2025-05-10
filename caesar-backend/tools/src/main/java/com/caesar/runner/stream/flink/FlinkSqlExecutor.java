package com.caesar.runner.stream.flink;

import com.caesar.runner.AbstractSqlExecutor;

import java.util.Map;

public class FlinkSqlExecutor extends AbstractSqlExecutor {
    @Override
    protected Object openConnect(String taskName) {
        return null;
    }

    @Override
    protected void closeConnect(Object o) {

    }

    @Override
    protected void alert(String taskName, Integer counter, String sql) {

    }

    @Override
    protected void persistExecutionLog(Map logMap) {

    }
}
