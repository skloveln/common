package com.github.sky.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description:日志工具
 * Author: sukai
 * Date: 2017-08-11
 */
public class LogUtils {

    private final static Logger logger = LoggerFactory.getLogger(LogUtils.class);

    public static void error(String title, Object... objects){
        if(logger.isErrorEnabled()){
            logger.debug(title, objects);
        }
    }

    public static void info(String title, Object... objects){
        if(logger.isInfoEnabled()){
            logger.info(title, objects);
        }
    }

    public static void debug(String title, Object... objects){
        if(logger.isDebugEnabled()){
            logger.debug(title, objects);
        }
    }
}
