package cn.zpc.mvc.store.controller;

import cn.zpc.common.handler.exception.GlobalExceptionResult;
import cn.zpc.common.serivce.BaseService;
import cn.zpc.common.utils.DateUtils;
import cn.zpc.common.web.result.DataResult;
import cn.zpc.common.web.result.Result;
import cn.zpc.mvc.store.entity.Store;
import cn.zpc.mvc.store.service.StoreRecommendService;
import cn.zpc.mvc.store.service.StoreService;
import cn.zpc.mvc.user.security.UserContext;
import cn.zpc.mvc.user.security.annotation.Authorization;
import cn.zpc.mvc.user.security.annotation.CurrentUser;
import cn.zpc.mvc.user.service.UserCollectionService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotNull;
import java.util.*;

@Api(description = "店铺信息控制器")
@RestController
public class StoreController extends BaseService{


    @Autowired
    private StoreService storeService;
    @Autowired
    private UserCollectionService userCollectionService;
    @Autowired
    private StoreRecommendService storeRecommendService;


    @Deprecated
    @ApiOperation(value = "店铺详情信息", notes = "店铺页面的信息" +
            "<br>status 0-正常 1-待上架 2-下架" +
            "<br>type 1-个人店铺 2-企业商铺 3-地接商铺" +
            "<br>style 1-影棚 2-实景")
    @Authorization
    @RequestMapping(value = "/store/info/get", method = RequestMethod.POST)
    public Result getStoreInfo(

            @ApiParam(value = "店铺的Id", required = true)
            @RequestParam
            @NotNull Integer storeId,

            @ApiIgnore
            @CurrentUser UserContext userContext) {

        if(! storeService.checkStoreExist(storeId)){
            throw new GlobalExceptionResult("store.notExist", 1002);
        }

        Integer userId = userContext.getUserId();
        Map<String, Object> result = new HashMap<>();

        // 店铺的基本信息
        result.put("storeInfo", storeService.getStoreInfo(storeId));
        // 用户是否收藏
        result.put("collectStore", userCollectionService.checkCollection(userId,0,storeId));
        // 场景
        result.put("sceneInfo", storeService.getSceneInfo(storeId, userId, 0, 0));
        // VR场景
        result.put("VR", storeService.getVrInfo(storeId, 1, 10));
        // 得到场景标签;
//        result.put("sceneTags", storeService.getSceneTags(storeId));
        // 店铺服务
        result.put("storeService", storeService.getStoreServices(storeId));
        // 周边场景
        result.put("storePeriphery",storeService.getStorePeriphery(storeId, 1, 10).getList());
        // 联系人
        result.put("contacts", storeService.getStoreContacts(storeId));
        // 店主信息
        result.put("user", storeService.getStoreUser(storeId));

        return DataResult.getNormal(result);
    }


    @ApiOperation(value = "商铺页更多花絮")
    @Authorization
    @RequestMapping(value = "/store/info/titbits/more", method = RequestMethod.POST)
    public Result getTitbitsMore(
            @ApiParam(value = "页码")
            @RequestParam(defaultValue = "1") Integer pageNum,

            @ApiParam(value = "每页大小")
            @RequestParam(defaultValue = "10") Integer pageSize,

            @ApiParam(value = "店铺的Id", required = true)
            @RequestParam
            @NotNull Integer storeId,

            @ApiIgnore
            @CurrentUser UserContext userContext){


        PageInfo pageInfo = storeService.getTitbitsList(storeId, pageNum, pageSize);
        Map<String, Object> map = new HashMap<>();
        map.put("total", pageInfo.getTotal());
        map.put("list", pageInfo.getList());

        return new DataResult<>(map);
    }


    @ApiOperation(value = "店铺详情首页", notes = "店铺页面首页字段" +
            "<br/>storeInfo-基本信息" +
            "<br>&nbsp;&nbsp;&nbsp;&nbsp;status 0-正常 1-待上架 2-下架" +
            "<br>&nbsp;&nbsp;&nbsp;&nbsp;type 1-个人店铺 2-企业商铺 3-地接商铺" +
            "<br>&nbsp;&nbsp;&nbsp;&nbsp;style 1-影棚 2-实景" +
            "<br>&nbsp;&nbsp;&nbsp;&nbsp;openTime-开业时间" +
            "<br>&nbsp;&nbsp;&nbsp;&nbsp;introduction-关于这里的介绍" +
            "<br>&nbsp;&nbsp;&nbsp;&nbsp;remark-温馨提示" +
            "<br/>storeAd-广告图" +
            "<br/>hotScene-热门场景" +
            "<br>storeTitbits-精彩花絮" +
            "<br>storeService-商铺服务" +
            "<br/>storePeriphery-周边场景" +
            "<br/>abort-关于这里" +
            "<br/>storeFriendly-友情链接" +
            "<br/>collectStore-是否收藏" +
            "<br/>contacts-联系客服" +
            "<br/>complain-投诉人" +
            "<br/>user-店主信息")
    @Authorization
    @RequestMapping(value = "/store/info/home", method = RequestMethod.POST)
    public Result getStoreHome(
            @ApiParam(value = "店铺的Id", required = true)
            @RequestParam
            @NotNull Integer storeId,

            @ApiIgnore
            @CurrentUser UserContext userContext) {

        if(! storeService.checkStoreExist(storeId)){
            throw new GlobalExceptionResult("store.notExist", 1002);
        }
        Integer userId = userContext.getUserId();
        Map<String, Object> result = new HashMap<>();

        Store store = storeService.getStoreInfo(storeId);
        // 店铺的基本信息
        result.put("storeInfo", store);
        // 店铺的广告
        result.put("storeAd", storeService.getAdvertPic(storeId));
        // 热门场景
        result.put("hotScene", storeService.getRecommendScene(storeId));
        // 精彩花絮
        result.put("storeTitbits", storeService.getTitbitsList(storeId, 1, 2).getList());
        // 商铺服务
        result.put("storeService", storeService.getStoreHomeServices(storeId));
        // 周边场景
        result.put("storePeriphery", storeService.getStorePeriphery(storeId, 1, 3).getList());
        // 友情链接
        result.put("storeFriendly", storeService.getFriendlyStore(storeId));
        // 用户是否收藏
        result.put("collectStore", userCollectionService.checkCollection(userId,0,storeId));
        // 业务客户
        result.put("contacts", storeService.getStoreContacts(storeId));
        // 投诉人
        Map<String, Object> map = storeService.getStoreComplain(storeId);
        if(map.size() == 0 ){
            map.put("name", "智景");
            map.put("phone","84989842");
        }
        result.put("complain", map);
        // 店主信息
        result.put("user", storeService.getStoreUser(storeId));
        return DataResult.getNormal(result);
    }


    @ApiOperation(value = "店铺详情全部场景", notes = "店铺页面的信息" +
            "<br>mainImage-封面图  userCollection-是否收藏" +
            "<br>detailAddress-地址")
    @Authorization
    @RequestMapping(value = "/store/info/scene", method = RequestMethod.POST)
    public Result getStoreScene(
            @ApiParam(value = "店铺的Id", required = true)
            @RequestParam
            @NotNull Integer storeId,

            @ApiParam(value = "页码")
            @RequestParam(defaultValue = "0") Integer pageNum,

            @ApiParam(value = "每页大小")
            @RequestParam(defaultValue = "0") Integer pageSize,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        return new DataResult<>(storeService.getSceneInfo(storeId, userId, pageNum, pageSize));
    }


    @ApiOperation(value = "店铺详情VR模块", notes = "店铺页面的信息" +
            "<br>url-vr链接 sceneName-场景名称 mainImage-封面图")
    @Authorization
    @RequestMapping(value = "/store/info/vr", method = RequestMethod.POST)
    public Result getStoreVr(
            @ApiParam(value = "店铺的Id", required = true)
            @RequestParam
            @NotNull Integer storeId){


        return new DataResult<>(storeService.getVrInfo(storeId, 0, 0));
    }


    @ApiOperation(value = "店铺详情商铺服务", notes = "店铺页面的信息" +
            "<br>general-通用图标  specific-食住行图标" +
            "<br>image-图集 desc-温馨提示")
    @Authorization
    @RequestMapping(value = "/store/info/services", method = RequestMethod.POST)
    public Result getStoreServices(
            @ApiParam(value = "店铺的Id", required = true)
            @RequestParam
            @NotNull Integer storeId){


        return new DataResult<>(storeService.getStoreService(storeId));
    }


    @ApiOperation(value = "店铺详情周边", notes = "店铺页面的信息")
    @Authorization
    @RequestMapping(value = "/store/info/periphery", method = RequestMethod.POST)
    public Result getStorePeriphery(
            @ApiParam(value = "店铺的Id", required = true)
            @RequestParam
            @NotNull Integer storeId){


        return new DataResult<>(storeService.getStorePeriphery(storeId, 0, 0).getList());
    }

    @ApiOperation(value = "店铺模块更多商户", notes = "店铺页面的信息")
    @Authorization
    @RequestMapping(value = "/store/more", method = RequestMethod.POST)
    public Result storeMore(
            @ApiParam(value = "页码")
            @RequestParam(defaultValue = "1") Integer pageNum,

            @ApiParam(value = "每页大小")
            @RequestParam(defaultValue = "10") Integer pageSize,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        PageInfo pageInfo = storeService.getNewStoreMore(userId, pageNum, pageSize);
        Map<String, Object> map = new HashMap<>();
        map.put("list", pageInfo.getList());
        map.put("total", pageInfo.getTotal());

        return new DataResult<>(map);
    }


    @ApiOperation(value = "店铺模块（B页）", notes = "店铺页面的信息")
    @Authorization
    @RequestMapping(value = "/store/home", method = RequestMethod.POST)
    public Result storeHome(
            @ApiIgnore
            @CurrentUser UserContext userContext){
        Integer userId = userContext.getUserId();
        Map<String, Object> result = new HashMap<>();
        // B页精选推荐
        result.put("dynamic", storeRecommendService.getDynamic());
        // B页商户动态
        result.put("recommend",storeService.getNewSceneStores(1,3));
        result.put("hotStore", storeService.getHotStores(userId, 1, 10).getList());
        // 更新店铺热度
        if(!redisService.exists("update_store_hot") || !DateUtils.isSameDay(redisService.get("update_store_hot"), new Date())){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    storeService.updateStoreHot();
                }
            });
            thread.start();
            redisService.set("update_store_hot", new Date());
        }
        return new DataResult<>(result);
    }



}
