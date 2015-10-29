package com.pnlorf.controller;

import com.pnlorf.annotation.SystemLog;
import com.pnlorf.entity.RoleFormMap;
import com.pnlorf.mapper.RoleMapper;
import com.pnlorf.plugin.PageView;
import com.pnlorf.util.Common;
import com.pnlorf.util.Constant;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by 冰诺莫语 on 2015/10/29.
 */
@Controller
@RequestMapping("/role/")
public class RoleController extends BaseController {

    @Autowired
    private RoleMapper roleMapper;

    @RequestMapping("list")
    public String listUI(Model model) throws Exception {
        model.addAttribute("res", findByRes());
        return Common.BACKGROUND_PATH + "/system/role/list";
    }

    @ResponseBody
    @RequestMapping("findByPage")
    public PageView findByPage(String pageNow, String pageSize) throws Exception {
        RoleFormMap roleFormMap = getFormMap(RoleFormMap.class);
        roleFormMap = toFormMap(roleFormMap, pageNow, pageSize);
        pageView.setRecords(roleMapper.findByPage(roleFormMap));
        return pageView;
    }

    @RequestMapping("addUI")
    public String addUI(Model model) throws Exception {
        return Common.BACKGROUND_PATH + "/system/role/add";
    }

    @ResponseBody
    @RequestMapping("addEntity")
    @Transactional(readOnly = false)
    @SystemLog(module = "系统管理", methods = "组管理-新增组")
    public String addEntity() throws Exception {
        RoleFormMap roleFormMap = getFormMap(RoleFormMap.class);
        roleMapper.addEntity(roleFormMap);
        return Constant.SUCCESS;
    }

    @ResponseBody
    @RequestMapping("deleteEntity")
    @Transactional(readOnly = false)
    @SystemLog(module = "系统管理", methods = "组管理-删除组")
    public String deleteEntity() throws Exception {
        String[] ids = getParamValues("ids");
        for (String id : ids) {
            roleMapper.deleteByAttribute("id", id, RoleFormMap.class);
        }
        return Constant.SUCCESS;
    }

    @RequestMapping("editUI")
    public String editUI(Model model) throws Exception {
        String id = getParam("id");
        if (Common.isNotEmpty(id)) {
            model.addAttribute("role", roleMapper.findByFirst("id", id, RoleFormMap.class));
        }
        return Common.BACKGROUND_PATH + "/system/role/edit";
    }

    @ResponseBody
    @RequestMapping("editEntity")
    @Transactional(readOnly = false)
    @SystemLog(module = "系统管理", methods = "组管理-修改组")
    public String editEntity() throws Exception {
        RoleFormMap roleFormMap = getFormMap(RoleFormMap.class);
        roleMapper.editEntity(roleFormMap);
        return Constant.SUCCESS;
    }

    @RequestMapping("selRole")
    public String selectRole(Model model) throws Exception {
        RoleFormMap roleFormMap = getFormMap(RoleFormMap.class);
        Object userId = roleFormMap.get("userId");
        if (null != userId) {
            List<RoleFormMap> list = roleMapper.selectUserRole(roleFormMap);
            String ugid = "";
            for (RoleFormMap ml : list) {
                ugid += ml.get("id") + ",";
            }
            ugid = Common.trimComma(ugid);
            model.addAttribute("txtRoleSelect", ugid);
            model.addAttribute("userRole", list);
            if (StringUtils.isNotBlank(ugid)) {
                roleFormMap.put("where", " where id not in (" + ugid + ")");
            }
        }
        List<RoleFormMap> roles = roleMapper.findByWhere(roleFormMap);
        model.addAttribute("role", roles);
        return Common.BACKGROUND_PATH + "/system/user/roleSelect";

    }
}
