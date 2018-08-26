package cn.zpc.mvc.scene.service;

import cn.zpc.common.handler.exception.GlobalExceptionResult;
import cn.zpc.common.plugins.oss.OssPathConfig;
import cn.zpc.common.serivce.BaseService;
import cn.zpc.common.utils.StringUtils;
import cn.zpc.mvc.scene.dao.*;
import cn.zpc.mvc.scene.entity.SceneImage;
import cn.zpc.mvc.scene.entity.SceneInfo;
import cn.zpc.mvc.scene.entity.SceneServ;
import cn.zpc.mvc.scene.entity.SceneWorks;
import cn.zpc.mvc.scene.param.SceneBasicParam;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.*;
import java.util.List;

@Service
public class SceneManageService extends BaseService{

    @Autowired
    private SceneService sceneService;
    @Autowired
    private SceneInfoDao sceneInfoDao;
    @Autowired
    private SceneImageDao sceneImageDao;
    @Autowired
    private SceneTypeDao sceneTypeDao;
    @Autowired
    private SceneServiceDao sceneServiceDao;
    @Autowired
    private SceneWorksDao sceneWorksDao;
    @Autowired
    private SceneWorksImageDao sceneWorksImageDao;

    /**
     * 检查场景图片是否完整性
     * @param sceneId
     * @return
     */
    public Boolean checkImageComplement(Integer sceneId){
        return sceneImageDao.getImages(sceneId).size() > 0 ? true : false;
    }

    /**
     * 检查场景基本信息完整性
     * @param sceneId
     * @return
     */
    public Boolean checkInfoComplement(Integer sceneId){
        SceneInfo sceneInfo = sceneInfoDao.manageGet(sceneId);
        // 场景名称
        if(sceneInfo.getSceneName() == null || sceneInfo.getSceneName().isEmpty()){
            return false;
        }
        // 场景类型
        if(sceneInfo.getSceneTypeId() == null){
            return false;
        }
        // 场景价钱
        if(sceneInfo.getScenePriceType() == null || (sceneInfo.getScenePriceType()!=5 && (sceneInfo.getScenePrice()==null || sceneInfo.getScenePrice()==0))){
            return false;
        }
        // 场景面积
        if(sceneInfo.getSceneArea() == null || sceneInfo.getSceneArea() == 0){
            return false;
        }
        // 场景地址
        if(StringUtils.isEmpty(sceneInfo.getProvince())){
            return false;
        }
        if(StringUtils.isEmpty(sceneInfo.getCity())){
            return false;
        }
        // 场景标签
        if(StringUtils.isEmpty(sceneInfo.getSceneKeyword())){
            return false;
        }
        return true;
    }


    /**
     * 检验场景服务完整性
     * @param sceneId
     * @return
     */
    public Boolean checkServiceComplement(Integer sceneId){
        return sceneServiceDao.getIconBySceneId(sceneId).size() > 0 ? true : false;
    }


    /**
     * 检验场景拍摄作品完整性
     * @param sceneId
     * @return
     */
    public Boolean checkProductionComplement(Integer sceneId){
        SceneWorks sceneWorks = sceneWorksDao.getWorksBySceneId(sceneId);
        if(sceneWorks == null || StringUtils.isEmpty(sceneWorks.getWorksDesc())){
            return false;
        }
        return true;
    }

    /**
     *  检验某场景是否属于该用户
     */
    public Boolean isExist(Integer userId, Integer sceneId){
        return sceneInfoDao.getManage(userId, sceneId) != null ? true : false;
    }


    /**
     * 获取某位店主或个人的场景列表
     * @param userId
     * @return
     */
    public PageInfo<SceneInfo> getStatusList(Integer userId, Integer pageNum, Integer pageSize, Integer status){
        PageHelper.startPage(pageNum, pageSize, true);
        List<SceneInfo> list = sceneInfoDao.getListByUser(userId, status);

        return new PageInfo<>(sceneService.addSceneInfo(list));
    }


    /**
     * 更改场景的状态
     * @param userId
     * @param sceneId
     * @param status
     */
    @Transactional(rollbackFor = SQLException.class)
    public void changeStatus(Integer userId, Integer sceneId, Integer status){

        if(status == 0){
            if( checkImageComplement(sceneId) &&
                checkInfoComplement(sceneId) &&
                checkServiceComplement(sceneId)){
            }else {
                throw new GlobalExceptionResult("scene.notComplete", 1002);
            }
        }

        SceneInfo sceneInfo = sceneInfoDao.getManage(userId, sceneId);
        sceneInfo.setStatus(status);

        sceneInfoDao.update(sceneInfo);
    }


    /**
     * 删除指定场景
     * @param userId
     * @param sceneId
     */
    @Transactional(rollbackFor = SQLException.class)
    public void deleteScene(Integer userId, Integer sceneId){
        SceneInfo sceneInfo = sceneInfoDao.getManage(userId, sceneId);
        sceneInfo.setDeleted(true);
        if(sceneInfo.getStatus() == -1) {
            sceneInfoDao.delete(sceneId);
        }else{
            sceneInfoDao.update(sceneInfo);
        }
    }


    /**
     * 删除某场景指定图片
     * @param userId
     * @param sceneId
     * @param fileKey
     */
    @Transactional(rollbackFor = SQLException.class)
    public void deleteSceneImage(Integer userId, Integer sceneId, String fileKey){
        SceneImage sceneImage = sceneImageDao.getManage(userId, sceneId, fileKey);
        if(sceneImage != null){
            sceneImage.setDeleted(true);
            sceneImageDao.update(sceneImage);
        }
    }


    /**
     * 增加某场景指定图片
     * @param userId
     * @param sceneId
     * @param fileKey
     */
    @Transactional(rollbackFor = SQLException.class)
    public void addSceneImage(Integer userId, Integer sceneId, String fileKey){

        SceneImage sceneImage = new SceneImage();
        sceneImage.setCreateTime(new Date());
        sceneImage.setUserId(userId);
        sceneImage.setSceneId(sceneId);
        sceneImage.setImageName(fileKey);
        sceneImageDao.insert(sceneImage);

        // 改变场景至草稿箱
        SceneInfo sceneInfo = sceneInfoDao.manageGet(sceneId);
        sceneInfo.setStatus(-1);
        sceneInfoDao.update(sceneInfo);
    }


    /**
     * 获取场景信息
     * @param sceneId
     * @return
     */
    @Transactional(rollbackFor = SQLException.class)
    public SceneInfo getBasicInfo(Integer userId, Integer sceneId){
        SceneInfo sceneInfo = sceneInfoDao.getManage(userId, sceneId);
        // 场景类型
        if(sceneInfo.getSceneTypeId() != null && sceneInfo.getSceneTypeId() != 0) {
            sceneInfo.setSceneType(sceneTypeDao.get(sceneInfo.getSceneTypeId()).getSceneTypeName());
        }
        return sceneInfo;
    }


    /**
     * 编辑场景信息
     * @param sceneBasicParam
     * @return
     */
    @Transactional(rollbackFor = SQLException.class)
    public Boolean editBasicInfo(Integer userId, SceneBasicParam sceneBasicParam){

        SceneInfo sceneInfo = sceneBasicParam.getSceneInfo();
        sceneInfo.setUserId(userId);
        sceneInfo.setStatus(-1);
        sceneInfo.setUpdateTime(new Date());
        return sceneInfoDao.update(sceneInfo) > 0;
    }


    /**
     * 新增场景信息
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = SQLException.class)
    public SceneInfo addBasicInfo(Integer userId){

        SceneInfo sceneInfo = new SceneInfo();
        sceneInfo.setUserId(userId);
        sceneInfo.setStatus(-1);
        sceneInfo.setCreateTime(new Date());
        sceneInfoDao.insert(sceneInfo);

        return sceneInfo;
    }


    /**
     * 获取全部场景服务
     * @return
     */
    public List<SceneServ> getAllService(){
        List<SceneServ> sceneServs = sceneServiceDao.getResource();
        for(SceneServ serv : sceneServs){
            serv.setSceneServiceIcon(ossService.getSimpleUrl(OssPathConfig.getServiceIconPath(serv.getSceneServiceIcon())));
        }
        return sceneServs;
    }


    /**
     * 编辑场景服务
     * @param sceneId
     * @param stringList
     */
    @Transactional(rollbackFor = SQLException.class)
    public void editSceneService(Integer sceneId, List<String> stringList) {
        // 删除服务
        sceneServiceDao.deleteService(sceneId);
        // 增加服务
        for(String str : stringList) {
            sceneServiceDao.insertSceneService(sceneId, Integer.parseInt(str));
        }
    }


    /**
     * 编辑场景作品
     * @param sceneId
     * @param worksDesc
     */
    @Transactional(rollbackFor = SQLException.class)
    public void editSceneWorks(Integer sceneId, String worksDesc, String fileKeys) {
        // 删除作品
        sceneWorksDao.deleteBySceneId(sceneId);
        // 增加作品
        sceneWorksDao.insertWorks(sceneId, worksDesc);
        // 删除图片
        List<String> list = StringUtils.splitString(fileKeys);
        for(String fileName : list) {
            sceneWorksImageDao.deleteImage(sceneId, fileName);
        }
        // 改变场景至草稿箱
        SceneInfo sceneInfo = sceneInfoDao.manageGet(sceneId);
        sceneInfo.setStatus(-1);
        sceneInfoDao.update(sceneInfo);
    }

    /**
     * 重新排序场景
     * @param ids
     */
    @Transactional(rollbackFor = Exception.class)
    public void orderScene(Integer userId, String ids){

        sceneInfoDao.clearWeight(userId);
        List<Integer> idList = StringUtils.splitInteger(ids);
        int weight = 3;
        for(Integer id : idList){
            SceneInfo sceneInfo = sceneInfoDao.get(id);
            sceneInfo.setWeight(weight--);
            sceneInfoDao.update(sceneInfo);
        }
    }

    /**
     * 保存场景作品图片
     * @param sceneId
     * @param imageName
     */
    @Transactional(rollbackFor = Exception.class)
    public void addWorksImage(Integer sceneId, String imageName){

        sceneWorksImageDao.insertImage(sceneId, imageName);

    }


  /**
  * Description: 获取场景列表
  * Param:
  * return:
  * Author: W
  * Date: 2018/4/17 14:50
  */
    public PageInfo<SceneInfo> getSceneList(Integer userId, Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum, pageSize, true);
        List<SceneInfo> list = sceneInfoDao.getSceneList(userId);

        return new PageInfo<>(sceneService.addSceneInfo(list));
    }


  /**
  * Description: 对场景进行排序.  按照最大id值对场景权重进行排序
  * Param:
  * return:
  * Author: W
  * Date: 2018/4/17 15:26
  */
    @Transactional(rollbackFor = Exception.class)
    public void orderSceneWeight(Integer userId, String ids){

        sceneInfoDao.clearWeight(userId);
        List<Integer> idList = StringUtils.splitInteger(ids);
        Integer maxId= Collections.max(idList);
        for(Integer id : idList){
            SceneInfo sceneInfo = sceneInfoDao.get(id);
            sceneInfo.setWeight(maxId--);
            sceneInfoDao.update(sceneInfo);
        }
    }

    /** 
    * Description: 先根据场景id删除图片,再根据图片名字对图片进行权重赋值
    * Param:  
    * return:  
    * Author: W
    * Date: 2018/4/18 13:51
    */ 
    @Transactional(rollbackFor = Exception.class)
    public void updateSceneImage(Integer sceneId,Integer userId, String sceneNames){

        sceneImageDao.clearSceneImages(sceneId);
        List<String> sceneNameList = StringUtils.splitString(sceneNames);
        int weight =sceneNameList.size()+1;
        for(String sceneName : sceneNameList){
            SceneImage sceneImage = new SceneImage();
            sceneImage.setUserId(userId);
            sceneImage.setImageName(sceneName);
            sceneImage.setSceneId(sceneId);
            sceneImage.setWeight(weight--);
            sceneImage.setCreateTime(new Date());
            sceneImageDao.insertSceneImage(sceneImage);
        }
    }
}
