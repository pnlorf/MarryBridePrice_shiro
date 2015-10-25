package com.pnlorf.controller;

import com.pnlorf.entity.ResFormMap;
import com.pnlorf.entity.UserFormMap;
import com.pnlorf.mapper.ResourceMapper;
import com.pnlorf.plugin.PageView;
import com.pnlorf.util.Common;
import com.pnlorf.util.FormMap;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.session.defaults.DefaultSqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by 冰诺莫语 on 2015/10/22.
 */
public class BaseController {

    private final Logger logger = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    private ResourceMapper resourceMapper;

    public PageView pageView = null;

    public PageView getPageView(String pageNow, String pageSize) {
        if (Common.isEmpty(pageNow)) {
            pageView = new PageView(1);
        } else {
            pageView = new PageView(Integer.parseInt(pageNow));
        }

        if (Common.isEmpty(pageSize)) {
            pageSize = "10";
        }

        pageView.setPageSize(Integer.parseInt(pageSize));
        return pageView;
    }

    public <T> T toFormMap(T t, String pageNow, String pageSize) {
        FormMap<String, Object> formMap = (FormMap<String, Object>) t;
        formMap.put("paging", getPageView(pageNow, pageSize));
        return t;
    }

    /**
     * 获取返回某一页面的按钮组，
     *
     * @return
     */
    public List<ResFormMap> findByRes() {
        // 资源ID
        String id = getParam("id");
        // 获取request
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        // 通过工具类获取当前登录的bean
        UserFormMap userFormMap = (UserFormMap) Common.findUserSession(request);
        // user id
        int userId = userFormMap.getInt("id");
        ResFormMap resQueryForm = new ResFormMap();
        resQueryForm.put("parentId", id);
        resQueryForm.put("userId", userId);
        List<ResFormMap> res = resourceMapper.findRes(resQueryForm);
        for (ResFormMap resFormMap : res) {
            Object o = resFormMap.get("description");
            if (o != null && !Common.isEmpty(o.toString())) {
                resFormMap.put("description", Common.string2Html(o.toString()));
            }
        }
        return res;
    }

    /**
     * 获取页面传递的某一个采参数值
     *
     * @param key
     * @return
     */
    public String getParam(String key) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request.getParameter(key);
    }

    /**
     * 获取页面传递的某一个数组值
     *
     * @param key
     * @return
     */
    public String[] getParamValues(String key) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request.getParameterValues(key);
    }

	/*
     * @ModelAttribute
	 * 这个注解作用.每执行controllor前都会先执行这个方法
	 * @param request
	 * @throws Exception
	 * @throws
	 */
    /*@ModelAttribute
    public void init(HttpServletRequest request){
		String path = Common.BACKGROUND_PATH;
		Object ep = request.getSession().getAttribute("basePath");
		if(ep!=null){
			if(!path.endsWith(ep.toString())){
				Common.BACKGROUND_PATH = "/WEB-INF/jsp/background"+ep;
			}
		}
	}*/

    /**
     * 获取传递的所有参数
     * 反射实例化对象，再设置属性值
     * 通过泛型回传对象
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getFormMap(Class<T> clazz) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Enumeration<String> en = request.getParameterNames();
        T t = null;
        try {
            t = clazz.newInstance();
            FormMap<String, Object> map = (FormMap<String, Object>) t;
            while (en.hasMoreElements()) {
                String nms = en.nextElement().toString();
                if (nms.endsWith("[]")) {
                    String[] as = request.getParameterValues(nms);
                    if (as != null && as.length != 0 && as.toString() != "[]") {
                        String mName = t.getClass().getSimpleName().toString();
                        if (nms.toUpperCase().startsWith(mName)) {
                            nms = nms.substring(nms.toUpperCase().indexOf(mName) + 1);
                            map.put(nms, as);
                        }
                    }
                } else {
                    String as = request.getParameter(nms);
                    if (!Common.isEmpty(as)) {
                        String mName = t.getClass().getSimpleName().toUpperCase();
                        if (nms.toUpperCase().startsWith(mName)) {
                            nms = nms.substring(mName.length() + 1);
                            map.put(nms, as);
                        }
                    }
                }
            }
        } catch (InstantiationException e) {
            logger.warn("Exception:", e.getMessage());
            logger.debug("Exception", e);
        } catch (IllegalAccessException e) {
            logger.warn("Exception:", e.getMessage());
            logger.debug("Exception", e);
        }
        return t;
    }

}
