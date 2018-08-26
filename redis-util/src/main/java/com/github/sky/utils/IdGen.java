package com.github.sky.utils;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * Description:ID生成器
 * Author: sukai
 * Date: 2017-07-27
 */
@Service
@Lazy(false)
public class IdGen {

    private static SecureRandom random = new SecureRandom();

    public static String uuid(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 校验码生成器
     */
    public static String generate(){

        int len = 6;
        int max = (int) (Math.pow(10, len) - 1);
        int min = 0;
        String code = String.valueOf(random.nextInt(max) % (max - min + 1) + min);
        if(code.length() < len){
            int zeroC = len - code.length();
            for(int n = 0; n < zeroC; n ++){
                code = "0" + code;
            }
        }

        return String.valueOf(code);
    }

}
