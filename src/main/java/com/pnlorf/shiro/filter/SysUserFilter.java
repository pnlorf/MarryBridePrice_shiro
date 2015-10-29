package com.pnlorf.shiro.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.pnlorf.entity.UserFormMap;
import com.pnlorf.mapper.UserMapper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.filter.PathMatchingFilter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by 冰诺莫语 on 2015/10/21.
 */
public class SysUserFilter extends PathMatchingFilter {

    @Autowired
    private UserMapper userMapper;

    @Override
    protected boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {

        String username = (String) SecurityUtils.getSubject().getPrincipal();
        UserFormMap userFormMap = new UserFormMap();
        userFormMap.put("accountName", "" + username + "");
        request.setAttribute("user", userMapper.findByNames(userFormMap));
        return true;
    }
}
