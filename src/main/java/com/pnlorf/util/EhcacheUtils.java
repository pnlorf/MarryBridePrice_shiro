package com.pnlorf.util;

import com.pnlorf.shiro.SecurityRealm;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * ehcache工具类
 * <p>
 * Created by 冰诺莫语 on 2015/10/21.
 */
public class EhcacheUtils {

    private static final Logger logger = LoggerFactory.getLogger(EhcacheUtils.class);

    private static CacheManager cacheManager = null;

    private static Cache cache = null;

    /**
     * 元素最大数量
     */
    public static int MAX_ELEMENTS_IN_MEMORY = 50000;

    /**
     * 是否把溢出数据持久化到硬盘
     */
    public static boolean OVERFLOW_TO_DISK = true;

    /**
     * 是否会死亡
     */
    public static boolean ETERNAL = false;

    /**
     * 缓存的间歇时间
     */
    public static int TIME_TO_IDLE_SECONDS = 600;

    /**
     * 存活时间(默认一天)
     */
    public static int TIME_TO_lIVE_SECONDS = 86400;

    /**
     * 需要持久化到硬盘否
     */
    public static boolean DISK_PERSISTENT = false;

    /**
     * 内存存取策略
     */
    public static String MEMORY_STORE_EVICTION_POLICY = "LFU";

    static {
        EhcacheUtils.initCacheManager();
        EhcacheUtils.initCache("cache");
    }

    /**
     * 初始化缓存管理容器
     *
     * @return
     */
    public static CacheManager initCacheManager() {
        try {
            if (cacheManager == null) {
                cacheManager = CacheManager.getInstance();
            }
        } catch (Exception e) {
            logger.error("Init CacheManager exception.", e);
        }
        return cacheManager;
    }

    /**
     * 初始化缓存管理容器
     *
     * @param path ehcache.xml存放的路径
     * @return
     */
    public static CacheManager initCacheManager(String path) {
        try {
            if (cacheManager == null) {
                cacheManager = CacheManager.getInstance().create(path);
            }
        } catch (Exception e) {
            logger.error("Init CacheManager exception.", e);
        }
        return cacheManager;
    }

    /**
     * 初始化Cache
     *
     * @param cacheName
     * @return
     */
    public static Cache initCache(String cacheName) {
        checkCacheManager();
        if (null == cacheManager.getCache(cacheName)) {
            cacheManager.addCache(cacheName);
        }
        cache = cacheManager.getCache(cacheName);
        return cache;
    }

    /**
     * 添加缓存
     * <p>
     * 注意：以下缓存是永久有效，是系统初始化数据到缓存中
     * 如果不需要永久有效，请调用其他方法
     *
     * @param key   关键字
     * @param value 值
     */
    public static void put(Object key, Object value) {
        checkCache();
        // 创建Element，然后放入Cache对象中
        Element element = new Element(key, value);
        cache.put(element);
    }

    /**
     * 获取cache
     *
     * @param key 关键字
     * @return
     */
    public static Object get(Object key) {
        checkCache();
        Element element = cache.get(key);
        if (null == element) {
            return null;
        }
        return element.getObjectValue();
    }

    /**
     * 初始化缓存
     *
     * @param cacheName           缓存名称
     * @param maxElementsInMemory 元素最大数量
     * @param overflowToDisk      是否持久化到硬盘
     * @param eternal             是否会死亡
     * @param timeToLiveSeconds   缓存存活时间
     * @param timeToIdleSeconds   缓存的间隔时间
     * @return
     * @throws Exception
     */
    public static Cache initCache(String cacheName, int maxElementsInMemory, boolean overflowToDisk, boolean eternal,
                                  long timeToLiveSeconds, long timeToIdleSeconds) throws Exception {
        try {
            CacheManager singletonManager = CacheManager.create();
            Cache myCache = singletonManager.getCache(cacheName);
            if (myCache != null) {
                CacheConfiguration config = cache.getCacheConfiguration();
                config.setTimeToLiveSeconds(timeToLiveSeconds);
                config.setMaxElementsInMemory(maxElementsInMemory);
                config.setOverflowToDisk(overflowToDisk);
                config.setEternal(eternal);
                config.setTimeToIdleSeconds(timeToIdleSeconds);
            }
            if (myCache == null) {
                Cache memoryOnlyCache = new Cache(cacheName, maxElementsInMemory, overflowToDisk, eternal, timeToLiveSeconds, timeToIdleSeconds);
                singletonManager.addCache(memoryOnlyCache);
                myCache = singletonManager.getCache(cacheName);
            }
            return myCache;
        } catch (Exception e) {
            logger.error("init cache " + cacheName + " failed!!!", e);
            throw new Exception("init cache " + cacheName + " failed!!!");
        }
    }

    /**
     * 初始化cache
     *
     * @param cacheName         cache的名字
     * @param timeToLiveSeconds 有效时间
     * @return cache 缓存
     * @throws Exception
     */
    public static Cache initCache(String cacheName, long timeToLiveSeconds) throws Exception {
        return EhcacheUtils.initCache(cacheName, MAX_ELEMENTS_IN_MEMORY, OVERFLOW_TO_DISK, ETERNAL, timeToLiveSeconds, TIME_TO_IDLE_SECONDS);
    }

    /**
     * 初始化cache
     *
     * @param cacheName cache名
     * @return cache
     * @throws Exception
     */
    public static Cache initMyCache(String cacheName) throws Exception {
        return EhcacheUtils.initCache(cacheName, TIME_TO_lIVE_SECONDS);
    }

    /**
     * 修改缓存容器配置
     *
     * @param cacheName           缓存名
     * @param timeToLiveSeconds   有效时间
     * @param maxElementsInMemory 最大数量
     * @return
     * @throws Exception
     */
    public static boolean modifyCache(String cacheName, long timeToLiveSeconds, int maxElementsInMemory)
            throws Exception {
        try {
            if (StringUtils.isNotBlank(cacheName) && timeToLiveSeconds != 0L && maxElementsInMemory != 0L) {
                CacheManager myCacheManager = CacheManager.create();
                Cache myCache = myCacheManager.getCache(cacheName);
                CacheConfiguration config = myCache.getCacheConfiguration();
                config.setTimeToLiveSeconds(timeToLiveSeconds);
                config.setMaxElementsInMemory(maxElementsInMemory);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            logger.error("modify cache " + cacheName + "failed!!!", e);
            throw new Exception("modify cache " + cacheName + "failed!!!");
        }
    }

    /**
     * 向指定缓存中设置值
     *
     * @param cacheName 缓存名
     * @param key       键
     * @param value     值
     * @return 返回真true
     * @throws Exception 异常
     */
    public static boolean setValue(String cacheName, String key, Object value) throws Exception {
        try {
            CacheManager myCacheManager = CacheManager.create();
            Cache myCache = myCacheManager.getCache(cacheName);
            if (myCache == null) {
                myCache = initCache(cacheName);
            }
            myCache.put(new Element(key, value));
            return true;
        } catch (Exception e) {
            logger.error("set cache " + cacheName + " failed!!!", e);
            throw new Exception("set cache " + cacheName + " failed!!!");
        }
    }

    /**
     * 向指定缓存中设置值
     *
     * @param cacheName         缓存名
     * @param key               键
     * @param value             值
     * @param timeToLiveSeconds 存活时间
     * @return 真true
     * @throws Exception 抛出异常
     */
    public static boolean setValue(String cacheName, String key, Object value, Integer timeToLiveSeconds) throws Exception {
        try {
            CacheManager mCacheManager = CacheManager.create();
            Cache mCache = mCacheManager.getCache(cacheName);
            if (mCache == null) {
                initCache(cacheName, timeToLiveSeconds);
                mCache = mCacheManager.getCache(cacheName);
            }
            mCache.put(new Element(key, value, ETERNAL, TIME_TO_IDLE_SECONDS, timeToLiveSeconds));
            return true;
        } catch (Exception e) {
            logger.error("set cache " + cacheName + " failed!!!", e);
            throw new Exception("set cache " + cacheName + " failed!!!");
        }
    }

    /**
     * 从ehcache的缓存中取值
     *
     * @param cacheName 缓存名
     * @param key       键
     * @return 返回Object类型的值
     * @throws Exception 异常
     */
    public static Object getValue(String cacheName, String key) throws Exception {
        try {
            CacheManager mCacheManager = CacheManager.create();
            Cache mCache = mCacheManager.getCache(cacheName);
            if (mCache == null) {
                mCache = initMyCache(cacheName);
            }
            return mCache.get(key).getValue();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 删除指定的ehcache缓存
     *
     * @param cacheName 缓存名
     * @return 真true
     * @throws Exception 失败抛出异常
     */
    public static boolean removeEhcache(String cacheName) throws Exception {
        try {
            CacheManager mCacheManager = CacheManager.create();
            mCacheManager.removeCache(cacheName);
            return true;
        } catch (Exception e) {
            logger.error("remove cache \" + cacheName + \" failed!!!", e);
            throw new Exception("remove cache " + cacheName + " failed!!!");
        }
    }

    /**
     * 删除所有的EhCache缓存
     *
     * @return true
     * @throws Exception 失败抛出异常
     */
    public static boolean removeAllEhcache() throws Exception {
        try {
            CacheManager mCacheManager = CacheManager.create();
            mCacheManager.removalAll();
            return true;
        } catch (Exception e) {
            logger.error("remove all cache failed!!!", e);
            throw new Exception("remove all cache failed!!!");
        }
    }

    /**
     * 删除EhCache中的缓存元素
     *
     * @param cacheName 缓存名
     * @param key       键
     * @return true
     * @throws Exception 失败抛出异常
     */
    public static boolean removeElement(String cacheName, String key) throws Exception {
        try {
            CacheManager mCacheManager = CacheManager.create();
            Cache mCache = mCacheManager.getCache(cacheName);
            mCache.remove(key);
            return true;
        } catch (Exception e) {
            logger.error("remove cache " + cacheName + " key: " + key + " failed!!!", e);
            throw new Exception("remove cache " + cacheName + " key: " + key + " failed!!!");
        }
    }

    /**
     * 删除指定缓存中的所有元素
     *
     * @param cacheName
     * @return
     * @throws Exception
     */
    public static boolean removeAllElement(String cacheName) throws Exception {
        try {
            CacheManager mCacheManager = CacheManager.create();
            Cache mCache = mCacheManager.getCache(cacheName);
            mCache.removeAll();
            return true;
        } catch (Exception e) {
            logger.error("remove cache " + cacheName + " # all elements failed!!!", e);
            throw new Exception("remove cache " + cacheName + " # all elements failed!!!");
        }
    }

    /**
     * 释放CacheManager
     */
    public static void shutdown() {
        cacheManager.shutdown();
    }

    /**
     * 移除cache
     *
     * @param cacheName
     */
    public static void removeCache(String cacheName) {
        checkCacheManager();
        cache = cacheManager.getCache(cacheName);
        if (null != cache) {
            cacheManager.removeCache(cacheName);
        }
    }

    /**
     * 移除cache中的key对应的元素
     *
     * @param key
     */
    public static void remove(String key) {
        checkCache();
        cache.remove(key);
    }

    /**
     * 移除所有cache
     */
    public static void removeAllCache() {
        checkCacheManager();
        cacheManager.removalAll();
        ;
    }

    /**
     * 移除所有Element
     */
    public static void removeAllKey() {
        checkCache();
        cache.removeAll();
    }

    /**
     * 获取所有的cache名称
     *
     * @return
     */
    public static String[] getAllCaches() {
        checkCacheManager();
        return cacheManager.getCacheNames();
    }

    /**
     * 获取Cache所有的keys
     *
     * @return
     */
    public static List getKeys() {
        checkCache();
        return cache.getKeys();
    }

    /**
     * 检测cacheManager
     */
    private static void checkCacheManager() {
        if (null == cacheManager) {
            throw new IllegalArgumentException("调用前请先初始化CacheManager值：EHCacheUtil.initCacheManager");
        }
    }

    /**
     * 监测cache
     */
    private static void checkCache() {
        if (null == cache) {
            throw new IllegalArgumentException("调用前请先初始化Cache值：EHCacheUtil.initCache(参数)");
        }
    }
}
























