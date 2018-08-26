package cn.zpc.mvc.scene.dao;

import cn.zpc.common.dao.CrudDao;
import cn.zpc.common.dao.annotation.Dao;
import cn.zpc.mvc.scene.entity.SceneWorks;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Dao
public interface SceneWorksDao extends CrudDao<SceneWorks>{

    /**
     * 获取指定场景的作品
     * @param sceneId
     * @return
     */
    SceneWorks getWorksBySceneId(@Param("sceneId") Integer sceneId);

    /**
     * 删除指定作品
     * @param sceneId
     */
    Integer deleteBySceneId(@Param("sceneId") Integer sceneId);

    /**
     * 增加作品
     * @param sceneId
     * @param worksDesc
     * @return
     */
    Integer insertWorks(@Param("sceneId") Integer sceneId, @Param("worksDesc") String worksDesc);

}
