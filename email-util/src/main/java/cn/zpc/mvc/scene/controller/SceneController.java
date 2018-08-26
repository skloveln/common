package cn.zpc.mvc.scene.controller;

import cn.zpc.common.serivce.BaseService;
import cn.zpc.common.utils.MathUtils;
import cn.zpc.common.web.result.DataResult;
import cn.zpc.common.web.result.MessageResult;
import cn.zpc.common.web.result.Result;
import cn.zpc.mvc.scene.entity.SceneInfo;
import cn.zpc.mvc.scene.entity.SceneMap;
import cn.zpc.mvc.scene.service.SceneService;
import cn.zpc.mvc.scene.service.SceneTypeService;
import cn.zpc.mvc.sys.entity.SearchResult;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(description = "场景信息控制器")
@RestController
public class SceneController extends BaseService{

    @Autowired
    private SceneService sceneService;
    @Autowired
    private UserCollectionService userCollectionService;
    @Autowired
    private SceneTypeService sceneTypeService;


    /**
     * 场景Tab页
     * @return
     */
    @ApiOperation(value = "场景Tab页（C页）",  notes="total-结果数</br>" +
            "list-type 1-场景 2-图集</br> ")
    @Authorization
    @RequestMapping(value = "/scene/tab",method = RequestMethod.POST)
    public Result sceneTab(
            @ApiParam(value = "页码")
            @RequestParam(defaultValue = "1") Integer pageNum,

            @ApiParam(value = "每页大小")
            @RequestParam(defaultValue = "20") Integer pageSize,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        if(pageSize == 0){
            pageSize = 1;
        }
        Integer userId = userContext.getUserId();
        PageInfo pageInfo = sceneService.getSceneTab(userId, pageNum, pageSize);
        Map<String, Object> map = new HashMap<>();
        map.put("total", pageInfo.getTotal());
        map.put("list", pageInfo.getList());

        return new DataResult<>(map);
    }


    /**
     * 场景详情
     * @param sceneId
     * @return
     */
    @ApiOperation(value = "场景详情",notes="场景的详细信息")
    @Authorization
    @RequestMapping(value = "/scene/info/get",method = RequestMethod.POST)
    public Result sceneInfo(
            @ApiParam(value = "场景信息的Id",required = true)
            @NotNull
            @RequestParam(name="sceneId") Integer sceneId,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        Map<String, Object> result = new HashMap<>();
        SceneInfo detail = sceneService.getSceneInfo(sceneId);
        if(detail == null){
            return MessageResult.getExceptionMessage("没有这个场景");
        }
        detail.setSceneType(sceneTypeService.getSceneTypeName(detail.getSceneTypeId()));

        // 用户是否收藏
        result.put("userCollect", userCollectionService.checkCollection(userId, 1, sceneId));
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


    @ApiOperation(value = "场景分类信息")
    @Authorization
    @RequestMapping(value = "/scene/type/get",method = RequestMethod.POST)
    public Result sceneTypeInfo(){


        return DataResult.getNormal(sceneTypeService.getAllTypes());
    }


    @ApiOperation(value = "更多热门场景")
    @RequestMapping(value = "/scene/hot/more", method = RequestMethod.POST)
    @Authorization
    public Result getSceneMore(
            @ApiParam(value = "页码")
            @RequestParam(defaultValue = "1") Integer pageNum,

            @ApiParam(value = "每页大小")
            @RequestParam(defaultValue = "20") Integer pageSize,

            @ApiIgnore
            @CurrentUser UserContext userContext) {

        Integer userId = userContext.getUserId();

        Map<String, Object> map = new HashMap<>();
        PageInfo pageInfo = sceneService.getHotScenes(userId, pageNum, pageSize);
        map.put("list", pageInfo.getList());
        map.put("total", pageInfo.getTotal());

        return DataResult.getNormal(map);
    }


    @ApiOperation(value = "获取一定范围内的场景坐标", notes = "id: 对应类型的Id<br/>" +
            "type: 1-场景 2-图集<br/>" +
            "lon: 经度<br/>" +
            "lat: 纬度<br/>")
    @Authorization
    @RequestMapping(value = "/scene/map/lon/lat", method = RequestMethod.POST)
    public Result getSceneLonLat(
            @ApiParam(value = "屏幕左上角坐标经度")
            @RequestParam Double topLon,

            @ApiParam(value = "屏幕左上角坐标纬度")
            @RequestParam Double topLat,

            @ApiParam(value = "屏幕右下角坐标经度")
            @RequestParam Double bottomLon,

            @ApiParam(value = "屏幕右下角坐标纬度")
            @RequestParam Double bottomLat,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        List<SceneMap> list =  sceneService.getSceneMap(topLon, topLat, bottomLon, bottomLat);
        Map<String, Object> map = new HashMap<>();
        map.put("list", list);
        map.put("total", list.size());
        return new DataResult<>(map);
    }


    @ApiOperation(value = "获取指定坐标最近的10个场景", notes = "type: 1-场景 2-图集<br/>" +
            "lon: 经度<br/>" +
            "lat: 纬度<br/>")
    @Authorization
    @RequestMapping(value = "/scene/map/list", method = RequestMethod.POST)
    public Result getSceneLonLat(
            @ApiParam(value = "坐标经度")
            @RequestParam Double lon,

            @ApiParam(value = "坐标纬度")
            @RequestParam Double lat,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Map<String, Object> map = new HashMap<>();
        List<SearchResult> list = sceneService.getMapNearestScene(userContext.getUserId(), lon, lat, 10);
        map.put("list", list);
        map.put("total", list.size());

        return new DataResult<>(map);
    }


    @ApiOperation(value = "地图中对应坐标场景详情",notes="对应坐标场景的详细信息<br/>" +
            "type: 1-场景 2-图集<br/>" +
            "info: 具体详情")
    @Authorization
    @RequestMapping(value = "/scene/map/info", method = RequestMethod.POST)
    public Result sceneInfo(
            @ApiParam(value = "坐标经度")
            @RequestParam Double lon,

            @ApiParam(value = "坐标纬度")
            @RequestParam Double lat,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        lon = MathUtils.saveDouble(lon, 6);
        lat = MathUtils.saveDouble(lat, 6);

        return new DataResult<>(sceneService.getMapSceneInfo(userContext.getUserId(), lon , lat));
    }

}
