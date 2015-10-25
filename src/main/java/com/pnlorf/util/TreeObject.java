package com.pnlorf.util;


import org.apache.ibatis.session.defaults.DefaultSqlSession;

import java.util.ArrayList;
import java.util.List;

/**
 * 这个是列表数树形式显示的实体
 * 这里的字段实在前台显示所有的，可修改
 * <p>
 * Created by 冰诺莫语 on 2015/10/23.
 */
public class TreeObject {
    private Integer id;
    private Integer parentId;
    private String name;
    private String parentName;
    private String resKey;
    private String resUrl;
    private Integer level;
    private String type;
    private String description;
    private String icon;
    private Integer isHide;
    private List<TreeObject> children = new ArrayList<TreeObject>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getResKey() {
        return resKey;
    }

    public void setResKey(String resKey) {
        this.resKey = resKey;
    }

    public String getResUrl() {
        return resUrl;
    }

    public void setResUrl(String resUrl) {
        this.resUrl = resUrl;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getIsHide() {
        return isHide;
    }

    public void setIsHide(Integer isHide) {
        this.isHide = isHide;
    }

    public List<TreeObject> getChildren() {
        return children;
    }

    public void setChildren(List<TreeObject> children) {
        this.children = children;
    }
}
