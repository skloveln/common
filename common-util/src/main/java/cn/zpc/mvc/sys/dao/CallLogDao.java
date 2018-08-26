package cn.zpc.mvc.sys.dao;

import cn.zpc.common.dao.CrudDao;
import cn.zpc.common.dao.annotation.Dao;
import cn.zpc.mvc.sys.entity.CallLog;
import org.apache.ibatis.annotations.Param;

import java.util.*;

@Dao
public interface CallLogDao extends CrudDao<CallLog>{

    /**
     * 获取某店铺的通话记录
     * @param storeId
     * @return
     */
    List<CallLog> getList(@Param("storeId") Integer storeId);


}
