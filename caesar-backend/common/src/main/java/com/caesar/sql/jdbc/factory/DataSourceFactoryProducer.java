package com.caesar.sql.jdbc.factory;


import com.caesar.sql.jdbc.mysql.MySQLDataSourceFactory;

public class DataSourceFactoryProducer {
    public static DataSourceFactory getFactory(String dbType, String url, String username, String password) {
        if (dbType.equalsIgnoreCase("MYSQL")) {
            return new MySQLDataSourceFactory(url, username, password);
        }
        throw new IllegalArgumentException("Unsupported database type: " + dbType);
    }
}
