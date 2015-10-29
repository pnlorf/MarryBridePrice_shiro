package com.pnlorf.shiro;

import com.pnlorf.entity.ResFormMap;
import com.pnlorf.mapper.ResourceMapper;
import com.pnlorf.util.ConfigUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.config.Ini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


/**
 * 产生责任链，确定每个url的访问权限
 * <p>
 * Created by 冰诺莫语 on 2015/10/21.
 */
public class ChainDefinitionSectionMetaSource implements FactoryBean<Ini.Section> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ResourceMapper resourceMapper;

    // 静态资源的访问权限
    private String filterChainDefinitions = null;


    @Override
    public Ini.Section getObject() throws Exception {
        new ConfigUtils().initTableField();
        Ini ini = new Ini();
        // 加载默认的url
        ini.load(filterChainDefinitions);
        Ini.Section section = ini.getSection(Ini.DEFAULT_SECTION_NAME);
        // 循环Resource的url，逐个添加到section中。section就是filterChainDefinitionMap
        // 里面的键就是链接URL，值就是存在什么条件才能访问链接
        List<ResFormMap> list = resourceMapper.findByWhere(new ResFormMap());
        for (ResFormMap resources : list) {
            // 构成permission字符串
            if (StringUtils.isNotEmpty(resources.get("resUrl") + "") && StringUtils.isNotEmpty(resources.get("resKey") + "")) {
                String permission = "perms[" + resources.get("resKey") + "]";
                logger.info(permission);
                // 不对角色进行权限验证
                // 如需要则permission = "roles["+resources.getResKey() + "]";
                section.put(resources.get("resUrl") + "", permission);
            }
        }
        // 所有资源的访问权限，必须放在最后
        /*section.put("/**","authc");*/
        section.put("/**", "authc, kickout, sysUser, user");
        return section;
    }

    @Override
    public Class<?> getObjectType() {
        return this.getClass();
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    /**
     * 通过filterChainDefinitions对默认的url过滤定义
     *
     * @param filterChainDefinitions 默认的url过滤定义
     */
    public void setFilterChainDefinitions(String filterChainDefinitions) {
        this.filterChainDefinitions = filterChainDefinitions;
    }
}
