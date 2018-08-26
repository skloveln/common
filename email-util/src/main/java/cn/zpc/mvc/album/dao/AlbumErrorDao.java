package cn.zpc.mvc.album.dao;

import cn.zpc.common.dao.CrudDao;
import cn.zpc.common.dao.annotation.Dao;
import cn.zpc.mvc.album.entity.AlbumComment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Dao
public interface AlbumErrorDao extends CrudDao<AlbumComment>{

    /**
     * 获取所有纠错评论
     * @return
     */
    List<AlbumComment> getAllError(@Param("albumId") Integer albumId);

    /**
     * 获取某一图集纠错评论数量
     * @return
     */
    Integer getErrorCount(@Param("id") Integer id);

    /**
     * 对某一纠错评论删除
     * @param id
     * @return
     */
    Integer delete(@Param("id") Integer id, @Param("userId") Integer userId);
}
