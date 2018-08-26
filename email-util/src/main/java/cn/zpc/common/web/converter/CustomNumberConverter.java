package cn.zpc.common.web.converter;

import cn.zpc.common.utils.StringUtils;
import org.springframework.core.convert.converter.Converter;

/**
 * Description: 自定义字符串转换
 * Author: Simon
 * Date: 2017-08-09
 */
public class CustomNumberConverter implements Converter<String, Integer> {


    public Integer convert(String s) {
        if(StringUtils.isEmpty(s)){
            s = "0";
        }
        try {
            return Integer.parseInt(s);
        }catch (Exception e){
            return 0;
        }
    }
}
