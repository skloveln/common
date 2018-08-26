package cn.zpc.common.utils;

import java.beans.PropertyDescriptor;
import java.io.StringReader;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Attribute;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestExecutionListeners;

import java.util.*;

/**
 * Author: sukai
 * Date: 2017/8/4.
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils{


    /**
     * 以逗号切割字符串
     * @param keywords
     * @return
     */
    public static List<String> splitKeyword(String keywords){
        ArrayList<String> list = new ArrayList<String>();
        if(StringUtils.isNotBlank(keywords)){
            String[] split = null;
            keywords = keywords.trim();
            if(keywords.contains(" ")){
                split = keywords.split(" ");
            }
            if(keywords.contains(",")){
                split = keywords.trim().split(",");
            }
            if(split == null || split.length == 0){
                list.add(keywords);
                return list;
            }
            for(String str : split){
                if(!str.isEmpty()){
                    list.add(str);
                }
            }
            return list;
        }
        return list;
    }

    /**
     * 以空格切割字符串
     * @param keywords
     * @return
     */
    public static List<String> splitString(String keywords){
        ArrayList<String> list = new ArrayList<String>();
        if(StringUtils.isNotBlank(keywords)){
            keywords = keywords.trim();
            String[] split = null;
            if(keywords.contains(" ")){
                split = keywords.split(" ");
            }
            if(split == null || split.length == 0){
                list.add(keywords);
                return list;
            }
            for(String str : split){
                if(!str.isEmpty()){
                    list.add(str);
                }
            }
            return list;
        }
        return list;
    }

    /**
     * 以空格切割字符串
     * @param keywords
     * @return
     */
    public static List<Integer> splitInteger(String keywords){
        ArrayList<Integer> list = new ArrayList<Integer>();
        if(StringUtils.isNotBlank(keywords)){
            keywords = keywords.trim();
            String[] split = null;
            if(keywords.contains(" ")){
                split = keywords.split(" ");
            }
            if(split == null || split.length == 0){
                list.add(Integer.parseInt(keywords));
                return list;
            }
            for(String str : split){
                if(!str.isEmpty()){
                    list.add(Integer.parseInt(str));
                }
            }
            return list;
        }
        return list;
    }

    //将javabean实体类转为map类型，然后返回一个map类型的值
    public static Map<String, Object> beanToMap(Object obj) {
        Map<String, Object> params = new HashMap<String, Object>(0);
        try {
            PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
            PropertyDescriptor[] descriptors = propertyUtilsBean.getPropertyDescriptors(obj);
            for (int i = 0; i < descriptors.length; i++) {
                String name = descriptors[i].getName();
                if (!"class".equals(name)) {
                    params.put(name, propertyUtilsBean.getNestedProperty(obj, name));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }


    public static String[] split(String text){
        if(isNotEmpty(text)){
            if(text.contains(";")){
                return text.split(";");
            }
            if(text.contains(" ")){
                return text.split(" ");
            }
            if(text.contains(",")){
                return text.split(",");
            }
        }
        return null;
    }
}
