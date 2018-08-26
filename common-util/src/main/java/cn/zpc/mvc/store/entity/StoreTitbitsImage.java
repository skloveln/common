package cn.zpc.mvc.store.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class StoreTitbitsImage {

    private Integer id;
    private Integer titbitsId;
    private String imageName;
    private Boolean deleted;

    public StoreTitbitsImage(){}

    public StoreTitbitsImage(Integer titbitsId, String imageName){
        this.imageName = imageName;
        this.titbitsId = titbitsId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTitbitsId() {
        return titbitsId;
    }

    public void setTitbitsId(Integer titbitsId) {
        this.titbitsId = titbitsId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    @JsonIgnore
    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
