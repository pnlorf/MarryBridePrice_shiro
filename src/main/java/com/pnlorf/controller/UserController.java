package com.pnlorf.controller;

import com.pnlorf.annotation.SystemLog;
import com.pnlorf.entity.UserFormMap;
import com.pnlorf.entity.UserResFormMap;
import com.pnlorf.entity.UserRoleFormMap;
import com.pnlorf.exception.SystemException;
import com.pnlorf.mapper.UserMapper;
import com.pnlorf.plugin.PageView;
import com.pnlorf.util.Common;
import com.pnlorf.util.Constant;
import com.pnlorf.util.PasswordHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by 冰诺莫语 on 2015/10/29.
 */
@Controller
@RequestMapping("/user/")
public class UserController extends BaseController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserMapper userMapper;

    @RequestMapping("list")
    public String listUI(Model model) throws Exception {
        model.addAttribute("res", findByRes());
        return Common.BACKGROUND_PATH + "/system/user/list";
    }

    @ResponseBody
    @RequestMapping("findByPage")
    public PageView findByPage(String pageNow, String pageSize) throws Exception {
        UserFormMap userFormMap = getFormMap(UserFormMap.class);
        userFormMap = toFormMap(userFormMap, pageNow, pageSize);
        pageView.setRecords(userMapper.findUserPage(userFormMap)); // 不调用默认的分页，调用自己的mapper中的findUserPage
        return pageView;
    }

    @RequestMapping("addUI")
    public String addUI(Model model) throws Exception {
        return Common.BACKGROUND_PATH + "/system/user/add";
    }

    @ResponseBody
    @RequestMapping("addEntity")
    @SystemLog(module = "系统管理", methods = "用户管理-新增用户") // 凡需要处理业务逻辑的，都需要记录操作日志
    @Transactional(readOnly = false) // 需要事务操作必须加入此注解
    public String addEntity(String txtGroupsSelect) {
        try {
            UserFormMap userFormMap = getFormMap(UserFormMap.class);
            userFormMap.put("txtGroupsSelect", txtGroupsSelect);
            PasswordHelper passwordHelper = new PasswordHelper();
            userFormMap.set("password", "123456789");
            passwordHelper.encryptPassword(userFormMap);
            userMapper.addEntity(userFormMap); // 新增后返回新增信息
            if (!Common.isEmpty(txtGroupsSelect)) {
                String[] txt = txtGroupsSelect.split(",");
                UserRoleFormMap userRoleFormMap = new UserRoleFormMap();
                for (String roleId : txt) {
                    userRoleFormMap.put("userId", userFormMap.get("id"));
                    userRoleFormMap.put("roleId", roleId);
                    userMapper.addEntity(userRoleFormMap);
                }
            }
        } catch (Exception e) {
            logger.error("添加账号异常", e);
            throw new SystemException("添加账号异常");
        }
        return Constant.SUCCESS;
    }

    @ResponseBody
    @RequestMapping("deleteEntity")
    @Transactional(readOnly = false) //需要事务操作必须加入此注解
    @SystemLog(module = "系统管理", methods = "用户管理-删除用户") //凡需要处理业务逻辑的.都需要记录操作日志
    public String deleteEntity() throws Exception {
        String[] ids = getParamValues("ids");
        for (String id : ids) {
            userMapper.deleteByAttribute("userId", id, UserRoleFormMap.class);
            userMapper.deleteByAttribute("userId", id, UserResFormMap.class);
            userMapper.deleteByAttribute("id", id, UserFormMap.class);
        }
        return Constant.SUCCESS;
    }

    @RequestMapping("editUI")
    public String editUI(Model model) throws Exception {
        String id = getParam("id");
        if (Common.isNotEmpty(id)) {
            model.addAttribute("user", userMapper.findByFirst("id", id, UserFormMap.class));
        }
        return Common.BACKGROUND_PATH + "/system/user/edit";
    }

    @ResponseBody
    @RequestMapping("editEntity")
    @Transactional(readOnly = false)
    @SystemLog(module = "系统管理", methods = "用户管理-修改用户")
    public String editEntity(String txtGroupsSelect) throws Exception {
        UserFormMap userFormMap = getFormMap(UserFormMap.class);
        userFormMap.put("txtGroupsSelect", txtGroupsSelect);
        userMapper.deleteByAttribute("userId", userFormMap.getStr("id"), UserRoleFormMap.class);
        userMapper.editEntity(userFormMap);
        if (!Common.isEmpty(txtGroupsSelect)) {
            String[] txt = txtGroupsSelect.split(",");
            for (String roleId : txt) {
                UserRoleFormMap userRoleFormMap = new UserRoleFormMap();
                userRoleFormMap.put("userId", userFormMap.get("id"));
                userRoleFormMap.put("roleId", roleId);
                userMapper.addEntity(userRoleFormMap);
            }
        }
        return Constant.SUCCESS;
    }

    /**
     * 验证账号是否存在
     *
     * @param name
     * @return
     */
    @RequestMapping("isExist")
    @ResponseBody
    public boolean isExist(String name) {
        UserFormMap account = userMapper.findByFirst("accountName", name, UserFormMap.class);
        if (account == null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 密码修改
     *
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping("updatePassword")
    public String updatePassword(Model model) throws Exception {
        return Common.BACKGROUND_PATH + "/system/user/updatePassword";
    }

    /**
     * 保存新密码
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("editPassword")
    @ResponseBody
    @Transactional(readOnly = false)
    @SystemLog(module = "系统管理", methods = "用户管理-修改密码")
    public String editPassword() throws Exception {
        // 当验证通过后，把用户信息放在session里
        UserFormMap userFormMap = getFormMap(UserFormMap.class);
        userFormMap.put("password", userFormMap.get("newpassword"));
        // 这里对修改的密码进行加密
        PasswordHelper passwordHelper = new PasswordHelper();
        passwordHelper.encryptPassword(userFormMap);
		userMapper.editEntity(userFormMap);
        return Constant.SUCCESS;
    }
}
