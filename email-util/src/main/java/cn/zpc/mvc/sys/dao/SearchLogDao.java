package cn.zpc.mvc.sys.dao;

import cn.zpc.common.dao.CrudDao;
import cn.zpc.common.dao.annotation.Dao;
import cn.zpc.mvc.sys.entity.SearchLog;
import cn.zpc.mvc.sys.entity.SearchResult;
import org.apache.ibatis.annotations.Param;

import java.util.*;

@Dao
public interface SearchLogDao extends CrudDao<SearchLog>{

    /**
     * 获取热词
     * @return
     */
    List<SearchLog> getHotKeywords();

    /**
     * 增加热词记录
     * @param keywords
     * @param date
     * @return
     */
    int insertHotWords(@Param("keywords") String keywords, @Param("createTime") Date date);


    /**
     * 全局搜索
     * @param keywords
     * @param address
     * @param typeIds
     * @param priceType
     * @param priceMin
     * @param priceMax
     * @param areaMin
     * @param areaMax
     * @return
     */
    List<SearchResult> search(@Param("keywords") String keywords, @Param("address") String address,
                              @Param("type") Set<Integer> typeIds, @Param("priceType") Integer priceType,
                              @Param("priceMin") Integer priceMin, @Param("priceMax") Integer priceMax,
                              @Param("areaMin") Integer areaMin, @Param("areaMax")Integer areaMax);
}
