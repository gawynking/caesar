package com.caesar.mysql.engine;

import com.caesar.engine.Engine;
import com.caesar.runner.ExecutionResult;
import com.caesar.task.Task;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLEngine implements Engine {

    private String jdbcDriver;
    private String jdbcUrl;
    private String username;
    private String password;

    public MySQLEngine(String jdbcDriver, String jdbcUrl, String username, String password) {
        this.jdbcDriver = jdbcDriver;
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    @Override
    public ExecutionResult execute(Task task) {
        // 显式加载 MySQL JDBC 驱动
        try {
            Class.forName(jdbcDriver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load MySQL JDBC driver.", e);
        }

        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             Statement stmt = conn.createStatement()) {
            String[] codes = task.getCode().split(";");
            for(String code:codes){
                stmt.execute(code);
            }
            return new ExecutionResult(true, "Execution successful.");
        } catch (SQLException e) {
            e.printStackTrace();
            return new ExecutionResult(false, e.getMessage());
        }
    }

}
