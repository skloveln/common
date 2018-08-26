package cn.zpc.mvc.store.dao;

import cn.zpc.common.dao.CrudDao;
import cn.zpc.common.dao.annotation.Dao;
import cn.zpc.mvc.store.entity.StoreServicesDesc;
import org.apache.ibatis.annotations.Param;

@Dao
public interface StoreServicesDescDao extends CrudDao<StoreServicesDesc>{

    /**
     * 获取店铺的服务
     * @param storeId
     * @return
     */
    StoreServicesDesc getServices(@Param("storeId") Integer storeId);

}
