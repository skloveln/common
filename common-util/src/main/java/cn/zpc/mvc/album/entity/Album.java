package cn.zpc.mvc.album.entity;

import cn.zpc.common.entity.UrlEntity;
import cn.zpc.common.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;

public class Album {

    private Integer id;
    private Integer userId;
    private String describe;
    private String province;
    private String city;
    private String address;
    private Double lon;
    private Double lat;
    private String phone;
    private String tags;
    private Boolean deleted;
    private Date createTime;
    private Integer likeNum;      // 图集点赞数
    private Integer repostNum;    // 转发数


    private Integer commentNum;  // 总评论数
    private Integer errorNum;    // 纠错数
    private Integer hot;         // 热度
    private UrlEntity mainImage;    // 封面图
    private Boolean liked;          // 某用户是否点赞
    private Integer groupNum;       // 用户一天中发的图集数
    private List<UrlEntity> images;
    private Integer praiseCount;  // 某用户点赞数
    private Boolean userCollect;    // 是否收藏

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    @JsonIgnore
    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    @JsonIgnore
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @JsonIgnore
    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setLikeNum(Integer likeNum) {
        this.likeNum = likeNum;
    }

    public void setRepostNum(Integer repostNum) {
        this.repostNum = repostNum;
    }

    public Integer getLikeNum() {
        return likeNum;
    }

    public Integer getRepostNum() {
        return repostNum;
    }

    public Integer getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(Integer commentNum) {
        this.commentNum = commentNum;
    }

    public UrlEntity getMainImage() {
        return mainImage;
    }

    public void setMainImage(UrlEntity mainImage) {
        this.mainImage = mainImage;
    }

    public Integer getHot() {
        return hot;
    }

    public void setHot(Integer hot) {
        this.hot = hot;
    }

    public Boolean getLiked() {
        return liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }

    public Integer getErrorNum() {
        return errorNum;
    }

    public void setErrorNum(Integer errorNum) {
        this.errorNum = errorNum;
    }

    public List<String> getTagsList(){
        return StringUtils.splitString(tags);
    }

    @JsonIgnore
    public Integer getGroupNum() {
        return groupNum;
    }

    public void setGroupNum(Integer groupNum) {
        this.groupNum = groupNum;
    }

    public List<UrlEntity> getImages() {
        return images;
    }

    public void setImages(List<UrlEntity> images) {
        this.images = images;
    }

    public Integer getPraiseCount() {
        return praiseCount;
    }

    public void setPraiseCount(Integer praiseCount) {
        this.praiseCount = praiseCount;
    }

    public Boolean getUserCollect() {
        return userCollect;
    }

    public void setUserCollect(Boolean collect) {
        userCollect = collect;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }
}
