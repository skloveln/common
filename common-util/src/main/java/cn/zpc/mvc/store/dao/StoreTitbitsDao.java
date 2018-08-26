package cn.zpc.mvc.store.dao;

import cn.zpc.common.dao.CrudDao;
import cn.zpc.common.dao.annotation.Dao;
import cn.zpc.mvc.store.entity.StoreTitbits;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Dao
public interface StoreTitbitsDao extends CrudDao<StoreTitbits>{

    /**
     * 获取某店铺花絮列表
     * @param storeId
     * @return
     */
    List<StoreTitbits> getList(@Param("storeId") Integer storeId);


}
