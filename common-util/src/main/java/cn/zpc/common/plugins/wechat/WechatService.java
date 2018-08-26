package cn.zpc.common.plugins.wechat;

import cn.zpc.common.plugins.oss.OssPathConfig;
import cn.zpc.common.serivce.BaseService;
import cn.zpc.common.utils.FileUtils;
import cn.zpc.common.utils.HttpClientUtils;
import cn.zpc.mvc.user.entity.UserWechat;
import cn.zpc.mvc.user.entity.WechatToken;
import cn.zpc.mvc.user.utils.UserUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Description:
 * User: sukai
 * Date: 2018-03-22   13:08
 */
@Service
public class WechatService extends BaseService{

    private final static String ServiceAppID = "wx97feea69bdd70e82";
    private final static String ServiceAppSecret = "2a5c7c363486e0bc9d0e656963e9ad1b";

    private final static String AppID = "wxc9535664ea1e5f31";
    private final static String AppSecret = "6cc1f476de81d752e45a2051fb69e3ab";

    /**
     * 获取AccessToken等信息
     * @param code
     * @return
     */
    public WechatToken getAccessToken(String code){
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + AppID + "&secret=" + AppSecret + "&code=" + code + "&grant_type=authorization_code";
        JSONObject json = HttpClientUtils.httpPostRequestJson(url);
        String accessToken = (String) json.get("access_token");  // 5分钟失效
        String openId = (String) json.get("openid");
        String refreshToken = (String) json.get("refresh_token");
        return new WechatToken(openId, accessToken, refreshToken);
    }

    /**
     * 获取微信用户的信息
     * @param accessToken
     * @param openId
     * @return
     */
    public UserWechat getWechatInfo(String accessToken, String refreshToken, String openId, String fileParentPath){
        String url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openId +"&lang=en";
        JSONObject json =  HttpClientUtils.httpPostRequestJson(url);
        openId = (String) json.get("openid");
        String nickname = (String) json.get("nickname");
        Integer sex = (Integer) json.get("sex");
        String province = (String) json.get("province");
        String city = (String) json.get("city");
        String country = (String) json.get("country");
        String headImgUrl = (String) json.get("headimgurl");
        JSONArray array = (JSONArray) json.get("privilege");
        String privilege = "";
        for (Object object: array) {
            privilege += object + ",";
        }
        if (privilege.length() > 0) {
            privilege = privilege.substring(0, privilege.length() - 1);
        }
        String unionId = (String) json.get("unionid");

        // 保存用户微信信息
        UserWechat userWechat = new UserWechat();
        if(fileParentPath != null) {
            //保存图片
            String fileName = UserUtils.generateToken("image_" + System.currentTimeMillis()) + ".jpg";
            String filePath = fileParentPath + "/" + fileName;
            FileUtils.download(headImgUrl, filePath);
            File imageFile = new File(filePath);
            // 上传图片
            ossService.putFile(OssPathConfig.getUserAvatarPath(fileName), imageFile);
            userWechat.setHeadImg(fileName);
        }
        userWechat.setCity(city);
        userWechat.setCountry(country);
        userWechat.setNickName(nickname);
        userWechat.setPrivilege(privilege);
        userWechat.setProvince(province);
        userWechat.setUnionId(unionId);
        userWechat.setRefreshToken(refreshToken);
        userWechat.setSex(sex);
        userWechat.setOpenId(openId);

        return userWechat;
    }

}
