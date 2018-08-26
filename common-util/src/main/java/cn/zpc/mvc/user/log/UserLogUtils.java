package cn.zpc.mvc.user.log;

import cn.zpc.common.utils.SpringContextHolder;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Description: 系统日志工具类
 * Author: sukai
 * Date: 2017-08-24
 */
@Service
@Transactional(readOnly = false)
public class UserLogUtils {

    private static UserLogDao logDao = SpringContextHolder.getBean(UserLogDao.class);

    private static MessageSource messageSource = SpringContextHolder.getBean(MessageSource.class);

    public static void saveLog(UserLog.LogType key, String text, Object... params){

        String message = messageSource.getMessage(text, params, LocaleContextHolder.getLocale());
        UserLog log = new UserLog();
        log.setKey(key);
        log.setText(message);
        log.setCreateDate(new Date());
        logDao.insert(log);

    }
}
