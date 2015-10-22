package com.pnlorf.entity;

import com.pnlorf.annotation.TableSeg;
import com.pnlorf.util.FormMap;

/**
 * 角色role实体表
 * Created by 冰诺莫语 on 2015/10/22.
 */
@TableSeg(tableName = "role", id = "id")
public class RoleFormMap extends FormMap<String, Object> {

    private static final long serialVersionUID = 1L;
}
