package cn.zpc.mvc.user.service;

import cn.zpc.common.plugins.oss.OssPathConfig;
import cn.zpc.common.serivce.BaseService;
import cn.zpc.common.web.result.DataResult;
import cn.zpc.common.web.result.Result;
import cn.zpc.mvc.album.entity.Album;
import cn.zpc.mvc.album.service.AlbumService;
import cn.zpc.mvc.scene.dao.SceneImageDao;
import cn.zpc.mvc.scene.dao.SceneInfoDao;
import cn.zpc.mvc.scene.dao.SceneTypeDao;
import cn.zpc.mvc.scene.entity.SceneImage;
import cn.zpc.mvc.scene.entity.SceneInfo;
import cn.zpc.mvc.scene.entity.SceneType;
import cn.zpc.mvc.scene.service.SceneService;
import cn.zpc.mvc.store.entity.Store;
import cn.zpc.mvc.store.service.StoreService;
import cn.zpc.mvc.sys.entity.SearchResult;
import cn.zpc.mvc.user.dao.UserCollectionDao;
import cn.zpc.mvc.user.entity.UserCollection;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.*;

/**
 * Description:
 * Author: sukai
 * Date: 2017-08-15
 */
@Service
public class UserCollectionService extends BaseService{

    @Autowired
    private SceneTypeDao sceneTypeDao;
    @Autowired
    private UserCollectionDao userCollectionDao;
    @Autowired
    private SceneImageDao sceneImageDao;
    @Autowired
    private StoreService storeService;
    @Autowired
    private AlbumService albumService;
    @Autowired
    private SceneInfoDao sceneInfoDao;
    @Autowired
    private SceneService sceneService;

    /**
     * 检验用户是否收藏某个信息
     * @param userId
     * @param collectionType 0-店铺 1-场景 2: 图集
     * @param targetId
     * @return
     */
    public Boolean checkCollection(Integer userId, Integer collectionType, Integer targetId){
        UserCollection userCollection = userCollectionDao.getCollection(userId, targetId, collectionType);
        return  userCollection != null && userCollection.getDeleted() == false;
    }

    /**
     * 用户取消收藏某个信息
     * @param targetType  0:店铺  1：场景  2: 图集
     * @param targetId
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = SQLException.class)
    public Boolean userCancelCollection(Integer targetType, Integer targetId, Integer userId){

        UserCollection userCollection = userCollectionDao.getCollection(userId, targetId, targetType);
        userCollection.setUpdateTime(new Date());
        userCollection.setDeleted(true);
//        userCollection.setUserId(null);
//        userCollection.setTargetId(null);
//        userCollection.setCollectionType(null);
        return userCollectionDao.update(userCollection) > 0;
    }

    /**
     * 用户收藏某个信息
     * @param targetType  0:店铺  1：场景  2：图集
     * @param targetId 收藏的目标id
     * @param userId 用户Id
     * @return
     */
    @Transactional(rollbackFor = SQLException.class)
    public Boolean insertUserCollection(Integer targetType, Integer targetId, Integer userId){

        UserCollection userCollection = userCollectionDao.getCollection(userId, targetId, targetType);

        if(userCollection == null) {
            userCollection = new UserCollection();
            userCollection.setCollectionType(targetType);
            userCollection.setCreateTime(new Date());
            userCollection.setDeleted(false);
            userCollection.setTargetId(targetId);
            userCollection.setUpdateTime(new Date());
            userCollection.setUserId(userId);
            return userCollectionDao.insert(userCollection) > 0;
        }else {
            userCollection.setUpdateTime(new Date());
            userCollection.setDeleted(false);
            return userCollectionDao.update(userCollection) > 0;
        }

    }


    /**
     * 获取用户收藏店铺的信息
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Map getCollectStoreInfo(Integer userId, Integer pageNum, Integer pageSize){

        Map<String, Object> result = new HashMap<>();
        PageHelper.startPage(pageNum, pageSize, true);
        List<Store> storeList = userCollectionDao.getStoreList(userId);
        //设置店铺的封面图及logo
        for(Store store : storeList){
            store.setMainImage(ossService.getSimpleUrl(OssPathConfig.getStoreImagePath(store.getMainImage())));
            store.setLogo(ossService.getSimpleUrl(OssPathConfig.getStoreLogoPath(store.getLogo())));
            store.setSceneTypes(storeService.getAllSceneTypes(store.getId()));
            store.setCreateTime(null);
            store.setUserId(null);
            store.setAvatar(null);
        }
        result.put("totalSize", new PageInfo<>(storeList).getTotal());
        result.put("storeInfo", storeList);

        return result;
    }


    /**
     * 获取用户收藏场景的信息
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Result getCollectSceneInfo(Integer userId, Integer pageNum, Integer pageSize){

        Map<String, Object> result = new HashMap<>();
        PageHelper.startPage(pageNum, pageSize, true);
        List<SceneInfo> sceneList = userCollectionDao.getSceneList(userId);
        for (SceneInfo sceneInfo : sceneList){
            SceneImage image = sceneImageDao.getMainImage(sceneInfo.getId());
            // 设置封面图
            sceneInfo.setMainImage(ossService.getSimpleUrl(OssPathConfig.getSceneImagePath(image.getImageName())));
            // 设置收藏信息
            sceneInfo.setUserCollection(checkCollection(userId,1, sceneInfo.getId()));
            // 设置场景类型
            sceneInfo.setSceneType(sceneTypeDao.get(sceneInfo.getSceneTypeId()).getSceneTypeName());
            // 去除多余信息
            sceneInfo.setLat(null);
            sceneInfo.setLon(null);
        }
        result.put("totalSize", new PageInfo<>(sceneList).getTotal());
        result.put("sceneInfo", sceneList);

        return DataResult.getNormal(result);
    }


    /**
     * 获取用户收藏场景和图集的信息
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Result getCollectSceneAlbumInfo(Integer userId, Integer pageNum, Integer pageSize){
        Map<String, Object> result = new HashMap<>();
        PageHelper.startPage(pageNum, pageSize, true);
        List<SearchResult> list = userCollectionDao.getSceneAlbumList(userId);
        for (SearchResult single : list){
            if(single.getType() == 1){
                SceneInfo sceneInfo = sceneInfoDao.get(single.getId());
                if(sceneInfo != null){
                    single.setObject(sceneService.addSceneInfo(sceneInfo, userId));
                }else{
                    single.setType(0);
                    single.setObject("该数据已失效。");
                }
            }else if(single.getType() == 2){
                single.setObject(albumService.getSimpleInfo(single.getId(), userId));
            }
        }
        result.put("totalSize", new PageInfo<>(list).getTotal());
        result.put("sceneInfo", list);

        return DataResult.getNormal(result);
    }
}
