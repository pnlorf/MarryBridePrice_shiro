package com.pnlorf.controller;

import com.pnlorf.entity.ServerInfoFormMap;
import com.pnlorf.mapper.ServerInfoMapper;
import com.pnlorf.plugin.PageView;
import com.pnlorf.util.Common;
import com.pnlorf.util.PropertiesUtils;
import com.pnlorf.util.SystemInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by 冰诺莫语 on 2015/10/26.
 */
@Controller
@RequestMapping("/monitor/")
public class MonitorController extends BaseController {

    @Autowired
    private ServerInfoMapper serverInfoMapper;

    @RequestMapping("list")
    public String listUI() throws Exception {
        return Common.BACKGROUND_PATH + "/system/monitor/list";
    }

    @ResponseBody
    @RequestMapping("findByPage")
    public PageView findByPage(String pageNow, String pageSize) {
        ServerInfoFormMap serverInfoFormMap = getFormMap(ServerInfoFormMap.class);
        serverInfoFormMap = toFormMap(serverInfoFormMap, pageNow, pageSize);
        pageView.setRecords(serverInfoMapper.findByPage(serverInfoFormMap));
        return pageView;
    }

    @RequestMapping("monitor")
    public String monitor() throws Exception {
        return Common.BACKGROUND_PATH + "/system/monitor/monitor";
    }

    @RequestMapping("info")
    public String info(Model model) throws Exception {
        model.addAttribute("cpu", PropertiesUtils.getPropertiesByKey("cpu"));
        model.addAttribute("jvm", PropertiesUtils.getPropertiesByKey("jvm"));
        model.addAttribute("ram", PropertiesUtils.getPropertiesByKey("ram"));
        model.addAttribute("toEmail", PropertiesUtils.getPropertiesByKey("toEmail"));
        return Common.BACKGROUND_PATH + "/system/monitor/info";
    }

    @RequestMapping("systemInfo")
    public String systemInfo(Model model) throws Exception {
        model.addAttribute("systemInfo", SystemInfo.SystemProperty());
        return Common.BACKGROUND_PATH + "/system/monitor/systemInfo";
    }



}
