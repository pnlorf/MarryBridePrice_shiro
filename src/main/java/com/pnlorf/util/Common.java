package com.pnlorf.util;

import com.pnlorf.annotation.TableSeg;
import org.apache.commons.lang.StringUtils;

/**
 * Created by 冰诺莫语 on 2015/10/21.
 */
public class Common {

    /**
     * 去除字符串最后一个逗号，若传入为空则返回空字符串
     *
     * @param param
     * @return
     */
    public static String trimComma(String param) {
        if (StringUtils.isNotBlank(param)) {
            if (param.endsWith(",")) {
                return param.substring(0, param.length() - 1);
            } else {
                return param;
            }
        } else {
            return "";
        }
    }

    public static FormMap<String, Object> toHashMap(Object parameterObject) {
        FormMap<String, Object> froMmap = (FormMap<String, Object>) parameterObject;
        try {
            String name = parameterObject.getClass().getName();
            Class<?> clazz = Class.forName(name);
            boolean flag = clazz.isAnnotationPresent(TableSeg.class); // 某个类是不是存在TableSeg注解
            if (flag) {
                TableSeg table = (TableSeg) clazz.getAnnotation(TableSeg.class);
                // logger.info(" 公共方法被调用,传入参数 ==>> " + froMmap);
                froMmap.put("ly_table", table.tableName());
            } else {
                throw new NullPointerException("在" + name + " 没有找到数据库表对应该的注解!");
            }
            return froMmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return froMmap;
    }
}
