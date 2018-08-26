package cn.zpc.mvc.store.entity;

import cn.zpc.common.entity.UrlEntity;

import java.util.List;

public class StorePeriphery {

    private Integer id;
    private Integer storeId;
//    private String imageUrl;
    private String imageDesc;

    private List<UrlEntity> imageList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

//    public String getImageUrl() {
//        return imageUrl;
//    }
//
//    public void setImageUrl(String imageUrl) {
//        this.imageUrl = imageUrl;
//    }

    public String getImageDesc() {
        return imageDesc;
    }

    public void setImageDesc(String imageDesc) {
        this.imageDesc = imageDesc;
    }

    public void setImageList(List<UrlEntity> imageList) {
        this.imageList = imageList;
    }

    public List<UrlEntity> getImageList() {
        return imageList;
    }
}
