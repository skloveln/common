package cn.zpc.mvc.store.dao;

import cn.zpc.common.dao.CrudDao;
import cn.zpc.common.dao.annotation.Dao;
import cn.zpc.mvc.store.entity.StoreServicesImage;
import org.apache.ibatis.annotations.Param;

import java.util.*;

@Dao
public interface StoreServicesImageDao extends CrudDao<StoreServicesImage>{

    /**
     * 获取图片集
     * @param storeId
     * @return
     */
    List<StoreServicesImage>getImagesByStoreId(@Param("storeId") Integer storeId);

    /**
     * 删除某个图片
     * @param imageName
     * @param storeId
     * @return
     */
    Integer deleteImage(@Param("imageName") String imageName, @Param("storeId") Integer storeId);



}
