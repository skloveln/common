package cn.zpc.mvc.album.dao;

import cn.zpc.common.dao.CrudDao;
import cn.zpc.common.dao.annotation.Dao;
import cn.zpc.mvc.album.entity.AlbumComment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Dao
public interface AlbumCommentDao extends CrudDao<AlbumComment>{

    /**
     * 获取所有评论
     * @return
     */
    List<AlbumComment> getAllComment(@Param("albumId") Integer albumId);

    /**
     * 获取某一图集评论数量
     * @return
     */
    Integer getCommentCount(@Param("id") Integer id);

    /**
     * 对某一评论进行删除
     * @param id
     * @return
     */
    Integer delete(@Param("id") Integer id, @Param("userId") Integer userId);
}
