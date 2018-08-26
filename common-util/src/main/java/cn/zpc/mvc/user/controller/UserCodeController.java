package cn.zpc.mvc.user.controller;

import cn.zpc.common.handler.exception.GlobalExceptionResult;
import cn.zpc.common.web.result.DataResult;
import cn.zpc.common.web.result.MessageResult;
import cn.zpc.common.web.result.Result;
import cn.zpc.mvc.user.security.UserContext;
import cn.zpc.mvc.user.security.annotation.Authorization;
import cn.zpc.mvc.user.security.annotation.CurrentUser;
import cn.zpc.mvc.user.service.UserService;
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

/**
 * Description: 用户邀请码
 * User: sukai
 * Date: 2018-03-28   10:31
 */
@Api(description = "用户邀请码")
@RestController
public class UserCodeController {

    @Autowired
    private UserService userService;


    @ApiOperation(value = "判断用户是否是新用户", notes = "data: true , false")
    @RequestMapping(value = "/user/is/new",method = RequestMethod.POST)
    @Authorization
    public Result userIsNew(

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();

        return new DataResult<>(userService.isNewUser(userId));
    }


    @ApiOperation(value = "获取用户邀请码", notes = "返回结果:<br/>" +
            "code: s3qw7p  邀请码<br/>" +
            "num: 4  邀请人数<br/>" +
            "integral: 5   积分")
    @RequestMapping(value = "/user/invitation/code",method = RequestMethod.POST)
    @Authorization
    public Result getUserCode(

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        Map<String, Object> map = new HashMap<>();
        map.put("code", userService.getNewCode(userId));
        map.put("num", userService.getInvitatoryNum(userId));
        map.put("integral", userService.getIntegral(userId));

        return new DataResult<>(map);
    }


    @ApiOperation(value = "输入邀请码")
    @RequestMapping(value = "/user/input/code",method = RequestMethod.POST)
    @Authorization
    public Result getUserCode(

            @ApiParam(value = "邀请码, 前台校验一下不能输入自己的邀请码")
            @RequestParam String code,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        userService.inputCode(userId, code);

        return new MessageResult();
    }


    @ApiOperation(value = "兑换奖励")
    @RequestMapping(value = "/user/exchange/prize",method = RequestMethod.POST)
    @Authorization
    public Result exchangePrize(
            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();

        return userService.exchangePrize(userId);
    }




}
