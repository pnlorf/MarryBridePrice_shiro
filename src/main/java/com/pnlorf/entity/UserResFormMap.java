package com.pnlorf.entity;

import com.pnlorf.annotation.TableSeg;
import com.pnlorf.util.FormMap;

/**
 * 实体表
 * Created by 冰诺莫语 on 2015/10/29.
 */
@TableSeg(tableName = "user_res", id = "id")
public class UserResFormMap extends FormMap<String, Object> {
    private static final long serialVersionUID = 1L;
}
