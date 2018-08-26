package cn.zpc.mvc.album.dao;

import cn.zpc.common.dao.CrudDao;
import cn.zpc.common.dao.annotation.Dao;
import cn.zpc.mvc.album.entity.AlbumImage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Dao
public interface AlbumImageDao extends CrudDao<AlbumImage>{

    /**
     * 获取某一图集所有图片
     * @param albumId
     * @return
     */
    List<String> getAlbumImages(@Param("albumId")Integer albumId);

    /**
     * 删除某张图
     * @param userId
     * @param imageName
     * @return
     */
    Integer deleteImage(@Param("userId")Integer userId, @Param("image")String imageName);
}
