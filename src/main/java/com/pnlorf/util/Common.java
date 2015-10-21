package com.pnlorf.util;

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
}
