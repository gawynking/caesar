package com.caesar.runner.batch.doris;

import com.caesar.runner.AbstractSqlExecutor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class DorisSqlExecutor extends AbstractSqlExecutor<Connection> {

    public static void main(String[] args) {
        new DorisSqlExecutor().execute(args, new HandlerCallback<Connection>() {
            @Override
            public void process(Connection connection, String sql) throws Exception {
            }
        });
    }

    @Override
    protected Connection openConnect(String taskName) {
        return null;
    }

    @Override
    protected void closeConnect(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void alert(String taskName, Integer counter, String sql) {

    }

    @Override
    protected void persistExecutionLog(Map<String, Object> logMap) {

    }

}
