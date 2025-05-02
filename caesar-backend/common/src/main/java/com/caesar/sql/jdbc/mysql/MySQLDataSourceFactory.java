package com.caesar.sql.jdbc.mysql;

import com.alibaba.druid.pool.DruidDataSource;
import com.caesar.config.CommonConfig;
import com.caesar.config.CommonConstant;
import com.caesar.sql.jdbc.datasource.DataSource;
import com.caesar.sql.jdbc.datasource.impl.DruidDataSourceAdapter;
import com.caesar.sql.jdbc.factory.DataSourceFactory;

public class MySQLDataSourceFactory implements DataSourceFactory {

    static {
        try {
            String driver = CommonConfig.getString(CommonConstant.DATASOURCE_DRIVER_CLASS_NAME);
            Class.forName(driver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String url;
    private String username;
    private String password;

    public MySQLDataSourceFactory(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public DataSource createDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(password);
        druidDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        druidDataSource.setInitialSize(1);
        druidDataSource.setMaxActive(5);

        return new DruidDataSourceAdapter(druidDataSource);
    }
}
