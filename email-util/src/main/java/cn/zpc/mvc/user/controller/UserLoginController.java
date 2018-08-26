package cn.zpc.mvc.user.controller;

import cn.zpc.common.serivce.BaseService;
import cn.zpc.common.utils.*;
import cn.zpc.common.web.result.DataResult;
import cn.zpc.common.web.result.Result;
import cn.zpc.common.web.validators.sequence.First;
import cn.zpc.common.web.validators.sequence.Second;
import cn.zpc.mvc.user.entity.User;
import cn.zpc.mvc.user.security.config.JwtSetting;
import cn.zpc.mvc.user.security.factory.AuthTokenFactory;
import cn.zpc.mvc.user.security.UserContext;
import cn.zpc.mvc.user.security.model.BaseToken;
import cn.zpc.mvc.user.service.UserService;
import cn.zpc.mvc.user.service.UserWechatService;
import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;

/**
 * Description:用户控制器
 * 登录，注册，修改密码
 * Author: sukai
 * Date: 2017-08-16
 */
@RestController
@Api(description = "用户登录注册管理", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated({First.class, Second.class, Default.class})
public class UserLoginController extends BaseService{

    private final AuthTokenFactory tokenFactory;
    private final UserService userService;
    private final JwtSetting jwtSetting;
    private final UserWechatService userWechatService;

    @Autowired
    public UserLoginController(AuthTokenFactory tokenFactory, UserService userService, JwtSetting jwtSetting, UserWechatService userWechatService) {
        this.tokenFactory = tokenFactory;
        this.userService = userService;
        this.jwtSetting = jwtSetting;
        this.userWechatService = userWechatService;
    }


    @ApiOperation(value = "发送登录验证码", notes = "根据手机号发送验证码")
    @RequestMapping(value = "/user/send/login/code", method = RequestMethod.POST)
    public Result getLoginCode(
            @ApiParam(name = "phone", value = "手机号")
            @RequestParam
            @NotEmpty(message = "手机号不能为空", groups = First.class)
            @Pattern(regexp = PatternUtils.PATTERN_PHONE, message = "请输入正确的手机号")
            String phone){

        return userService.sendLoginCode(phone);
    }


    @ApiOperation(value = "发送注册验证码", notes = "根据手机号发送验证码")
    @RequestMapping(value = "/user/send/register/code", method = RequestMethod.POST)
    public Result getRegisterCode(
            @ApiParam(name = "phone", value = "手机号")
            @RequestParam
            @NotEmpty(message = "手机号不能为空", groups = First.class)
            @Pattern(regexp = PatternUtils.PATTERN_PHONE, message = "请输入正确的手机号")
                    String phone){

        return userService.sendRegisterCode(phone);
    }


    @ApiOperation(value = "发送修改密码验证码", notes = "根据手机号发送验证码")
    @RequestMapping(value = "/user/send/reset/password/code", method = RequestMethod.POST)
    public Result getResetPasswordCode(
            @ApiParam(name = "phone", value = "手机号")
            @RequestParam
            @NotEmpty(message = "手机号不能为空", groups = First.class)
            @Pattern(regexp = PatternUtils.PATTERN_PHONE, message = "请输入正确的手机号")
                    String phone){

        return userService.sendResetPasswordCode(phone);
    }


    @ApiOperation(value = "发送绑定手机验证码", notes = "根据手机号发送验证码</br>" +
            "data: true 已经注册 false 没有注册</br>")
    @RequestMapping(value = "/user/send/bind/phone/code", method = RequestMethod.POST)
    public Result getBindPhoneCode(
            @ApiParam(name = "phone", value = "手机号")
            @RequestParam
            @NotEmpty(message = "手机号不能为空", groups = First.class)
            @Pattern(regexp = PatternUtils.PATTERN_PHONE, message = "请输入正确的手机号")
                    String phone){

        return userService.sendBindPhoneCode(phone);
    }


    @ApiOperation(value = "使用验证码登录", notes = "校验验证码")
    @RequestMapping(value = "/user/login/with/code", method = RequestMethod.POST)
    public Result verifyCode(
            @ApiParam(name = "phone", value = "电话号")
            @RequestParam String phone,

            @ApiParam(name = "code", value = "验证码")
            @RequestParam String code){

        return userService.loginWithCode(phone, code);
    }


    @ApiOperation(value = "使用密码登录", notes = "使用密码登录账号")
    @RequestMapping(value = "/user/login", method = RequestMethod.POST)
    public Result login(
            @ApiParam(value = "手机号", name = "phone")
            @RequestParam String phone,
            @ApiParam(value = "密码", name = "password")
            @RequestParam String password){

        return userService.loginWithPassword(phone, password);
    }


    @ApiOperation(value = "刷新登录令牌", notes = "刷新登录令牌,更新用户信息")
    @RequestMapping(value = "/user/swap/token", method = RequestMethod.POST)
    public Result swapToken(
            @ApiParam(value = "长效刷新令牌", name = "refreshToken")
            @NotEmpty(message = "令牌不能为空")
            @RequestParam("refreshToken") String swapToken){

        BaseToken refreshTokenOld = new BaseToken(swapToken);
        Jws<Claims> jwsClaims = refreshTokenOld.parseClaims(jwtSetting.getTokenSigningKey());
        String subject = jwsClaims.getBody().getSubject();
        UserContext context = UserContext.create(subject);
        // 刷新用户的Toke，accessToken和refreshToken都刷新
        userService.refreshTokenByUserId(context.getUserId(), swapToken);
        BaseToken accessJwtToken = tokenFactory.createAccessJwtToken(context);
        BaseToken refreshToken = tokenFactory.createRefreshToken(context);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accessToken", accessJwtToken.getToken());
        jsonObject.put("refreshToken", refreshToken.getToken());

        DataResult<JSONObject> dataResult = new DataResult<>();
        dataResult.setData(jsonObject);
        return dataResult;
    }


    @ApiOperation(value = "判断用户是否注册", notes = "根据手机号判断用户是否注册过")
    @RequestMapping(value = "/user/register/status", method = RequestMethod.POST)
    public Result registerStatus(
            @ApiParam(value = "手机号", name = "phone", required = true)
            @RequestParam
            @NotEmpty(message = "手机号不能为空") String phone){

        User user = userService.getUser(phone);
        return new DataResult<>(user != null);
    }


    @ApiOperation(value = "注册成为用户", notes = "使用验证码密码手机号注册，如果已经注册用户返回错误")
    @RequestMapping(value = "/user/register", method = RequestMethod.POST)
    public Result register(
            @ApiParam(value = "手机号", name = "phone", required = true)
            @RequestParam
            @NotEmpty(message = "手机号不能为空") String phone,

            @ApiParam(value = "密码", name = "password", required = true)
            @RequestParam
            @Size(min = 6, max = 18, message = "密码长度为6-18位") String password,

            @ApiParam(value = "验证码", name = "code", required = true)
            @RequestParam
            @NotEmpty(message = "验证码不能为空") String code){

        return userService.register(phone, password, code, 0, "其他");
    }


    @ApiOperation(value = "忘记密码，重置密码", notes = "使用验证码手机号重置密码")
    @RequestMapping(value = "/user/reset/password", method = RequestMethod.POST)
    public Result forgetPassword(
            @ApiParam(name = "phone", value = "手机号", required = true)
            @RequestParam
            @NotNull(message = "user.phone.notNull")
            @NotEmpty(message = "user.phone.notEmpty")
            @Pattern(regexp = PatternUtils.PATTERN_PHONE, message = "user.phone.pattern") String phone,

            @RequestParam
            @ApiParam(name = "password", value = "密码", required = true)
            @NotEmpty(message = "user.password.notEmpty")
            @Size(min = 6, max = 18, message = "user.password.size") String password,

            @ApiParam(name = "code", value = "验证码", required = true)
            @RequestParam String code){

        return userService.resetPassword(phone, password, code);
    }


    @ApiOperation(value = "微信登录", notes = "使用微信登录返回结果状态码</br>" +
            "code：  2001  未绑定手机号</br>" +
            "code：  1002  异常</br>" +
            "code：  1000  登录成功</br>")
    @RequestMapping(value = "/user/wechat/login", method = RequestMethod.POST)
    public Result wechatLogin(
            @ApiParam(name = "code", value = "微信code码", required = true)
            @RequestParam String code,  HttpServletRequest request){

        String filePath = request.getServletContext().getRealPath("/WEB-INF/upload");
        return  userWechatService.login(code, filePath);
    }


    @ApiOperation(value = "微信绑定手机号", notes = "使用微信登录</br>")
    @RequestMapping(value = "/user/wechat/bind/phone", method = RequestMethod.POST)
    public Result wechatLogin(
            @ApiParam(name = "openId", value = "openId", required = true)
            @RequestParam String openId,

            @ApiParam(name = "phone", value = "手机号", required = true)
            @RequestParam
            @NotNull(message = "user.phone.notNull")
            @NotEmpty(message = "user.phone.notEmpty")
            @Pattern(regexp = PatternUtils.PATTERN_PHONE, message = "user.phone.pattern") String phone,

            @ApiParam(name = "code", value = "手机验证码", required = true)
            @RequestParam String code,

            @RequestParam(required = false)
            @ApiParam(name = "password", value = "设置密码")
            @Size(min = 6, max = 18, message = "user.password.size") String password){

        return userService.bindPhone(openId, phone, code, password);
    }

}
