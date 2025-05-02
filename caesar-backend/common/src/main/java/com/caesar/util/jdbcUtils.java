package com.caesar.util;//package com.caesar.sql.jdbc;

import com.caesar.config.CommonConfig;
import com.caesar.config.CommonConstant;
import com.caesar.sql.jdbc.datasource.DataSource;
import com.caesar.sql.jdbc.factory.DataSourceFactory;
import com.caesar.sql.jdbc.factory.DataSourceFactoryProducer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public class jdbcUtils {

    private static jdbcUtils instance;
    private static DataSource dataSource;

    public static jdbcUtils getInstance() {
        if (instance == null) {
            synchronized (jdbcUtils.class) {
                if (instance == null) {
                    instance = new jdbcUtils();
                }
            }
        }
        return instance;
    }

    private jdbcUtils() {
        String dbType = null;
        String url = CommonConfig.getString(CommonConstant.DATASOURCE_URL);
        String username = CommonConfig.getString(CommonConstant.DATASOURCE_USERNAME);
        String password = CommonConfig.getString(CommonConstant.DATASOURCE_PASSWORD);
        if(url.contains("mysql")){
            dbType = "MYSQL";
        }
        DataSourceFactory factory = DataSourceFactoryProducer.getFactory(dbType, url, username, password);
        dataSource = factory.createDataSource();
    }

    private static Connection getConnection(){
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 执行更新语句
     *
     * @param sql
     * @param params
     * @return
     */
    public int executeUpdate(String sql, Object[] params) {
        int rtn = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            pstmt = conn.prepareStatement(sql);

            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
            }

            rtn = pstmt.executeUpdate();

            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return rtn;
    }


    /**
     * 执行查询语句
     *
     * @param sql
     * @param params
     * @param callback
     */
    public void executeQuery(String sql, Object[] params, QueryCallback callback) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);

            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
            }

            rs = pstmt.executeQuery();

            callback.process(rs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }


    /**
     * 批量执行
     *
     * @param sql
     * @param paramsList
     * @return
     */
    public int[] executeBatch(String sql, List<Object[]> paramsList) {
        int[] rtn = null;
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();

            // 第一步：使用Connection对象，取消自动提交
            conn.setAutoCommit(false);

            pstmt = conn.prepareStatement(sql);

            // 第二步：使用PreparedStatement.addBatch()方法加入批量的SQL参数
            if (paramsList != null && paramsList.size() > 0) {
                for (Object[] params : paramsList) {
                    for (int i = 0; i < params.length; i++) {
                        pstmt.setObject(i + 1, params[i]);
                    }
                    pstmt.addBatch();
                }
            }

            // 第三步：使用PreparedStatement.executeBatch()方法，执行批量的SQL语句
            rtn = pstmt.executeBatch();

            // 最后一步：使用Connection对象，提交批量的SQL语句
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return rtn;
    }


    /**
     * 定义回调接口，提供更强大的查询结果处理能力
     */
    public static interface QueryCallback {

        /**
         * 处理查询结果
         *
         * @param rs
         * @throws Exception
         */
        void process(ResultSet rs) throws Exception;

    }


}
