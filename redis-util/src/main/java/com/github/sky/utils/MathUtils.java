package com.github.sky.utils;

public class MathUtils {

    /**
     * 保留n位小数（不四舍五入）
     */
    public static Double saveDouble(Double source, Integer n){
        String str = "" + source;
        if(str.length() - str.indexOf(".") - 1 >= n) {
            str = str.substring(0, str.indexOf(".")+n+1);
        }
        return Double.parseDouble(str);
    }


}
