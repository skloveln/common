package cn.zpc.mvc.sys.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SearchResult {

    private Integer id;
    private Integer type; // 结果类型 1-场景 2-图集 3-资讯
    private Object object;

    public SearchResult(){

    }

    public SearchResult(Integer type, Object object){
        this.object = object;
        this.type = type;
    }

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

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
