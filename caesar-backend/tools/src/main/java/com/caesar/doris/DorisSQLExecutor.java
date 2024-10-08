package com.caesar.doris;

import com.caesar.AbstractSqlExecutor;

import java.sql.Connection;

/**
 * Doris SQL提交入口
 */
public class DorisSQLExecutor extends AbstractSqlExecutor<Connection> {

    public static void main(String[] args) {
        
    }

    @Override
    protected Connection openConnect() {
        return null;
    }

    @Override
    protected void executeSQL(Connection connection, String sql) {

    }


    @Override
    protected void closeConnect(Connection connection) {

    }
}
