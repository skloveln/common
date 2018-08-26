package cn.zpc.mvc.scene.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SceneServ {

    private Integer id;
    private Integer typeId;
    private String sceneServiceName;
    private String sceneServiceDesc;
    private String sceneServiceIcon;
    private Boolean deleted;
    private Boolean selected;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSceneServiceName() {
        return sceneServiceName;
    }

    public void setSceneServiceName(String sceneServiceName) {
        this.sceneServiceName = sceneServiceName;
    }

    public String getSceneServiceDesc() {
        return sceneServiceDesc;
    }

    public void setSceneServiceDesc(String sceneServiceDesc) {
        this.sceneServiceDesc = sceneServiceDesc;
    }

    public String getSceneServiceIcon() {
        return sceneServiceIcon;
    }

    public void setSceneServiceIcon(String sceneServiceIcon) {
        this.sceneServiceIcon = sceneServiceIcon;
    }

    @JsonIgnore
    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
