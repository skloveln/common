package cn.zpc.mvc.store.dao;

import cn.zpc.common.dao.CrudDao;
import cn.zpc.common.dao.annotation.Dao;
import cn.zpc.mvc.store.entity.StoreContacts;
import org.apache.ibatis.annotations.Param;

@Dao
public interface StoreComplainDao extends CrudDao<StoreContacts>{


    StoreContacts getComplain(@Param("storeId") Integer storeId);

}
