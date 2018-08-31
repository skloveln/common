package com.github.sky;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description:正则
 * Author: sukai
 * Date: 2017-08-16
 */
public class PatternUtils {

    public final static String PATTERN_PHONE = "1[34578]\\d{9}";


    public static boolean match(String text, String reg){
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }
}
