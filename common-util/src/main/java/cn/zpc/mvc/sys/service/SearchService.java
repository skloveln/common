package cn.zpc.mvc.sys.service;

import cn.zpc.common.plugins.oss.OssPathConfig;
import cn.zpc.common.redis.Cache;
import cn.zpc.common.redis.Redis;
import cn.zpc.common.serivce.BaseService;
import cn.zpc.common.utils.StringUtils;
import cn.zpc.mvc.album.dao.AlbumDao;
import cn.zpc.mvc.album.entity.Album;
import cn.zpc.mvc.album.service.AlbumService;
import cn.zpc.mvc.scene.dao.SceneInfoDao;
import cn.zpc.mvc.scene.dao.SceneExtraDao;
import cn.zpc.mvc.scene.entity.SceneInfo;
import cn.zpc.mvc.scene.entity.SceneExtra;
import cn.zpc.mvc.scene.entity.SceneType;
import cn.zpc.mvc.scene.service.SceneExtraService;
import cn.zpc.mvc.scene.service.SceneService;
import cn.zpc.mvc.scene.service.SceneTypeService;
import cn.zpc.mvc.store.dao.StoreDao;
import cn.zpc.mvc.store.entity.Store;
import cn.zpc.mvc.store.service.StoreService;
import cn.zpc.mvc.sys.dao.SearchLogDao;
import cn.zpc.mvc.sys.entity.SearchLog;
import cn.zpc.mvc.sys.entity.SearchResult;
import cn.zpc.mvc.user.entity.User;
import cn.zpc.mvc.user.service.UserCollectionService;
import cn.zpc.mvc.user.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SearchService extends BaseService{

    @Autowired
    private SceneService sceneService;
    @Autowired
    private SceneTypeService sceneTypeService;
    @Autowired
    private SceneInfoDao sceneInfoDao;
    @Autowired
    private StoreDao storeDao;
    @Autowired
    private SearchLogDao searchLogDao;
    @Autowired
    private StoreService storeService;
    @Autowired
    private SceneExtraDao sceneExtraDao;
    @Autowired
    private UserCollectionService userCollectionService;
    @Autowired
    private AlbumDao albumDao;
    @Autowired
    private AlbumService albumService;
    @Autowired
    private UserService userService;
    @Autowired
    private SceneExtraService sceneExtraService;

    /**
     * 按场景类型搜索场景
     * @param sceneTypeId
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageInfo<SceneInfo> searchSceneByType(Integer userId, Integer sceneTypeId, Integer pageNum, Integer pageSize){

        Set<Integer> types = new HashSet<>();
        types.add(sceneTypeId);
        Set<SceneType> typeList = sceneTypeService.getChildren(sceneTypeId);
        for(SceneType sceneType : typeList){
            types.add(sceneType.getId());
        }
        PageHelper.startPage(pageNum, pageSize, true);
        List<SceneInfo> list = sceneInfoDao.getListByType(types);
        list = sceneService.addSceneInfo(list, userId);

        return new PageInfo<>(list);
    }

    /**
     * 全局搜索
     * @param keyword
     * @return
     */
    public PageInfo<SearchResult> search(Integer userId, Integer style, String keyword, String address,
                                           Integer type, Integer priceType, Integer priceMin,
                                           Integer priceMax, Integer areaMin, Integer areaMax,
                                           Integer pageNum, Integer pageSize){
        PageInfo<SearchResult> pageInfo = new PageInfo<>();
        List<SearchResult> list = new ArrayList<>();
        if(style == 1) {
            Set<Integer> types = sceneTypeService.getChildrenTypeIds(type);
            PageHelper.startPage(pageNum, pageSize, true);
            List<SearchResult> searchList = searchLogDao.search(keyword, address, types, priceType, priceMin, priceMax, areaMin, areaMax);
            for(SearchResult single : searchList){
                if(single.getType() == 1){
                    SceneInfo sceneInfo = sceneService.addSceneInfo(sceneInfoDao.get(single.getId()), userId);
                    single.setObject(sceneInfo);
                }else if(single.getType() == 2){
                    single.setObject(albumService.getSimpleInfo(single.getId(), userId));
                }else if(single.getType() == 3){
                    single.setObject(sceneExtraService.getInfo(single.getId()));
                }
            }
            pageInfo = new PageInfo<>(searchList);
        }else if(style == 2) {
            // 搜索店铺
            PageInfo<Store> pageInfoStore = searchStore(userId, keyword, address, null, null, pageNum, pageSize);
            for(Store store: pageInfoStore.getList()){
                list.add(new SearchResult(3, store));
            }
            pageInfo.setList(list);
            pageInfo.setTotal(pageInfoStore.getTotal());
        }
        return pageInfo;
    }


    /**
     * 按关键字搜索场景
     * @param keyword
     * @return
     */
    public PageInfo<SceneInfo> searchScene(Integer userId, String keyword, String address,
                                           Integer type, Integer priceType, Integer priceMin,
                                           Integer priceMax, Integer areaMin, Integer areaMax,
                                           Integer pageNum, Integer pageSize){

        PageHelper.startPage(pageNum, pageSize, true);
        Set<Integer> types = sceneTypeService.getChildrenTypeIds(type);
        List<SceneInfo> list = sceneInfoDao.search(keyword, address, types, priceType, priceMin, priceMax, areaMin, areaMax);
        list = sceneService.addSceneInfo(list, userId);
        return new PageInfo<>(list);
    }


    /**
     * 按关键字搜索店铺
     * @param keyword
     * @return
     */
    public PageInfo<Store> searchStore(Integer userId, String keyword, String address, Integer style, Integer type, Integer pageNum, Integer pageSize){

        PageHelper.startPage(pageNum, pageSize, true);
        List<Store> list = storeDao.search(keyword, address, style, type);
        for(Store store : list){
            store.setMainImage(ossService.getSimpleUrl(OssPathConfig.getStoreImagePath(store.getMainImage())));
            store.setSceneTypes(storeService.getAllSceneTypes(store.getId()));
            if(userId != null){
                store.setUserCollect(userCollectionService.checkCollection(userId, 0, store.getId()));
            }
        }
        return new PageInfo<>(list);
    }

    /**
     * 搜索资讯
     * @param keyword
     * @param address
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageInfo<SceneExtra> searchInformation(String keyword, String address, Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum, pageSize, true);
        List<SceneExtra> list = sceneExtraDao.search(keyword, address);
        for(SceneExtra info : list){
            if(StringUtils.isNotEmpty(info.getImage())) {
                info.setImage(ossService.getSimpleUrl(OssPathConfig.getSceneExtraPath(info.getImage())));
            }else {
                info.setImage(info.getExtUrl());
            }
        }
        return new PageInfo<>(list);
    }

    /**
     * 搜索图集
     * @param keyword
     * @param address
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageInfo<Album> searchAlbum(String keyword, String address, Integer userId, Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum, pageSize, true);
        List<Album> list = albumDao.search(keyword, address);
        for(Album info : list){
           info.setMainImage(albumService.getMainImage(info.getId()));
           info.setCommentNum(albumService.getCommentNum(info.getId()));
           info.setUserCollect(userCollectionService.checkCollection(userId, 2, info.getId()));
        }
        return new PageInfo<>(list);
    }


    /**
     * 增加搜索日志
     * @param keyword 搜索关键词
     * @param userId 搜索用户
     * @param type 搜索类型 0-未指定 1-店铺 2-场景 3-图集
     * @param others 其他备注
     * @return
     */
    public Boolean addSearchLog(String keyword, Integer userId, Integer type, String others){

        SearchLog searchLog = new SearchLog();
        searchLog.setCreateTime(new Date());
        if(userId != null) {
            searchLog.setUserId(userId);
        }
        searchLog.setKeyword(keyword);
        searchLog.setOthers(others);
        searchLog.setType(type);

        return searchLogDao.insert(searchLog) > 0;
    }

    /**
     * 获取热门搜索关键字
     * @return
     */
    public List<String> hotKeywords() {
        // 查询redis
        String hotWords = "";
        Cache cache = Redis.use();
        if(cache.exists("_search_hotKeywords")){  // 从redis缓存中去取
            hotWords =  cache.get("_search_hotKeywords");
        }else{  // 统计热词并存入redis
            List<SearchLog> logs = searchLogDao.getHotKeywords();
            for(SearchLog log : logs){
                if(!log.getKeyword().isEmpty()){
                    hotWords += log.getKeyword() + ",";
                }
            }
            if(!hotWords.isEmpty()){
                hotWords = hotWords.substring(0,hotWords.length()-1);
            }
            searchLogDao.insertHotWords(hotWords, new Date());
            cache.setex("_search_hotKeywords", 3600*24, hotWords); // 储存一天失效
        }
        List<String> list = StringUtils.splitKeyword(hotWords);
        return list;
    }
}
