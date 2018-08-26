package cn.zpc.common.plugins.log;

import cn.zpc.common.redis.RedisService;
import cn.zpc.common.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Description:日志缓存插件
 * Author: Simon
 * Date: 2017-09-08
 */
@Service
@Scope("singleton")
public class LogCacheService {

    private final static Logger logger = LoggerFactory.getLogger(LogCacheService.class);

    private final static String separator = "|";

    private final static String CACHE_ACTION_KEY_PREFIX = "cache_url_key_";

    private final RedisService redisService;

    private final ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    public LogCacheService(RedisService redisService, ThreadPoolTaskExecutor taskExecutor) {
        this.redisService = redisService;
        this.taskExecutor = taskExecutor;
    }


    public void cacheAction(String key, String method){

        taskExecutor.execute(() -> {
            String cacheKey = (CACHE_ACTION_KEY_PREFIX + key) + separator +
                    method + separator + DateUtils.formatDate(new Date());

            if(logger.isDebugEnabled()){
                logger.debug("cache url key:" + cacheKey);
            }
            redisService.incr(cacheKey);
        });

    }

}
