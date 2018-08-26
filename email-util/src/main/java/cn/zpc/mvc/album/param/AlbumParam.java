package cn.zpc.mvc.album.param;

import cn.zpc.common.utils.StringUtils;
import cn.zpc.mvc.album.entity.Album;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;

import java.util.*;

@ApiModel(value = "图集基本参数", description = "基本参数")
public class AlbumParam {

    @ApiParam(value = "场景描述", required = true)
    @ApiModelProperty
    private String desc;

    @ApiParam(value = "省（市）", required = true)
    @ApiModelProperty
    private String province;

    @ApiParam(value = "市（区）", required = true)
    @ApiModelProperty
    private String city;

    @ApiParam(value = "具体地址")
    private String address;

    @ApiParam(value = "经度")
    private Double lon;

    @ApiParam(value = "纬度")
    private Double lat;

    @ApiParam(value = "联系电话")
    private String phone;

    @ApiParam(value = "标签   多个空格隔开", required = true)
    @ApiModelProperty
    private String tags;


    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public Album getAlbum(Integer userId){
        Album album = new Album();
        album.setUserId(userId);
        album.setDescribe(desc);
        album.setProvince(province);
        album.setCity(city);
        if(StringUtils.isNotEmpty(address)){
            if(!address.contains(province+city)){
                address = province + city + address;
            }
        }else {
            address = province + city;
        }
        album.setAddress(address);
        album.setLon(lon);
        album.setLat(lat);
        album.setPhone(phone);
        album.setTags(tags);
        album.setDeleted(false);
        album.setCreateTime(new Date());
        return album;
    }


}
