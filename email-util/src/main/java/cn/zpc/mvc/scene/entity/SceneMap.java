package cn.zpc.mvc.scene.entity;


public class SceneMap {

    private Integer id;
    private Integer type;  // 1-场景 2-图集
    private Double lon;    // 经度
    private Double lat;    // 纬度

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
}
