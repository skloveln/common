package cn.zpc.mvc.scene.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SceneWorks {

    private Integer id;
    private Integer sceneId;
    private String imageUrl;
    private String imageDesc;

    private String worksDesc;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonIgnore
    public Integer getSceneId() {
        return sceneId;
    }

    public void setSceneId(Integer sceneId) {
        this.sceneId = sceneId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageDesc() {
        return imageDesc;
    }

    public void setImageDesc(String imageDesc) {
        this.imageDesc = imageDesc;
    }

    public String getWorksDesc() {
        return worksDesc;
    }

    public void setWorksDesc(String worksDesc) {
        this.worksDesc = worksDesc;
    }
}
