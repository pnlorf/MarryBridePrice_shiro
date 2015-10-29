package com.pnlorf.controller;

import com.pnlorf.annotation.SystemLog;
import com.pnlorf.entity.*;
import com.pnlorf.mapper.ResourceMapper;
import com.pnlorf.mapper.RoleMapper;
import com.pnlorf.util.Common;
import com.pnlorf.util.Constant;
import com.pnlorf.util.TreeObject;
import com.pnlorf.util.TreeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 冰诺莫语 on 2015/10/29.
 */
@Controller
@RequestMapping("/resources/")
public class ResourcesController extends BaseController {

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private RoleMapper roleMapper;

    /**
     * 存放返回界面的model
     *
     * @param model
     * @return
     */
    @ResponseBody
    @RequestMapping("treelists")
    public ResFormMap findByPage(Model model) {
        ResFormMap resFormMap = getFormMap(ResFormMap.class);
        String order = " order by level asc";
        resFormMap.put("$orderby", order);
        List<ResFormMap> mps = resourceMapper.findByNames(resFormMap);
        List<TreeObject> list = new ArrayList<TreeObject>();
        for (ResFormMap map : mps) {
            TreeObject ts = new TreeObject();
            Common.flushObject(ts, map);
            list.add(ts);
        }
        TreeUtil treeUtil = new TreeUtil();
        List<TreeObject> ns = treeUtil.getChildTreeObjects(list, 0);
        resFormMap = new ResFormMap();
        resFormMap.put("treelists", ns);
        return resFormMap;
    }

    @ResponseBody
    @RequestMapping("reslists")
    public List<TreeObject> resLists(Model model) throws Exception {
        ResFormMap resFormMap = getFormMap(ResFormMap.class);
        List<ResFormMap> mps = resourceMapper.findByWhere(resFormMap);
        List<TreeObject> list = new ArrayList<TreeObject>();
        for (ResFormMap map : mps) {
            TreeObject ts = new TreeObject();
            Common.flushObject(ts, map);
            list.add(ts);
        }
        TreeUtil treeUtil = new TreeUtil();
        List<TreeObject> ns = treeUtil.getChildTreeObjects(list, 0, "　");
        return ns;
    }

    /**
     * 存放返回界面的model
     *
     * @param model
     * @return
     */
    @RequestMapping("list")
    public String list(Model model) {
        model.addAttribute("res", findByRes());
        return Common.BACKGROUND_PATH + "/system/resources/list";
    }

    /**
     * 跳转到修改界面
     *
     * @param model
     * @return
     */
    @RequestMapping("editUI")
    public String editUI(Model model) {
        String id = getParam("id");
        if (Common.isNotEmpty(id)) {
            model.addAttribute("resources", resourceMapper.findByFirst("id", id, ResFormMap.class));
        }
        return Common.BACKGROUND_PATH + "/system/resources/edit";
    }

    /**
     * 跳转到新增界面
     *
     * @param model
     * @return
     */
    @RequestMapping("addUI")
    public String addUI(Model model) {
        return Common.BACKGROUND_PATH + "/system/resources/add";
    }

    /**
     * 权限分配页面
     *
     * @param model
     * @return
     */
    @RequestMapping("permissions")
    public String permissions(Model model) {
        ResFormMap resFormMap = getFormMap(ResFormMap.class);
        List<ResFormMap> mps = resourceMapper.findByWhere(resFormMap);
        List<TreeObject> list = new ArrayList<TreeObject>();
        for (ResFormMap map : mps) {
            TreeObject ts = new TreeObject();
            Common.flushObject(ts, map);
            list.add(ts);
        }
        TreeUtil treeUtil = new TreeUtil();
        List<TreeObject> ns = treeUtil.getChildTreeObjects(list, 0);
        model.addAttribute("permissions", ns);
        return Common.BACKGROUND_PATH + "/system/resources/permissions";
    }

    /**
     * 添加菜单
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("addEntity")
    @ResponseBody
    @Transactional(readOnly = false)
    @SystemLog(module = "系统管理", methods = "资源管理-新增资源")
    public String addEntity() throws Exception {
        ResFormMap resFormMap = getFormMap(ResFormMap.class);
        if ("2".equals(resFormMap.get("type"))) {
            resFormMap.put("description", Common.htmltoString(resFormMap.get("description") + ""));
        }
        Object o = resFormMap.get("ishide");
        if (null == o) {
            resFormMap.set("ishide", "0");
        }
        resourceMapper.addEntity(resFormMap);
        return Constant.SUCCESS;
    }

    /**
     * 更新菜单
     *
     * @param model
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("editEntity")
    @Transactional(readOnly = false)
    @SystemLog(module = "系统管理", methods = "资源管理-修改资源")
    public String editEntity(Model model) throws Exception {
        ResFormMap resFormMap = getFormMap(ResFormMap.class);
        if ("2".equals(resFormMap.get("type"))) {
            resFormMap.put("description", Common.htmltoString(resFormMap.get("description") + ""));
        }
        Object o = resFormMap.get("ishide");
        if (null == o) {
            resFormMap.set("ishide", "0");
        }
        resourceMapper.editEntity(resFormMap);
        return Constant.SUCCESS;
    }

    /**
     * 根据ID删除菜单
     *
     * @param model
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("deleteEntity")
    @SystemLog(module = "系统管理", methods = "资源管理-删除资源")//凡需要处理业务逻辑的.都需要记录操作日志
    public String deleteEntity(Model model) throws Exception {
        String[] ids = getParamValues("ids");
        for (String id : ids) {
            resourceMapper.deleteByAttribute("id", id, ResFormMap.class);
        }
        ;
        return Constant.SUCCESS;
    }

    @RequestMapping("sortUpdate")
    @ResponseBody
    @Transactional(readOnly = false)//需要事务操作必须加入此注解
    public String sortUpdate(Params params) throws Exception {
        List<String> ids = params.getId();
        List<String> es = params.getRowId();
        List<ResFormMap> maps = new ArrayList<ResFormMap>();
        for (int i = 0; i < ids.size(); i++) {
            ResFormMap map = new ResFormMap();
            map.put("id", ids.get(i));
            map.put("level", es.get(i));
            maps.add(map);
        }
        resourceMapper.updateSortOrder(maps);
        return Constant.SUCCESS;
    }

    /**
     * 查找相关资源
     * 2015年10月27日
     */
    @ResponseBody
    @RequestMapping("findRes")
    public List<ResFormMap> findUserRes() {
        ResFormMap resFormMap = getFormMap(ResFormMap.class);
        List<ResFormMap> resQ = new ArrayList<ResFormMap>();
        // 根据不同的参数处理不同的查找
        // 如果是权限，则直接查找 return
        // 如果是用户，则先查找相关角色，查找结果组合成List return
        if (resFormMap.containsKey("roleId")) {
            resQ = resourceMapper.findRes(resFormMap);
            return resQ;
        } else if (resFormMap.containsKey("userId")) {
            RoleFormMap roleFormMap = new RoleFormMap();
            roleFormMap.put("userId", resFormMap.get("userId"));
            List<RoleFormMap> roles = roleMapper.selectUserRole(roleFormMap);

            for (RoleFormMap role : roles) {
                resFormMap.clear();
                resFormMap.put("roleId", role.get("id"));
                List<ResFormMap> rs = resourceMapper.findRes(resFormMap);
                for (ResFormMap res : rs) {
                    resQ.add(res);
                }
            }
            return resQ;
        }
        return resQ;
    }

    @ResponseBody
    @RequestMapping("addUserRes")
    @Transactional(readOnly = false)
    @SystemLog(module = "系统管理", methods = "用户管理/组管理-修改权限")
    public String addUserRes() throws Exception {
        String userId = "";
        String u = getParam("userId");
        String g = getParam("roleId");
        if (null != u && !Common.isEmpty(u.toString())) {
            userId = u.toString();
        } else if (null != g && !Common.isEmpty(g.toString())) {
            List<UserRoleFormMap> gs = resourceMapper.findByAttribute("roleId", g.toString(), UserRoleFormMap.class);
            for (UserRoleFormMap ug : gs) {
                userId += ug.get("userId") + ",";
            }
        }
        userId = Common.trimComma(userId);
        String[] users = userId.split(",");
        for (String uid : users) {
            resourceMapper.deleteByAttribute("userId", uid, UserResFormMap.class);
            String[] s = getParamValues("resId[]");
            List<UserResFormMap> resUserFormMaps = new ArrayList<UserResFormMap>();
            for (String rid : s) {
                UserResFormMap resUserFormMap = new UserResFormMap();
                resUserFormMap.put("resId", rid);
                resUserFormMap.put("userId", uid);
                resUserFormMaps.add(resUserFormMap);

            }
            resourceMapper.batchSave(resUserFormMaps);
        }
        return Constant.SUCCESS;
    }

    @ResponseBody
    @RequestMapping("findByButton")
    public List<ButtonFormMap> findByButtom() {
        return resourceMapper.findByWhere(new ButtonFormMap());
    }

    /**
     * 验证菜单是否存在
     *
     * @param name
     * @return
     */
    @RequestMapping("isExist")
    @ResponseBody
    public boolean isExist(String name, String resKey) {
        ResFormMap resFormMap = getFormMap(ResFormMap.class);
        List<ResFormMap> r = resourceMapper.findByNames(resFormMap);
        if (r.size() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return
     * @throws Exception
     * @desc 角色分配权限
     */
    @ResponseBody
    @RequestMapping("addRoleRes")
    @Transactional(readOnly = false)//需要事务操作必须加入此注解
    @SystemLog(module = "系统管理", methods = "角色管理/组管理-修改权限")//凡需要处理业务逻辑的.都需要记录操作日志
    public String addRoleRes() throws Exception {
        //角色对应多个资源id
        String roleId = getParam("roleId");
        String[] s = getParamValues("resId[]");
        List<RoleResFormMap> roleResFormMaps = new ArrayList<RoleResFormMap>();
        //角色关联多个资源id,先删除后保存
        resourceMapper.deleteByAttribute("roleId", roleId, RoleResFormMap.class);
        for (String rid : s) {
            RoleResFormMap roleResFormMap = new RoleResFormMap();
            roleResFormMap.put("resId", rid);
            roleResFormMap.put("roleId", roleId);
            roleResFormMaps.add(roleResFormMap);
        }
        resourceMapper.batchSave(roleResFormMaps);
        return Constant.SUCCESS;
    }
}
