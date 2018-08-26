package cn.zpc.mvc.store.controller;

import cn.zpc.common.entity.UrlEntity;
import cn.zpc.common.handler.exception.GlobalExceptionResult;
import cn.zpc.common.plugins.oss.OssPathConfig;
import cn.zpc.common.serivce.BaseService;
import cn.zpc.common.utils.FileUtils;
import cn.zpc.common.web.result.DataResult;
import cn.zpc.common.web.result.MessageResult;
import cn.zpc.common.web.result.Result;
import cn.zpc.mvc.store.dao.StoreDao;
import cn.zpc.mvc.store.dao.StorePeripheryDao;
import cn.zpc.mvc.store.entity.Store;
import cn.zpc.mvc.store.entity.StorePeriphery;
import cn.zpc.mvc.store.service.StoreManageService;
import cn.zpc.mvc.store.service.StoreService;
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
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Api(description = "店铺周边模块(管理接口)")
@RestController
public class StorePeripheryController extends BaseService{

    @Autowired
    private StoreService storeService;
    @Autowired
    private StoreManageService storeManageService;
    @Autowired
    private StoreDao storeDao;
    @Autowired
    private StorePeripheryDao storePeripheryDao;


    @ApiOperation(value = "(管理接口)获取店铺周边列表")
    @Authorization
    @RequestMapping(value = "/store/periphery/list/get", method = RequestMethod.POST)
    public Result getStorePeripheryInfo(
            @ApiParam(value = "页码")
            @RequestParam(defaultValue = "1") Integer pageNum,

            @ApiParam(value = "每页大小")
            @RequestParam(defaultValue = "10") Integer pageSize,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        Store store = storeDao.getByUser(userId);
        if(store == null){
            throw new GlobalExceptionResult("user.not.have.store", 1002);
        }

        Integer storeId = store.getId();
        Map<String, Object> result = new HashMap<>();

        PageInfo pageInfo = storeService.getStorePeriphery(storeId, pageNum, pageSize);

        result.put("list", pageInfo.getList());
        result.put("total", pageInfo.getTotal());

        return new DataResult<>(result);
    }


    @ApiOperation(value = "(管理接口)获取某一个周边场景信息")
    @Authorization
    @RequestMapping(value = "/store/periphery/get", method = RequestMethod.POST)
    public Result getStorePeripheryInfo(
            @ApiParam(required = true, value = "周边场景Id")
            @RequestParam Integer peripheryId,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        Store store = storeDao.getByUser(userId);
        if(store == null){
            throw new GlobalExceptionResult("user.not.have.store", 1002);
        }
        StorePeriphery storePeriphery = storePeripheryDao.get(peripheryId);
        if(storePeriphery == null || !storePeriphery.getStoreId().equals(store.getId())){
            throw new GlobalExceptionResult("periphery.notExist", 1002);
        }

        return new DataResult<>(storeService.getSinglePeriphery(peripheryId));
    }


    @ApiOperation(value = "(管理接口)上传店铺周边图片")
    @Authorization
    @RequestMapping(value = "/store/periphery/image/upload", method = RequestMethod.POST)
    public Result getStorePeripheryInfo(
            @ApiParam(required = true)
            @RequestParam MultipartFile file,

            HttpServletRequest request,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        Store store = storeDao.getByUser(userId);
        if(store == null){
            throw new GlobalExceptionResult("user.not.have.store", 1002);
        }

        String str = request.getSession().getServletContext().getRealPath("upload");
        File newFile = FileUtils.transferFile(file, userId, str);
        String url = ossService.putFile(OssPathConfig.getStorePeripheryPath(newFile.getName()), newFile);
        newFile.deleteOnExit();

        return new DataResult<>(new UrlEntity(newFile.getName(), url));
    }


    @ApiOperation(value = "(管理接口)编辑店铺周边保存")
    @Authorization
    @RequestMapping(value = "/store/periphery/edit", method = RequestMethod.POST)
    public Result getStorePeripheryInfo(
            @ApiParam(required = true, value = "描述")
            @RequestParam String desc,

            @ApiParam(required = true, value = "周边的Id")
            @RequestParam Integer peripheryId,

            @ApiParam(value = "新增的图片 fileKey 多个以空格隔开")
            @RequestParam(required = false) String saveFileKey,

            @ApiParam(value = "要删除的图片 fileKey 多个以空格隔开")
            @RequestParam(required = false) String deleteFileKey,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        Store store = storeDao.getByUser(userId);
        if(store == null){
            throw new GlobalExceptionResult("user.not.have.store", 1002);
        }

        storeManageService.editPeriphery(saveFileKey, deleteFileKey, peripheryId, desc);

        return new MessageResult();
    }


    @ApiOperation(value = "(管理接口)删除店铺周边")
    @Authorization
    @RequestMapping(value = "/store/periphery/delete", method = RequestMethod.POST)
    public Result deleteStorePeriphery(
            @ApiParam(required = true, value = "周边的Id, 多个以空格隔开")
            @RequestParam String peripheryIdStr,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        Store store = storeDao.getByUser(userId);
        if(store == null){
            throw new GlobalExceptionResult("user.not.have.store", 1002);
        }

        storeManageService.deletePeriphery(peripheryIdStr, store.getId());

        return new MessageResult();
    }


    @ApiOperation(value = "(管理接口)新增店铺周边保存")
    @Authorization
    @RequestMapping(value = "/store/periphery/add", method = RequestMethod.POST)
    public Result getStorePeripheryInfo(
            @ApiParam(required = true, value = "图片描述")
            @RequestParam String desc,

            @ApiParam(required = true, value = "要保存的图片，多个fileKey以空格隔开")
            @RequestParam String fileKey,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        Store store = storeDao.getByUser(userId);
        if(store == null){
            throw new GlobalExceptionResult("user.not.have.store", 1002);
        }
        Integer storeId = store.getId();
        storeManageService.addPeriphery(storeId, fileKey, desc);

        return new MessageResult();
    }


}
