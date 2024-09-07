package com.caesar.sql.jdbc.datasource.impl;

import com.alibaba.druid.pool.DruidDataSource;
import com.caesar.sql.jdbc.datasource.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DruidDataSourceAdapter implements DataSource {
    private DruidDataSource druidDataSource;

    public DruidDataSourceAdapter(DruidDataSource druidDataSource) {
        this.druidDataSource = druidDataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return druidDataSource.getConnection();
    }

}
