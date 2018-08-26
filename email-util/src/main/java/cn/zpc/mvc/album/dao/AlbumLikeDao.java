package cn.zpc.mvc.album.dao;

import cn.zpc.common.dao.annotation.Dao;
import org.apache.ibatis.annotations.Param;

@Dao
public interface AlbumLikeDao {

    /**
     * 获取用户对某一图集的点赞情况
     * @param userId
     * @param albumId
     * @return
     */
    Integer get(@Param("userId") Integer userId, @Param("albumId") Integer albumId);


    /**
     * 获取用户对某一图集的点赞次数
     * @param userId
     * @param albumId
     * @return
     */
    Integer getCount(@Param("userId") Integer userId, @Param("albumId") Integer albumId);


    /**
     * 插入数据
     * @param userId
     * @param albumId
     * @return
     */
    Integer insert(@Param("userId") Integer userId, @Param("albumId") Integer albumId);

    /**
     * 增加点赞次数
     * @param userId
     * @param albumId
     * @return
     */
    Integer addLikedCount(@Param("userId") Integer userId, @Param("albumId") Integer albumId);
}
