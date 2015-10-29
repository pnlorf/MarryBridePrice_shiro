package com.pnlorf.controller;

import com.pnlorf.entity.ResFormMap;
import com.pnlorf.entity.RoleFormMap;
import com.pnlorf.entity.UserFormMap;
import com.pnlorf.entity.UserLoginFormMap;
import com.pnlorf.mapper.ResourceMapper;
import com.pnlorf.mapper.RoleMapper;
import com.pnlorf.mapper.UserLoginMapper;
import com.pnlorf.util.Common;
import com.pnlorf.util.TreeObject;
import com.pnlorf.util.TreeUtil;
import com.sun.javafx.sg.prism.NGShape;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 进行管理后台框架界面的类
 * <p>
 * Created by 冰诺莫语 on 2015/10/23.
 */
@Controller
@RequestMapping("/")
public class BackgroundController extends BaseController {

    private final Logger logger = LoggerFactory.getLogger(BackgroundController.class);

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private UserLoginMapper userLoginMapper;

    @Autowired
    private RoleMapper roleMapper;

    @RequestMapping(value = "login", method = RequestMethod.GET, produces = "text/html; charset=utf-8")
    public String login(HttpServletRequest request) {
        request.removeAttribute("error");
        return "/login";
    }

    /**
     * @param username
     * @param password
     * @param request
     * @return
     */
    @RequestMapping(value = "login", method = RequestMethod.POST, produces = "text/html; charset=utf-8")
    public String login(String username, String password, HttpServletRequest request) {
        try {
            if (!request.getMethod().equals("POST")) {
                request.setAttribute("error", "支持POST方法提交！");
            }
            if (Common.isEmpty(username) || Common.isEmpty(password)) {
                request.setAttribute("error", "用户名或密码不能为空！");
                return "/login";
            }
            // 想要得到SecurityUtils.getSubject()的对象访问地址必须跟shiro的拦截地址内向匹配，不然回报空指针
            Subject user = SecurityUtils.getSubject();
            // 认证执行者交由ShiroDbRealm中的doGetAuthenticationInfo处理
            // 当以上认证成功后会向下下执行，认证失败会抛出异常
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            try {
                user.login(token);
            } catch (LockedAccountException lae) {
                token.clear();
                logger.warn("用户已被锁定", lae.getMessage());
                logger.debug("用户已被锁定", lae);
                request.setAttribute("error", "用户已被锁定不能登录，请与管理员联系");
                return "/login";
            } catch (ExcessiveAttemptsException e) {
                token.clear();
                logger.warn("账号：" + username + " 登录失败次数过多，锁定十分钟", e.getMessage());
                logger.debug("账号：" + username + " 登录失败次数过多，锁定十分钟", e);
                request.setAttribute("error", "账号：" + username + " 登录失败次数过多，锁定十分钟");
                ;
                return "/login";
            } catch (AuthenticationException e) {
                token.clear();
                logger.warn("用户名或密码不正确！", e.getMessage());
                logger.debug("用户名或密码不正确！", e);
                request.setAttribute("error", "用户名或密码不正确！");
                return "/login";
            }
            UserLoginFormMap userLoginFormMap = new UserLoginFormMap();
            Session session = SecurityUtils.getSubject().getSession();
            userLoginFormMap.put("userId", session.getAttribute("userSessionId"));
            userLoginFormMap.put("accountName", username);
            userLoginFormMap.put("loginIp", session.getHost());
            userLoginMapper.addEntity(userLoginFormMap);
            request.removeAttribute("error");
        } catch (Exception e) {
            logger.error("登录异常", e);
            request.setAttribute("error", "登录异常，请联系管理员！");
            return "/login";
        }
        return "redirect:index";
    }

    @RequestMapping("index")
    public String index(Model model) throws Exception {
        // 获取登录的bean
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        UserFormMap userFormMap = (UserFormMap) Common.findUserSession(request);
        RoleFormMap roleFormMap = new RoleFormMap();
        roleFormMap.put("userId", userFormMap.get("id"));
        List<RoleFormMap> roles = roleMapper.selectUserRole(roleFormMap);
        List<TreeObject> list = new ArrayList<TreeObject>();
        for (RoleFormMap role : roles) {
            String roleId = String.valueOf(role.get("id"));
            if (StringUtils.isNotBlank(roleId)){
                List<ResFormMap> maps = resourceMapper.findRoleResources(roleId);
                for (ResFormMap map : maps){
                    TreeObject ts = new TreeObject();
                    Common.flushObject(ts, map);
                    list.add(ts);
                }
            }
        }
        TreeUtil treeUtil = new TreeUtil();
        List<TreeObject> ns = treeUtil.getChildTreeObjects(list, 0);
        model.addAttribute("list", ns);
        // 登陆的信息回传页面
        model.addAttribute("userFormMap", userFormMap);
        return "/index";
    }

    @RequestMapping("menu")
    public String menu(Model model) {
        return "/framework/menu";
    }

    /**
     * 获取某个用户的全县资源
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("findAuthority")
    public List<String> findAuthority(HttpServletRequest request) {
        return null;
    }

    @RequestMapping("download")
    public void download(String fileName, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        request.setCharacterEncoding("utf-8");
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        String ctxPath = request.getSession().getServletContext().getRealPath("/") + "\\" + "filezip\\";
        String downloadPath = ctxPath + fileName;
        logger.info("下载路径:" + downloadPath);
        try {
            long fileLenth = new File(downloadPath).length();
            response.setContentType("application/x-msdownload");
            response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes("utf-8"), "ISO8859-1"));
            response.setHeader("Content-Length", String.valueOf(fileLenth));
            bis = new BufferedInputStream(new FileInputStream(downloadPath));
            bos = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
            bos.flush();
        } catch (Exception e) {
            logger.error("下载异常:", e);
        } finally {
            if (bos != null) {
                bos.close();
            }
            if (bis != null) {
                bis.close();
            }
        }
    }

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public String logout() {
        // 使用权限管理工具进行用户的退出，注销登录
        SecurityUtils.getSubject().logout();//session会注销，在SessionListener监听session销毁，清理权限缓存
        return "redirect:login";
    }

    @RequestMapping("install")
    public String install() throws Exception {
        try {
            Properties props = Resources.getResourceAsProperties("jdbc.properties");
            String url = props.getProperty("jdbc.url");
            String driver = props.getProperty("jdbc.driverClass");
            String username = props.getProperty("jdbc.username");
            String password = props.getProperty("jdbc.password");
            Class.forName(driver).newInstance();
            Connection conn = DriverManager.getConnection(url, username, password);
            ScriptRunner runner = new ScriptRunner(conn);
            runner.setErrorLogWriter(null);
            runner.setLogWriter(null);
            runner.runScript(new InputStreamReader(getClass().getResourceAsStream("/install.sql"), "utf-8"));
        } catch (Exception e) {
            logger.error("初始化失败！请联系管理员", e);
            return "初始化失败！请联系管理员";
        }
        return "/install";
    }

}
