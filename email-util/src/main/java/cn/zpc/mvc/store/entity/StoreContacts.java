package cn.zpc.mvc.store.entity;

public class StoreContacts {

    private Integer id;
    private Integer storeId;
    private String name;
    private String phone;

    public StoreContacts(){}

    public StoreContacts(Integer storeId, String name, String phone){
        this.storeId = storeId;
        this.name = name;
        this.phone = phone;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
