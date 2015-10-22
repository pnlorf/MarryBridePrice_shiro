package com.pnlorf.mapper;

import java.util.List;
import java.util.Objects;

/**
 * 已经实现基本的增、删、改、查接口，不需要重复写
 * 所有mapper都继承这个BaseMapper
 * <p>
 * Created by 冰诺莫语 on 2015/10/22.
 */
public interface BaseMapper {

    /**
     * 1.穿入继承FormMap的子类对象，返回时一个List<T>集合
     * 2.调用findByPage接口，必须传入PageView对象！formMap.set("paging", pageView);
     * 3.根据多字段分页查询
     * 4.如果是多个id，用","组成字符串。
     * 5.formMap.put("id", "xxx")或formMap.put("id", "xxx,xxx,xxx")
     * 6.formMap.put("name", "xxx")或formMap.put("name", "xxx.xxx.xxx")
     * 7.兼容模糊查询。formMap.put("name","用户%")或formMap.put("name", "%用户%")
     *
     * @param formMap
     * @param <T>
     * @return <T> List<T>
     */
    <T> List<T> findByPage(T formMap);

    /**
     * 1.自定义where查询条件，传入继承FormMap的子类对象，返回是一个List<T>集合
     * 2.返回查询条件数据，如不传入，则返回所有数据。如果附加条件，如下
     * 3.formMap.put("where", "id=XX and name=XX order by XX")
     *
     * @param formMap
     * @param <T>
     * @return <T> List<T>
     */
    <T> List<T> findByWhere(T formMap);

    /**
     * 1.更新数据
     * 2.传入继承FormMap的子类对象
     *
     * @param formMap
     * @throws Exception
     */
    void editEntity(Object formMap) throws Exception;

    /**
     * 1.根据多字段查询
     * 2.传入继承FormMap的子类对象
     * 3.如果是多个id，用","组成字符串。
     * 4.formMap.put("id", "xxx")或formMap.put("id", "xxx,xxx,xxx")
     * 5.formMap.put("name", "xxx")或formMap.put("name", "xxx.xxx.xxx")
     * 6.兼容模糊查询。formMap.put("name","用户%")或formMap.put("name", "%用户%")
     * 7.兼容排序查询。formMap.put("$orderby","order by id desc");
     *
     * @param formMap
     * @param <T>
     * @return
     */
    <T> List<T> findByNames(T formMap);

    /**
     * 1.根据某个字段查询数据
     *
     * @param key
     * @param value
     * @param clazz
     * @param <T>
     * @return
     */
    <T> List<T> findByAttribute(String key, String value, Class<T> clazz);

    /**
     * 1.根据某个字段删除数据
     *
     * @param key
     * @param value
     * @param clazz
     * @throws Exception
     */
    void deleteByAttribute(String key, String value, Class clazz) throws Exception;

    /**
     * 1.传入继承FormMap的子类对象
     * 2.保存数据，保存数据后返回子类对象的所有数据，包括id..主键统一返回为id
     *
     * @param formMap
     * @throws Exception
     */
    void addEntity(Object formMap) throws Exception;

    /**
     * 1.批量保存数据，乳沟是mysq，在驱动连接加上&allowMultiQueries=true这个参数
     * 2.传入继承FormMap的子类对象的一个List集合
     *
     * @param formMap
     * @throws Exception
     */
    void batchSave(List formMap) throws Exception;

    /**
     * 1.根据多个字段删除，传入继承FormMap的子类对象
     * 2.如果是多个id值，用","组成字符串
     * 3.formMap.put("id","xxx") 或 formMap.put("id","xxx,xxx,xxx")
     * 4.formMap.put("name","xxx") 或 formMap.put("name","xxx,xxx,xxx")
     *
     * @param formMap
     * @throws Exception
     */
    void deleteByNames(Object formMap) throws Exception;

    /**
     * 1.根据某个字段查询数据，返回一个对象，如果返回多个值，则异常
     *
     * @param key
     * @param value
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T findByFirst(String key, String value, Class<T> clazz);
}
