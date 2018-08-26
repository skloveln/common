package cn.zpc.mvc.scene.service;

import cn.zpc.common.entity.UrlEntity;
import cn.zpc.common.plugins.oss.OssPathConfig;
import cn.zpc.common.serivce.BaseService;
import cn.zpc.common.utils.StringUtils;
import cn.zpc.mvc.album.dao.AlbumDao;
import cn.zpc.mvc.album.entity.Album;
import cn.zpc.mvc.album.service.AlbumService;
import cn.zpc.mvc.scene.dao.*;
import cn.zpc.mvc.scene.entity.*;
import cn.zpc.mvc.store.dao.StoreDao;
import cn.zpc.mvc.store.entity.Store;
import cn.zpc.mvc.store.service.StoreService;
import cn.zpc.mvc.sys.entity.SearchResult;
import cn.zpc.mvc.user.dao.AdvertDao;
import cn.zpc.mvc.user.dao.UserDao;
import cn.zpc.mvc.user.entity.User;
import cn.zpc.mvc.user.service.UserCollectionService;
import cn.zpc.mvc.user.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.*;

@Service
public class SceneService extends BaseService{

    @Autowired
    private StoreDao storeDao;
    @Autowired
    private SceneInfoDao sceneInfoDao;
    @Autowired
    private SceneTypeDao sceneTypeDao;
    @Autowired
    private SceneImageDao sceneImageDao;
    @Autowired
    private SceneServiceDao sceneServiceDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private SceneWorksDao sceneWorksDao;
    @Autowired
    private SceneWorksImageDao sceneWorksImageDao;
    @Autowired
    private UserCollectionService userCollectionService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private SceneService sceneService;
    @Autowired
    private AlbumService albumService;
    @Autowired
    private UserService userService;
    @Autowired
    private AlbumDao albumDao;

    /**
     * 获取场景tab页信息
     * @param userId
     * @return
     */
    public PageInfo<SearchResult> getSceneTab(Integer userId, Integer pageNum, Integer pageSize){

        PageHelper.startPage(pageNum, pageSize, true);
        List<SearchResult> list = sceneInfoDao.getSceneTab(userId);
        for(SearchResult single : list){
            if(single.getType() == 1){
                SceneInfo sceneInfo = addSceneInfo(sceneInfoDao.get(single.getId()), userId);
                User user = userService.getUser(sceneInfo.getUserId());
                Map<String, Object> map = new HashMap<>();
                map.put("scene", sceneInfo);
                map.put("user", user);
                single.setObject(map);
            }else if(single.getType() == 2){
                single.setObject(albumService.getSimpleInfo(single.getId(), userId));
            }
        }

        return new PageInfo<>(list);
    }

    /**
     * 完善场景信息
     * @param sceneInfo
     * @param userId
     * @return
     */
    public SceneInfo addSceneInfo(SceneInfo sceneInfo, Integer userId){
            SceneImage sceneImage = sceneImageDao.getMainImage(sceneInfo.getId());
            if(sceneImage != null) {
                sceneInfo.setMainImage(ossService.getSimpleUrl(OssPathConfig.getSceneImagePath(sceneImage.getImageName())));
            }
            if(sceneInfo.getSceneTypeId() != null && sceneInfo.getSceneTypeId() != 0) {
                sceneInfo.setSceneType(sceneTypeDao.get(sceneInfo.getSceneTypeId()).getSceneTypeName());
            }
            if(userId != null) {
                sceneInfo.setUserCollection(userCollectionService.checkCollection(userId, 1, sceneInfo.getId()));
            }
        return sceneInfo;
    }

    /**
     * 完善场景信息
     * @param sceneInfos
     * @param userId
     * @return
     */
    public List<SceneInfo> addSceneInfo(List<SceneInfo> sceneInfos, Integer userId){
        for(SceneInfo sceneInfo : sceneInfos){
            SceneImage sceneImage = sceneImageDao.getMainImage(sceneInfo.getId());
            if(sceneImage!=null) {
                sceneInfo.setMainImage(ossService.getSimpleUrl(OssPathConfig.getSceneImagePath(sceneImage.getImageName())));
            }
            if(sceneInfo.getSceneTypeId() != null && sceneInfo.getSceneTypeId() != 0) {
                sceneInfo.setSceneType(sceneTypeDao.get(sceneInfo.getSceneTypeId()).getSceneTypeName());
            }
            sceneInfo.setUserCollection(userCollectionService.checkCollection(userId, 1, sceneInfo.getId()));
        }
        return sceneInfos;
    }

    /**
     * 完善场景信息
     * @param sceneInfos
     * @return
     */
    public List<SceneInfo> addSceneInfo(List<SceneInfo> sceneInfos){
        for(SceneInfo sceneInfo : sceneInfos){
            SceneImage sceneImage = sceneImageDao.getMainImage(sceneInfo.getId());
            if(sceneImage!=null) {
                sceneInfo.setMainImage(ossService.getSimpleUrl(OssPathConfig.getSceneImagePath(sceneImage.getImageName())));
            }
            if(sceneInfo.getSceneTypeId() != null && sceneInfo.getSceneTypeId() != 0) {
                sceneInfo.setSceneType(sceneTypeDao.get(sceneInfo.getSceneTypeId()).getSceneTypeName());
            }
        }
        return sceneInfos;
    }

    /**
     * 获取某位店主或个人的VR场景列表(按热度降序)
     * @param userId
     * @return
     */
    public List<SceneInfo> getVrSceneListByUser(Integer userId, Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum, pageSize, true);
        return sceneInfoDao.getVrListByUser(userId);
    }


    /**
     * 获取某位店主或个人的场景列表（已上架场景）
     * @param userId
     * @return
     */
    public List<SceneInfo> getSceneListByUser(Integer userId, Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum, pageSize, true);
        return sceneInfoDao.getListByUser(userId, 1);
    }

    /**
     * 获取店铺的主推场景
     * @param userId
     * @return
     */
    public List<SceneInfo> getRecommendScene(Integer userId){
        PageHelper.startPage(1, 3,true);
        return sceneInfoDao.getListByUser(userId, 1);
    }


    /**
     * 获取场景基本信息
     * @param sceneId
     * @return
     */
    public SceneInfo getSceneInfo(Integer sceneId){

        return  sceneInfoDao.get(sceneId);

    }


    /**
     * 获取场景的图片
     * @param sceneId 场景Id
     * @param waterMark 是否加水印
     * @return
     */
    public List<UrlEntity> getSceneImages(Integer sceneId, Boolean waterMark){

        List<SceneImage> images =  sceneImageDao.getImages(sceneId);
        List<UrlEntity> result = new ArrayList<>();
        if (images.isEmpty()){
            return  result;
        }
        for(SceneImage image : images){
            UrlEntity urlEntity = new UrlEntity();
            urlEntity.setFileKey(image.getImageName());
            if(waterMark) {
                urlEntity.setSimpleUrl(ossService.getSimpleWaterMarkUrl(OssPathConfig.getSceneImagePath(image.getImageName())));
                urlEntity.setOriginUrl(ossService.getOriginWaterMarkUrl(OssPathConfig.getSceneImagePath(image.getImageName())));
            }else {
                urlEntity.setSimpleUrl(ossService.getSimpleUrl(OssPathConfig.getSceneImagePath(image.getImageName())));
                urlEntity.setOriginUrl(ossService.getOriginUrl(OssPathConfig.getSceneImagePath(image.getImageName())));
            }
            result.add(urlEntity);
        }

        return result;
    }


    /**
     * 获取场景的主要图片
     * @param sceneId
     * @return
     */
    public String getSceneMainImage(Integer sceneId){

        SceneImage image =  sceneImageDao.getMainImage(sceneId);
        String url = null;
        if(image != null) {
            url = ossService.getSimpleUrl(OssPathConfig.getSceneImagePath(image.getImageName()));
        }
        return url;
    }

    /**
     * 获取场景可以提供的服务
     * @param sceneId
     * @return
     */
    public List<SceneServ> getSceneServiceIcon(Integer sceneId){

        List<SceneServ> sceneServs = sceneServiceDao.getIconBySceneId(sceneId);
        for(SceneServ serv : sceneServs){
            serv.setSceneServiceIcon(ossService.getSimpleUrl(OssPathConfig.getServiceIconPath(serv.getSceneServiceIcon())));
        }

        return sceneServs;
    }

    /**
     * 根据场景Id获取场景主信息
     * @param sceneId
     * @return
     */
    public User getSceneUser(Integer sceneId){
        User user =  userDao.get(sceneInfoDao.get(sceneId).getUserId());
        user.setAvatar(ossService.getSimpleUrl(OssPathConfig.getUserAvatarPath(user.getAvatar())));
        return user;
    }

    /**
     * 根据场景Id获取场景的联系人
     * @param sceneId
     * @return
     */
    public List<Map<String, Object>> getSceneContacts(Integer sceneId){
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        SceneInfo sceneInfo = sceneInfoDao.manageGet(sceneId);
        User user =  userDao.get(sceneInfo.getUserId());
        map.put("name", user.getNickname());
        map.put("phone", user.getPhone());
        list.add(map);
        if(user.getStatus() != 0){ // 如果是店铺
            Integer storeId = storeDao.getByUser(sceneInfo.getUserId()).getId();
            list.addAll(storeService.getOtherContacts(storeId));
        }
        return list;
    }


    /**
     * 获取场景拍摄过的作品
     * @param sceneId
     * @return
     */
    public Map<String, Object> getSceneWorks(Integer sceneId){

        SceneWorks sceneWorks =  sceneWorksDao.getWorksBySceneId(sceneId);
        if(sceneWorks == null){
            sceneWorks = new SceneWorks();
            sceneWorks.setWorksDesc("");
        }
        Map result = new HashMap<>();
        result.put("worksDesc", sceneWorks.getWorksDesc());
        List list = new ArrayList();
        List<String> images = sceneWorksImageDao.getList(sceneId);
        for(String name : images){
            list.add(new UrlEntity(name, ossService.getSimpleUrl(OssPathConfig.getSceneWorksPath(name))));
        }
        result.put("worksImage", list);
        return result;
    }

    /**
     * 获取热门的场景
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = SQLException.class)
    public PageInfo<SceneInfo> getHotScenes(Integer userId, Integer pageNum, Integer pageSize){

        PageHelper.startPage(pageNum, pageSize,true);
        List<SceneInfo> list = sceneInfoDao.getHotScene();
        if(userId != null){
            for(SceneInfo info : list){
                info.setUserCollection(userCollectionService.checkCollection(userId,1, info.getId()));
            }
        }
        return new PageInfo<>(addSceneInfo(list));
    }


    /**
     * 获取最新发布的场景
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = SQLException.class)
    public PageInfo<SceneInfo> getNewScenes(Integer userId, Integer pageNum, Integer pageSize){

        PageHelper.startPage(pageNum, pageSize,true);
        List<SceneInfo> list = sceneInfoDao.getListByTime();
        for(SceneInfo scene : list){
            scene.setUserCollection(userCollectionService.checkCollection(userId, 1, scene.getId()));
            SceneImage sceneImage = sceneImageDao.getMainImage(scene.getId());
            scene.setMainImage(ossService.getSimpleUrl(OssPathConfig.getSceneImagePath(sceneImage.getImageName())));
            scene.setSceneType(sceneTypeDao.get(scene.getSceneTypeId()).getSceneTypeName());
        }

        return new PageInfo<>(list);
    }


    /**
     * 根据场景信息获取店铺信息
     * @param sceneId
     * @return
     */
    public Store getStore(Integer sceneId) {
        return storeDao.getByUser(sceneInfoDao.get(sceneId).getUserId());
    }


    /**
     * 获取VR列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageInfo getVrList(Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum, pageSize, true);
        List<SceneInfo> sceneInfos = sceneInfoDao.getVrList();
        for(SceneInfo infos : sceneInfos){
            infos.setMainImage(sceneService.getSceneMainImage(infos.getId()));
        }
        return new PageInfo(sceneInfos);
    }


    /**
     * 获取首页主推场景
     * @return
     */
    public List<SceneInfo> getHomeRecommendScene(Integer userId,  Integer pageNum,  Integer pageSize){
        List<SceneInfo> result = new ArrayList<>();
        List<Integer> list = sceneInfoDao.getRecommendScene();
        for(Integer id : list){
            SceneInfo sceneInfo = sceneInfoDao.get(id);
            sceneInfo.setTop(true);
            result.add(sceneInfo);
        }
        result = addSceneInfo(result, userId);
        if(result.size() < 15){
            PageInfo<SceneInfo> pageInfo = getHotScenes(userId, 1, 15-result.size());
            result.addAll(pageInfo.getList());
        }
        return result;
    }


    /**
     * 更新场景热度
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateSceneHot(){/*
        List<ApiLog> list = apiLogDao.getByRequestUrl("/scene/info/get");
        Map<Integer, Integer> map = new HashMap();
        for(ApiLog log : list){
            String param = log.getParam();
            if(param != null && StringUtils.isNotEmpty(param)){
                param = param.substring(param.indexOf("sceneId=") + 8);
                param = param.substring(0, param.indexOf('\t'));
                Integer num = Integer.parseInt(param);
                if(map.get(num) != null){
                    map.put(num, map.get(num) + 1);
                }else {
                    map.put(num, 1);
                }
            }
        }
        List<SceneInfo> sceneInfos = sceneInfoDao.getListByTime();
        for(SceneInfo info : sceneInfos){
            if(map.get(info.getId()) != null) {
                // 原始热度
                Integer a = info.getHot();
                // 增量
                Integer b = 200 * map.get(info.getId()) / list.size();
                // 时间衰减量
                Integer c = 0;
                if(a > 30 && a < 80) {
                    c = -2;
                }else if(a > 80){
                    c = -4;
                }
                Integer hot = a + b + c;
                if(hot > 100){
                    hot = 100;
                }else if(hot < 10){
                    hot = 10;
                }
                info.setHot(hot);
            }
            sceneInfoDao.update(info);
        }
*/
        List<SceneInfo> sceneInfos = sceneInfoDao.getListByTime();
        Random random = new Random();
        for(SceneInfo info : sceneInfos){
            info.setHot(random.nextInt(101));
            sceneInfoDao.update(info);
        }
    }

    /**
     * 获取指定范围内的场景坐标
     * @param topLon
     * @param topLat
     * @param bottomLon
     * @param bottomLat
     */
    public List<SceneMap> getSceneMap(Double topLon, Double topLat, Double bottomLon, Double bottomLat) {

       return sceneInfoDao.getSceneMap(topLon, topLat, bottomLon, bottomLat);
    }


    /**
     * 获取指定点附近的场景
     * @param lon
     * @param lat
     */
    public List<SearchResult> getMapNearestScene(Integer userId, Double lon, Double lat, Integer count) {
        List<SearchResult> results = new ArrayList<>();
        PageHelper.startPage(1, count, true);
        List<SceneMap> list = sceneInfoDao.getNearestScene(lon, lat);
        for(SceneMap sceneMap : list){
            if(sceneMap.getType() == 1){ // 专业场景
                SearchResult searchResult = new SearchResult();
                searchResult.setType(1);
                searchResult.setObject(addSceneInfo(sceneInfoDao.get(sceneMap.getId()), userId));
                results.add(searchResult);
            }else if (sceneMap.getType() == 2){ // 图集
                SearchResult searchResult = new SearchResult();
                searchResult.setType(2);
                searchResult.setObject(albumService.getSimpleInfo(sceneMap.getId(), userId));
                results.add(searchResult);
            }
        }
        return results;
    }


    /**
     * 获取指定坐标的场景(包括图集信息)
     * @param lon
     * @param lat
     */
    public List getMapSceneInfo(Integer userId, Double lon, Double lat) {

        List<Map> result = new ArrayList<>();
        List<SceneMap> listMap = sceneInfoDao.getLonLatScene(lon, lat);
        for(SceneMap sceneMap : listMap){
            Map<String, Object> map = new HashMap<>();
            map.put("type", sceneMap.getType());
            if(sceneMap.getType() == 1){ // 专业场景
                map.put("object", addSceneInfo(sceneInfoDao.get(sceneMap.getId()), userId));
            }else if (sceneMap.getType() == 2){ // 图集
                map.put("object", albumService.getSimpleInfo(sceneMap.getId(), userId));
            }
            result.add(map);
        }

        return result;
    }

    /**
     * 统计场景总数与城市总数
     * @return
     */
    public Map<String, Integer> countCityAndScene() {
        Map<String, Integer> map = new HashMap<>();
        Integer albumNum = albumDao.countAlbum();
        Integer sceneNum = sceneInfoDao.countScene(null);
        List<String> list = sceneInfoDao.selectCity();
        map.put("city", list.size() + 5 + 10);
        map.put("total", albumNum + sceneNum + 3000);
        return map;
    }

}
