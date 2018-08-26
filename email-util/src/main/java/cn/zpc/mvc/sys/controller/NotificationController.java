package cn.zpc.mvc.sys.controller;

import cn.zpc.common.serivce.BaseService;
import cn.zpc.common.web.result.DataResult;
import cn.zpc.common.web.result.Result;
import cn.zpc.mvc.sys.entity.Notification;
import cn.zpc.mvc.sys.service.NotificationService;
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

import java.util.HashMap;
import java.util.Map;

@Api(description = "通知控制器")
@RestController
public class NotificationController extends BaseService{

    @Autowired
    private NotificationService notificationService;


    @ApiOperation(value = "获取通过审核的消息列表", notes = "字段<br/>" +
            "type 1-认证通知 2-场景审核通知" +
            "ext 扩展项 type=2是场景Id" +
            "content 通知内容<br/>" +
            "imageUrl 小图")
    @Authorization
    @RequestMapping(value = "/notice/pass", method = RequestMethod.POST)
    public Result getPass(
            @ApiParam(value = "页码")
            @RequestParam(defaultValue = "1") Integer pageNum,

            @ApiParam(value = "每页大小")
            @RequestParam(defaultValue = "20") Integer pageSize,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Map<String, Object> map = new HashMap<>();
        PageInfo pageInfo = notificationService.getPass(pageNum, pageSize, userContext.getUserId());
        map.put("total", pageInfo.getTotal());
        map.put("list", pageInfo.getList());
        return new DataResult<>(map);
    }


    @ApiOperation(value = "获取未通过审核的消息列表", notes = "字段<br/>" +
            "type 1-认证通知 2-场景审核通知" +
            "ext 扩展项 type为2是场景Id" +
            "content 通知内容<br/>" +
            "imageUrl 小图")
    @Authorization
    @RequestMapping(value = "/notice/no/pass", method = RequestMethod.POST)
    public Result getNoPass(
            @ApiParam(value = "页码")
            @RequestParam(defaultValue = "1") Integer pageNum,

            @ApiParam(value = "每页大小")
            @RequestParam(defaultValue = "20") Integer pageSize,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Map<String, Object> map = new HashMap<>();
        PageInfo pageInfo = notificationService.getNoPass(pageNum, pageSize, userContext.getUserId());
        map.put("total", pageInfo.getTotal());
        map.put("list", pageInfo.getList());
        return new DataResult<>(map);
    }


    @ApiOperation(value = "获取系统通知的消息列表", notes = "字段<br/>" +
            "type 3-图集屏蔽通知 4-网页链接 5-场景推送 type=6 商铺Id type=7 简版场景id<br>" +
            "content 通知内容<br/>" +
            "imageUrl 小图")
    @Authorization
    @RequestMapping(value = "/notice/system", method = RequestMethod.POST)
    public Result getSystem(
            @ApiParam(value = "页码")
            @RequestParam(defaultValue = "1") Integer pageNum,

            @ApiParam(value = "每页大小")
            @RequestParam(defaultValue = "20") Integer pageSize,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Map<String, Object> map = new HashMap<>();
        PageInfo pageInfo = notificationService.getSystem(pageNum, pageSize, userContext.getUserId());
        map.put("total", pageInfo.getTotal());
        map.put("list", pageInfo.getList());
        return new DataResult<>(map);
    }
}
