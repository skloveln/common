package cn.zpc.common.serivce;

import cn.zpc.common.plugins.email.EmailService;
import cn.zpc.common.plugins.oss.OssService;
import cn.zpc.common.plugins.sms.SmsService;
import cn.zpc.common.plugins.wechat.WechatService;
import cn.zpc.common.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Description:基础服务
 * Author: sukai
 * Date: 2017-08-14
 */
public abstract class BaseService {

    @Autowired
    public OssService ossService;

    @Autowired
    public SmsService smsService;

    @Autowired
    public EmailService emailService;

    @Autowired
    public RedisService redisService;

    @Autowired
    public WechatService wechatService;

}
