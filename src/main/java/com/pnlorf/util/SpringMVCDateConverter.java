package com.pnlorf.util;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.request.WebRequest;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * spring3mvc的日期传递[前台-后台]bug：
 * org.springframework.validation.BindException
 * 的解决方式，包括xml的配置
 * new SimpleDateFormat("yyyy-MM-dd"); 这里的日期格式必须与提交的日期格式一直
 * <p>
 * Created by 冰诺莫语 on 2015/11/3.
 */
public class SpringMVCDateConverter implements WebBindingInitializer {
    @Override
    public void initBinder(WebDataBinder webDataBinder, WebRequest webRequest) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        webDataBinder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
    }
}
