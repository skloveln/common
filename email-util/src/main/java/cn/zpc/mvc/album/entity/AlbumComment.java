package cn.zpc.mvc.album.entity;

import cn.zpc.mvc.user.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.*;

public class AlbumComment {

    public final static Integer Error =  1;
    public final static Integer Comment =  2;

    private Integer id;
    private Integer albumId;
    private Integer userId;
    private Integer replyUserId;
    private String content;
    private Date createTime;

    private Boolean toReply;
    private User user;  // 回复人
    private User replyUser;  // 被回复人


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Integer albumId) {
        this.albumId = albumId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getReplyUserId() {
        return replyUserId;
    }

    public void setReplyUserId(Integer replyUserId) {
        this.replyUserId = replyUserId;
    }

    public Boolean getToReply() {
        return toReply;
    }

    public void setToReply(Boolean toReply) {
        this.toReply = toReply;
    }

    public User getReplyUser() {
        return replyUser;
    }

    public void setReplyUser(User replyUser) {
        this.replyUser = replyUser;
    }
}
