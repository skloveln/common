package cn.zpc.mvc.scene.dao;

import cn.zpc.common.dao.CrudDao;
import cn.zpc.common.dao.annotation.Dao;
import cn.zpc.mvc.scene.entity.SceneImage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Dao
public interface SceneImageDao extends CrudDao<SceneImage>{
    /**
     * 根据权重降序获取封面图片
     * @param sceneId
     * @return
     */
    List<SceneImage> getImages(@Param("sceneId") Integer sceneId);

    SceneImage getMainImage(@Param("sceneId") Integer sceneId);

    List<SceneImage> getImagesByUserId(@Param("userId") Integer userId);

    SceneImage getManage(@Param("userId") Integer userId, @Param("sceneId") Integer sceneId, @Param("imageName") String imageName);

    /**
     * 清空场景图片
     * @param sceneId
     * @return
     */
    Integer clearSceneImages(@Param("sceneId") Integer sceneId);
    /**
     * 清空场景图片
     * @param
     * @return
     */
    Integer insertSceneImage(SceneImage sceneImage);
}
