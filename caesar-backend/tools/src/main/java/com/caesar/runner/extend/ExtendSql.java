package com.caesar.runner.extend;

public interface ExtendSql<T> {

     Boolean validateSqlSyntax(String sql);

     T parseMeta(String sql);

}
