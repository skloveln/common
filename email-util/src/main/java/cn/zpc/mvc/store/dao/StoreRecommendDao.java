package cn.zpc.mvc.store.dao;

import cn.zpc.common.dao.CrudDao;
import cn.zpc.common.dao.annotation.Dao;
import cn.zpc.mvc.store.entity.StoreRecommend;

import java.util.*;

@Dao
public interface StoreRecommendDao extends CrudDao<StoreRecommend>{

    /**
     * 获取商户模块推荐店铺
     * @return
     */
    List<StoreRecommend> getRecommend();

    /**
     * 获取首页店铺推荐
     * @return
     */
    List<StoreRecommend> getHomeRecommend();

    /**
     * 获取精选
     * @return
     */
    List<StoreRecommend> getAdvert();



}
