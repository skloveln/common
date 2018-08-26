package cn.zpc.mvc.user.entity;

import cn.zpc.common.entity.DataEntity;
import cn.zpc.common.utils.PatternUtils;
import cn.zpc.common.web.result.DataResult;
import cn.zpc.common.web.validators.sequence.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Description:用户基本信息
 * Author: sukai
 * Date: 2017-08-14
 */
public class User extends DataEntity{

    public static String DEFAULT_AVATAR_MAN = "moren_nan.png";
    public static String DEFAULT_AVATAR_WOMAN = "moren_nv.png";

    private Integer id;

    private String nickname;    // 昵称

    @NotEmpty(message = "{user.phone.notEmpty}", groups = {First.class})
    @Pattern(regexp = PatternUtils.PATTERN_PHONE, message = "请输入正确的手机号", groups = Second.class)
    private String phone;       // 手机号码
    private String email;       // 个人邮箱

    @Size(min = 6, max = 18, message = "{user.password.size}")
    @NotEmpty(message = "{user.password.notEmpty}")
    private String password;      // 密码
    private String salt;          // 加密码
    private String profession;  // 职业
    private Integer gender;       // 性别
    private Date age;             // 出生年月
    private String avatar;        // 头像
    private Date createDate;      // 注册时间
    private boolean deleted;      // 逻辑删除
    private Integer status;

    private String refreshToken;    // 长效令牌
    private String accessToken;   // 短效令牌
    private Integer integral;  // 积分
    private Integer storeType; // 商铺类型
    private Date expireTime; // 商铺到期时间

    public Integer getIntegral() {
        return integral;
    }

    public void setIntegral(Integer integral) {
        this.integral = integral;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public Date getAge() {
        return age;
    }

    public void setAge(Date age) {
        this.age = age;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @JsonIgnore
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @JsonIgnore
    public boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Integer getStoreType() {
        return storeType;
    }

    public void setStoreType(Integer storeType) {
        this.storeType = storeType;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
