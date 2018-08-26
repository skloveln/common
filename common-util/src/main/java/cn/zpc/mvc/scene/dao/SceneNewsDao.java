package cn.zpc.mvc.scene.dao;

import cn.zpc.common.dao.CrudDao;
import cn.zpc.common.dao.annotation.Dao;
import cn.zpc.mvc.scene.entity.SceneNews;
import org.apache.ibatis.annotations.Param;

import java.util.*;

@Dao
public interface SceneNewsDao extends CrudDao<SceneNews>{

    /**
     * 快报列表
     * @return
     */
    List<SceneNews> getList();

    /**
     * 按时间获取快报
     * @param date
     * @return
     */
    List<SceneNews> getListByDate(@Param("date") Date date);


    /**
     * 测试方法
     * @param id
     * @param date
     * @return
     */
    Integer updateTime(@Param("id") Integer id, @Param("date") Date date);
}
