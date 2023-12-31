package com.ruoyi.common.mybatis.method;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 批量插入方法实现
 */
@Slf4j
public class DuplicateKeyUpdateMethod extends AbstractMethod {
    public DuplicateKeyUpdateMethod() {
        super("duplicateKeyUpdate");
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        KeyGenerator keyGenerator = new NoKeyGenerator();
        String sqlScript = "<script>\nINSERT INTO %s %s VALUES %s %s \n</script>";
        String columnScript = SqlScriptUtils.convertTrim(getAllInsertSqlColumnMaybeIf(tableInfo), "(", ")", (String) null, ",");
        String valuesScript = getAllInsertSqlPropertyMaybeIf(tableInfo);
        String sqlDuplicate = sqlDuplicate(tableInfo);
        String sql = String.format(sqlScript, tableInfo.getTableName(), columnScript, valuesScript, sqlDuplicate);


        SqlSource sqlSource = this.languageDriver.createSqlSource(this.configuration, sql, modelClass);
        return this.addInsertMappedStatement(mapperClass, modelClass, sqlSource, new NoKeyGenerator(), null, null);

    }

    /**
     * sqlDuplicate
     *
     * @return
     */
    private String sqlDuplicate(TableInfo tableInfo) {
        StringBuilder sqlDuplicate = new StringBuilder();
        sqlDuplicate.append(" ON DUPLICATE KEY UPDATE ");
        sqlDuplicate.append(" <trim suffixOverrides=\",\"> ");


        List<TableFieldInfo> fieldList = tableInfo.getFieldList();

        fieldList.forEach((tempColumn) -> {
            sqlDuplicate.append("<if test=\"null != list[0].");
            sqlDuplicate.append(tempColumn.getProperty());
            sqlDuplicate.append("\">");
            sqlDuplicate.append(tempColumn.getColumn());
            sqlDuplicate.append(" = ");
            sqlDuplicate.append(" values(");
            sqlDuplicate.append(tempColumn.getColumn());
            sqlDuplicate.append("),</if>");
        });
        sqlDuplicate.append("</trim>");
        return sqlDuplicate.toString();
    }

    /**
     * 复写方法
     *
     * @param tableInfo
     * @return
     */
    private String getAllInsertSqlColumnMaybeIf(TableInfo tableInfo) {
        return tableInfo.getKeyInsertSqlColumn(false, "", false) + (String) tableInfo.getFieldList().stream().map(tableFieldInfo -> {

            String sqlScript = tableFieldInfo.getInsertSqlColumn();
            String property = "list[0]." + tableFieldInfo.getProperty();
            FieldStrategy fieldStrategy = tableFieldInfo.getInsertStrategy();

            if (fieldStrategy == FieldStrategy.NEVER) {
                return null;
            } else if (fieldStrategy == FieldStrategy.IGNORED) {
                return sqlScript;
            } else {
                return fieldStrategy == FieldStrategy.NOT_EMPTY && tableFieldInfo.isCharSequence() ? SqlScriptUtils.convertIf(sqlScript, String.format("%s != null and %s != ''", property, property), false) : SqlScriptUtils.convertIf(sqlScript, String.format("%s != null", property), false);
            }
        }).filter(Objects::nonNull).collect(Collectors.joining(""));
    }

    /**
     * 复写insert
     *
     * @param tableInfo
     * @return
     */
    public String getAllInsertSqlPropertyMaybeIf(TableInfo tableInfo) {
        String newPrefix = "list[0].";
        String insertSql = tableInfo.getKeyInsertSqlProperty(false, "record.", false) + (String) tableInfo.getFieldList().stream().map((tableFieldInfo) -> {

            String sqlScript = "#{record." + tableFieldInfo.getProperty() + "},";
            String property = newPrefix + tableFieldInfo.getProperty();
            FieldStrategy fieldStrategy = tableFieldInfo.getInsertStrategy();

            if (fieldStrategy == FieldStrategy.NEVER) {
                return null;
            } else if (fieldStrategy == FieldStrategy.IGNORED) {
                return sqlScript;
            } else {
                return fieldStrategy == FieldStrategy.NOT_EMPTY && tableFieldInfo.isCharSequence() ? SqlScriptUtils.convertIf(sqlScript, String.format("%s != null and %s != ''", property, property), false) : SqlScriptUtils.convertIf(sqlScript, String.format("%s != null", property), false);
            }

        }).filter(Objects::nonNull).collect(Collectors.joining(""));

        StringBuilder sql = new StringBuilder();
        sql.append("<foreach collection=\"list\" item=\"record\" separator=\",\" >");
        sql.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        sql.append(insertSql);
        sql.append("</trim>");
        sql.append("</foreach>");
        return sql.toString();
    }

}
