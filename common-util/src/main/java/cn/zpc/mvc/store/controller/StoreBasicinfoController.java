package cn.zpc.mvc.store.controller;

import cn.zpc.common.entity.UrlEntity;
import cn.zpc.common.handler.exception.GlobalExceptionResult;
import cn.zpc.common.plugins.oss.OssPathConfig;
import cn.zpc.common.serivce.BaseService;
import cn.zpc.common.utils.FileUtils;
import cn.zpc.common.web.result.DataResult;
import cn.zpc.common.web.result.MessageResult;
import cn.zpc.common.web.result.Result;
import cn.zpc.mvc.scene.dao.SceneTypeDao;
import cn.zpc.mvc.scene.entity.SceneInfo;
import cn.zpc.mvc.scene.service.SceneManageService;
import cn.zpc.mvc.scene.service.SceneService;
import cn.zpc.mvc.store.dao.StoreDao;
import cn.zpc.mvc.store.entity.Store;
import cn.zpc.mvc.store.param.StoreInfoParam;
import cn.zpc.mvc.store.service.StoreManageService;
import cn.zpc.mvc.store.service.StoreService;
import cn.zpc.mvc.user.security.UserContext;
import cn.zpc.mvc.user.security.annotation.Authorization;
import cn.zpc.mvc.user.security.annotation.CurrentUser;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(description = "店铺基本信息(管理接口)")
@RestController
public class StoreBasicinfoController extends BaseService {

    @Autowired
    private StoreService storeService;
    @Autowired
    private StoreDao storeDao;
    @Autowired
    private StoreManageService storeManageService;
    @Autowired
    private SceneService sceneService;
    @Autowired
    private SceneTypeDao sceneTypeDao;
    @Autowired
    private SceneManageService sceneManageService;


    @ApiOperation(value = "场景店铺信息预览", notes = "店铺页面的信息" +
            "<br>status 0-正常 1-待上架 2-下架" +
            "<br>type 1-个人店铺 2-企业商铺 3-地接商铺" +
            "<br>style 1-影棚 2-实景")
    @Authorization
    @RequestMapping(value = "/store/info/preview", method = RequestMethod.POST)
    public Result getStoreInfo(
            @ApiIgnore
            @CurrentUser UserContext userContext) {

        Integer userId = userContext.getUserId();
        Store store = storeDao.getByUser(userId);
        if(store == null){
            throw new GlobalExceptionResult("user.not.have.store", 1002);
        }
        Integer storeId = store.getId();
        Map<String, Object> result = new HashMap<>();

        // 店铺的基本信息
        result.put("storeInfo", storeService.getStoreInfo(storeId));
        // 场景
        result.put("sceneInfo", storeService.getSceneInfo(storeId, userId, 0, 0));
        // VR场景
        result.put("VR", storeService.getVrInfo(storeId, 1, 10));
        // 得到场景标签;
//        result.put("sceneTags", storeService.getSceneTags(storeId));
        // 店铺服务
//        result.put("storeService", storeService.getStoreServices(storeId));
        // 周边场景
        result.put("storePeriphery",storeService.getStorePeriphery(storeId, 1, 10).getList());
        // 联系人
        result.put("contacts", storeService.getStoreContacts(storeId));
        // 店主信息
        result.put("user", storeService.getStoreUser(storeId));

        return DataResult.getNormal(result);
    }


    @ApiOperation(value = "(管理接口)上传店铺封面图", notes = "fileKey-文件名 simpleUrl-缩略图")
    @Authorization
    @RequestMapping(value = "/store/mainImage/upload", method = RequestMethod.POST)
    public Result uploadMainImage(

            @ApiParam(required = true)
            @RequestParam
            @NotNull MultipartFile file,

            HttpServletRequest request,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        String savePath = request.getSession().getServletContext().getRealPath("upload");
        File image = FileUtils.transferFile(file, userContext.getUserId(), savePath);
        String url = ossService.putFile(OssPathConfig.getStoreImagePath(image.getName()), image);
        UrlEntity urlEntity = new UrlEntity(image.getName(), url);
        image.deleteOnExit();
        return new DataResult<>(urlEntity);
    }


    @ApiOperation(value = "(管理接口)上传店铺Logo图", notes = "fileKey-文件名 simpleUrl-缩略图")
    @Authorization
    @RequestMapping(value = "/store/logo/upload", method = RequestMethod.POST)
    public Result uploadLogo(
            @ApiParam(required = true)
            @RequestParam
            @NotNull MultipartFile file,

            HttpServletRequest request,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        String savaPath = request.getSession().getServletContext().getRealPath("upload");
        File image = FileUtils.transferFile(file, userContext.getUserId(), savaPath);
        String url = ossService.putFile(OssPathConfig.getStoreLogoPath(image.getName()), image);
        UrlEntity urlEntity = new UrlEntity(image.getName(), url);
        image.deleteOnExit();
        return new DataResult<>(urlEntity);
    }


    @ApiOperation(value = "(管理接口)编辑店铺资料", notes = "编辑店铺的信息")
    @Authorization
    @RequestMapping(value = "/store/info/edit", method = RequestMethod.POST)
    public Result editStoreInfo(
            @Validated StoreInfoParam storeInfoParam,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();

        return storeManageService.updateStoreInfo(storeInfoParam, userId);
    }


    @ApiOperation(value = "(管理接口)获取店铺基础资料", notes = "获取店铺基本信息" +
            "<br>status 0-正常 1-待上架 2-下架" +
            "<br>type 1-个人店铺 2-企业商铺 3-地接商铺" +
            "<br>style 1-影棚 2-实景")
    @Authorization
    @RequestMapping(value = "/store/basicInfo/get", method = RequestMethod.POST)
    public Result getStoreBasicInfo(
            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        Store store = storeDao.getByUser(userId);
        if(store == null){
            throw new GlobalExceptionResult("user.not.have.store", 1002);
        }
        Integer storeId = store.getId();
        Map<String, Object> result = new HashMap<>();

        // 店铺的基本信息
        result.put("storeInfo", store);
        // 店铺联系人
        result.put("contacts", storeService.getOtherContacts(storeId));
        // Logo图片信息
        result.put("logo", new UrlEntity(store.getLogo(), ossService.getSimpleUrl(OssPathConfig.getStoreLogoPath(store.getLogo()))));
        // 封面图信息
        result.put("mainImage", new UrlEntity(store.getMainImage(), ossService.getSimpleUrl(OssPathConfig.getStoreImagePath(store.getMainImage()))));
        // 维护人
        result.put("complain", storeService.getStoreComplain(storeId));
        return new DataResult<>(result);
    }


    @ApiOperation(value = "(管理接口)获取店铺VR资料", notes = "字段：VR.url VR.sceneName VR.mainImage")
    @Authorization
    @RequestMapping(value = "/store/vr/get", method = RequestMethod.POST)
    public Result getStoreVrInfo(
            @ApiParam(value = "页码")
            @RequestParam(defaultValue = "1") Integer pageNum,

            @ApiParam(value = "每页大小")
            @RequestParam(defaultValue = "10") Integer pageSize,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        Store store = storeDao.getByUser(userId);
        if(store == null){
            throw new GlobalExceptionResult("user.not.have.store", 1002);
        }
        Integer storeId = store.getId();
        Map<String, Object> result = new HashMap<>();

        result.put("VR", storeService.getVrInfo(storeId, pageNum, pageSize));

        return new DataResult<>(result);
    }


    @ApiOperation(value = "(管理接口) 获取店铺通话记录")
    @Authorization
    @RequestMapping(value = "/store/call/list/get", method = RequestMethod.POST)
    public Result getCallList(
            @ApiParam(value = "页码")
            @RequestParam(defaultValue = "1") Integer pageNum,

            @ApiParam(value = "每页大小")
            @RequestParam(defaultValue = "10") Integer pageSize,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        Store store = storeDao.getByUser(userId);
        if(store == null){
            throw new GlobalExceptionResult("user.not.have.store", 1002);
        }
        Integer storeId = store.getId();

        PageInfo pageInfo = storeManageService.getCallList(storeId, userId, pageNum, pageSize);

        Map<String, Object> result = new HashMap<>();
        result.put("list", pageInfo.getList());
        result.put("total", pageInfo.getTotal());

        return new DataResult<>(result);
    }


    @ApiOperation(value = "(管理接口) 获取店铺主推场景")
    @Authorization
    @RequestMapping(value = "/store/scene/order/get", method = RequestMethod.POST)
    public Result getImportScene(
            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        Store store = storeDao.getByUser(userId);
        if(store == null){
            throw new GlobalExceptionResult("user.not.have.store", 1002);
        }

        List<SceneInfo> list = sceneService.getSceneListByUser(userId, 1,3);
        for(SceneInfo sceneInfo :  list){
            sceneInfo.setMainImage(sceneService.getSceneMainImage(sceneInfo.getId()));
            sceneInfo.setSceneType(sceneTypeDao.get(sceneInfo.getSceneTypeId()).getSceneTypeName());
        }

        return new DataResult<>(list);
    }
    @Deprecated
    @ApiOperation(value = "主推场景选择列表", notes = "店铺页面的信息" +
            "<br>mainImage-封面图  userCollection-是否收藏" +
            "<br>detailAddress-地址")
    @Authorization
    @RequestMapping(value = "/store/scene/select/list", method = RequestMethod.POST)
    public Result getStoreScene(
            @ApiParam(value = "页码")
            @RequestParam(defaultValue = "0") Integer pageNum,

            @ApiParam(value = "每页大小")
            @RequestParam(defaultValue = "0") Integer pageSize,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        Store store = storeDao.getByUser(userId);
        if(store == null){
            throw new GlobalExceptionResult("user.not.have.store", 1002);
        }
        Integer storeId = store.getId();
        return new DataResult<>(storeService.getSceneInfo(storeId, userId, pageNum, pageSize));
    }

    @Deprecated
    @ApiOperation(value = "主推场景更新", notes = "店铺页面的信息" +
            "<br>mainImage-封面图  userCollection-是否收藏" +
            "<br>detailAddress-地址")
    @Authorization
    @RequestMapping(value = "/store/scene/order/update", method = RequestMethod.POST)
    public Result editStoreSceneOrder(
            @ApiParam(value = "场景编号排序，eg: 24 192 42")
            @NotEmpty
            @RequestParam String ids,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        Store store = storeDao.getByUser(userId);
        if(store == null){
            throw new GlobalExceptionResult("user.not.have.store", 1002);
        }

        sceneManageService.orderScene(userId, ids);

        return new MessageResult();
    }


    @ApiOperation(value = "(管理接口) 获取友情店铺", notes = "选中有个selected字段为true")
    @Authorization
    @RequestMapping(value = "/store/friendly/list", method = RequestMethod.POST)
    public Result getFriendlyStore(
            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        Store store = storeDao.getByUser(userId);
        if(store == null){
            throw new GlobalExceptionResult("user.not.have.store", 1002);
        }
        Integer storeId = store.getId();

        Map map = storeManageService.getFriendlyStoreList(userId, storeId, 0, 0);

        return new DataResult<>(map);
    }


    @ApiOperation(value = "(管理接口) 编辑友情店铺保存")
    @Authorization
    @RequestMapping(value = "/store/friendly/update", method = RequestMethod.POST)
    public Result editFriendlyStore(

            @ApiParam(value = "所有勾选的店铺Id集合，空格隔开 eg: 10001 10023 10025")
            @RequestParam(required = false) String ids,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        Store store = storeDao.getByUser(userId);
        if(store == null){
            throw new GlobalExceptionResult("user.not.have.store", 1002);
        }
        Integer storeId = store.getId();

        storeManageService.editFriendlyStore(storeId, ids);

        return new MessageResult();
    }


    @ApiOperation(value = "(管理接口) 获取主题选择")
    @Authorization
    @RequestMapping(value = "/store/subject/get", method = RequestMethod.POST)
    public Result getStoreAdvertSubject(
            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        Store store = storeDao.getByUser(userId);
        if(store == null){
            throw new GlobalExceptionResult("user.not.have.store", 1002);
        }
        Integer storeId = store.getId();

        return new DataResult<>(storeManageService.getAdvertSubject(storeId));
    }


    @ApiOperation(value = "(管理接口) 编辑店铺主题")
    @Authorization
    @RequestMapping(value = "/store/subject/select", method = RequestMethod.POST)
    public Result selectStoreAdvertSubject(
            @ApiParam(value = "选择的id")
            @RequestParam Integer id,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        Store store = storeDao.getByUser(userId);
        if(store == null){
            throw new GlobalExceptionResult("user.not.have.store", 1002);
        }
        Integer storeId = store.getId();

        storeManageService.editAdvertSubject(storeId, id);

        return new MessageResult();
    }


    /**
    * Description: 根据权重和创建时间获取场景列表
    * Param:
    * return:
    * Author: W
    * Date: 2018/4/17 15:03
    */
    @ApiOperation(value = "(管理接口) 个人主推场景列表")
    @Authorization
    @RequestMapping(value = "/store/scene/list/get",method = RequestMethod.POST)
    public  Result getSceneList(
            @ApiIgnore
            @CurrentUser UserContext userContext,
            @ApiParam(value = "页码")
            @RequestParam(defaultValue = "1") Integer pageNum,

            @ApiParam(value = "每页大小")
            @RequestParam(defaultValue = "10") Integer pageSize
    ){
        Integer userId = userContext.getUserId();
        Store store = storeDao.getByUser(userId);
        if(store == null){
            throw new GlobalExceptionResult("user.not.have.store", 1002);
        }
        PageInfo<SceneInfo> pageInfo = sceneManageService.getSceneList(userId, pageNum, pageSize);
        Map<String, Object> map = new HashMap<>();
        map.put("list", pageInfo.getList());
        map.put("total", pageInfo.getTotal());
        return new DataResult<>(map);
    }

    /** 
    * Description: 更新主推场景顺序.主推场景排序页 
    * Param:  
    * return:  
    * Author: W
    * Date: 2018/4/17 17:40
    */ 
    @ApiOperation(value = "主推场景更新", notes = "店铺页面的信息" +
            "<br>mainImage-封面图  userCollection-是否收藏" +
            "<br>detailAddress-地址")
    @Authorization
    @RequestMapping(value = "/store/scene/list/update", method = RequestMethod.POST)
    public Result updateStoreSceneList(
            @ApiParam(value = "场景编号排序，eg: 24 192 42")
            @NotEmpty
            @RequestParam String ids,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        Store store = storeDao.getByUser(userId);
        if(store == null){
            throw new GlobalExceptionResult("user.not.have.store", 1002);
        }
        sceneManageService.orderSceneWeight(userId, ids);
        return new MessageResult();
    }

}
