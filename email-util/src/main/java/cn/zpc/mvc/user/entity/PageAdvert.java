package cn.zpc.mvc.user.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;

public class PageAdvert {

    private Integer id;
    private Integer type;    // 类型
    private String ext;      // 扩展内容
    private String imageUrl; // 图片链接

    @JsonIgnore
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
