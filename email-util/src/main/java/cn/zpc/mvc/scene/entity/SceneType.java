package cn.zpc.mvc.scene.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class SceneType {

    private Integer id;
    private String sceneTypeName;
    private Integer sceneTypeParentId;
    private String imageUrl;
    private Integer hot;
    private Boolean deleted;

    private List<SceneType> childList;
    private Boolean hasChild;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSceneTypeName() {
        return sceneTypeName;
    }

    public void setSceneTypeName(String sceneTypeName) {
        this.sceneTypeName = sceneTypeName;
    }

    public Integer getSceneTypeParentId() {
        return sceneTypeParentId;
    }

    public void setSceneTypeParentId(Integer sceneTypeParentId) {
        this.sceneTypeParentId = sceneTypeParentId;
    }

    public Boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public List<SceneType> getChildList() {
        return childList;
    }

    public void setChildList(List<SceneType> childList) {
        this.childList = childList;
    }

    public Boolean getHasChild() {
        return hasChild;
    }

    public void setHasChild(Boolean hasChild) {
        this.hasChild = hasChild;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @JsonIgnore
    public Integer getHot() {
        return hot;
    }

    public void setHot(Integer hot) {
        this.hot = hot;
    }
}
