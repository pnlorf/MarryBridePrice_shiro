package com.pnlorf.controller;

import com.pnlorf.entity.LogFormMap;
import com.pnlorf.mapper.LogMapper;
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
@RequestMapping("/log/")
public class LogController extends BaseController {

    @Autowired
    private LogMapper logMapper;

    @RequestMapping("list")
    public String listUI(Model model) throws Exception {
        return Common.BACKGROUND_PATH + "/system/log/list";
    }

    @ResponseBody
    @RequestMapping("findByPage")
    public PageView findByPage(String pageNow,
                               String pageSize) throws Exception {
        LogFormMap logFormMap = getFormMap(LogFormMap.class);
        String order = " order by id asc";
        logFormMap.put("$orderby", order);
        logFormMap = toFormMap(logFormMap, pageNow, pageSize);
        pageView.setRecords(logMapper.findByPage(logFormMap));
        return pageView;
    }
}
