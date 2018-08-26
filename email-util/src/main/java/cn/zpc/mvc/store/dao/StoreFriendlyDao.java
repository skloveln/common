package cn.zpc.mvc.store.dao;

import cn.zpc.common.dao.annotation.Dao;
import org.apache.ibatis.annotations.Param;

@Dao
public interface StoreFriendlyDao {

    /**
     * 获取友情商铺集合
     * @param storeId
     * @return
     */
    String getFriendly(@Param("storeId") Integer storeId);

    /**
     * 删除友情店铺
     * @param storeId
     * @return
     */
    Integer delete(@Param("storeId") Integer storeId);

    /**
     * 插入友情推荐
     * @param storeId
     * @return
     */
    Integer insert(@Param("storeId") Integer storeId, @Param("ids") String ids);
}
