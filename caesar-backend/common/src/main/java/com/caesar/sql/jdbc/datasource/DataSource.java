package com.caesar.sql.jdbc.datasource;

import java.sql.Connection;
import java.sql.SQLException;

public interface DataSource {
    Connection getConnection() throws SQLException;
}
