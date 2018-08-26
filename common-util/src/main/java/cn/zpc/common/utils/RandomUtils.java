package cn.zpc.common.utils;

import org.apache.commons.lang3.RandomStringUtils;
import java.util.Random;

/**
 * Author: sukai
 * Date: 2017/8/17.
 */
public class RandomUtils {

    /**
     * 获取指定位数随机码
     * @param num
     * @return
     */
    public static String getRandomCode(int num){
        return RandomStringUtils.randomAlphanumeric(num);
    }


    public static int[] randomIntArray(int min, int max, int len) throws Exception {

        int[] arr = new int[len];
        if(max - min < len){
            throw new Exception("max - min must big than len");
        }
        int cur = 0;
        while (true){
            int i = randomInt(min, max);
            if(!arrContains(arr, i)){
                arr[cur] = i;
                cur ++;
            }

            if(cur >= len){
                break;
            }

        }
        return arr;
    }

    public static int randomInt(int min, int max){
        int g = max - min;
        Random random = new Random();
        int v = random.nextInt(g);

        return v + min;
    }


    private static boolean arrContains(int[] arr, int key){
        for(int i : arr){
            if(i == key){
                return true;
            }
        }
        return false;
    }
}