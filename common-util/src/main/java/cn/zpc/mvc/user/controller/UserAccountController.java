package cn.zpc.mvc.user.controller;

import cn.zpc.common.web.result.DataResult;
import cn.zpc.common.web.result.MessageResult;
import cn.zpc.common.web.result.Result;
import cn.zpc.mvc.user.security.UserContext;
import cn.zpc.mvc.user.security.annotation.Authorization;
import cn.zpc.mvc.user.security.annotation.CurrentUser;
import cn.zpc.mvc.user.service.UserAccountService;
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

@Api(description = "用户账户控制")
@RestController
public class UserAccountController {

    @Autowired
    private UserAccountService userAccountService;

    @ApiOperation(value = "分享成功增加积分")
    @Authorization
    @RequestMapping(value = "/user/integral/add", method = RequestMethod.POST)
    public Result addIntegral(
            @ApiParam(value = "分享的目标类型", required = true)
            @RequestParam Integer targetType,

            @ApiParam(value = "分享的目标Id", required = true)
            @RequestParam Integer targetId,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        userAccountService.addIntegral(userId, targetType, targetId);

        return MessageResult.getNormalMessage();
    }


    @ApiOperation(value = "获取用户积分")
    @Authorization
    @RequestMapping(value = "/user/integral/get", method = RequestMethod.POST)
    public Result getIntegral(
            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        Map<String, Object> map = new HashMap<>();
        map.put("integral", userAccountService.getIntegral(userId));

        return DataResult.getNormal(map);
    }

}
