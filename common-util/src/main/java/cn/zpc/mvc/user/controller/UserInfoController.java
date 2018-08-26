package cn.zpc.mvc.user.controller;

import cn.zpc.common.handler.exception.GlobalExceptionResult;
import cn.zpc.common.serivce.BaseService;
import cn.zpc.common.utils.FileUtils;
import cn.zpc.common.utils.PatternUtils;
import cn.zpc.common.utils.StringUtils;
import cn.zpc.common.web.result.DataResult;
import cn.zpc.common.web.result.MessageResult;
import cn.zpc.common.web.result.Result;
import cn.zpc.common.web.validators.sequence.First;
import cn.zpc.common.web.validators.sequence.Second;
import cn.zpc.mvc.album.service.AlbumService;
import cn.zpc.mvc.store.entity.Store;
import cn.zpc.mvc.store.service.StoreService;
import cn.zpc.mvc.user.dao.UserDao;
import cn.zpc.mvc.user.entity.User;
import cn.zpc.mvc.user.entity.UserWechat;
import cn.zpc.mvc.user.param.UserParam;
import cn.zpc.mvc.user.security.UserContext;
import cn.zpc.mvc.user.security.annotation.Authorization;
import cn.zpc.mvc.user.security.annotation.CurrentUser;
import cn.zpc.mvc.user.service.UserService;
import cn.zpc.mvc.user.service.UserWechatService;
import cn.zpc.mvc.user.utils.UserUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Description: 用户信息
 * Author: sukai
 * Date: 2017-08-28
 */
@Api(description = "用户信息管理", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@Validated({First.class, Second.class, Default.class})
public class UserInfoController extends BaseService {

    @Autowired
    private UserWechatService userWechatService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private AlbumService albumService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private UserService userService;


    @ApiOperation(value = "获取(一组或单个)用户信息", notes = "职业信息选择")
    @Authorization
    @RequestMapping(value = "/user/someone/info", method = RequestMethod.POST)
    public Result getUserInfo(
            @ApiParam(value = "用户Id集合", required = true)
            @RequestParam
            @NotEmpty String userIds){

        List<User> result = new ArrayList<>();
        List<Integer> list = StringUtils.splitInteger(userIds);
        for(Integer i : list){
            result.add(userService.getUser(i));
        }

        return new DataResult<>(result);
    }


    @ApiOperation(value = "职业选择", notes = "职业信息选择")
    @Authorization
    @RequestMapping(value = "/user/profession/select", method = RequestMethod.POST)
    public Result selectProfession(){

        List<String> list = userDao.selectProfession();

        return new DataResult<>(list);
    }


    @ApiOperation(value = "上传头像", notes = "上传头像需要验证身份信息")
    @Authorization
    @RequestMapping(value = "/user/upload/avatar", method = RequestMethod.POST)
    public Result uploadAvatar(
            @ApiParam(required = true)
            @RequestParam
            MultipartFile file,

            HttpServletRequest request,

            @ApiIgnore
            @CurrentUser UserContext userContext) {

        String originalFilename = file.getOriginalFilename(); // 获取原文件名
        String fileName = UserUtils.generateToken(String.valueOf(userContext.getUserId()));  // 根据ID生成新的文件名
        if (originalFilename.contains(".")) { // 加文件后缀
            String fileExt = originalFilename.substring(originalFilename.lastIndexOf("."));
            fileName += fileExt;
        }
        String path = request.getSession().getServletContext().getRealPath("upload");
        File targetFile = new File(path, fileName);
        try {
            FileUtils.transferTo(file, targetFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new GlobalExceptionResult("upload.file.exception", Result.EXCEPTION);
        }
        return userService.uploadUserAvatar(fileName, targetFile, userContext.getUserId());
    }


    @ApiOperation(value = "更新个人信息", notes = "个人信息更新，除图片")
    @Authorization
    @RequestMapping(value = "/user/update/info", method = RequestMethod.POST)
    public Result updateUser(@Validated UserParam userParam,
                             @ApiIgnore
                             @CurrentUser UserContext userContext) {
        if (userParam == null) {
            throw new GlobalExceptionResult("user.param.notNull", Result.EXCEPTION);
        }
        User user = userParam.getUser();
        user.setId(userContext.getUserId()); // 设置token所携带的用户编号

        return userService.updateInfo(user);
    }


    @ApiOperation(value = "获取个人信息", notes = "获取个人资料 <br>gender（0-男 1-女）   <br>status（0-普通用户 1-店铺用户 2-认证中）   <br>storeType（1-个人店铺用户 2-商家店铺用户 3-地接店铺用户）")
    @Authorization
    @RequestMapping(value = "/user/get/info", method = RequestMethod.POST)
    public Result getUserInfo(
            @ApiIgnore
            @CurrentUser UserContext userContext) {

        User user = userService.getUser(userContext.getUserId());

        return DataResult.getNormal(user);
    }


    @ApiOperation(value = "修改登录密码", notes = "修改登录密码")
    @Authorization
    @RequestMapping(value = "/user/update/password", method = RequestMethod.POST)
    public Result updateUserPassword(
            @ApiParam(value = "原密码", name = "oldpwd", required = true)
            @RequestParam
            @NotEmpty(message = "原密码不能为空") String oldpwd,

            @ApiParam(value = "新密码", name = "newpwd", required = true)
            @RequestParam
            @NotEmpty(message = "新密码不能为空") String newpwd,

            @ApiIgnore
            @CurrentUser UserContext userContext) {
        User user = userService.get(userContext.getUserId());
        String salt = RandomStringUtils.randomAlphanumeric(10);
        if(user.getPassword().equals(userService.encryptPassword(user.getPhone(), oldpwd, user.getSalt()))){
            String newPassword = userService.encryptPassword(user.getPhone(), newpwd, salt);
            user.setSalt(salt);
            user.setPassword(newPassword);
            userService.update(user);
            return MessageResult.getNormalMessage();
        }else {
            return MessageResult.getExceptionMessage("原密码错误");
        }
    }


    @ApiOperation(value = "查看某用户资料(新)", notes = "此接口图集只给2天的数据, 查看更多请求->/album/user/list")
    @Authorization
    @RequestMapping(value = "/user/info/page", method = RequestMethod.POST)
    public Result userInfoPage(
            @ApiParam()
            @RequestParam Integer userId,

            @ApiIgnore
            @CurrentUser UserContext userContext) {

        Integer viewId = userContext.getUserId();
        // 用户信息
        User user = userService.getUser(userId);
        // 图册
        PageInfo pageInfo = albumService.getGroupByTime(1, 2, userId, viewId);
        // 店铺信息
        Store store = storeService.getStoreInfoByUser(userId, viewId.equals(userId));
        Map<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("album", pageInfo.getList());
        map.put("isStore",userService.isStoreUser(userId, viewId.equals(userId)));
        map.put("storeInfo", store);

        return new DataResult<>(map);
    }


    @Deprecated
    @ApiOperation(value = "查看某用户资料")
    @Authorization
    @RequestMapping(value = "/view/user/info", method = RequestMethod.POST)
    public Result viewUserInfo(
            @ApiParam()
            @RequestParam Integer userId,

            @ApiIgnore
            @CurrentUser UserContext userContext) {

        Integer viewId = userContext.getUserId();
        // 用户信息
        User user = userService.getUser(userId);
        // 图册
        PageInfo pageInfo = albumService.getUserAlbum(1, 5, userId, viewId);
        // 店铺信息
        Store store = storeService.getStoreInfoByUser(userId, viewId.equals(userId));
        Map<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("album", pageInfo.getList());
        map.put("isStore",userService.isStoreUser(userId, viewId.equals(userId)));
        map.put("storeInfo", store);

        return new DataResult<>(map);
    }


    @ApiOperation(value = "查看用户微信绑定情况", notes = "data: bind  是否绑定</br>" +
            "data: nickName  xxxxx 微信昵称")
    @Authorization
    @RequestMapping(value = "/user/wechat/info", method = RequestMethod.POST)
    public Result viewUserWechatInfo(
            @ApiIgnore
            @CurrentUser UserContext userContext) {

        Map<String, Object> map = new HashMap<>();
        Integer userId = userContext.getUserId();
        UserWechat userWechat = userWechatService.findByUserId(userId);
        map.put("bind", userWechat!=null);
        if(userWechat != null){
            map.put("nickName", userWechat.getNickName());
        }
        return new DataResult<>(map);
    }


    @ApiOperation(value = "解绑微信")
    @Authorization
    @RequestMapping(value = "/user/unbind/wechat", method = RequestMethod.POST)
    public Result unbindWechat(
            @ApiIgnore
            @CurrentUser UserContext userContext) {

        Integer userId = userContext.getUserId();
        userWechatService.deleteWechat(userId);

        return new MessageResult();
    }


    @ApiOperation(value = "绑定微信")
    @Authorization
    @RequestMapping(value = "/user/bind/wechat", method = RequestMethod.POST)
    public Result bindWechat(
            @ApiIgnore
            @CurrentUser UserContext userContext,

            @ApiParam(name = "code", value = "微信code码", required = true)
            @RequestParam String code) {

        Integer userId = userContext.getUserId();

        return userWechatService.bind(code, userId);

    }


    @ApiOperation(value = "密码验证")
    @RequestMapping(value = "/user/check/password", method = RequestMethod.POST)
    @Authorization
    public Result login(
            @ApiParam(value = "密码", name = "password")
            @RequestParam String password,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        return new DataResult<>(userService.checkPassword(userContext.getUserId(), password));
    }


    @ApiOperation(value = "发送更换手机验证码", notes = "根据手机号发送验证码</br>")
    @RequestMapping(value = "/user/send/change/phone/code", method = RequestMethod.POST)
    @Authorization
    public Result sendChangePhoneCode(
            @ApiParam(name = "phone", value = "手机号")
            @RequestParam
            @NotEmpty(message = "手机号不能为空", groups = First.class)
            @Pattern(regexp = PatternUtils.PATTERN_PHONE, message = "请输入正确的手机号")
                    String phone){

        return userService.sendChangePhoneCode(phone);
    }


    @ApiOperation(value = "更换绑定手机")
    @RequestMapping(value = "/user/change/phone", method = RequestMethod.POST)
    @Authorization
    public Result changePhone(
            @RequestParam
            @ApiParam(name = "password", value = "密码", required = true)
            @NotEmpty(message = "user.password.notEmpty")
            @Size(min = 6, max = 18, message = "user.password.size") String password,

            @ApiParam(name = "phone", value = "要变更的手机号")
            @RequestParam
            @NotEmpty(message = "手机号不能为空", groups = First.class)
            @Pattern(regexp = PatternUtils.PATTERN_PHONE, message = "请输入正确的手机号")
                    String phone,

            @ApiParam(value = "验证码", name = "code", required = true)
            @RequestParam
            @NotEmpty(message = "验证码不能为空") String code,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();

        return userService.changePhone(phone, code, userId, password);
    }



}