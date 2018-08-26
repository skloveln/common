package cn.zpc.mvc.sys.controller;

import cn.zpc.common.handler.exception.GlobalExceptionResult;
import cn.zpc.common.plugins.oss.OssPathConfig;
import cn.zpc.common.serivce.BaseService;
import cn.zpc.common.utils.StringUtils;
import cn.zpc.common.web.result.DataResult;
import cn.zpc.common.web.result.MessageResult;
import cn.zpc.common.web.result.Result;
import cn.zpc.mvc.album.service.AlbumService;
import cn.zpc.mvc.scene.entity.SceneInfo;
import cn.zpc.mvc.scene.service.SceneService;
import cn.zpc.mvc.scene.service.SceneTypeService;
import cn.zpc.mvc.store.entity.Store;
import cn.zpc.mvc.store.service.StoreService;
import cn.zpc.mvc.sys.entity.SearchResult;
import cn.zpc.mvc.sys.service.SearchService;
import cn.zpc.mvc.user.security.UserContext;
import cn.zpc.mvc.user.security.annotation.Authorization;
import cn.zpc.mvc.user.security.annotation.CurrentUser;
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


import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Api(description = "分享控制器")
@RestController
public class ShareController extends BaseService{

    @Autowired
    private StoreService storeService;
    @Autowired
    private SceneService sceneService;
    @Autowired
    private SceneTypeService sceneTypeService;
    @Autowired
    private AlbumService albumService;
    @Autowired
    private SearchService searchService;


    @ApiOperation(value = "图片处理")
    @RequestMapping(value = "/image/handle", method = RequestMethod.POST)
    public Result imageHandle(
            @ApiParam(value = "fileKey", required = true)
            @RequestParam String fileKey,

            @ApiParam(value = "图片类型: 1-场景 2-店铺 3-图集", required = true)
            @RequestParam Integer type) {

        String key = "";
        switch (type){
            case 1:
                key = OssPathConfig.getSceneImagePath(fileKey);
                break;
            case 2:
                key = OssPathConfig.getStoreImagePath(fileKey);
                break;
            case 3:
                key = OssPathConfig.getAlbumImagePath(fileKey);
                break;
        }
        return new DataResult<>(ossService.getMicroAppUrl(key));
    }


    @ApiOperation(value = "场景详情",notes="场景的详细信息")
    @RequestMapping(value = "/h5/scene/info/get",method = RequestMethod.POST)
    public Result sceneInfo(
            @NotNull
            @ApiParam(value = "场景信息的Id",required = true)
            @RequestParam(name="sceneId") Integer sceneId){

        Map<String, Object> result = new HashMap<>();
        SceneInfo detail = sceneService.getSceneInfo(sceneId);
        if(detail == null){
            return MessageResult.getExceptionMessage("没有这个场景");
        }
        detail.setSceneType(sceneTypeService.getSceneTypeName(detail.getSceneTypeId()));
        // 场景基本信息
        result.put("sceneInfo", detail);
        // 场景图片
        result.put("sceneImage", sceneService.getSceneImages(sceneId, true));
        // 场景服务
        result.put("sceneService", sceneService.getSceneServiceIcon(sceneId));
        // 场景主信息
        result.put("user", sceneService.getSceneUser(sceneId));
        // 场景联系人
        result.put("contacts", sceneService.getSceneContacts(sceneId));
        // 场景拍过的电影
        result.put("works", sceneService.getSceneWorks(sceneId));
        // 店铺信息
        result.put("storeInfo", sceneService.getStore(sceneId));

        return DataResult.getNormal(result);
    }


    @Deprecated
    @ApiOperation(value = "店铺详情", notes = "店铺页面的信息")
    @RequestMapping(value = "/h5/store/info/get", method = RequestMethod.POST)
    public Result getStoreInfo(
            @ApiParam(value = "店铺的Id", required = true)
            @RequestParam
            @NotNull Integer storeId) {

        if(! storeService.checkStoreExist(storeId)){
            throw new GlobalExceptionResult("store.notExist", 1002);
        }
        Map<String, Object> result = new HashMap<>();
        // 店铺的基本信息
        result.put("storeInfo", storeService.getStoreInfo(storeId));
        // 场景
        result.put("sceneInfo", storeService.getSceneInfo(storeId, null, 0, 0));
        // VR场景
        result.put("VR", storeService.getVrInfo(storeId, 1, 10));
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
    @RequestMapping(value = "/h5/store/info/home", method = RequestMethod.POST)
    public Result getStoreHome(
            @ApiParam(value = "店铺的Id", required = true)
            @RequestParam
            @NotNull Integer storeId) {

        if(! storeService.checkStoreExist(storeId)){
            throw new GlobalExceptionResult("store.notExist", 1002);
        }
        Map<String, Object> result = new HashMap<>();
        Store store = storeService.getStoreInfo(storeId);
        // 店铺的基本信息
        result.put("storeInfo", store);
        // 店铺的广告
        result.put("storeAd", storeService.getAdvertPic(storeId));
        // 热门场景
        result.put("hotScene", storeService.getRecommendScene(storeId));
        // 精彩花絮
        result.put("storeTitbits", storeService.getTitbitsList(storeId, 1, 1).getList());
        // 商铺服务
        result.put("storeService", storeService.getStoreHomeServices(storeId));
        // 周边场景
        result.put("storePeriphery", storeService.getStorePeriphery(storeId, 1, 3).getList());
        // 友情链接
        result.put("storeFriendly", storeService.getFriendlyStore(storeId));
        // 店主信息
        result.put("user", storeService.getStoreUser(storeId));

        return DataResult.getNormal(result);
    }


    @ApiOperation(value = "店铺详情全部场景", notes = "店铺页面的信息" +
            "<br>mainImage-封面图  userCollection-是否收藏" +
            "<br>detailAddress-地址")
    @RequestMapping(value = "/h5/store/info/scene", method = RequestMethod.POST)
    public Result getStoreScene(
            @ApiParam(value = "店铺的Id", required = true)
            @RequestParam
            @NotNull Integer storeId,

            @ApiParam(value = "页码")
            @RequestParam(defaultValue = "0") Integer pageNum,

            @ApiParam(value = "每页大小")
            @RequestParam(defaultValue = "0") Integer pageSize){

        return new DataResult<>(storeService.getSceneInfo(storeId, null, pageNum, pageSize));
    }


    @ApiOperation(value = "店铺详情VR模块", notes = "店铺页面的信息" +
            "<br>url-vr链接 sceneName-场景名称 mainImage-封面图")
    @RequestMapping(value = "/h5/store/info/vr", method = RequestMethod.POST)
    public Result getStoreVr(
            @ApiParam(value = "店铺的Id", required = true)
            @RequestParam
            @NotNull Integer storeId){


        return new DataResult<>(storeService.getVrInfo(storeId, 0, 0));
    }


    @ApiOperation(value = "店铺详情商铺服务", notes = "店铺页面的信息" +
            "<br>general-通用图标  specific-食住行图标" +
            "<br>image-图集 desc-温馨提示")
    @RequestMapping(value = "/h5/store/info/services", method = RequestMethod.POST)
    public Result getStoreServices(
            @ApiParam(value = "店铺的Id", required = true)
            @RequestParam
            @NotNull Integer storeId){


        return new DataResult<>(storeService.getStoreService(storeId));
    }


    @ApiOperation(value = "店铺详情周边", notes = "店铺页面的信息")
    @RequestMapping(value = "/h5/store/info/periphery", method = RequestMethod.POST)
    public Result getStorePeriphery(
            @ApiParam(value = "店铺的Id", required = true)
            @RequestParam
            @NotNull Integer storeId){


        return new DataResult<>(storeService.getStorePeriphery(storeId, 0, 0).getList());
    }


    @ApiOperation(value = "获取某一图集详情", notes = "给纠错10条， 评论20条，剩余分页获取" +
            "</br>commentNum 总评论数   普通评论数自己算一下（总评论数减去纠错数）" +
            "</br>errorNum 纠错数" +
            "</br>storeType 1-个人店铺 2-商家店铺 3-地接店铺")
    @RequestMapping(value = "/h5/album/info", method = RequestMethod.POST)
    public Result getAlbumList(
            @ApiParam(value = "图集Id")
            @RequestParam Integer albumId ){

        Map<String, Object> map = albumService.getInfo(albumId, null);

        return new DataResult<>(map);
    }


    @ApiOperation(value = "分享多个图集")
    @RequestMapping(value = "/h5/album/more", method = RequestMethod.POST)
    public Result getAlbumMore(
            @ApiParam(value = "图集Id集合, eg: 1 2 3 4")
            @RequestParam String albumIds ){

        return new DataResult<>(albumService.getMoreInfo(albumIds));
    }


    @ApiOperation(value = "场景Tab页",  notes="total-结果数</br>" +
            "list-type 1-场景 2-图集</br> ")
    @RequestMapping(value = "/h5/scene/tab",method = RequestMethod.POST)
    public Result sceneTab(
            @ApiParam(value = "页码")
            @RequestParam(defaultValue = "1") Integer pageNum,

            @ApiParam(value = "每页大小")
            @RequestParam(defaultValue = "20") Integer pageSize){

        PageInfo pageInfo = sceneService.getSceneTab(null, pageNum, pageSize);
        Map<String, Object> map = new HashMap<>();
        map.put("total", pageInfo.getTotal());
        map.put("list", pageInfo.getList());

        return new DataResult<>(map);
    }


    @ApiOperation(value = "全局搜索", notes = "total-结果数</br>" +
            "list-type 1-场景 2-图集 3-资讯</br> ")
    @RequestMapping(value = "/h5/search/global",method = RequestMethod.POST)
    public Result searchGlobal(
            @ApiParam(name = "keyword", value = "搜索的内容")
            @RequestParam(required = false) String keyword,

            @ApiParam(name = "style", value = "搜索的类型, 1-场景 2-店铺")
            @RequestParam(defaultValue = "1") Integer style,

            @ApiParam(value = "当前页码，默认1", name = "pageNum")
            @Max(value = 100)
            @Min(value = 1)
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,

            @ApiParam(value = "每页长度，默认20", name = "pageSize")
            @Max(value = 50)
            @Min(value = 1)
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,

            @ApiParam(value = "区域")
            @RequestParam(required = false) String address,

            @ApiParam(value = "场景类型Id")
            @RequestParam(required = false) Integer type,

            @ApiParam(value = "价格区间，eg: 0  5000")
            @RequestParam(required = false) String price,

            @ApiParam(value = "价格类型 1.每小时 2.每天 3.每周 4.每月 5.面议")
            @RequestParam(required = false) Integer priceType,

            @ApiParam(value = "面积区间，eg: 0  100")
            @RequestParam(required = false) String area) {

        Integer priceMin=null, priceMax=null, areaMin=null, areaMax=null;
        if((type != null && type != 0)
                || StringUtils.isNotEmpty(price)
                || (priceType != null && priceType != 0)
                || StringUtils.isNotEmpty(area)){
            if(StringUtils.isNotEmpty(price)){
                priceMax = StringUtils.splitInteger(price).get(1);
                priceMin = StringUtils.splitInteger(price).get(0);
            }
            if(StringUtils.isNotEmpty(area)){
                areaMin = StringUtils.splitInteger(area).get(0);
                areaMax = StringUtils.splitInteger(area).get(1);
            }
        }

        Map<String, Object> map = new TreeMap<>();
        PageInfo<SearchResult> pageInfo = searchService.search(null, style, keyword, address, type, priceType, priceMin,
                priceMax, areaMin, areaMax, pageNum, pageSize);

        map.put("list",  pageInfo.getList());
        map.put("total", pageInfo.getTotal());

        if(style == 0) {
            searchService.addSearchLog(keyword, null, 0, null);
        }else {
            searchService.addSearchLog(keyword, null, 2, null);
        }

        return new DataResult<>(map);
    }

}
