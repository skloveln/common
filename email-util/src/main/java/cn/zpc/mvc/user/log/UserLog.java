package cn.zpc.mvc.user.log;

import java.util.Date;

/**
 * Description:操作日志
 * Author: sukai
 * Date: 2017-08-24
 *
 * todo 待补充
 */
public class UserLog {

    /**
     * 关键字索引
     */
    public enum LogType{
        LoginCode, // 发送登录验证码
        RegisterCode, // 发送注册验证码
        ResetPasswordCode, // 发送更新密码验证码
        BindCode,  // 发送绑定手机号验证码
        ChangePhoneCode,  // 发送更换手机号验证码
        SysStart, // 系统启动
        SensitiveOption, // 敏感操作
        Exception // 异常
    }

    private int id; // 编号
    private LogType key; // 索引关键字，
    private String text; // 日志内容
    private Date createDate; // 创建时间

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LogType getKey() {
        return key;
    }

    public void setKey(LogType key) {
        this.key = key;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
