package cn.zpc.common.config;

import cn.zpc.common.utils.PropertiesLoader;
import cn.zpc.common.utils.StringUtils;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Description:全局配置
 * Author: sukai
 * Date: 2017-08-14
 */
public class Global {

    /**
     * 保存全局属性值
     */
    private final static Map<String, Object> map = Maps.newHashMap();


    public final static String CURRENT_AUTHENTICATION = "current_authentication"; // 根据令牌获取当前用户
    public final static String AUTHORIZATION = "authorization"; // header token资源


    private final static PropertiesLoader loader = new PropertiesLoader("application.properties");


    /**
     * 获取配置
     */
    public static Object getConfig(String key) {
        Object value = map.get(key);
        if (value == null){
            value = loader.getProperty(key);
            map.put(key, value != null ? value : StringUtils.EMPTY);
        }
        return value;
    }



}
