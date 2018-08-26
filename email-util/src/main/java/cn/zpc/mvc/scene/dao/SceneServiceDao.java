package cn.zpc.mvc.scene.dao;

import cn.zpc.common.dao.CrudDao;
import cn.zpc.common.dao.annotation.Dao;
import cn.zpc.mvc.scene.entity.SceneServ;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Dao
public interface SceneServiceDao extends CrudDao<SceneServ>{

    /**
     * 获取某场景提供的服务
     * @param sceneId
     * @return
     */
    List<SceneServ> getIconBySceneId(@Param("sceneId") Integer sceneId);

    /**
     * 获取全部场景服务资源
     * @return
     */
    List<SceneServ> getResource();

    /**
     * 获取食住行服务资源
     * @return
     */
    List<SceneServ> getFoodResource();

    /**
     * 删除指定场景服务
     * @param sceneId
     * @return
     */
    Integer deleteService(@Param("sceneId")Integer sceneId);

    /**
     * 增加场景服务
     * @param sceneId
     * @param typeId
     * @return
     */
    Integer insertSceneService(@Param("sceneId")Integer sceneId, @Param("typeId")Integer typeId);
}
