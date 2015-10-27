package com.pnlorf.util;

import com.pnlorf.annotation.TableSeg;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.type.DoubleTypeHandler;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by 冰诺莫语 on 2015/10/21.
 */
public class Common {

    /**
     * 后台访问
     */
    public static final String BACKGROUND_PATH = "WEB-INF/jsp";
    /**
     * 后台访问
     */
    public static final String WEB_PATH = "/WEB-INF/jsp/web";

    private static final String EN_NAME = "en_name";

    private static final String ZH_NAME = "zh_name";

    private static final String ZB_NAME = "zb_name";
    // 默认除法运算精度
    private static final int DEF_DIV_SCALE = 10;


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
                froMmap.put(Constant.WOLF_TABLE, table.tableName());
            } else {
                throw new NullPointerException("在" + name + " 没有找到数据库表对应该的注解!");
            }
            return froMmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return froMmap;
    }


    /**
     * 判断变量是否为空
     *
     * @param s
     * @return
     */
    public static boolean isEmpty(String s) {
        if (null == s || "".equals(s) || "".equals(s.trim()) || "null".equalsIgnoreCase(s)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取登录账号的的对象
     *
     * @param request
     * @return Object 返回是Object..需要转型为(Account)Object
     */
    public static Object findUserSession(HttpServletRequest request) {
        return (Object) request.getSession().getAttribute("userSession");
    }

    /**
     * html转议
     *
     * @param content
     * @return
     * @descript
     */
    public static String string2Html(String content) {
        if (content == null)
            return "";
        String html = content;
        html = html.replace("&apos;", "'");
        html = html.replaceAll("&amp;", "&");
        html = html.replace("&quot;", "\""); // "
        html = html.replace("&nbsp;&nbsp;", "\t");// 替换跳格
        html = html.replace("&nbsp;", " ");// 替换空格
        html = html.replace("&lt;", "<");
        html = html.replaceAll("&gt;", ">");

        return html;
    }

    /**
     * 将Map形式的键值对中的值转换为泛型形参给出的类中的属性值， t一般代表pojo类
     *
     * @param t
     * @param params
     * @param <T>
     * @return
     */
    public static <T extends Object> T flushObject(T t, Map<String, Object> params) {
        if (params == null || t == null) {
            return t;
        }

        Class<?> clazz = t.getClass();
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                Field[] fields = clazz.getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    String name = fields[i].getName(); //获取属性的名字
                    Object value = params.get(name);
                    if (value != null && !"".equals(value)) {
                        // 注意下面一句，不设置true的话，不能修改private类型变量的值
                        fields[i].setAccessible(true);
                        fields[i].set(t, value);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return t;
    }

    /**
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到小数点以后10位，以后的数字四舍五入
     *
     * @param v1 被除数
     * @param v2 出书
     * @return 两个参数的商
     */
    public static double div(double v1, double v2) {
        return div(v1, v2, DEF_DIV_SCALE);
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。
     *
     * @param v1          被除数
     * @param v2          除数
     * @param defDivScale 表示需要精确到小数点以后几位
     * @return 两个参数的商
     */
    public static double div(double v1, double v2, int defDivScale) {
        if (defDivScale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, defDivScale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


}
