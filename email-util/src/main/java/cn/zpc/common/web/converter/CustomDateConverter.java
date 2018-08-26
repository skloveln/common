package cn.zpc.common.web.converter;

import cn.zpc.common.utils.DateUtils;
import org.springframework.core.convert.converter.Converter;

import java.util.Date;

/**
 * Description:时间转换器
 * Author: Simon
 * Date: 2017-08-09
 */
public class CustomDateConverter implements Converter<Date, String> {


    public String convert(Date date) {
        return DateUtils.formatDateTime(date);
    }
}
