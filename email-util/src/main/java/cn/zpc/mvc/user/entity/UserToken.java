package cn.zpc.mvc.user.entity;

import cn.zpc.common.entity.DataEntity;

import java.util.Date;

/**
 * Description:身份登录信息
 * Author: sukai
 * Date: 2017-08-15
 */
public class UserToken extends DataEntity{

    private Integer id;
    private User user;          // 用户
    private String token;       // refreshToken 令牌
    private Date expiredDate;   // 过期时间
    private Date createTime;    // 创建时间
    private Date accessTime;    // 访问时间
    private Integer count;      // 访问次数

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Date expiredDate) {
        this.expiredDate = expiredDate;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(Date accessTime) {
        this.accessTime = accessTime;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
