package com.pnlorf.mapper;


import com.pnlorf.entity.RoleFormMap;

import java.util.List;

/**
 * Created by 冰诺莫语 on 2015/10/29.
 */
public interface RoleMapper extends BaseMapper {

    List<RoleFormMap> selectUserRole(RoleFormMap roleFormMap);

    void deleteById(RoleFormMap roleFormMap);
}
