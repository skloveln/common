package cn.zpc.mvc.user.controller;

import cn.zpc.common.serivce.BaseService;
import cn.zpc.common.utils.StringUtils;
import cn.zpc.common.web.result.DataResult;
import cn.zpc.common.web.result.Result;
import cn.zpc.common.web.result.MessageResult;
import cn.zpc.mvc.user.security.UserContext;
import cn.zpc.mvc.user.security.annotation.Authorization;
import cn.zpc.mvc.user.security.annotation.CurrentUser;
import cn.zpc.mvc.user.service.UserCollectionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Description:用户收藏箱
 * Author: sukai
 * Date: 2017-08-28
 */
@RestController
@Api(description = "用户收藏箱", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class UserCollectController extends BaseService {

    @Autowired
    private UserCollectionService userCollectionService;


    /**
     * 收藏某条信息
     */
    @Authorization
    @ApiOperation(value = "收藏某条信息", notes = "收藏信息不限类型")
    @RequestMapping(value = "/user/collect", method = RequestMethod.POST)
    public Result collectOne(
            @ApiParam(name = "targetId", value = "收藏的对应信息的Id", required = true)
            @RequestParam("targetId")
            @NotNull    int targetId,

            @ApiParam(name = "targetType", value = "收藏的目标类型, 店铺为0，场景为1，图集为2", required = true)
            @RequestParam("targetType")
            @NotNull    int targetType,

            @ApiIgnore
            @CurrentUser    UserContext userContext){

        Integer userId = userContext.getUserId();

        userCollectionService.insertUserCollection(targetType, targetId, userId);

        return MessageResult.getNormalMessage();
    }


    /**
     * 取消收藏某条信息
     */
    @Authorization
    @ApiOperation(value = "取消收藏一条信息", notes = "根据编号取消收藏信息")
    @RequestMapping(value = "/user/collect/cancel", method = RequestMethod.POST)
    public Result cancel(
            @ApiParam(name = "targetId", value = "对应信息的编号Id", required = true)
            @RequestParam("targetId")
            @NotNull     int targetId,

            @ApiParam(name = "targetType", value = "目标类型, 店铺为0，场景为1，图集为2", required = true)
            @RequestParam("targetType")
            @NotNull     int targetType,

            @ApiIgnore
            @CurrentUser    UserContext userContext){

        Integer userId = userContext.getUserId();

        userCollectionService.userCancelCollection(targetType, targetId, userId);

        return MessageResult.getNormalMessage();

    }

    /**
     * 取消收藏多条信息
     */
    @Authorization
    @ApiOperation(value = "取消收藏多条信息", notes = "根据编号取消收藏信息")
    @RequestMapping(value = "/user/collect/cancel/list", method = RequestMethod.POST)
    public Result cancel(
            @ApiParam(name = "targetId", value = "对应信息Id集合，空格隔开，eg:{10010 10083 20303}", required = true)
            @RequestParam("targetId")
            @NotEmpty    String targetIdList,

            @ApiParam(name = "targetType", value = "目标类型, 店铺为0，场景为1，图集为2", required = true)
            @RequestParam("targetType")
            @NotNull     int targetType,

            @ApiIgnore
            @CurrentUser    UserContext userContext){

        Integer userId = userContext.getUserId();

        List<String> list = StringUtils.splitString(targetIdList);

        for(String targetId : list) {
            userCollectionService.userCancelCollection(targetType, Integer.parseInt(targetId), userId);
        }

        return MessageResult.getNormalMessage();
    }


    /**
     * 获取店铺收藏列表
     */
    @Authorization
    @ApiOperation(value = "获取店铺收藏列表", notes = "店铺收藏列表")
    @RequestMapping(value = "/user/collection/store", method = RequestMethod.POST)
    public Result collectionStore(
            @ApiParam(value = "当前页码，最大100", name = "pageNum")
            @Max(value = 100)
            @Min(value = 1)
            @RequestParam(value = "pageNum", defaultValue = "1")    int pageNum,

            @ApiParam(value = "每页长度，最大50", name = "pageSize")
            @Max(value = 50)
            @Min(value = 1)
            @RequestParam(value = "pageSize", defaultValue = "10")    int pageSize,

            @ApiIgnore
            @CurrentUser    UserContext userContext) {


        return new DataResult<>(userCollectionService.getCollectStoreInfo(userContext.getUserId(), pageNum, pageSize));
    }


    /**
     * 获取场景收藏列表
     */
    @Deprecated
    @Authorization
    @ApiOperation(value = "获取场景收藏列表", notes = "场景收藏列表")
    @RequestMapping(value = "/user/collection/scene", method = RequestMethod.POST)
    public Result collectionScene(
            @ApiParam(value = "当前页码，最大100", name = "pageNum")
            @Max(value = 100)
            @Min(value = 1)
            @RequestParam(value = "pageNum", defaultValue = "1")    int pageNum,

            @ApiParam(value = "每页长度，最大50", name = "pageSize")
            @Max(value = 50)
            @Min(value = 1)
            @RequestParam(value = "pageSize", defaultValue = "10")    int pageSize,

            @ApiIgnore
            @CurrentUser    UserContext userContext) {


        return userCollectionService.getCollectSceneInfo(userContext.getUserId(), pageNum, pageSize);
    }


    /**
     * 获取新场景（包括图集）收藏列表
     */
    @Authorization
    @ApiOperation(value = "获取场景(包括图集)收藏列表", notes = "场景(包括图集)收藏列表")
    @RequestMapping(value = "/user/collection/scene/album", method = RequestMethod.POST)
    public Result collectionAlbum(
            @ApiParam(value = "当前页码，最大100", name = "pageNum")
            @Max(value = 100)
            @Min(value = 1)
            @RequestParam(value = "pageNum", defaultValue = "1")    int pageNum,

            @ApiParam(value = "每页长度，最大50", name = "pageSize")
            @Max(value = 50)
            @Min(value = 1)
            @RequestParam(value = "pageSize", defaultValue = "10")    int pageSize,

            @ApiIgnore
            @CurrentUser    UserContext userContext) {


        return userCollectionService.getCollectSceneAlbumInfo(userContext.getUserId(), pageNum, pageSize);
    }
}
