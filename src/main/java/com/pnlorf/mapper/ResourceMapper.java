package com.pnlorf.mapper;

import com.pnlorf.entity.ResFormMap;

import java.util.List;

/**
 * Created by 冰诺莫语 on 2015/10/21.
 */
public interface ResourceMapper extends BaseMapper {

    List<ResFormMap> findChildLists(ResFormMap map);

    List<ResFormMap> findRes(ResFormMap map);

    void updateSortOrder(List<ResFormMap> maps);

    List<ResFormMap> findUserResources(String userId);
}
