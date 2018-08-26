package cn.zpc.common.web.result;

/**
 * Description:消息基类
 * Author: sukai
 * Date: 2017-08-15
 */
public interface Result {
    // 正常返回
    int NORMAL = 1000;
    // 身份验证失败（token错误或失效）
    int AUTH_FAILURE = 1001;
    // 异常信息
    int EXCEPTION = 1002;
    // 用户已经存在
    int USER_EXISTS = 1003;
    // 验证码错误
    int CODE_ERROR = 1004;
    // 用户未注册
    int NOT_REGISTER = 1005;
    // 登录密码错误
    int PWD_ERROR = 1006;
    // 未绑定手机号
    int NOT_BIND_PHONE = 2001;
    // 该手机号已绑定微信
    int ALREADY_BIND_PHONE = 2002;
}
