package com.pnlorf.controller;

import com.pnlorf.entity.UserLoginFormMap;
import com.pnlorf.mapper.UserLoginMapper;
import com.pnlorf.plugin.PageView;
import com.pnlorf.util.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by 冰诺莫语 on 2015/10/29.
 */
@Controller
@RequestMapping("/userlogin/")
public class UserLoginController extends BaseController {

    @Autowired
    private UserLoginMapper userLoginMapper;

    @RequestMapping("listUI")
    public String listUI(Model model) throws Exception {
        return Common.BACKGROUND_PATH + "/system/userlogin/list";
    }

    @ResponseBody
    @RequestMapping("findByPage")
    public PageView findByPage(String pageNow, String pageSize) throws Exception {
        UserLoginFormMap userLoginFormMap = getFormMap(UserLoginFormMap.class);
        userLoginFormMap = toFormMap(userLoginFormMap, pageNow, pageSize);
        pageView.setRecords(userLoginMapper.findByPage(userLoginFormMap));
        return pageView;
    }

}
