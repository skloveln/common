package cn.zpc.mvc.album.dao;

import cn.zpc.common.dao.CrudDao;
import cn.zpc.common.dao.annotation.Dao;
import cn.zpc.mvc.album.entity.Album;
import org.apache.ibatis.annotations.Param;

import java.util.*;

@Dao
public interface AlbumDao extends CrudDao<Album>{

    /**
     * 按关键字搜索
     * @param keywords
     * @return
     */
    List<Album> search(@Param("keywords") String keywords, @Param("address") String address);

    /**
     * 按时间顺序获取图集列表
     * @return
     */
    List<Album> getListByCreateTime(@Param("userId") Integer userId);

    /**
     * 获取某一用户的图集
     * @param userId
     * @return
     */
    List<Album> getByUser(@Param("userId") Integer userId, @Param("viewId") Integer viewId);


    /**
     * 删除某一个图集
     * @param id
     * @return
     */
    Integer deletedAlbum(@Param("id") Integer id);

    /**
     * 删除某一个用户发过的图集
     * @param userId
     * @return
     */
    Integer deletedUserAlbum(@Param("userId") Integer userId);

    /**
     * 对某一图集进行点赞
     * @param id
     * @return
     */
    Integer likeAlbum(@Param("id") Integer id);


    /**
     * 对某一图集进行转发
     * @param id
     * @return
     */
    Integer repostAlbum(@Param("id") Integer id);

    /**
     * 获取某一用户的图集时间分组
     * @param userId
     * @param viewId
     * @return
     */
    List<Album> getUserAlbumGroupByTime(@Param("userId") Integer userId, @Param("viewId") Integer viewId);

    /**
     * 获取某一用户某一天的图集
     * @param userId
     * @param viewId
     * @return
     */
    List<Album> getUserByDate(@Param("userId") Integer userId, @Param("viewId") Integer viewId, @Param("date") Date date);

    /**
     * 获取图集总数
     * @return
     */
    Integer countAlbum();
}
