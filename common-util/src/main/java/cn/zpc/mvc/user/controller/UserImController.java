package cn.zpc.mvc.user.controller;

import cn.zpc.common.web.result.DataResult;
import cn.zpc.common.web.result.Result;
import cn.zpc.mvc.user.security.UserContext;
import cn.zpc.mvc.user.security.annotation.Authorization;
import cn.zpc.mvc.user.security.annotation.CurrentUser;
import cn.zpc.mvc.user.service.UserImService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Description:
 * User: sukai
 * Date: 2018-04-09   17:36
 */
@Api(description = "用户即时通讯控制")
@RestController
public class UserImController {

    @Autowired
    private UserImService userImService;


    @ApiOperation(value = "获取用户的token")
    @Authorization
    @RequestMapping(value = "/user/im/token", method = RequestMethod.POST)
    public Result getImToken(
            @ApiIgnore
            @CurrentUser UserContext userContext){


        return new DataResult<>(userImService.userRegisterToken(userContext.getUserId()));
    }


}
