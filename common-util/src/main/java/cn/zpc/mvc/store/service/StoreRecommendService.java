package cn.zpc.mvc.store.service;

import cn.zpc.common.plugins.oss.OssPathConfig;
import cn.zpc.common.serivce.BaseService;
import cn.zpc.mvc.store.dao.StoreDao;
import cn.zpc.mvc.store.dao.StoreRecommendDao;
import cn.zpc.mvc.store.entity.Store;
import cn.zpc.mvc.store.entity.StoreRecommend;
import cn.zpc.mvc.user.dao.UserDao;
import cn.zpc.mvc.user.service.UserCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.*;

@Service
public class StoreRecommendService extends BaseService{

    @Autowired
    private StoreRecommendDao storeRecommendDao;
    @Autowired
    private StoreService storeService;
    @Autowired
    private UserCollectionService userCollectionService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private StoreDao storeDao;

    /**
     * 获取店铺动态
     * @return
     */
    public List<Map> getDynamic(){
        List result = new ArrayList();
        List<StoreRecommend> list = storeRecommendDao.getRecommend();
        for(StoreRecommend recommend : list){
            Map map = new HashMap();
            Store store = storeService.getStoreInfo(recommend.getStoreId());
            map.put("storeName", store.getName());
            map.put("storeId", store.getId());
            map.put("scene", storeService.getRecommendScene(store.getId()));
            map.put("titbits", storeService.getTitbitsList(store.getId(), 0, 1).getList());
            result.add(map);
        }
        return result;
    }

    /**
     * 获取首页推荐店铺
     * @return
     */
    @Transactional(rollbackFor = SQLException.class)
    public List<Map> getRecommendStores(Integer userId) {
        List<Map> result = new ArrayList<>();
        List<StoreRecommend> stores = storeRecommendDao.getHomeRecommend();
        for(StoreRecommend recommend : stores){
            Store store = storeDao.get(recommend.getStoreId());
            Map<String, Object> map = new TreeMap<>();
            map.put("storeId", store.getId());
            map.put("storeName", store.getName());
            map.put("storeAddress", store.getAddress());
            map.put("storeImage", ossService.getSimpleUrl(OssPathConfig.getStoreImagePath(store.getMainImage())));
            map.put("storekeeper", userDao.get(store.getUserId()).getNickname());
            map.put("sceneTypes", storeService.getAllSceneTypes(store.getId()));
            map.put("storeType", store.getType());
            map.put("storeHot", store.getHot());
            if(userId != null){
                map.put("storeCollect", userCollectionService.checkCollection(userId, 0, store.getId()));
            }
            map.put("recommendScene", storeService.getRecommendScene(store.getId()));
            result.add(map);
        }

        return result;
    }

    /**
     * 获取精选推荐
     */
    @Deprecated
    public List<Map> getAdert(){
        List result = new ArrayList();
        List<StoreRecommend> list = storeRecommendDao.getAdvert();
        for(StoreRecommend recommend : list){
            Map map = new HashMap();
            map.put("url", recommend.getUrl());
            map.put("image", ossService.getSimpleUrl(OssPathConfig.getStoreAdvertPath(recommend.getImage())));
            result.add(map);
        }
        return  result;
    }


}
