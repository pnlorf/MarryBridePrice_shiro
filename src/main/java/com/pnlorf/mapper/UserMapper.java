package com.pnlorf.mapper;

import com.pnlorf.entity.UserFormMap;

import java.util.List;

/**
 * Created by 冰诺莫语 on 2015/10/25.
 */
public interface UserMapper extends BaseMapper {

    public List<UserFormMap> findUserPage(UserFormMap userFormMap);
}
