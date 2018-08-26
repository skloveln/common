package cn.zpc.mvc.scene.dao;

import cn.zpc.common.dao.CrudDao;
import cn.zpc.common.dao.annotation.Dao;
import cn.zpc.mvc.scene.entity.SceneType;

import java.util.List;

@Dao
public interface SceneTypeDao extends CrudDao<SceneType> {

    /**
     * 获取顶层场景类型
     * @return
     */
    List<SceneType> getTopTypes();


    /**
     * 获取指定场景的子场景
     * @param id
     * @return
     */
    List<SceneType> getChildTypes(Integer id);

    /**
     * 获取热门场景类型
     * @return
     */
    List<SceneType> getHotTypes();

}
