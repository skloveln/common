package cn.zpc.mvc.store.service;

import cn.zpc.common.entity.UrlEntity;
import cn.zpc.common.handler.exception.GlobalExceptionResult;
import cn.zpc.common.plugins.oss.OssPathConfig;
import cn.zpc.common.serivce.BaseService;
import cn.zpc.common.utils.StringUtils;
import cn.zpc.mvc.scene.dao.SceneImageDao;
import cn.zpc.mvc.scene.dao.SceneInfoDao;
import cn.zpc.mvc.scene.dao.SceneServiceDao;
import cn.zpc.mvc.scene.dao.SceneTypeDao;
import cn.zpc.mvc.scene.entity.SceneInfo;
import cn.zpc.mvc.scene.entity.SceneServ;
import cn.zpc.mvc.scene.service.SceneService;
import cn.zpc.mvc.store.dao.*;
import cn.zpc.mvc.store.entity.*;
import cn.zpc.mvc.sys.dao.ApiLogDao;
import cn.zpc.mvc.sys.entity.ApiLog;
import cn.zpc.mvc.user.dao.UserDao;
import cn.zpc.mvc.user.entity.User;
import cn.zpc.mvc.user.service.UserCollectionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.*;

@Service
public class StoreService extends BaseService{

    @Autowired
    private SceneImageDao sceneImageDao;
    @Autowired
    private StoreContactsDao storeContactsDao;
    @Autowired
    private StoreDao storeDao;
    @Autowired
    private SceneService sceneService;
    @Autowired
    private SceneInfoDao sceneInfoDao;
    @Autowired
    private StorePeripheryDao storePeripheryDao;
    @Autowired
    private UserCollectionService userCollectionService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private SceneTypeDao sceneTypeDao;
    @Autowired
    private StoreTitbitsDao storeTitbitsDao;
    @Autowired
    private StoreTitbitsImageDao storeTitbitsImageDao;
    @Autowired
    private StoreServicesDao storeServicesDao;
    @Autowired
    private StoreServicesImageDao storeServicesImageDao;
    @Autowired
    private StoreAdvertDao storeAdvertDao;
    @Autowired
    private StoreServicesDescDao storeServicesDescDao;
    @Autowired
    private SceneServiceDao sceneServiceDao;
    @Autowired
    private StoreFriendlyDao storeFriendlyDao;
    @Autowired
    private StoreComplainDao storeComplainDao;
    @Autowired
    private ApiLogDao apiLogDao;

    /**
     * 检验店铺是否存在
     * @return
     */
    public Boolean checkStoreExist(Integer storeId){

        return storeDao.get(storeId) != null;
    }


    /**
     * 获取指定店铺的基本信息
     * @param storeId
     * @return
     */
    public Store getStoreInfo(Integer storeId){
        Store store = storeDao.get(storeId);
        if(store.getType() != 1) {
            store.setLogo(ossService.getSimpleUrl(OssPathConfig.getStoreLogoPath(store.getLogo())));
        }else{
            User user = userDao.get(store.getUserId());
            store.setLogo(ossService.getSimpleUrl(OssPathConfig.getUserAvatarPath(user.getAvatar())));
        }
        store.setMainImage(ossService.getSimpleUrl(OssPathConfig.getStoreImagePath(store.getMainImage())));
        int sceneSize = sceneService.getSceneListByUser(store.getUserId(), 0,0).size();
        store.setSceneSize(sceneSize);
       return store;
    }

    /**
     * 根据用户Id获取店铺信息
     * @param userId 查看谁的店铺
     * @param isSelf 是不是本人查看
     * @return
     */
    public Store getStoreInfoByUser(Integer userId, Boolean isSelf){
        Store store = storeDao.getByUser(userId);
        if(store != null && (store.getStatus() == 0 || isSelf)) {
            if (store.getType() != 1) {
                store.setLogo(ossService.getSimpleUrl(OssPathConfig.getStoreLogoPath(store.getLogo())));
            } else {
                User user = userDao.get(store.getUserId());
                store.setLogo(ossService.getSimpleUrl(OssPathConfig.getUserAvatarPath(user.getAvatar())));
            }
            store.setMainImage(ossService.getSimpleUrl(OssPathConfig.getStoreImagePath(store.getMainImage())));
        }else {
            return null;
        }
        return store;
    }

    /**
     * 获取店铺的广告图
     * @param storeId
     */
    public String getAdvertPic(Integer storeId){
        String image = storeAdvertDao.getImage(storeId);
        return ossService.getSimpleUrl(OssPathConfig.getStoreAdvertPath(image));
    }

    /**
     * 获取店铺的VR场景
     * @param storeId 店铺的Id
     * @return
     */
    public List<Map> getVrInfo(Integer storeId, Integer pageNum, Integer pageSize) {
        List<Map> result = new ArrayList<>();
        if(!checkStoreExist(storeId)){
            return result;
        }
        Integer userId = storeDao.get(storeId).getUserId();
        List<SceneInfo> scenes = sceneService.getVrSceneListByUser(userId, pageNum, pageSize);
        for(SceneInfo scene : scenes){
            Map<String, Object> map = new HashMap<>();
            map.put("url", scene.getVrUrl());
            map.put("sceneName", scene.getSceneName());
            map.put("mainImage", ossService.getSimpleUrl(OssPathConfig.getSceneImagePath(sceneImageDao.getMainImage(scene.getId()).getImageName())));
            result.add(map);
        }
        return result;
    }


    /**
     * 获取店铺的所有场景
     * @param storeId 店铺的Id
     * @return
     */
    public List<SceneInfo> getSceneInfo(Integer storeId, Integer looker, Integer pageNum, Integer pageSize) {
        List<SceneInfo> result = new ArrayList<>();
        if(!checkStoreExist(storeId)){
            return result;
        }
        Integer userId = storeDao.get(storeId).getUserId();
        result = sceneService.getSceneListByUser(userId, pageNum, pageSize);
        for(SceneInfo sceneInfo :  result){
            if(looker != null) {
                sceneInfo.setUserCollection(userCollectionService.checkCollection(looker, 1, sceneInfo.getId()));
            }
            sceneInfo.setMainImage(sceneService.getSceneMainImage(sceneInfo.getId()));
            sceneInfo.setSceneType(sceneTypeDao.get(sceneInfo.getSceneTypeId()).getSceneTypeName());
        }
        return result;
    }


    /**
     * 获取店铺的主推场景
     * @param storeId 店铺的Id
     * @return
     */
    public List<SceneInfo> getRecommendScene(Integer storeId) {
        Integer userId = storeDao.get(storeId).getUserId();
        List<SceneInfo> result = sceneService.getRecommendScene(userId);
        for(SceneInfo sceneInfo :  result){
            sceneInfo.setMainImage(sceneService.getSceneMainImage(sceneInfo.getId()));
            sceneInfo.setSceneType(sceneTypeDao.get(sceneInfo.getSceneTypeId()).getSceneTypeName());
        }
        return result;
    }

    /**
     * 得到店铺所有场景的标签
     * @param storeId
     * @return
     */
    public Set<String> getSceneTags(Integer storeId){
        Set<String> result = new TreeSet<>();
        Integer userId = storeDao.get(storeId).getUserId();
        List<SceneInfo> sceneInfos = sceneService.getSceneListByUser(userId, 0, 0);
        for(SceneInfo scene: sceneInfos){
            result.addAll(scene.getTagArray());
        }
        return result;
    }


    /**
     * 获取某店铺的所有场景类型
     * @param storeId
     * @return
     */
    public Set<String> getAllSceneTypes(Integer storeId){
        Set<String> set = new HashSet<>();
        Integer storekeeperId = storeDao.get(storeId).getUserId();
        List<SceneInfo> sceneInfoList = sceneService.getSceneListByUser(storekeeperId, 0, 0);
        for(SceneInfo sceneInfo : sceneInfoList){
            set.add(sceneTypeDao.get(sceneInfo.getSceneTypeId()).getSceneTypeName());
        }
        return set;
    }


    /**
     * 获取热门店铺
     * @return
     */
    @Transactional(rollbackFor = SQLException.class)
    public PageInfo getHotStores(Integer userId, Integer pageNum, Integer pageSize) {
        List<Map> result = new ArrayList<>();
        PageHelper.startPage(pageNum, pageSize,true);
        List<Store> stores = storeDao.getHotStore();
        PageInfo pageInfo = new PageInfo(stores);
        for(Store store : stores){
            Map<String, Object> map = new TreeMap<>();
            map.put("storeId", store.getId());
            map.put("storeName", store.getName());
            map.put("storeAddress", store.getAddress());
            map.put("storeImage", ossService.getSimpleUrl(OssPathConfig.getStoreImagePath(store.getMainImage())));
            map.put("storekeeper", userDao.get(store.getUserId()).getNickname());
            map.put("sceneTypes", getAllSceneTypes(store.getId()));
            map.put("storeType", store.getType());
            map.put("storeHot", store.getHot());
            if(userId != null){
                map.put("storeCollect", userCollectionService.checkCollection(userId, 0, store.getId()));
            }
            map.put("recommendScene", getRecommendScene(store.getId()));
            result.add(map);
        }
        pageInfo.setList(result);
        return pageInfo;
    }


    /**
     * 获取更多热门店铺
     * @return
     */
    @Deprecated
    @Transactional(rollbackFor = SQLException.class)
    public List<Store> getHotStoresMore(Integer userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize,false);
        List<Store> stores = storeDao.getHotStore();
        for(Store store : stores){
            store.setMainImage(ossService.getSimpleUrl(OssPathConfig.getStoreImagePath(store.getMainImage())));
            store.setSceneTypes(getAllSceneTypes(store.getId()));
            if(userId != null){
                store.setUserCollect(userCollectionService.checkCollection(userId, 0, store.getId()));
            }
            store.setAvatar(null);
            store.setUserId(null);
            store.setCreateTime(null);
            store.setLogo(null);
            store.setHot(null);
            store.setStatus(null);
        }
        return stores;
    }

    /**
     * 获取更多新店铺
     * @return
     */
    @Transactional(rollbackFor = SQLException.class)
    public PageInfo getNewStoreMore(Integer userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize,true);
        List<Store> stores = storeDao.getNewList();
        for(Store store : stores){
            store.setMainImage(ossService.getSimpleUrl(OssPathConfig.getStoreImagePath(store.getMainImage())));
            if(userId != null){
                store.setUserCollect(userCollectionService.checkCollection(userId, 0, store.getId()));
            }
            store.setSceneTypes(getAllSceneTypes(store.getId()));
        }
        return new PageInfo(stores);
    }

    /**
     * 获取最新店铺列表
     * @return
     */
    @Transactional(rollbackFor = SQLException.class)
    public List<Map> getNewSceneStores(Integer pageNum, Integer pageSize) {
        List<Map> result = new ArrayList<>();
        PageHelper.startPage(pageNum, pageSize,false);
        List<SceneInfo> sceneInfos = sceneInfoDao.getNewSceneGroupUser();
        for(SceneInfo info : sceneInfos){
            Store store = storeDao.getByUser(info.getUserId());
            Map<String, Object> map = new HashMap<>();
            map.put("id", store.getId());
            map.put("name", store.getName());
            if(store.getType() == 1) {
                map.put("logo", ossService.getSimpleUrl(OssPathConfig.getUserAvatarPath(userDao.get(info.getUserId()).getAvatar())));
            }else {
                map.put("logo", ossService.getSimpleUrl(OssPathConfig.getStoreLogoPath(store.getLogo())));
            }
            map.put("avatar", ossService.getSimpleUrl(OssPathConfig.getUserAvatarPath(userDao.get(info.getUserId()).getAvatar())));
            map.put("image", ossService.getSimpleUrl(OssPathConfig.getSceneImagePath(sceneImageDao.getMainImage(info.getId()).getImageName())));
            map.put("province", store.getProvince());
            map.put("city", store.getCity());
            map.put("sceneId", info.getId());
            map.put("type", store.getType());
            result.add(map);
        }
        return result;
    }

    /**
     * 获取店铺可以提供的服务
     * @param storeId
     * @return
     */
    @Deprecated
    public Map getStoreServices(Integer storeId){
        Map<String, Object> result = new HashMap<>();
        result.put("Info", getServicesInfo(storeId));
        result.put("image", getServiceImages(storeId));
        return result;
    }

    /**
     * 获取店铺可以提供的服务
     * @param storeId
     * @return
     */
    public Map getStoreHomeServices(Integer storeId){
        Map<String, Object> result = new HashMap<>();
        StoreServicesDesc desc = storeServicesDescDao.getServices(storeId);
        if(desc != null){
            List<Integer> ids = StringUtils.splitInteger(desc.getGeneral());
            List<String> strs = new ArrayList<>();
            for(Integer id : ids){
                strs.add(sceneServiceDao.get(id).getSceneServiceName());
            }
            result.put("general",strs);
            String descStr = "";
            if(desc.getDesc() != null){
                descStr = desc.getDesc();
            }
            result.put("desc", descStr);
        }
        return result;
    }

    /**
     * 获取店铺可以提供的服务
     * @param storeId
     * @return
     */
    public Map getStoreService(Integer storeId){
        Map<String, Object> result = new HashMap<>();
        StoreServicesDesc desc = storeServicesDescDao.getServices(storeId);
        if(desc != null){
            // 通用图标
            List<Integer> ids = StringUtils.splitInteger(desc.getGeneral());
            List<Map> strs = new ArrayList<>();
            for(Integer id : ids){
                Map<String, Object> map = new HashMap<>();
                SceneServ serv = sceneServiceDao.get(id);
                map.put("name", serv.getSceneServiceName());
                map.put("icon", ossService.getSimpleUrl(OssPathConfig.getServiceIconPath(serv.getSceneServiceIcon())));
                strs.add(map);
            }
            // 食住行图标
            List<Integer> extids = StringUtils.splitInteger(desc.getExt());
            List<Integer> s = new ArrayList<>();
            List<SceneServ> servs = sceneServiceDao.getFoodResource();
            for(SceneServ ser : servs){
                s.add(ser.getTypeId());
            }
            List<Map> spc = new ArrayList<>();
            for(Integer id : s){
                Map<String, Object> map = new HashMap<>();
                SceneServ serv = sceneServiceDao.get(id);
                map.put("name", serv.getSceneServiceName());
                if(extids.contains(id)) {
                    map.put("icon", ossService.getSimpleUrl(OssPathConfig.getServiceIconPath(serv.getSceneServiceIcon())));
                }else{
                    map.put("icon", ossService.getSimpleUrl(OssPathConfig.getServiceIconPath("no_" + serv.getSceneServiceIcon())));
                }
                spc.add(map);
            }
            result.put("general", strs);
            result.put("specific", spc);
            result.put("desc", desc.getDesc());
        }

        result.put("image", getServiceImages(storeId));
        return result;
    }


    /**
     * 获取店铺周边场景列表
     * @param storeId
     * @return
     */
    public PageInfo getStorePeriphery(Integer storeId, Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum, pageSize, true);
        List<StorePeriphery> list = storePeripheryDao.getListByStoreId(storeId);
        for(StorePeriphery storePeriphery : list){
            // 获取图片集
            List<String> images = storePeripheryDao.getPeripheryImages(storePeriphery.getId());
            List urlList = new ArrayList();
            for(String imageName: images){
                String simpleUrl = ossService.getSimpleUrl(OssPathConfig.getStorePeripheryPath(imageName));
                urlList.add(new UrlEntity(imageName, simpleUrl));
            }
            storePeriphery.setImageList(urlList);
        }

        return new PageInfo(list);
    }

    /**
     * 获取商铺友情商户
     * @param storeId
     * @return
     */
    public List getFriendlyStore(Integer storeId){
        List result = new ArrayList();
        String ids = storeFriendlyDao.getFriendly(storeId);
        List<Integer> list = StringUtils.splitInteger(ids);
        for(Integer id : list){
            Map map = new HashMap();
            Store store = storeDao.get(id);
            map.put("logo", ossService.getSimpleUrl(OssPathConfig.getStoreLogoPath(store.getLogo())));
            map.put("name", store.getName());
            map.put("id", store.getId());
            result.add(map);
        }
        return result;
    }


    /**
     * 获取某一个周边场景信息
     * @return
     */
    public StorePeriphery getSinglePeriphery(Integer peripheryId){
        StorePeriphery periphery = storePeripheryDao.get(peripheryId);
        if(periphery != null){
            List<String> images = storePeripheryDao.getPeripheryImages(peripheryId);
            List urlList = new ArrayList();
            for(String imageName: images){
                String simpleUrl = ossService.getSimpleUrl(OssPathConfig.getStorePeripheryPath(imageName));
                urlList.add(new UrlEntity(imageName, simpleUrl));
            }
            periphery.setImageList(urlList);
        }
        return periphery;
    }


    /**
     * 根据场景Id获取场景的联系人
     * @param storeId
     * @return
     */
    public List<Map<String, Object>> getStoreContacts(Integer storeId){
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        Store store = storeDao.get(storeId);
        User user =  userDao.get(store.getUserId());
        List contacts = getOtherContacts(storeId);
        list.addAll(contacts);
        if(contacts.size() == 0){
            map.put("name", user.getNickname());
            map.put("phone", user.getPhone());
            list.add(map);
        }
        return list;
    }

    /**
     * 获取店铺投诉客户电话
     */
    public Map<String, Object> getStoreComplain(Integer storeId){
        Map<String, Object> map = new HashMap<>();
        StoreContacts contacts = storeComplainDao.getComplain(storeId);
        if(contacts != null) {
            map.put("name", contacts.getName());
            map.put("phone", contacts.getPhone());
        }

        return map;
    }

    /**
     * 获取店铺的更多联系人
     * @param storeId
     * @return
     */
    public List getOtherContacts(Integer storeId){
        List list = new ArrayList();
        List<StoreContacts> contacts = storeContactsDao.getListByStoreId(storeId);
        for(StoreContacts storeContacts : contacts){
            Map map = new HashMap<>();
            map.put("name", storeContacts.getName());
            map.put("phone", storeContacts.getPhone());
            list.add(map);
        }
        return list;
    }


    /**
     * 获取店主的信息
     * @param storeId
     * @return
     */

    public User getStoreUser(Integer storeId){
        User user = userDao.get(storeDao.get(storeId).getUserId());
        user.setAvatar(ossService.getSimpleUrl(OssPathConfig.getUserAvatarPath(user.getAvatar())));

        return user;
    }


    /**
     * 获取店铺的花絮列表
     * @param storeId
     * @return
     */
    public PageInfo getTitbitsList(Integer storeId, Integer pageNum, Integer pageSize) {
        // 检测店铺是否存在
        if(!checkStoreExist(storeId)){
            throw new GlobalExceptionResult("store.notExist", 1002);
        }
        List<Map> result = new ArrayList();
        PageHelper.startPage(pageNum, pageSize, true);
        for(StoreTitbits titbits : storeTitbitsDao.getList(storeId)){
            result.add(getTitbits(titbits.getId(), storeId));
        }
        return new PageInfo(result);
    }

    /**
     * 获取某一花絮信息
     * @param id
     * @param storeId
     */
    @Transactional(rollbackFor = SQLException.class)
    public Map getTitbits(Integer id, Integer storeId) {
        Map<String, Object> map = new HashMap<>();
        StoreTitbits storeTitbits = storeTitbitsDao.get(id);
        if(storeTitbits.getStoreId().equals(storeId)){
            List<StoreTitbitsImage> images = storeTitbitsImageDao.getList(id);
            List list = new ArrayList();
            for(StoreTitbitsImage image : images){
                String name = image.getImageName();
                String simple = ossService.getSimpleUrl(OssPathConfig.getStoreTitbitsPath(name));
                String origin = ossService.getOriginUrl(OssPathConfig.getStoreTitbitsPath(name));
                list.add(new UrlEntity(name, simple, origin));
            }
            map.put("info", storeTitbits);
            map.put("images", list);
        }else{
            throw new GlobalExceptionResult("titbits.notExist", 1002);
        }
        return map;
    }

    /**
     * 获取服务图片集
     * @param storeId
     * @return
     */
    public List getServiceImages(Integer storeId){
        List result = new LinkedList();
        List<StoreServicesImage> images = storeServicesImageDao.getImagesByStoreId(storeId);
        for(StoreServicesImage image : images){
            String fileKey = image.getImageName();
            String simleUrl = ossService.getSimpleUrl(OssPathConfig.getStoreServicesPath(fileKey));
            String originUrl = ossService.getOriginUrl(OssPathConfig.getStoreServicesPath(fileKey));
            result.add(new UrlEntity(fileKey, simleUrl, originUrl));
        }
        return result;
    }


    /**
     * 获取店铺服务
     * @param storeId
     * @return
     */
    @Deprecated
    public StoreServices getServicesInfo(Integer storeId){
        return storeServicesDao.getByStoreId(storeId);
    }


    /**
     * 保存店铺服务信息
     * @param storeId
     * @param eat
     * @param stay
     * @param trip
     * @param desc
     * @return
     */
    @Deprecated
    @Transactional(rollbackFor = SQLException.class)
    public void saveServicesInfo(Integer storeId, String eat, String stay, String trip, String desc){
        StoreServices services = new StoreServices();
        if(StringUtils.isNotEmpty(eat)) {
            services.setEat(eat);
        }
        if(StringUtils.isNotEmpty(stay)) {
            services.setStay(stay);
        }
        if(StringUtils.isNotEmpty(trip)){
            services.setTrip(trip);
        }
        if(StringUtils.isNotEmpty(desc)){
            services.setDesc(desc);
        }
        services.setStoreId(storeId);

        if(getServicesInfo(storeId) == null){
            storeServicesDao.insert(services);
        }else{
            storeServicesDao.update(services);
        }
    }


    /**
     * 更新店铺热度
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStoreHot(){
/*
        List<ApiLog> list = apiLogDao.getByRequestUrl("/store/info/home");
        Map<Integer, Integer> map = new HashMap();
        for(ApiLog log : list){
            String param = log.getParam();
            if(param != null && StringUtils.isNotEmpty(param)){
                param = param.substring(param.indexOf("storeId=") + 8);
                param = param.substring(0, param.indexOf('\t'));
                Integer num = Integer.parseInt(param);
                if(map.get(num) != null){
                    map.put(num, map.get(num) + 1);
                }else {
                    map.put(num, 1);
                }
            }
        }
        List<Store> stores = storeDao.getNewList();
        for(Store info : stores){
            if(map.get(info.getId()) != null) {
                // 原始热度
                Integer a = info.getHot();
                // 增量
                Integer b = 300 * map.get(info.getId()) / list.size();
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
            storeDao.update(info);
        }
*/
        List<Store> stores = storeDao.getNewList();
        Random random = new Random();
        for(Store info : stores){
            info.setHot(random.nextInt(101));
            storeDao.update(info);
        }
    }
}
