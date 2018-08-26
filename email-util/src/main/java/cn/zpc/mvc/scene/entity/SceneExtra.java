package cn.zpc.mvc.scene.entity;


import cn.zpc.common.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * 场景资讯
 */
public class SceneExtra {

    private Integer id;
    private String name;        // 标题
    private String image;       // 图片名
    private String extUrl;     // 图片链接(外链， 不放在本地)
    private String url;       // 文章链接
    private String keywords;  // 关键字

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @JsonIgnore
    public String getExtUrl() {
        return extUrl;
    }

    public void setExtUrl(String extUrl) {
        this.extUrl = extUrl;
    }

    @JsonIgnore
    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public List<String> getTags(){
        if(keywords != null){
            return StringUtils.splitString(keywords);
        }
        return null;
    }

}
