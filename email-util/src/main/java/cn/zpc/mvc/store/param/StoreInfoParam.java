package cn.zpc.mvc.store.param;

import cn.zpc.common.handler.exception.GlobalExceptionResult;
import cn.zpc.common.utils.StringUtils;
import cn.zpc.mvc.store.entity.Store;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;

@ApiModel(value = "店铺基本参数", description = "基本信息参数")
public class StoreInfoParam {

    @ApiParam(required = true, value = "商户名字")
    @NotEmpty
    @ApiModelProperty(value = "店铺名字")
    private String name;

    @NotEmpty
    @ApiModelProperty(required = true, value = "省")
    private String province;

    @NotEmpty
    @ApiModelProperty(required = true, value = "市")
    private String city;

    @ApiParam(required = true, value = "店铺地址")
    @ApiModelProperty
    @NotEmpty
    private String address;

    @ApiParam(value = "开业时间, yyyy-MM-dd")
    private String openTime;

    @ApiParam(value = "商铺介绍(企业商户必填)")
    private String introduction;

    @ApiParam(required = true,value = "商铺风格（1-影棚  2-实景）")
    @ApiModelProperty
    @NotNull
    private Integer style;

    @ApiParam(value = "logo图(企业商户必须填), 这里填写上传图片时返回的fileKey")
    @ApiModelProperty
    private String logo;

    @ApiParam(required = true, value = "封面图, 这里填写上传图片时返回的fileKey")
    @ApiModelProperty
    @NotEmpty
    private String mainImage;

    @ApiParam(value = "客服1")
    private String contracts1;

    @ApiParam(value = "客服1电话")
    private String phone1;

    @ApiParam(value = "客服2")
    private String contracts2;

    @ApiParam(value = "客服2电话")
    private String phone2;

    @ApiParam(value = "投诉客服")
    private String complain;

    @ApiParam(value = "投诉客服电话")
    private String phone3;

    @ApiParam(value = "商铺备注")
    @ApiModelProperty(allowEmptyValue = true)
    private String storeRemark;


    /**
     * 从参数中获取店铺信息进行封装
     * @return
     */
    public Store getStore(){

        Store store = new Store();
        store.setName(name);
        store.setCity(city);
        store.setProvince(province);
        if(StringUtils.isNotEmpty(province) && StringUtils.isNotEmpty(city)) {
            store.setAddress(province + city + address);
        }
        if(StringUtils.isNotEmpty(introduction)) {
            store.setIntroduction(introduction);
        }
        store.setStyle(style);
        store.setLogo(logo);
        store.setMainImage(mainImage);
        store.setRemark(storeRemark);
        if(StringUtils.isNotEmpty(openTime)) {
            try {
                store.setOpenTime(new SimpleDateFormat("yyyy-MM-dd").parse(openTime));
            }catch (Exception e){
                throw new GlobalExceptionResult("date format error", 1002);
            }
        }
        return store;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public Integer getStyle() {
        return style;
    }

    public void setStyle(Integer style) {
        this.style = style;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public String getContracts1() {
        return contracts1;
    }

    public void setContracts1(String contracts1) {
        this.contracts1 = contracts1;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getContracts2() {
        return contracts2;
    }

    public void setContracts2(String contracts2) {
        this.contracts2 = contracts2;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getStoreRemark() {
        return storeRemark;
    }

    public void setStoreRemark(String storeRemark) {
        this.storeRemark = storeRemark;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getComplain() {
        return complain;
    }

    public void setComplain(String complain) {
        this.complain = complain;
    }

    public String getPhone3() {
        return phone3;
    }

    public void setPhone3(String phone3) {
        this.phone3 = phone3;
    }
}
