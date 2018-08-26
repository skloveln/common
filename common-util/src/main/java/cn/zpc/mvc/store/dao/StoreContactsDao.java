package cn.zpc.mvc.store.dao;

import cn.zpc.common.dao.CrudDao;
import cn.zpc.common.dao.annotation.Dao;
import cn.zpc.mvc.store.entity.StoreContacts;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Dao
public interface StoreContactsDao extends CrudDao<StoreContacts>{

    /**
     * 获取联系人列表
     * @param storeId
     * @return
     */
    List<StoreContacts> getListByStoreId(@Param("storeId") Integer storeId);

    /**
     * 获取联系人根据电话号和店铺Id
     * @param storeId
     * @param phone
     * @return
     */
    StoreContacts getByStoreIdAndPhone(@Param("storeId") Integer storeId, @Param("phone") String phone);
}
