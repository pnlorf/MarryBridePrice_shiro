package com.pnlorf.shiro;

import com.pnlorf.mapper.ResourceMapper;
import com.pnlorf.util.ConfigUtils;
import org.apache.shiro.config.Ini;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 产生责任链，确定每个url的访问权限
 * <p>
 * Created by 冰诺莫语 on 2015/10/21.
 */
public class ChainDefinitionSectionMetaSource implements FactoryBean<Ini.Section> {

    @Autowired
    private ResourceMapper resourceMapper;

    // 静态资源的访问权限
    private String filterChainDefinitions = null;


    @Override
    public Ini.Section getObject() throws Exception {
        new ConfigUtils().initTableField();
        return null;
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    public void setFilterChainDefinitions(String filterChainDefinitions) {
        this.filterChainDefinitions = filterChainDefinitions;
    }
}
