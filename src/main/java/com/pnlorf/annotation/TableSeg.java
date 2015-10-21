package com.pnlorf.annotation;

import com.pnlorf.shiro.SecurityRealm;

import java.lang.annotation.*;

/**
 * 需要给两个值
 * <p>
 * tableName = "表名"
 * <p>
 * id = "表的主键" (如果是多个主键，默认是第一个)
 * <p>
 * Created by 恒安 on 2015/10/21.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface TableSeg {

    /**
     * 表名
     *
     * @return
     */
    public String tableName();

    /**
     * 表的主键，如果是多个主键，默认为第一个
     *
     * @return
     */
    public String id();
}
