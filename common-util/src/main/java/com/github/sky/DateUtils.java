package com.github.sky;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.apache.commons.lang3.time.DateFormatUtils.format;

/**
 * Description: 日期工具类
 * Author: sukai
 * Date: 2017-07-28
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {


    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    /**
     * 得到当前日期字符串 格式（yyyy-MM-dd）
     */
    public static String getDate() {
        return getDate("yyyy-MM-dd");
    }

    /**
     * 得到当前日期字符串 格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
     */
    public static String    getDate(String pattern) {
        return format(new Date(), pattern);
    }

    /**
     * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
     */
    public static String formatDate(Date date, Object... pattern) {
        String formatDate = null;
        if (pattern != null && pattern.length > 0) {
            formatDate = format(date, pattern[0].toString());
        } else {
            formatDate = format(date, "yyyy-MM-dd");
        }
        return formatDate;
    }

    /**
     * 得到日期时间字符串，转换格式（yyyy-MM-dd HH:mm:ss）
     */
    public static String formatDateTime(Date date) {
        return formatDate(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 得到日期字符串，转换格式 yyyy-MM-dd
     * @param date
     * @return
     */
    public static String formatDate(Date date) {
        return formatDate(date, "yyyy-MM-dd");
    }


    /**
     * 转换为时间（天,时:分:秒.毫秒）
     * @param timeMillis 时长统计
     */
    public static String formatDateTime(long timeMillis){
        long day = timeMillis/(24*60*60*1000);
        long hour = (timeMillis/(60*60*1000)-day*24);
        long min = ((timeMillis/(60*1000))-day*24*60-hour*60);
        long s = (timeMillis/1000-day*24*60*60-hour*60*60-min*60);
        long sss = (timeMillis-day*24*60*60*1000-hour*60*60*1000-min*60*1000-s*1000);
        return (day>0?day+",":"")+hour+":"+min+":"+s+"."+sss;
    }

    /**
     * 得到某个日期的起始时间，eg 2018-01-29 22:23:21 --->  2018-01-29 00:00:00
     * @return
     */
    public static Date getStartTime(Date date){
        String str = new SimpleDateFormat("yyyy-MM-dd").format(date);
        try {
            date =  new SimpleDateFormat("yyyy-MM-dd").parse(str);
        }catch (Exception e){
            throw new RuntimeException();
        }
        return date;
    }

}
