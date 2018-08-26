package cn.zpc.mvc.sys.controller;

import cn.zpc.common.utils.StringUtils;
import cn.zpc.common.web.result.DataResult;
import cn.zpc.common.web.result.Result;
import cn.zpc.mvc.scene.service.SceneService;
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
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Api(description = "搜索控制器")
@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;
    @Autowired
    private SceneService sceneService;

    @Deprecated
    @ApiOperation(value = "搜索页主页")
    @Authorization
    @RequestMapping(value = "/search/home",method = RequestMethod.POST)
    public Result searchHome(
            @ApiIgnore
            @CurrentUser UserContext userContext){

        Map<String, Object> map = new HashMap<>();

        // 热门搜索
        map.put("hotKeywords", searchService.hotKeywords());
        // 推荐场景4个
        map.put("hotScene", sceneService.getHotScenes(userContext.getUserId(), 1, 4).getList());

        return new DataResult<>(map);
    }


    @ApiOperation(value = "搜索页热门关键字")
    @Authorization
    @RequestMapping(value = "/search/hot",method = RequestMethod.POST)
    public Result hotSearch() {

        Map<String, Object> map = new HashMap<>();
        map.put("hotKeywords", searchService.hotKeywords());

        return new DataResult<>(map);
    }


    @ApiOperation(value = "全局搜索", notes = "total-结果数</br>" +
            "list-type 1-场景 2-图集 3-资讯</br> ")
    @Authorization
    @RequestMapping(value = "/search/global",method = RequestMethod.POST)
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
            @RequestParam(required = false) String area,

            @ApiIgnore
            @CurrentUser UserContext userContext) {

        Integer priceMin=null, priceMax=null, areaMin=null, areaMax=null;
        if((type != null && type != 0)
                || StringUtils.isNotEmpty(price)
                || (priceType != null && priceType != 0)
                || StringUtils.isNotEmpty(area)){
            if(StringUtils.isNotEmpty(price)){
                priceMin = StringUtils.splitInteger(price).get(0);
                priceMax = StringUtils.splitInteger(price).get(1);
            }
            if(StringUtils.isNotEmpty(area)){
                areaMin = StringUtils.splitInteger(area).get(0);
                areaMax = StringUtils.splitInteger(area).get(1);
            }
        }

        Integer userId = userContext.getUserId();

        Map<String, Object> map = new TreeMap<>();
        PageInfo<SearchResult> pageInfo = searchService.search(userId, style, keyword, address, type, priceType, priceMin,
                priceMax, areaMin, areaMax, pageNum, pageSize);

        map.put("list",  pageInfo.getList());
        map.put("total", pageInfo.getTotal());

        if(style == 0) {
            searchService.addSearchLog(keyword, userContext.getUserId(), 0, null);
        }else {
            searchService.addSearchLog(keyword, userContext.getUserId(), 2, null);
        }

        return new DataResult<>(map);
    }


    @Deprecated
    @ApiOperation(value = "全局搜索", notes = "scene-场景 sceneTotal-场景总数</br>" +
            "store-店铺 storeTotal-店铺数</br> " +
            "extraScene-资讯 extraSceneTotal-资讯数</br>")
    @Authorization
    @RequestMapping(value = "/search/search",method = RequestMethod.POST)
    public Result search(
            @ApiParam(name = "keyword", value = "搜索的内容")
            @RequestParam(required = false) String keyword,

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
            @RequestParam(required = false) String area,

            @ApiIgnore
            @CurrentUser UserContext userContext) {

        boolean flag = true;
        Integer priceMin=null, priceMax=null, areaMin=null, areaMax=null;
        if((type != null && type != 0)
                || (priceType != null && priceType != 0)
                || StringUtils.isNotEmpty(price)
                || StringUtils.isNotEmpty(area)){
            flag = false;
            if(StringUtils.isNotEmpty(price)){
                priceMin = StringUtils.splitInteger(price).get(0);
                priceMax = StringUtils.splitInteger(price).get(1);
            }
            if(StringUtils.isNotEmpty(area)){
                areaMin = StringUtils.splitInteger(area).get(0);
                areaMax = StringUtils.splitInteger(area).get(1);
            }
        }

        Integer userId = userContext.getUserId();

        Map<String, Object> map = new TreeMap<>();
        // 搜索场景
        PageInfo pageInfoScene = searchService.searchScene(userId, keyword, address, type, priceType, priceMin,
                priceMax, areaMin, areaMax, pageNum, pageSize);
        // 搜索店铺
        PageInfo pageInfoStore = searchService.searchStore(userId, keyword, address, null, null, pageNum, pageSize);
        // 搜索资讯
        PageInfo pageInfoInformation = searchService.searchInformation(keyword, address, pageNum, pageSize);
        // 搜索图集
        PageInfo pageInfoAlbum = searchService.searchAlbum(keyword, address, userId, pageNum, pageSize);

        map.put("scene",  pageInfoScene.getList());
        map.put("sceneTotal", pageInfoScene.getTotal());
        if(flag) {
            map.put("store", pageInfoStore.getList());
            map.put("storeTotal", pageInfoStore.getTotal());
            map.put("extraScene", pageInfoInformation.getList());
            map.put("extraSceneTotal", pageInfoInformation.getTotal());
            map.put("album", pageInfoAlbum.getList());
            map.put("albumTotal", pageInfoAlbum.getTotal());
            searchService.addSearchLog(keyword, userContext.getUserId(), 0, null);
        }else {
            searchService.addSearchLog(keyword, userContext.getUserId(), 2, null);
        }

        return new DataResult<>(map);
    }



    @ApiOperation(value = "场景搜索", notes = "scene-场景 sceneTotal-场景总数</br>")
    @Authorization
    @RequestMapping(value = "/search/scene",method = RequestMethod.POST)
    public Result searchScene(
            @ApiParam(name = "keyword", value = "搜索的内容")
            @RequestParam(required = false) String keyword,

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
            @RequestParam(required = false) String area,

            @ApiIgnore
            @CurrentUser UserContext userContext) {

        Integer priceMin=null, priceMax=null, areaMin=null, areaMax=null;
        if((type != null && type != 0)
                || (priceType != null && priceType != 0)
                || StringUtils.isNotEmpty(price)
                || StringUtils.isNotEmpty(area)){
            if(StringUtils.isNotEmpty(price)){
                priceMin = StringUtils.splitInteger(price).get(0);
                priceMax = StringUtils.splitInteger(price).get(1);
            }
            if(StringUtils.isNotEmpty(area)){
                areaMin = StringUtils.splitInteger(area).get(0);
                areaMax = StringUtils.splitInteger(area).get(1);
            }
        }

        Integer userId = userContext.getUserId();

        Map<String, Object> map = new TreeMap<>();
        PageInfo pageInfoScene = searchService.searchScene(userId, keyword, address, type, priceType, priceMin,
                priceMax, areaMin, areaMax, pageNum, pageSize);

        map.put("scene",  pageInfoScene.getList());
        map.put("sceneTotal", pageInfoScene.getTotal());

        searchService.addSearchLog(keyword, userContext.getUserId(), 2, null);


        return new DataResult<>(map);
    }


    @Deprecated
    @ApiOperation(value = "按场景类型搜索场景")
    @Authorization
    @RequestMapping(value = "/search/scene/type",method = RequestMethod.POST)
    public Result searchSceneByType(
            @ApiParam(name = "typeId", value = "场景类型编号", required = true)
            @RequestParam Integer typeId,

            @ApiParam(value = "当前页码，默认1", name = "pageNum")
            @Max(value = 100)
            @Min(value = 1)
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,

            @ApiParam(value = "每页长度，默认20", name = "pageSize")
            @Max(value = 50)
            @Min(value = 1)
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        Map<String, Object> map = new HashMap<>();
        PageInfo pageInfo = searchService.searchSceneByType(userId, typeId, pageNum, pageSize);
        map.put("list", pageInfo.getList());
        map.put("total", pageInfo.getTotal());

        return new DataResult<>(map);
    }


    @ApiOperation(value = "搜索店铺")
    @Authorization
    @RequestMapping(value = "/search/store",method = RequestMethod.POST)
    public Result searchStore(
            @ApiParam(name = "keyword", value = "搜索的内容")
            @RequestParam(required = false) String keyword,

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

            @ApiParam(value = "店铺风格类型 1-影棚  2-实景  （不选不传或传0）")
            @RequestParam(required = false) Integer style,

            @ApiParam(value = "店铺用户类型 1-个人店铺 2-商家店铺 3-地接店铺 （不选不传或传0）")
            @RequestParam(required = false) Integer type,

            @ApiIgnore
            @CurrentUser UserContext userContext) {

        Integer userId = userContext.getUserId();

        PageInfo pageInfoStore = searchService.searchStore(userId, keyword, address, style, type, pageNum, pageSize);

        Map<String, Object> map = new HashMap<>();
        map.put("store", pageInfoStore.getList());
        map.put("storeTotal", pageInfoStore.getTotal());

        searchService.addSearchLog(keyword, userContext.getUserId(), 1, null);

        return new DataResult<>(map);
    }


}
