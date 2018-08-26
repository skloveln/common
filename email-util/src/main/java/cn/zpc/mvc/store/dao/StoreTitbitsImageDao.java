package cn.zpc.mvc.store.dao;

import cn.zpc.common.dao.CrudDao;
import cn.zpc.common.dao.annotation.Dao;
import cn.zpc.mvc.store.entity.StoreTitbitsImage;
import org.apache.ibatis.annotations.Param;

import java.util.*;

@Dao
public interface StoreTitbitsImageDao extends CrudDao<StoreTitbitsImage>{

    /**
     * 获取某一花絮图集
     * @param titbitsId
     * @return
     */
    List<StoreTitbitsImage> getList(@Param("titbitsId") Integer titbitsId);

    /**
     * 删除图片
     * @param imageName
     * @param titbitsId
     * @return
     */
    Integer deleteImage(@Param("imageName")String imageName, @Param("titbitsId")Integer titbitsId);

    /**
     * 删除某一花絮的全部图片
     * @param titbitsId
     * @return
     */
    Integer deleteTitbits(@Param("titbitsId")Integer titbitsId);
}
