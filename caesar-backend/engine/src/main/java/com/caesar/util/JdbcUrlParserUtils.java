package com.caesar.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class JdbcUrlParserUtils {

    private static final Pattern JDBC_URL_PATTERN = Pattern.compile("jdbc:(?<dbType>\\w+):\\/\\/(?<hostname>[^:/]+)(:(?<port>\\d+))?(\\/|\\:)(?<database>[^?;]+)(\\?.*)?");

    public static JdbcUrlInfo parseJdbcUrl(String jdbcUrl) {
        Matcher matcher = JDBC_URL_PATTERN.matcher(jdbcUrl);
        if (matcher.matches()) {
            String dbType = matcher.group("dbType");
            String hostname = matcher.group("hostname");
            String port = matcher.group("port") != null ? matcher.group("port") : getDefaultPort(dbType);
            String database = matcher.group("database");
            return new JdbcUrlInfo(dbType, hostname, port, database);
        } else {
            throw new IllegalArgumentException("Invalid JDBC URL format: " + jdbcUrl);
        }
    }

    private static String getDefaultPort(String dbType) {
        switch (dbType.toLowerCase()) {
            case "mysql":
                return "3306";
            case "postgresql":
                return "5432";
            case "oracle":
                return "1521";
            case "sqlserver":
                return "1433";
            case "db2":
                return "50000";
            // 添加更多数据库类型及其默认端口
            default:
                return "unknown";
        }
    }


    public static class JdbcUrlInfo {
        private final String dbType;
        private final String hostname;
        private final String port;
        private final String database;

        public JdbcUrlInfo(String dbType, String hostname, String port, String database) {
            this.dbType = dbType;
            this.hostname = hostname;
            this.port = port;
            this.database = database;
        }

        public String getDbType() {
            return dbType;
        }

        public String getHostname() {
            return hostname;
        }

        public String getPort() {
            return port;
        }

        public String getDatabase() {
            return database;
        }
    }
}

