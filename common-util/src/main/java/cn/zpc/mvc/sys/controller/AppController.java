package cn.zpc.mvc.sys.controller;

import cn.zpc.common.serivce.BaseService;
import cn.zpc.common.utils.DateUtils;
import cn.zpc.common.web.result.DataResult;
import cn.zpc.common.web.result.Result;
import cn.zpc.mvc.scene.service.SceneExtraService;
import cn.zpc.mvc.scene.service.SceneService;
import cn.zpc.mvc.store.service.StoreRecommendService;
import cn.zpc.mvc.store.service.StoreService;
import cn.zpc.mvc.sys.service.SysAdvertService;
import cn.zpc.mvc.user.security.UserContext;
import cn.zpc.mvc.user.security.annotation.Authorization;
import cn.zpc.mvc.user.security.annotation.CurrentUser;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.text.SimpleDateFormat;
import java.util.*;


@Api(description = "APP首页控制器")
@RestController
public class AppController extends BaseService{

    @Autowired
    private SceneService sceneService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private SysAdvertService sysAdvertService;
    @Autowired
    private SceneExtraService sceneExtraService;
    @Autowired
    private StoreRecommendService storeRecommendService;

    @Deprecated
    @ApiOperation(value = "APP首页接口")
    @RequestMapping(value = "/app/home", method = RequestMethod.POST)
    @Authorization
    public Result getHomeInfo(
            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        Map<String, Object> result = new HashMap<>();

        // 首页Banner
        result.put("banner", sysAdvertService.getBanner());
        // 场景分类Icon
        result.put("sceneTypes", sysAdvertService.getTypesIcon());
        // 获取首页推荐场景（20个）
        result.put("hotScene", sceneService.getHotScenes(userId, 1,20).getList());
        // 获取首页热门店铺（8个）
        result.put("hotStore", storeService.getHotStores(null, 1, 8).getList());

        return DataResult.getNormal(result);
    }

    @ApiOperation(value = "APP新版首页接口（A页）",
            notes = "数据字段：<br/>" +
                    "横幅广告-banner  <br/>" +
                    "快报-news  10条<br/>" +
                    "商户推荐-hotStore  6个<br/>" +
                    "场景推荐-hotScene  5个<br/>" +
                    "资讯-extraScene  6个<br/>")
    @RequestMapping(value = "/app/home/new", method = RequestMethod.POST)
    @Authorization
    public Result getNewHomeInfo(
            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        Map<String, Object> result = new HashMap<>();

        // 首页Banner
        result.put("banner", sysAdvertService.getBanner());
        // 场景快报（10条）
        result.put("news", sceneExtraService.getNewsList(1, 10).getList());
        // 获取商户推荐
        result.put("hotStore", storeRecommendService.getRecommendStores(userId));
        // 获取场景推荐（15个）
        result.put("hotScene", sceneService.getHomeRecommendScene(userId, 0 ,15));
        // 获取场景资讯（6个）
        result.put("extraScene", sceneExtraService.getMoreScene(1,6).getList());

        // 更新场景热度
        if(!redisService.exists("update_scene_hot") || !DateUtils.isSameDay(redisService.get("update_scene_hot"), new Date())){

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    sceneService.updateSceneHot();
                }
            });
            thread.start();

            redisService.set("update_scene_hot", new Date());
        }

        return new DataResult<>(result);
    }


    @Deprecated
    @ApiOperation(value = "首页更多热门店铺")
    @RequestMapping(value = "/app/home/store/more", method = RequestMethod.POST)
    @Authorization
    public Result getStoreMore(
            @ApiParam(value = "页码")
            @RequestParam(defaultValue = "1") Integer pageNum,

            @ApiParam(value = "每页大小")
            @RequestParam(defaultValue = "20") Integer pageSize,

            @ApiIgnore
            @CurrentUser UserContext userContext) {

        Integer userId = userContext.getUserId();

        return DataResult.getNormal(storeService.getHotStoresMore(userId, pageNum, pageSize));
    }


    @ApiOperation(value = "APP首页店铺换一批")
    @RequestMapping(value = "/app/home/store/hot", method = RequestMethod.POST)
    @Authorization
    public Result getHotStore(
            @ApiParam(value = "页码")
            @RequestParam(defaultValue = "2") Integer pageNum,

            @ApiParam(value = "每页大小")
            @RequestParam(defaultValue = "6") Integer pageSize,

            @ApiIgnore
            @CurrentUser UserContext userContext) {

        Map map = new HashMap();
        Integer userId = userContext.getUserId();
        PageInfo pageInfo = storeService.getHotStores(userId, pageNum, pageSize);
        map.put("list", pageInfo.getList());
        map.put("total", pageInfo.getTotal());
        return DataResult.getNormal(map);
    }


    @ApiOperation(value = "首页更多推荐场景")
    @RequestMapping(value = "/app/home/scene/more", method = RequestMethod.POST)
    @Authorization
    public Result getSceneMore(
            @ApiParam(value = "页码")
            @RequestParam(defaultValue = "1") Integer pageNum,

            @ApiParam(value = "每页大小")
            @RequestParam(defaultValue = "20") Integer pageSize,

            @ApiIgnore
            @CurrentUser UserContext userContext) {

        Integer userId = userContext.getUserId();

        PageInfo pageInfo = sceneService.getHotScenes(userId, pageNum, pageSize);
        Map<String, Object> map = new HashMap<>();
        map.put("list", pageInfo.getList());
        map.put("total", pageInfo.getTotal());

        return DataResult.getNormal(map);
    }



    @ApiOperation(value = "首页更多场景资讯")
    @RequestMapping(value = "/app/home/scene/extra", method = RequestMethod.POST)
    @Authorization
    public Result getSceneExtra(
            @ApiParam(value = "页码")
            @RequestParam(defaultValue = "1") Integer pageNum,

            @ApiParam(value = "每页大小")
            @RequestParam(defaultValue = "20") Integer pageSize) {


        Map<String, Object> map = new HashMap<>();
        PageInfo pageInfo = sceneExtraService.getMoreScene(pageNum, pageSize);
        map.put("list", pageInfo.getList());
        map.put("total", pageInfo.getTotal());

        return DataResult.getNormal(map);
    }


    @ApiOperation(value = "首页更多快报")
    @RequestMapping(value = "/app/home/news/more", method = RequestMethod.POST)
    @Authorization
    public Result getNewsMore(
            @ApiParam(value = "日期 格式：yyyy-MM-dd")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            @RequestParam String date) throws Exception {

        Date time = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        Map<String, Object> map = new HashMap<>();
        PageInfo pageInfo = sceneExtraService.getMoreNews(time);
        map.put("list", pageInfo.getList());
        map.put("total", pageInfo.getTotal());

        return DataResult.getNormal(map);
    }


    @ApiOperation(value = "首页VR列表")
    @RequestMapping(value = "/app/home/vr", method = RequestMethod.POST)
    @Authorization
    public Result getVrMore(
            @ApiParam(value = "页码")
            @RequestParam(defaultValue = "1") Integer pageNum,

            @ApiParam(value = "每页大小")
            @RequestParam(defaultValue = "20") Integer pageSize){

        Map<String, Object> map = new HashMap<>();
        PageInfo pageInfo = sceneService.getVrList(pageNum, pageSize);
        map.put("list", pageInfo.getList());
        map.put("total", pageInfo.getTotal());

        return DataResult.getNormal(map);
    }


    /**
     * @description:    首页推荐场景下拉后,调用此接口 ,获取场景列表
     * @param userContext
     * @param pageNum
     * @param pageSize
     * @return: cn.zpc.common.web.result.Result
     * @author: W
     * @date: 2018/4/26  14:50
     */
    @ApiOperation(value = "下拉获取更多场景（A页）",
            notes = "场景推荐-hotScene  15个<br/>" )
    @RequestMapping(value = "/app/home/scene/list", method = RequestMethod.POST)
    @Authorization
    public Result getSceneHomeIndex(
            @ApiIgnore
            @CurrentUser UserContext userContext,
            @ApiParam(value = "页码")
            @RequestParam(defaultValue = "1") Integer pageNum,

            @ApiParam(value = "每页大小")
            @RequestParam(defaultValue = "15") Integer pageSize
            ){

        Integer userId = userContext.getUserId();
        Map<String, Object> result = new HashMap<>();

        // 获取场景推荐（10个）
        result.put("hotScene", sceneService.getHotScenes(userId,pageNum,pageSize));


        return new DataResult<>(result);
    }
}
