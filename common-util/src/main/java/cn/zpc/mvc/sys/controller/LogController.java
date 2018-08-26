package cn.zpc.mvc.sys.controller;

import cn.zpc.common.serivce.BaseService;
import cn.zpc.common.utils.PropertiesLoader;
import cn.zpc.common.web.result.DataResult;
import cn.zpc.common.web.result.MessageResult;
import cn.zpc.common.web.result.Result;
import cn.zpc.mvc.scene.service.SceneService;
import cn.zpc.mvc.sys.dao.CallLogDao;
import cn.zpc.mvc.sys.dao.SysLogDao;
import cn.zpc.mvc.sys.entity.CallLog;
import cn.zpc.mvc.sys.entity.SysLog;
import cn.zpc.mvc.user.security.UserContext;
import cn.zpc.mvc.user.security.annotation.Authorization;
import cn.zpc.mvc.user.security.annotation.CurrentUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Date;

@Api(description = "日志统计控制器")
@RestController
public class LogController extends BaseService{

    @Autowired
    private SysLogDao sysLogDao;
    @Autowired
    private CallLogDao callLogDao;
    @Autowired
    private SceneService sceneService;

    @ApiOperation(value = "APP埋点统计")
    @Authorization
    @RequestMapping(value = "/sys/log/click",method = RequestMethod.POST)
    public Result searchHome(
            @ApiParam(name = "position", value = "埋点位置, 位置都编号", required = true)
            @RequestParam Integer position,

            @RequestParam(name = "appOS") String appOS,

            @RequestParam(name = "appVersion") String appVersion,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        SysLog sysLog = new SysLog();
        sysLog.setVersion(appVersion);
        sysLog.setCode(position);
        sysLog.setCreateTime(new Date());
        sysLog.setOs(Integer.parseInt(appOS));
        sysLog.setUserId(userContext.getUserId());
        sysLogDao.insert(sysLog);

        return new MessageResult();
    }


    @ApiOperation(value = "打电话统计")
    @Authorization
    @RequestMapping(value = "/sys/log/call",method = RequestMethod.POST)
    public Result searchHome(
            @ApiParam(value = "店铺的编号", required = true)
            @RequestParam Integer storeId,

            @ApiParam(value = "本记号码，能获取到就传过来， 获取不到传空值")
            @RequestParam String phone,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        CallLog callLog = new CallLog();
        callLog.setPhone(phone);
        callLog.setStoreId(storeId);
        callLog.setUserId(userContext.getUserId());
        callLog.setCreateTime(new Date());
        callLogDao.insert(callLog);

        return new MessageResult();
    }


    @ApiOperation(value = "场景数统计")
    @RequestMapping(value = "/scene/count",method = RequestMethod.POST)
    public Result countScene(){

        return new DataResult<>( sceneService.countCityAndScene());
    }

}
