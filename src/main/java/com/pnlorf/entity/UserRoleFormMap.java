package com.pnlorf.entity;

import com.pnlorf.annotation.TableSeg;
import com.pnlorf.util.FormMap;

/**
 * 用户角色中间表实体表
 * <p>
 * Created by 冰诺莫语 on 2015/10/22.
 */
@TableSeg(tableName = "user_role", id = "id")
public class UserRoleFormMap extends FormMap<String, Object> {

    private static final long serialVersionUID = 1L;
}
