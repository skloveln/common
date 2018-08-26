package cn.zpc.mvc.store.service;

import cn.zpc.common.handler.exception.GlobalExceptionResult;
import cn.zpc.common.plugins.oss.OssPathConfig;
import cn.zpc.common.serivce.BaseService;
import cn.zpc.common.utils.StringUtils;
import cn.zpc.common.web.result.MessageResult;
import cn.zpc.common.web.result.Result;
import cn.zpc.mvc.scene.dao.SceneInfoDao;
import cn.zpc.mvc.scene.dao.SceneServiceDao;
import cn.zpc.mvc.scene.entity.SceneServ;
import cn.zpc.mvc.store.dao.*;
import cn.zpc.mvc.store.entity.*;
import cn.zpc.mvc.store.param.StoreInfoParam;
import cn.zpc.mvc.sys.dao.CallLogDao;
import cn.zpc.mvc.sys.entity.CallLog;
import cn.zpc.mvc.user.dao.UserCollectionDao;
import cn.zpc.mvc.user.dao.UserDao;
import cn.zpc.mvc.user.entity.User;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class StoreManageService extends BaseService{

    @Autowired
    private StoreDao storeDao;
    @Autowired
    private StoreContactsDao storeContactsDao;
    @Autowired
    private StorePeripheryDao storePeripheryDao;
    @Autowired
    private StoreTitbitsDao storeTitbitsDao;
    @Autowired
    private StoreTitbitsImageDao storeTitbitsImageDao;
    @Autowired
    private CallLogDao callLogDao;
    @Autowired
    private StoreServicesImageDao storeServicesImageDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserCollectionDao userCollectionDao;
    @Autowired
    private StoreFriendlyDao storeFriendlyDao;
    @Autowired
    private SceneServiceDao sceneServiceDao;
    @Autowired
    private StoreServicesDescDao storeServicesDescDao;
    @Autowired
    private StoreService storeService;
    @Autowired
    private StoreAdvertTemplateDao storeAdvertTemplateDao;
    @Autowired
    private StoreAdvertDao storeAdvertDao;
    @Autowired
    private StoreComplainDao storeComplainDao;
    @Resource
    private SceneInfoDao sceneInfoDao;

    /**
     * 更新店铺信息
     * @param storeInfoParam
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = SQLException.class)
    public Result updateStoreInfo(StoreInfoParam storeInfoParam, Integer userId){
        Store store = storeInfoParam.getStore();
        Store ago = storeDao.getByUser(userId);
        Integer storeId = ago.getId();
        store.setId(storeId);
        store.setUserId(userId);
        if(new Date().before(ago.getExpireTime()) &&  sceneInfoDao.getListByUser(userId, 1).size() > 0) {
            store.setStatus(0); // 店铺状态开业
        }else {
            store.setStatus(1); // 店铺不开业
        }
        storeDao.update(store);

        List<StoreContacts> contacts = storeContactsDao.getListByStoreId(storeId);
        for(StoreContacts storeContacts : contacts){
            storeContactsDao.delete(storeContacts.getId());
        }
        if(storeInfoParam.getPhone1() != null && !storeInfoParam.getPhone1().isEmpty()){
            storeContactsDao.insert(new StoreContacts(storeId, storeInfoParam.getContracts1(), storeInfoParam.getPhone1()));
        }
        if(storeInfoParam.getPhone2() != null && !storeInfoParam.getPhone2().isEmpty()){
            storeContactsDao.insert(new StoreContacts(storeId, storeInfoParam.getContracts2(), storeInfoParam.getPhone2()));
        }
        if(StringUtils.isNotEmpty(storeInfoParam.getPhone3()) && storeComplainDao.getComplain(storeId) != null){
            storeComplainDao.insert(new StoreContacts(storeId, storeInfoParam.getComplain(), storeInfoParam.getPhone3()));
        }

        return new MessageResult();
    }


    /**
     * 新增周边资料
     * @param storeId 店铺ID
     * @param fileKeys 文件名字符串
     * @param desc 周边描述
     */
    @Transactional(rollbackFor = SQLException.class)
    public void addPeriphery(Integer storeId, String fileKeys, String desc){
        // 增加周边
        StorePeriphery periphery = new StorePeriphery();
        periphery.setImageDesc(desc);
        periphery.setStoreId(storeId);
        storePeripheryDao.insert(periphery);
        // 增加图片
        List<String> list = StringUtils.splitString(fileKeys);
        for(String fileKey : list){
            storePeripheryDao.insertImage(periphery.getId(), fileKey);
        }
    }


    /**
     * 编辑周边资料
     * @param peripheryId 周边场景Id
     * @param desc 周边场景描述
     * @return 更新是否成功
     */
    @Transactional(rollbackFor = SQLException.class)
    public Boolean editPeriphery(String savaImages, String deleteImages, Integer peripheryId, String desc){
        List<String> saveList = StringUtils.splitString(savaImages);
        List<String> deleteList = StringUtils.splitString(deleteImages);
        for(String image : saveList){
            storePeripheryDao.insertImage(peripheryId, image);
        }
        for(String str : deleteList){
            storePeripheryDao.deleteImage(str);
        }
        StorePeriphery storePeriphery = storePeripheryDao.get(peripheryId);
        storePeriphery.setImageDesc(desc);
        return storePeripheryDao.update(storePeriphery) > 0;
    }


    /**
     * 删除店铺周边
     * @param peripheryIdStr
     */
    @Transactional(rollbackFor = SQLException.class)
    public void deletePeriphery(String peripheryIdStr, Integer storeId) {
        List<String> strings = StringUtils.splitString(peripheryIdStr);
        for(String str : strings) {
            Integer peripheryId = Integer.parseInt(str);
            StorePeriphery periphery = storePeripheryDao.get(peripheryId);
            if(periphery==null || !periphery.getStoreId().equals(storeId)){
                throw new GlobalExceptionResult("periphery.notExist", 1002);
            }
            storePeripheryDao.delete(peripheryId);
        }
    }




    /**
     * 新增一个花絮
     * @param storeId
     * @param title
     * @param date
     * @param fileKeys
     */
    @Transactional(rollbackFor = SQLException.class)
    public void addTitbits(Integer storeId, String title, Date date, String fileKeys){
        List<String> list = StringUtils.splitString(fileKeys);
        StoreTitbits titbits = new StoreTitbits();
        titbits.setTime(date);
        titbits.setTitle(title);
        titbits.setStoreId(storeId);
        storeTitbitsDao.insert(titbits);
        for(String str : list){
            storeTitbitsImageDao.insert(new StoreTitbitsImage(titbits.getId(), str));
        }
    }


    /**
     * 更新一个花絮
     * @param title
     * @param date
     * @param addFileKeys
     * @param deleteFileKeys
     */
    @Transactional(rollbackFor = Exception.class)
    public void editTitbits(Integer id, String title, Date date, String addFileKeys, String deleteFileKeys){
        List<String> add = StringUtils.splitString(addFileKeys);
        List<String> del = StringUtils.splitString(deleteFileKeys);

        StoreTitbits titbits = storeTitbitsDao.get(id);
        if(titbits == null) {
            throw new GlobalExceptionResult("titbits.notExist", 1002);
        }
        titbits.setTime(date);
        titbits.setTitle(title);
        storeTitbitsDao.update(titbits);
        for(String str : add){
            storeTitbitsImageDao.insert(new StoreTitbitsImage(titbits.getId(), str));
        }
        for(String str : del){
            storeTitbitsImageDao.deleteImage(str, titbits.getId());
        }

    }


    /**
     * 删除某一花絮信息
     * @param idStr
     * @param storeId
     */
    @Transactional(rollbackFor = SQLException.class)
    public void deleteTitbits(String idStr, Integer storeId) {
        List<String> list = StringUtils.splitString(idStr);
        for(String str : list){
            StoreTitbits storeTitbits = storeTitbitsDao.get(Integer.parseInt(str));
            if(storeTitbits.getStoreId().equals(storeId)){
                storeTitbits.setDeleted(true);
                storeTitbitsDao.update(storeTitbits);
                storeTitbitsImageDao.deleteTitbits(storeTitbits.getId());
            }else{
                throw new GlobalExceptionResult("titbits.notExist", 1002, str);
            }
        }
    }

    /**
     * 上传服务图片
     * @param newFile
     * @param storeId
     */
    @Transactional(rollbackFor = SQLException.class)
    public void uploadServicesImage(File newFile, Integer storeId) {

        StoreServicesImage services = new StoreServicesImage();
        services.setStoreId(storeId);
        services.setImageName(newFile.getName());
        storeServicesImageDao.insert(services);
    }


    /**
     * 获取店铺的通话记录
     * @param storeId
     * @return
     */
    public PageInfo getCallList(Integer storeId, Integer userId, Integer pageNum, Integer pageSize) {
        List result = new ArrayList();
        PageHelper.startPage(pageNum, pageSize, true);
        List<CallLog> list = callLogDao.getList(storeId);
        Long total = new PageInfo<>(list).getTotal();
        for(CallLog single : list){
            User user = userDao.get(single.getUserId());
            if(user.getId().equals(userId)){
                continue;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("phone", user.getPhone());
            map.put("nickname", user.getNickname());
            map.put("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(single.getCreateTime()));
            map.put("avatar", ossService.getSimpleUrl(OssPathConfig.getUserAvatarPath(user.getAvatar())));
            result.add(map);
        }
        PageInfo pageInfo = new PageInfo(result);
//        pageInfo.setList(result);
        pageInfo.setTotal(total);
        return pageInfo;
    }


    /**
     * 获取用户推荐店铺信息
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Map getFriendlyStoreList(Integer userId, Integer storeId, Integer pageNum, Integer pageSize){
        // 获取本店铺的友情店铺列表
        String ids = storeFriendlyDao.getFriendly(storeId);
        List<Integer> list = StringUtils.splitInteger(ids);
        // 获取店主店铺收藏列表
        Map<String, Object> result = new HashMap<>();
        PageHelper.startPage(pageNum, pageSize, true);
        List<Store> storeList = userCollectionDao.getStoreList(userId);
        List<Store> orderList = new ArrayList<>();
        //设置店铺的封面图及logo
        for(Store store : storeList){
            store.setMainImage(ossService.getSimpleUrl(OssPathConfig.getStoreImagePath(store.getMainImage())));
            if(list.contains(store.getId())){
                store.setSelected(true);
                orderList.add(store);
            }else {
                store.setSelected(false);
            }
        }
        storeList.removeAll(orderList);
        orderList.addAll(storeList);
        result.put("totalSize", new PageInfo<>(storeList).getTotal());
        result.put("storeInfo", orderList);

        return result;
    }

    /**
     * 编辑友情店铺
     * @param storeId
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void editFriendlyStore(Integer storeId, String ids){
        storeFriendlyDao.delete(storeId);
        storeFriendlyDao.insert(storeId, ids);
    }


    /**
     * 获取店铺服务信息
     * @param storeId
     * @return
     */
    public Map getServicesInfo(Integer storeId){

        Map result = new HashMap();
        StoreServicesDesc desc = storeServicesDescDao.getServices(storeId);
        List<Integer> a = new ArrayList<>();
        List<Integer> b = new ArrayList<>();
        if(desc != null) {
            a = StringUtils.splitInteger(desc.getGeneral());
            b = StringUtils.splitInteger(desc.getExt());
        }
        // 商铺服务
        List<SceneServ> special =  sceneServiceDao.getFoodResource();
        for(SceneServ serv : special){
            serv.setSceneServiceIcon(ossService.getSimpleUrl(OssPathConfig.getServiceIconPath(serv.getSceneServiceIcon())));
            if(b.contains(serv.getTypeId())){
                serv.setSelected(true);
            }else{
                serv.setSelected(false);
            }
        }
        // 通用设施
        List<SceneServ> usual = sceneServiceDao.getResource();
        for(SceneServ serv : usual){
            if(a.contains(serv.getTypeId())){
                serv.setSelected(true);
            }else{
                serv.setSelected(false);
            }
            serv.setSceneServiceIcon(ossService.getSimpleUrl(OssPathConfig.getServiceIconPath(serv.getSceneServiceIcon())));
        }
        // 温馨提示
        String tip = null;
        if(desc != null){
            tip = desc.getDesc();
        }
        // 图片列表
        List images = storeService.getServiceImages(storeId);

        result.put("special",special);
        result.put("usual",usual);
        result.put("tip", tip);
        result.put("images",images);

        return result;
    }


    /**
     * 更新服务信息
     * @param storeId
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateServicesInfo(Integer storeId, String general, String ext, String tip, String fileKeys){

        StoreServicesDesc desc = storeServicesDescDao.getServices(storeId);
        // 更新服务信息
        if(desc == null){
            desc = new StoreServicesDesc();
            desc.setDesc(tip);
            desc.setGeneral(general);
            desc.setExt(ext);
            desc.setStoreId(storeId);
            storeServicesDescDao.insert(desc);
        }else{
            desc.setDesc(tip);
            desc.setGeneral(general);
            desc.setExt(ext);
            storeServicesDescDao.update(desc);
        }
        // 处理图片
        List<String> images = StringUtils.splitString(fileKeys);
        for(String image : images){
            storeServicesImageDao.deleteImage(image, storeId);
        }
    }


    /**
     * 获取店铺广告主题
     * @param storeId
     * @return
     */
    public List<StoreAdvertTemplate> getAdvertSubject(Integer storeId){

        List<StoreAdvertTemplate> list = storeAdvertTemplateDao.getAll();
        String image = storeAdvertDao.getImage(storeId);
        if(StringUtils.isEmpty(image)){
            image = "default_store_ad.jpg";
        }
        for(StoreAdvertTemplate temp : list){
            if(temp.getImage().equals(image)){
                temp.setSelected(true);
            }else{
                temp.setSelected(false);
            }
            temp.setImage(ossService.getSimpleUrl(OssPathConfig.getStoreAdvertPath(temp.getImage())));
        }

        return list;
    }


    /**
     * 编辑店铺广告主题
     * @param storeId
     * @return
     */
    @Transactional(rollbackFor = SQLException.class)
    public void editAdvertSubject(Integer storeId, Integer id){

        StoreAdvertTemplate template = storeAdvertTemplateDao.get(id);
        String image = storeAdvertDao.getImage(storeId);
        if(StringUtils.isEmpty(image)){
            storeAdvertDao.insertAd(storeId, template.getImage());
        }else{
            storeAdvertDao.updateAd(storeId, template.getImage());
        }

    }
}
