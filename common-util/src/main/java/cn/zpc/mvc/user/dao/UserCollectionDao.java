package cn.zpc.mvc.user.dao;

import cn.zpc.common.dao.CrudDao;
import cn.zpc.common.dao.annotation.Dao;
import cn.zpc.mvc.album.entity.Album;
import cn.zpc.mvc.scene.entity.SceneInfo;
import cn.zpc.mvc.store.entity.Store;
import cn.zpc.mvc.sys.entity.SearchResult;
import cn.zpc.mvc.user.entity.UserCollection;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Dao
public interface UserCollectionDao extends CrudDao<UserCollection>{

    UserCollection getCollection(@Param("userId") Integer userId,
                                 @Param("targetId") Integer targetId,
                                 @Param("collectionType") Integer collectionType);

    /**
     * 获取店铺收藏列表
     * @param userId
     * @return
     */
    List<Store> getStoreList(@Param("userId") Integer userId);

    /**
     * 获取场景收藏列表
     * @param userId
     * @return
     */
    List<SceneInfo> getSceneList(@Param("userId") Integer userId);

    /**
     * 获取图集收藏列表
     * @param userId
     * @return
     */
    List<Album> getAlbumList(@Param("userId") Integer userId);

    /**
     * 获取场景（包括图集）收藏列表
     * @param userId
     * @return
     */
    List<SearchResult> getSceneAlbumList(@Param("userId") Integer userId);
}
