package com.pnlorf.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 * 对属性文件操作的工具类
 * 获取，新增，修改
 * 注意：一下方法读取属性文件会缓存问题，在修改属性文件时，不起作用
 * InputStream in = PropertiesUtils.class.getResourceAsStream("/config.properties");
 * 解决办法：
 * String savePath = PropertiesUtils.class.getResource("/config.properties").getPath();
 * <p>
 * Created by 冰诺莫语 on 2015/10/21.
 */
public class PropertiesUtils {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);

    /**
     * 获取属性文件的数据，根据key获取值
     *
     * @param key key
     * @return
     */
    public static String getPropertiesByKey(String key) {
        try {
            Properties props = getProperties();
            return props.getProperty(key);
        } catch (Exception e) {
            logger.warn("get properties by key exception.", e.getMessage());
            logger.debug("get properties by key exception.", e);
            return "";
        }
    }

    /**
     * 返回Properties
     *
     * @return
     */
    public static Properties getProperties() {
        Properties props = new Properties();
        String savePath = PropertiesUtils.class.getResource("/config.properties").getPath();
        // 以下方法读取属性文件会缓存问题
        // InputStream in= PropertiesUtils.class.getResourceAsStream("/config.properties");
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(savePath));
            props.load(in);
        } catch (Exception e) {
            logger.debug("load properties exception.", e);
            logger.warn("load properties exception.", e.getMessage());
            return null;
        }
        return props;
    }

    /**
     * 获取jdbc属性
     *
     * @return
     */
    public static Properties getJdbcProperties() {
        Properties properties = new Properties();
        String savePath = PropertiesUtils.class.getResource("/jdbc.properties").getPath();
        // 以下方法读取属性文件会缓存问题
        // InputStream in= PropertiesUtils.class.getResourceAsStream("/jdbc.properties");
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(savePath));
        } catch (Exception e) {
            logger.debug("get jdbc properties exception.", e);
            logger.warn("get jdbc properties exception.", e.getMessage());
            return null;
        }
        return properties;
    }

    /**
     * 写入Properties信息
     *
     * @param key   名称
     * @param value 值
     */
    public static void modifyProperties(String key, String value) {
        FileOutputStream outputStream = null;
        try {
            // 从输入流中读取属性列表(键和元素对)
            Properties properties = getProperties();
            properties.setProperty(key, value);
            String path = PropertiesUtils.class.getResource("/config.properties").getPath();
            outputStream = new FileOutputStream(path);
            properties.store(outputStream, "modify");
            outputStream.flush();
        } catch (Exception e) {
            logger.debug("modify properties exception.", e);
            logger.warn("modify properties exception.", e.getMessage());
        } finally {
            if (null != outputStream) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    logger.debug("modify properties exception.", e);
                    logger.warn("modify properties exception.", e.getMessage());
                }
            }
        }
    }
}
