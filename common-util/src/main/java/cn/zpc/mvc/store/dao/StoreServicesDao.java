package cn.zpc.mvc.store.dao;

import cn.zpc.common.dao.CrudDao;
import cn.zpc.common.dao.annotation.Dao;
import cn.zpc.mvc.store.entity.StoreServices;
import org.apache.ibatis.annotations.Param;

@Dao
public interface StoreServicesDao extends CrudDao<StoreServices>{

    /**
     * 获取店铺服务
     * @param storeId
     * @return
     */
    StoreServices getByStoreId(@Param("storeId") Integer storeId);


}
