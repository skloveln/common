package cn.zpc.mvc.user.controller;

import cn.zpc.common.plugins.oss.OssPathConfig;
import cn.zpc.common.serivce.BaseService;
import cn.zpc.common.utils.StringUtils;
import cn.zpc.common.web.result.DataResult;
import cn.zpc.common.web.result.MessageResult;
import cn.zpc.common.web.result.Result;
import cn.zpc.mvc.user.dao.AdvertDao;
import cn.zpc.mvc.user.entity.PageAdvert;
import cn.zpc.mvc.user.entity.UserDevice;
import cn.zpc.mvc.user.security.UserContext;
import cn.zpc.mvc.user.security.annotation.Authorization;
import cn.zpc.mvc.user.security.annotation.CurrentUser;
import cn.zpc.mvc.user.service.UserAppService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotNull;

/**
 * Description:应用控制
 * Author: sukai
 * Date: 2017-08-28
 */

@Api(description = "应用设备控制")
@RestController
public class UserAppController extends BaseService {

    @Autowired
    private UserAppService userAppService;
    @Autowired
    private AdvertDao advertDao;


    @ApiOperation(value = "更新应用", notes = "根据版本渠道, 更新")
    @RequestMapping(value = "/app/update", method = RequestMethod.POST)
    public Result update(
            @ApiParam("系统平台, ios-1  android-2")
            @RequestParam("appOs")
            @Range(min = 1, max = 2, message = "user.app.update.appOS.range")
            @NotNull    Integer appOs ){


       return userAppService.getNewVersionInfo(appOs);
    }


    /**
     * 上传设备信息
     */
    @ApiOperation(value = "上传设备信息")
    @Authorization
    @RequestMapping(value = "/user/upload/device/info", method = RequestMethod.POST)
    public Result uploadDeviceInfo(
            @ApiParam(value = "友盟的device_token", required = true)
            @RequestParam String deviceToken,

            @ApiParam(value = "设备的IMEI号")
            @RequestParam(required = false)  String deviceImei,

            @ApiParam(value = "系统类型")
            @RequestParam  String appOS,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        UserDevice userDevice = new UserDevice();
        userDevice.setUserId(userContext.getUserId());
        userDevice.setDevicePushId(deviceToken);
        userDevice.setAppOS(Integer.parseInt(appOS));
        if(StringUtils.isNotEmpty(deviceImei)) {
            userDevice.setDeviceImei(deviceImei);
        }
        userAppService.saveDeviceInfo(userDevice);

        return MessageResult.getNormalMessage();
    }


    @ApiOperation(value = "用户反馈")
    @Authorization
    @RequestMapping(value = "/user/feedback", method = RequestMethod.POST)
    public Result userFeed(
            @ApiParam(value = "反馈内容")
            @RequestParam String feedContent,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();

        userAppService.saveFeedInfo(feedContent, userId);

        return MessageResult.getNormalMessage();
    }


    @ApiOperation(value = "闪屏页广告", notes = "type:   1-场景 2-店铺 3-外链<br/>" +
            "ext:   type=1 场景Id  type=2 电铺Id type=3 url链接<br/>" +
            "imageUrl:   广告图片链接<br/>")
    @RequestMapping(value = "/app/advert/page", method = RequestMethod.POST)
    public Result advertPage(){

        PageAdvert pageAdvert = advertDao.getAdvert();
        pageAdvert.setImageUrl(ossService.getOriginUrl(OssPathConfig.getAdvertPath(pageAdvert.getImageUrl())));

        return new DataResult<>(pageAdvert);
    }


}
