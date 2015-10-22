package com.pnlorf.util;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Time;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

/**
 * SpringMVC把请求的所有参数封装到Map中，提供最常用的方法
 * <p>
 * Created by 冰诺莫语 on 2015/10/22.
 */
public class FormMap<K, V> extends HashMap<K, V> implements Serializable {

    private static final long serialVersionUID = 1L;

    public void set(K key, V value) {
        this.put(key, value);
    }

    /**
     * Get attribute of mysql type:varchar, char, enum, set, text, tinytext, mediumtext, longtext
     *
     * @param attr
     * @return
     */
    public String getStr(String attr) {
        return (String) this.get(attr);
    }

    /**
     * Get attribute of mysql type: int, integer, tinyint(n) n > 1, smallint, mediumint
     *
     * @param attr
     * @return
     */
    public Integer getInt(String attr) {
        return (Integer) this.get(attr);
    }

    /**
     * Get attribute of mysql type: bigint, unsigned int
     *
     * @param attr
     * @return
     */
    public Long getLong(String attr) {
        return (Long) this.get(attr);
    }

    /**
     * Get attribute of mysql type: unsigned bigint
     *
     * @return
     */
    public BigInteger getBigInteger(String attr) {
        return (BigInteger) this.get(attr);
    }

    /**
     * Get attribute of mysql type: date, year
     *
     * @param attr
     * @return
     */
    public Date getDate(String attr) {
        return (Date) this.get(attr);
    }

    /**
     * Get attribute of mysql type: date, year
     *
     * @param attr
     * @return
     */
    public Time getTime(String attr) {
        return (Time) this.get(attr);
    }

    /**
     * Get attribute of mysql type: timestamp, datetime
     *
     * @param attr
     * @return
     */
    public java.sql.Timestamp getTimestamp(String attr) {
        return (java.sql.Timestamp) this.get(attr);
    }

    /**
     * Get attribute of mysql type: real, double
     *
     * @param attr
     * @return
     */
    public Double getDouble(String attr) {
        return (Double) this.get(attr);
    }

    /**
     * Get attribute of mysql type: float
     *
     * @param attr
     * @return
     */
    public Float getFloat(String attr) {
        return (Float) this.get(attr);
    }

    /**
     * Get attribute of mysql type: bit, tinyint(1)
     *
     * @param attr
     * @return
     */
    public Boolean getBoolean(String attr) {
        return (Boolean) this.get(attr);
    }

    /**
     * Get attribute of mysql type: decimal, numeric
     *
     * @param attr
     * @return
     */
    public java.math.BigDecimal getBigDecimal(String attr) {
        return (java.math.BigDecimal) this.get(attr);
    }

    /**
     * Get attribute of mysql type: binary, varbinary, tinyblob, blob,
     * mediumblob, longblob
     *
     * @param attr
     * @return
     */
    public byte[] getBytes(String attr) {
        return (byte[]) this.get(attr);
    }

    /**
     * Get attribute of any type that extends from Number
     *
     * @param attr
     * @return
     */
    public Number getNumber(String attr) {
        return (Number) this.get(attr);
    }

    /**
     * Return attribute names of this model.
     *
     * @return
     */
    public String[] getAttrNames() {
        Set<K> attrNameSet = this.keySet();
        return attrNameSet.toArray(new String[attrNameSet.size()]);
    }

    /**
     * Return attribute values of this model.
     *
     * @return
     */
    public Object[] getAttrValues() {
        Collection<V> attrValueCollection = values();
        return attrValueCollection.toArray(new Object[attrValueCollection
                .size()]);
    }
}
