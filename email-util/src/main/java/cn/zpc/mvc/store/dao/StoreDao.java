package cn.zpc.mvc.store.dao;

import cn.zpc.common.dao.CrudDao;
import cn.zpc.common.dao.annotation.Dao;
import cn.zpc.mvc.store.entity.Store;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Dao
public interface StoreDao extends CrudDao<Store>{


    List<Integer> getOperations(@Param("storeId") Integer storeId);


    /**
     * 获取热门店铺，场景数大于3个
     * @return
     */
    List<Store> getHotStore();

    /**
     * 根据关键字搜索店铺
     * @param keyword
     * @return
     */
    List<Store> search(@Param("keyword") String keyword, @Param("address") String address,
                       @Param("style") Integer style, @Param("type") Integer type);

    /**
     * 根据用户Id获取店铺信息
     * @param userId
     * @return
     */
    Store getByUser(@Param("userId") Integer userId);

    /**
     *
     * @return
     */
    List<Store> getList();

    /**
     * 获取最新店铺列表
     * @return
     */
    List<Store> getNewList();

    /**
     * 删除商户
     * @param userId
     * @return
     */
    Integer deleteScene(@Param("userId")Integer userId);
}
