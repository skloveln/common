package cn.zpc.mvc.store.dao;

import cn.zpc.common.dao.annotation.Dao;
import org.apache.ibatis.annotations.Param;

@Dao
public interface StoreAdvertDao {

    /**
     * 获取店铺广告封面图
     * @param storeId
     * @return
     */
    String getImage(@Param("storeId") Integer storeId);


    Integer insertAd(@Param("storeId") Integer storeId, @Param("imageName") String imageName);



    Integer updateAd(@Param("storeId") Integer storeId, @Param("imageName") String imageName);
}
