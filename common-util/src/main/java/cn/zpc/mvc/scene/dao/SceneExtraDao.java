package cn.zpc.mvc.scene.dao;

import cn.zpc.common.dao.CrudDao;
import cn.zpc.common.dao.annotation.Dao;
import cn.zpc.mvc.scene.entity.SceneExtra;
import org.apache.ibatis.annotations.Param;

import java.util.*;

@Dao
public interface SceneExtraDao extends CrudDao<SceneExtra>{

    /**
     * 获取资讯列表
     * @return
     */
    List<SceneExtra> getList();

    /**
     * 按关键字搜索
     * @param keywords
     * @return
     */
    List<SceneExtra> search(@Param("keywords") String keywords, @Param("address") String address);

    /**
     *
     * @param id
     * @param imageName
     * @param keywords
     * @return
     */
    Integer updateData(@Param("id") Integer id, @Param("imageName") String imageName, @Param("keywords") String keywords);

    /**
     * 选择
     * @param id
     * @return
     */
    String selectKeywords(@Param("id") Integer id);
}
