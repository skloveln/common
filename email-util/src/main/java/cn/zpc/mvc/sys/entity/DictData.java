package cn.zpc.mvc.sys.entity;

/**
 * Description:数据字典
 * Author: sukai
 * Date: 2017-08-30
 */
public class DictData {



    private int id; // 编号
    private String type; // 可映射枚举类型，字典类型
    private String name; // 持久化key
    private int value; // 持久化value
    private String remarks; // 备注

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
