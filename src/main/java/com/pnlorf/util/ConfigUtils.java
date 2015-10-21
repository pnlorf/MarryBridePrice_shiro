package com.pnlorf.util;

import com.pnlorf.annotation.TableSeg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

/**
 * Created by 冰诺莫语 on 2015/10/21.
 */
public class ConfigUtils {

    private static final Logger logger = LoggerFactory.getLogger(ConfigUtils.class);

    /**
     * 初始化数据库表字段到缓存
     */
    public void initTableField() {
        // 记录总记录数
        Statement countStmt = null;
        ResultSet resultSet = null;
        Connection connection = null; //表示数据库的连接对象
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            Properties props = PropertiesUtils.getJdbcProperties();
            // 1.使用Class
            Class.forName(props.getProperty("jdbc.driverClassName"));
            String url = props.getProperty("jdbc.url");
            String db = url.substring(url.lastIndexOf("/") + 1);
            if (db.indexOf("?") > -1) {
                db = db.substring(0, db.indexOf("?"));
            }
            // 2.连接数据库
            connection = DriverManager.getConnection(url, props.getProperty("jdbc.username"), props.getProperty("jdbc.password"));
            String packageName = "com.pnlorf.entity";
            List<String> classNames = ClassUtil.getClassName(packageName, false);
            String tabs = "";
            if (classNames != null) {
                for (String className : classNames) {
                    Class<?> clazz = Class.forName(className);
                    boolean flag = clazz.isAnnotationPresent(TableSeg.class);// 某个类是不是存在TableSeg注解
                    if (flag) {
                        TableSeg table = clazz.getAnnotation(TableSeg.class);
                        tabs += "'" + table.tableName() + "',";
                        map.put(table.tableName(), table.id());
                    }
                }
            }
            tabs = Common.trimComma(tabs);
            // 尽量减少对数据库I/O流操作，一次查询所有表的字段
            String sql = "select TABLE_NAME, group_concat(COLUMN_NAME) COLUMN_NAME from information_schema.columns where table_name in (" + tabs + ") and table_schema = '" + db + "' GROUP BY TABLE_NAME";
            countStmt = connection.createStatement();
            resultSet = countStmt.executeQuery(sql);
            while (resultSet.next()) {
                Map<String, Object> m = new HashMap<String, Object>();
                m.put("field", resultSet.getString("COLUMN_NAME"));
                String ble = resultSet.getString("TABLE_NAME"); //表名
                m.put("column_key", map.get(ble));// 获取表的主键
                EhcacheUtils.put(ble, m); // 某表对应的主键和字段放到缓存
            }
        } catch (Exception e) {
            logger.error("初始化数据失败，没法加载表字段到缓存 -->> " + e.fillInStackTrace());
            ;
            logger.warn("初始化数据失败，没法加载表字段到缓存 -->>", e);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    logger.error("Result Set close exception.", e);
                }
            }

            if (countStmt != null) {
                try {
                    countStmt.close();
                } catch (SQLException e) {
                    logger.error("Count Statement close exception.", e);
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error("Connection close exception.", e);
                }
            }

        }
    }
}
