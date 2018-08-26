package cn.zpc.mvc.user.service;

import cn.zpc.common.serivce.BaseService;
import cn.zpc.common.utils.StringUtils;
import cn.zpc.common.web.result.DataResult;
import cn.zpc.common.web.result.Result;
import cn.zpc.mvc.user.dao.UserWeChatDao;
import cn.zpc.mvc.user.entity.User;
import cn.zpc.mvc.user.entity.UserWechat;
import cn.zpc.mvc.user.entity.WechatToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: 微信用户业务处理
 * User: sukai
 * Date: 2018-03-20   14:26
 */
@Service
public class UserWechatService extends BaseService{

    @Autowired
    private UserWeChatDao userWeChatDao;
    @Resource
    private UserService userService;


    /**
     * 检查该微信用户是否存在
     * @param openId
     * @return
     */
    public boolean exist(String openId){
        return userWeChatDao.findByOpenId(openId) != null;
    }

    /**
     * 保存微信用户信息
     * @param userWechat
     * @return
     */
    public int saveWechatUser(UserWechat userWechat){
        return userWeChatDao.insert(userWechat);
    }

    /**
     * 更新微信用户信息
     * @param userWechat
     * @return
     */
    public int updateWechatUser(UserWechat userWechat){
        return userWeChatDao.update(userWechat);
    }

    /**
     * 通过openId获取微信用户信息
     * @param openId
     * @return
     */
    public UserWechat findInfo(String openId){
        return userWeChatDao.findByOpenId(openId);
    }

    /**
     * 通过UserId获取微信信息
     * @param userId
     * @return
     */
    public UserWechat findByUserId(Integer userId){
        return userWeChatDao.findByUserId(userId);
    }

    /**
     * 解除微信绑定
     * @param userId
     * @return
     */
    public Integer deleteWechat(Integer userId){
        return userWeChatDao.deleteWechat(userId);
    }


    /**
     * 使用微信登录
     * @param code
     * @param fileParentPath
     * @return
     */
    public Result login(String code, String fileParentPath) {
        // 一、通过code换取授权access_token
        WechatToken wechatToken = wechatService.getAccessToken(code);
        // 判断微信用户是否存在
        if(exist(wechatToken.getOpenId())){ // 用户使用微信登录过
            // 判断是否绑定手机号
            UserWechat wechatUser = findInfo(wechatToken.getOpenId());
            if(StringUtils.isNotEmpty(wechatUser.getPhone())){ // 用户已经绑定手机号
                User user = userService.getUser(wechatUser.getUserId());
                userService.putToken(user);
                return new DataResult<>(user);
            }
        }else { // 用户没有使用微信登陆过
            // 二、拉取用户信息(需scope为 snsapi_userinfo)
            saveWechatUser(wechatService.getWechatInfo(wechatToken.getAccessToken(), wechatToken.getRefreshToken(), wechatToken.getOpenId(), fileParentPath));
        }
        Map<String, Object> map = new HashMap<>();
        map.put("openId", wechatToken.getOpenId());
        return new DataResult<>(Result.NOT_BIND_PHONE, "未绑定手机号", map);
    }


    /**
     * 使用微信绑定
     * @param code
     * @param userId
     * @return
     */
    public Result bind(String code, Integer userId) {
        Map<String, Object> map = new HashMap<>();
        UserWechat userWechat;
        String phone = userService.getUser(userId).getPhone();
        // 一、通过code换取授权access_token
        WechatToken wechatToken = wechatService.getAccessToken(code);
        // 判断微信用户是否存在
        if(exist(wechatToken.getOpenId())){ // 用户使用微信登录过
            userWechat = findInfo(wechatToken.getOpenId());
            userWechat.setUserId(userId);
            userWechat.setPhone(phone);
            updateWechatUser(userWechat);
        }else { // 用户没有使用微信登陆过
            // 二、拉取用户信息(需scope为 snsapi_userinfo)
            userWechat = wechatService.getWechatInfo(wechatToken.getAccessToken(), wechatToken.getRefreshToken(), wechatToken.getOpenId(), null);
            userWechat.setPhone(phone);
            saveWechatUser(userWechat);
        }
        map.put("bind", true);
        map.put("nickName", userWechat.getNickName());
        return new DataResult<>(map);
    }

}
