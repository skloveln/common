package cn.zpc.mvc.user.entity;

/**
 * Description: 微信的openId, refreshToken, accessToken信息
 * User: sukai
 * Date: 2018-04-11   16:22
 */
public class WechatToken {

    private String openId;
    private String accessToken;
    private String refreshToken;

    public WechatToken(String openId, String accessToken, String refreshToken) {
        this.openId = openId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
