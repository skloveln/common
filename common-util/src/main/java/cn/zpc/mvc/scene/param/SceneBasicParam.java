package cn.zpc.mvc.scene.param;

import cn.zpc.mvc.scene.entity.SceneInfo;
import io.swagger.annotations.ApiParam;

import javax.validation.constraints.NotNull;

public class SceneBasicParam {

    @ApiParam(required = true, value = "场景Id")
    @NotNull
    private Integer id;

    @ApiParam(required = false, value = "场景名称")
//    @NotEmpty
//    @ApiModelProperty
    private String name;

    @ApiParam(required = false, value = "场景类型Id")
//    @NotNull
//    @ApiModelProperty
    private Integer sceneTypeId;

    @ApiParam(required = false, value = "价格")
//    @NotNull
//    @ApiModelProperty
    private Integer price;

    @ApiParam(required = false, value = "价格类型（1.每小时 2.每天 3.每周 4.每月 5.面议）")
//    @NotNull
//    @ApiModelProperty
    private Integer priceType;

    @ApiParam(required = false, value = "面积")
//    @NotNull
//    @ApiModelProperty
    private Integer area;

    @ApiParam(value = "长")
    private double length;

    @ApiParam(value = "宽")
    private double width;

    @ApiParam(value = "高")
    private double height;

    @ApiParam(value = "经度")
    private double lon;

    @ApiParam(value = "纬度")
    private double lat;

    @ApiParam(required = false, value = "标签（多个标签以空格隔开）")
//    @NotEmpty
//    @ApiModelProperty
    private String tags;

    @ApiParam(required = false, value = "省")
//    @NotEmpty
//    @ApiModelProperty
    private String province;

    @ApiParam(required = false, value = "市")
//    @NotEmpty
//    @ApiModelProperty
    private String city;

    @ApiParam(required = false, value = "地址")
//    @NotEmpty
//    @ApiModelProperty
    private String address;

    @ApiParam(required = false, value = "详情描述")
//    @NotEmpty
//    @ApiModelProperty
    private String desc;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSceneTypeId() {
        return sceneTypeId;
    }

    public void setSceneTypeId(Integer sceneTypeId) {
        this.sceneTypeId = sceneTypeId;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getPriceType() {
        return priceType;
    }

    public void setPriceType(Integer priceType) {
        this.priceType = priceType;
    }

    public Integer getArea() {
        return area;
    }

    public void setArea(Integer area) {
        this.area = area;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * 从参数获取场景信息
     * @return
     */
    public SceneInfo getSceneInfo(){
        SceneInfo sceneInfo = new SceneInfo();
        sceneInfo.setId(id);
        sceneInfo.setSceneName(name);
        sceneInfo.setSceneTypeId(sceneTypeId);
        sceneInfo.setScenePrice(price);
        sceneInfo.setScenePriceType(priceType);
        sceneInfo.setSceneArea(area);
        sceneInfo.setLength(length);
        sceneInfo.setWidth(width);
        sceneInfo.setHeight(height);
        sceneInfo.setLon(lon);
        sceneInfo.setLat(lat);
        sceneInfo.setProvince(province);
        sceneInfo.setCity(city);
        if(province!=null && city!=null) {
            sceneInfo.setSceneAddress(province + city + address);
        }
        sceneInfo.setSceneKeyword(tags);
        sceneInfo.setSceneDesc(desc);

        return sceneInfo;
    }
}
