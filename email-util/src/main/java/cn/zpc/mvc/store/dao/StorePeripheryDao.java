package cn.zpc.mvc.store.dao;

import cn.zpc.common.dao.CrudDao;
import cn.zpc.common.dao.annotation.Dao;
import cn.zpc.mvc.store.entity.StorePeriphery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Dao
public interface StorePeripheryDao extends CrudDao<StorePeriphery>{

    /**
     * 根据店铺Id获取周边场景图片信息集
     * @param storeId
     * @return
     */
    List<StorePeriphery> getListByStoreId(@Param("storeId") Integer storeId);

    /**
     * 获取某个周边场景的所有图片
     * @param id
     * @return
     */
    List<String> getPeripheryImages(@Param("peripheryId") Integer id);

    /**
     * 删除指定图片
     * @param imageName
     * @return
     */
    Integer deleteImage(@Param("imageName") String imageName);

    /**
     * 增加指定周边场景图片
     * @param peripheryId
     * @param imageName
     * @return
     */
    Integer insertImage(@Param("peripheryId") Integer peripheryId, @Param("imageName") String imageName);
}
