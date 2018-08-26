package cn.zpc.mvc.store.controller;

import cn.zpc.common.entity.UrlEntity;
import cn.zpc.common.handler.exception.GlobalExceptionResult;
import cn.zpc.common.plugins.oss.OssPathConfig;
import cn.zpc.common.serivce.BaseService;
import cn.zpc.common.utils.FileUtils;
import cn.zpc.common.utils.StringUtils;
import cn.zpc.common.web.result.DataResult;
import cn.zpc.common.web.result.MessageResult;
import cn.zpc.common.web.result.Result;
import cn.zpc.mvc.store.dao.StoreDao;
import cn.zpc.mvc.store.dao.StoreServicesImageDao;
import cn.zpc.mvc.store.entity.Store;
import cn.zpc.mvc.store.service.StoreManageService;
import cn.zpc.mvc.store.service.StoreService;
import cn.zpc.mvc.user.security.UserContext;
import cn.zpc.mvc.user.security.annotation.Authorization;
import cn.zpc.mvc.user.security.annotation.CurrentUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;

@Api(description = "店铺服务模块(管理接口)")
@RestController
public class StoreServicesController extends BaseService {

    @Autowired
    private StoreService storeService;
    @Autowired
    private StoreManageService storeManageService;
    @Autowired
    private StoreDao storeDao;
    @Autowired
    private StoreServicesImageDao storeServicesImageDao;


    @Deprecated
    @ApiOperation(value = "(管理接口)获取店铺服务图片列表")
    @Authorization
    @RequestMapping(value = "/store/services/image/list",method = RequestMethod.POST)
    public Result servicesList(
            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        Store store = storeDao.getByUser(userId);
        if(store == null){
            throw new GlobalExceptionResult("user.not.have.store", 1002);
        }
        Integer storeId = store.getId();

        return new DataResult<>(storeService.getServiceImages(storeId));
    }


    @ApiOperation(value = "(管理接口)获取店铺服务信息")
    @Authorization
    @RequestMapping(value = "/store/services/info/get",method = RequestMethod.POST)
    public Result servicesInfo(
            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        Store store = storeDao.getByUser(userId);
        if(store == null){
            throw new GlobalExceptionResult("user.not.have.store", 1002);
        }
        Integer storeId = store.getId();

        return new DataResult<>(storeManageService.getServicesInfo(storeId));
    }


    @ApiOperation(value = "(管理接口)保存店铺服务信息", notes = "传什么保存什么，没用的不传")
    @Authorization
    @RequestMapping(value = "/store/services/edit/save",method = RequestMethod.POST)
    public Result servicesSave(
            @ApiParam(value = "选择的通用服务typeId集合，eg: 1 2 3")
            @RequestParam(required = false) String general,

            @ApiParam(value = "选择的食住服务typeId集合，eg: 15")
            @RequestParam(required = false) String extra,

            @ApiParam(value = "温馨提示的内容, 不传或传空就是删除原先内容")
            @RequestParam(required = false) String tip,

            @ApiParam(value = "要删除的图片集合")
            @RequestParam(required = false) String fileKeys,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        Store store = storeDao.getByUser(userId);
        if(store == null){
            throw new GlobalExceptionResult("user.not.have.store", 1002);
        }
        Integer storeId = store.getId();

        storeManageService.updateServicesInfo(storeId, general, extra, tip, fileKeys);

        return new MessageResult();
    }


    @Deprecated
    @ApiOperation(value = "(管理接口)保存店铺餐饮服务信息", notes = "传什么保存什么，没用的不传")
    @Authorization
    @RequestMapping(value = "/store/services/save",method = RequestMethod.POST)
    public Result servicesEatSave(
            @ApiParam(value = "餐饮档次信息，空格拼接, eg：15 25 35")
            @RequestParam(required = false) String eatString,

            @ApiParam(value = "房间档次信息，空格拼接, eg：15 25 35")
            @RequestParam(required = false) String stayString,

            @ApiParam(value = "车档次信息，空格拼接,价格和座位数英文逗号隔开,价格在前 eg：150,5  250,7  300,10")
            @RequestParam(required = false) String carString,

            @ApiParam(value = "服务补充描述")
            @RequestParam(required = false) String desc,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        Store store = storeDao.getByUser(userId);
        if(store == null){
            throw new GlobalExceptionResult("user.not.have.store", 1002);
        }
        Integer storeId = store.getId();

        storeService.saveServicesInfo(storeId, eatString, stayString, carString, desc);

        return new MessageResult();
    }


    @ApiOperation(value = "(管理接口)上传店铺服务图片")
    @Authorization
    @RequestMapping(value = "/store/services/image/upload", method = RequestMethod.POST)
    public Result uploadServicesImage(
            @ApiParam(required = true, value = "上传的图片")
            @RequestParam MultipartFile file,

            HttpServletRequest request,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        Store store = storeDao.getByUser(userId);
        if(store == null){
            throw new GlobalExceptionResult("user.not.have.store", 1002);
        }
        Integer storeId = store.getId();

        String path = request.getSession().getServletContext().getRealPath("upload");
        File newFile = FileUtils.transferFile(file, userId, path);
        // 插入图片
        storeManageService.uploadServicesImage(newFile, storeId);
        // 上传图片
        String fileName = newFile.getName();
        String url = ossService.putFile(OssPathConfig.getStoreServicesPath(fileName), newFile);
        // 删除缓存
        newFile.deleteOnExit();
        return new DataResult<>(new UrlEntity(fileName, url));
    }


    @Deprecated
    @ApiOperation(value = "(管理接口)删除店铺服务图片")
    @Authorization
    @RequestMapping(value = "/store/services/image/delete", method = RequestMethod.POST)
    public Result deleteServicesImage(
            @ApiParam(required = true, value = "fileKey 多个以空格隔开")
            @NotEmpty
            @RequestParam String fileKeyStr,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        Store store = storeDao.getByUser(userId);
        if(store == null){
            throw new GlobalExceptionResult("user.not.have.store", 1002);
        }
        Integer storeId = store.getId();

        List<String> list = StringUtils.splitString(fileKeyStr);

        for(String str : list){
            storeServicesImageDao.deleteImage(str, storeId);
        }

        return new MessageResult();
    }


}
