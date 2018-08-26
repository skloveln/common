package cn.zpc.mvc.user.entity;

import cn.zpc.common.entity.DataEntity;

import java.util.Date;

/**
 * Description:用户验证码
 * Author: sukai
 * Date: 2017-08-15
 */
public class UserCode extends DataEntity{

    private Integer id;
    private String code; // 验证码
    private String phone; // 目标手机
    private int type; // 验证码类型
    private Date createDate; // 发送时间
    private boolean isVerify; // 是否校验过
    private Date verifyDate; // 校验时间
    private boolean success; // 是否验证成功


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public boolean isVerify() {
        return isVerify;
    }

    public void setVerify(boolean verify) {
        isVerify = verify;
    }

    public Date getVerifyDate() {
        return verifyDate;
    }

    public void setVerifyDate(Date verifyDate) {
        this.verifyDate = verifyDate;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
