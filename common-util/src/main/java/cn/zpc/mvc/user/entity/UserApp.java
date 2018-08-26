package cn.zpc.mvc.user.entity;

import cn.zpc.common.entity.DataEntity;

/**
 * Description: 用户APP版本信息
 * Author: sukai
 * Date: 2017-11-3
 */
public class UserApp extends DataEntity{

    private Integer appOs;      // 平台名称
    private Integer versionCode; // 版本号
    private String versionName; // 版本名称
    private String content;     // 版本更新内容
    private String updateUrl;   // 更新资源定位
    private boolean forced;     // 是否强制更新

    public Integer getAppOs() {
        return appOs;
    }

    public void setAppOs(Integer appOs) {
        this.appOs = appOs;
    }

    public void setVersionCode(Integer versionCode) {
        this.versionCode = versionCode;
    }

    public Integer getVersionCode() {
        return versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }

    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }

    public boolean isForced() {
        return forced;
    }

    public void setForced(boolean forced) {
        this.forced = forced;
    }
}
