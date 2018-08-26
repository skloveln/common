package cn.zpc.mvc.sys.dao;

import cn.zpc.common.dao.CrudDao;
import cn.zpc.common.dao.annotation.Dao;
import cn.zpc.mvc.sys.entity.ApiLog;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Dao
public interface ApiLogDao extends CrudDao<ApiLog>{


    /**
     * 取得一个月内的数据请求
     * @param url
     * @return
     */
    List<ApiLog> getByRequestUrl(@Param("url") String url);

}
