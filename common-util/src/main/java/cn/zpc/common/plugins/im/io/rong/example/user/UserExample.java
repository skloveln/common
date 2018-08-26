package cn.zpc.common.plugins.im.io.rong.example.user;

import cn.zpc.common.plugins.im.io.rong.RongCloud;
import cn.zpc.common.plugins.im.io.rong.methods.user.User;
import cn.zpc.common.plugins.im.io.rong.models.*;
import cn.zpc.common.plugins.im.io.rong.models.response.*;
import cn.zpc.common.plugins.im.io.rong.models.user.UserModel;

/**
 * Demo class
 *
 * @author hc
 * @date 2017/12/30
 */
public class UserExample {
    /**
     * 此处替换成您的appKey
     * */
    private static final String appKey = "8luwapkv8jj8l";
    /**
     * 此处替换成您的appSecret
     * */
    private static final String appSecret = "1mX85y9UvB";
    /**
     * 自定义api地址
     * */
    private static final String api = "http://api.cn.ronghub.com";

    public static void main(String[] args) throws Exception {

        RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
        //自定义 api 地址方式
        // RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret,api);
        User User = rongCloud.user;

        /**
         * API 文档: http://rongcloud.github.io/server-sdk-nodejs/docs/v1/user/user.html#register
         *
         * 注册用户，生成用户在融云的唯一身份标识 Token
         */
        UserModel user = new UserModel()
                .setId("userxxd2")
                .setName("username")
                .setPortrait("http://www.rongcloud.cn/images/logo.png");
        TokenResult result = User.register(user);
        System.out.println("getToken:  " + result.toString());

        /**
         *
         * API 文档: http://rongcloud.github.io/server-sdk-nodejs/docs/v1/user/user.html#refresh
         *
         * 刷新用户信息方法
         */
        Result refreshResult = User.update(user);
        System.out.println("refresh:  " + refreshResult.toString());

    }
}
