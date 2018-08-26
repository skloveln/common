package cn.zpc.mvc.scene.dao;

import cn.zpc.common.dao.annotation.Dao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Dao
public interface SceneWorksImageDao {

    /**
     * 获取图片集合
     * @param sceneId
     * @return
     */
    List<String > getList(@Param("sceneId")Integer sceneId);

    /**
     * 删除图片
     * @param sceneId
     * @param image
     * @return
     */
    Integer deleteImage(@Param("sceneId") Integer sceneId, @Param("image") String image);

    /**
     * 插入图片
     * @param sceneId
     * @param image
     * @return
     */
    Integer insertImage(@Param("sceneId") Integer sceneId, @Param("image") String image);
}
