package cn.zpc.mvc.scene.entity;

import cn.zpc.common.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;

public class SceneInfo {

    private Integer id;
    private Integer userId; // 联系人Id
    private String sceneName; // 场景名称
    private Integer sceneTypeId; // 场景类型ID
    private String sceneType; // 场景类型
    private String sceneKeyword; // 场景关键字
    private Double length;
    private Double width;
    private Double height;
    private Integer sceneArea; // 场景面积
    private Integer scenePrice; // 场景价格
    private Integer scenePriceType; // 场景价格类型
    private String sceneDesc; // 场景描述
    private String province;
    private String city;
    private String sceneAddress; // 场景地址
    private Double lat; // 纬度
    private Double lon; // 经度
    private Integer status;  // 场景状态
    private String vrUrl; // vr链接
    private Integer hot; // 场景热度
    private Integer weight; // 权重，主推场景排序


    private boolean top;
    private Date createTime;
    private Date updateTime;
    private Boolean deleted;
    private String cause;

    private Boolean userCollection; // 用户是否收藏
    private String mainImage; // 列表展示的大图


    @JsonIgnore
    public Boolean getDeleted() {
        return deleted;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public String getSceneKeyword() {
        return sceneKeyword;
    }

    public void setSceneKeyword(String sceneKeyword) {
        this.sceneKeyword = sceneKeyword;
    }

    public Integer getSceneArea() {
        return sceneArea;
    }

    public void setSceneArea(Integer sceneArea) {
        this.sceneArea = sceneArea;
    }

    public Integer getScenePrice() {
        return scenePrice;
    }

    public void setScenePrice(Integer scenePrice) {
        this.scenePrice = scenePrice;
    }

    public Integer getScenePriceType() {
        return scenePriceType;
    }

    public void setScenePriceType(Integer scenePriceType) {
        this.scenePriceType = scenePriceType;
    }

    public String getSceneDesc() {
        return sceneDesc;
    }

    public void setSceneDesc(String sceneDesc) {
        this.sceneDesc = sceneDesc;
    }

    public String getSceneAddress() {
        return sceneAddress;
    }

    public void setSceneAddress(String sceneAddress) {
        this.sceneAddress = sceneAddress;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    @JsonIgnore
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSceneTypeId() {
        return sceneTypeId;
    }

    public void setSceneTypeId(Integer sceneTypeId) {
        this.sceneTypeId = sceneTypeId;
    }

    public String getSceneType() {
        return sceneType;
    }

    public void setSceneType(String sceneType) {
        this.sceneType = sceneType;
    }

    public String getVrUrl() {
        return vrUrl;
    }

    public void setVrUrl(String vrUrl) {
        this.vrUrl = vrUrl;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getHot() {
        return hot;
    }

    public void setHot(Integer hot) {
        this.hot = hot;
    }

    public Boolean getUserCollection() {
        return userCollection;
    }

    public void setUserCollection(Boolean userCollection) {
        this.userCollection = userCollection;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public List<String> getTagArray(){

        return StringUtils.splitString(this.sceneKeyword);
    }

    @JsonIgnore
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getCause() {
        return cause;
    }

    public String getPriceStr(){
        if(scenePriceType != null){
            switch (scenePriceType){
                case 1:
                    return scenePrice + "元/小时";
                case 2:
                    return scenePrice + "元/天";
                case 3:
                    return scenePrice + "元/周";
                case 4:
                    return scenePrice + "元/月";
                default:
                    return "面议";
            }
        }

        return null;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDetailAddress() {
        if(sceneAddress !=null && !sceneAddress.isEmpty()){
            try {
                return sceneAddress.substring(province.length()+city.length());
            }catch (Exception e){
                return null;
            }
        }
        return null;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }


    public boolean getTop() {
        return top;
    }

    public void setTop(boolean top) {
        this.top = top;
    }
}
