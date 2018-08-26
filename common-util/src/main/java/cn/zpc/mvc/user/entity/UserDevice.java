package cn.zpc.mvc.user.entity;

import cn.zpc.common.entity.DataEntity;

import java.util.Date;

/**
 * Description: 用户设备信息
 * Author: sukai
 * Date: 2017-09-06
 */
public class UserDevice extends DataEntity{

    private Integer id; //  编号
    private String deviceImei; // 设备识别字串
    private String devicePushId; // 友盟设备绑定编码
    private Integer userId; // 绑定用户编码
    private Date updateTime; // 更新时间
    private Date createTime; // 创建时间
    private Integer appOS;  // 系统版本


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDeviceImei() {
        return deviceImei;
    }

    public void setDeviceImei(String deviceImei) {
        this.deviceImei = deviceImei;
    }

    public String getDevicePushId() {
        return devicePushId;
    }

    public void setDevicePushId(String devicePushId) {
        this.devicePushId = devicePushId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getAppOS() {
        return appOS;
    }

    public void setAppOS(Integer appOS) {
        this.appOS = appOS;
    }
}
