package cn.zpc.mvc.store.entity;

import cn.zpc.common.utils.StringUtils;

import java.util.*;

@Deprecated
public class StoreServices {

    private Integer id;
    private Integer storeId;
    private String eat;
    private String stay;
    private String trip;
    private String desc;


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

    public String getEat() {
        return eat;
    }

    public void setEat(String eat) {
        this.eat = eat;
    }

    public String getStay() {
        return stay;
    }

    public void setStay(String stay) {
        this.stay = stay;
    }

    public String getTrip() {
        return trip;
    }

    public void setTrip(String trip) {
        this.trip = trip;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }





    public List<Double> getEatArrary(){
        return sort(eat);
    }

    public List<Double> getStayArrary(){
        return sort(stay);
    }

    public List<Map<String, Object>> getTripArrary(){
        List<Map<String, Object>> result = new ArrayList<>();
        if(!StringUtils.isEmpty(trip)){
            List<String> array = StringUtils.splitString(trip);
            for(String str : array){
                Map<String, Object> map = new HashMap<>();
                List list = StringUtils.splitKeyword(str);
                map.put("price", list.get(0));
                map.put("seatNum", list.get(1));
                result.add(map);
            }
        }
        return result;
    }

    private List<Double> sort(String source){
        List<Double> result = new ArrayList<>();
        if(!StringUtils.isEmpty(source)){
            List<String> array = StringUtils.splitString(source);
            for(String str : array){
                result.add(Double.parseDouble(str));
            }
            // 去重
            result = new ArrayList<>(new HashSet<>(result));
            // 排序
            Collections.sort(result);
        }
        return result;
    }


}
