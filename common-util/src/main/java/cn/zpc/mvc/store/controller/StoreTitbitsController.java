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
import cn.zpc.mvc.store.entity.Store;
import cn.zpc.mvc.store.service.StoreManageService;
import cn.zpc.mvc.store.service.StoreService;
import cn.zpc.mvc.user.security.UserContext;
import cn.zpc.mvc.user.security.annotation.Authorization;
import cn.zpc.mvc.user.security.annotation.CurrentUser;
import com.github.pagehelper.PageInfo;
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
import java.text.SimpleDateFormat;
import java.util.*;

@Api(description = "店铺花絮模块(管理接口)")
@RestController
public class StoreTitbitsController extends BaseService{

    @Autowired
    private StoreService storeService;
    @Autowired
    private StoreManageService storeManageService;
    @Autowired
    private StoreDao storeDao;


    @ApiOperation(value = "(管理接口)上传花絮图片")
    @Authorization
    @RequestMapping(value = "/store/titbits/image/upload", method = RequestMethod.POST)
    public Result uploadTitbitsImage(
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

        String str = request.getSession().getServletContext().getRealPath("upload");
        File newFile = FileUtils.transferFile(file, userId, str);
        String url = ossService.putFile(OssPathConfig.getStoreTitbitsPath(newFile.getName()), newFile);
        newFile.deleteOnExit();

        return new DataResult<>(new UrlEntity(newFile.getName(), url));
    }


    @ApiOperation(value = "(管理接口)新增一个花絮")
    @Authorization
    @RequestMapping(value = "/store/titbits/add", method = RequestMethod.POST)
    public Result addTitbits(
            @ApiParam(required = true, value = "时间, 格式：yyyy-MM-dd HH:mm:ss")
            @NotEmpty
            @RequestParam String time,

            @ApiParam(required = true, value = "标题")
            @NotEmpty
            @RequestParam String title,

            @ApiParam(required = true, value = "要保存的图片，多个fileKey以空格隔开")
            @NotEmpty
            @RequestParam String fileKeys,

            @ApiIgnore
            @CurrentUser UserContext userContext) throws Exception {

        Integer userId = userContext.getUserId();
        Store store = storeDao.getByUser(userId);
        if(store == null){
            throw new GlobalExceptionResult("user.not.have.store", 1002);
        }
        Integer storeId = store.getId();

        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
        storeManageService.addTitbits(storeId, title, date, fileKeys);

        return  new MessageResult();
    }


    @ApiOperation(value = "(管理接口)编辑一个花絮保存")
    @Authorization
    @RequestMapping(value = "/store/titbits/update",method = RequestMethod.POST)
    public Result editTitbits(
            @ApiParam(required = true, value = "花絮Id")
            @NotEmpty
            @RequestParam Integer id,

            @ApiParam(required = true, value = "时间, 格式：yyyy-MM-dd HH:mm:ss")
            @NotEmpty
            @RequestParam String time,

            @ApiParam(required = true, value = "标题")
            @NotEmpty
            @RequestParam String title,

            @ApiParam(value = "新增的图片，多个fileKey以空格隔开")
            @RequestParam(required = false) String addFileKeys,

            @ApiParam(value = "要删除的图片，多个fileKey以空格隔开")
            @RequestParam(required = false) String deleteFileKeys,

            @ApiIgnore
            @CurrentUser UserContext userContext) throws Exception{

        Integer userId = userContext.getUserId();
        Store store = storeDao.getByUser(userId);
        if(store == null){
            throw new GlobalExceptionResult("user.not.have.store", 1002);
        }

        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
        storeManageService.editTitbits(id, title, date, addFileKeys, deleteFileKeys);

        return  new MessageResult();
    }


    @ApiOperation(value = "(管理接口)删除一个或多个花絮")
    @Authorization
    @RequestMapping(value = "/store/titbits/delete", method = RequestMethod.POST)
    public Result deleteTitbits(
            @ApiParam(required = true, value = "花絮Id, 多个以空格隔开")
            @NotEmpty
            @RequestParam String idStr,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        Store store = storeDao.getByUser(userId);
        if(store == null){
            throw new GlobalExceptionResult("user.not.have.store", 1002);
        }
        Integer storeId = store.getId();

        storeManageService.deleteTitbits(idStr, storeId);

        return new MessageResult();
    }

    @ApiOperation(value = "(管理接口)获取一个花絮的信息")
    @Authorization
    @RequestMapping(value = "/store/titbits/get", method = RequestMethod.POST)
    public Result getTitbits(
            @ApiParam(required = true, value = "花絮Id")
            @NotEmpty
            @RequestParam Integer id,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        Store store = storeDao.getByUser(userId);
        if(store == null){
            throw new GlobalExceptionResult("user.not.have.store", 1002);
        }
        Integer storeId = store.getId();

        return new DataResult<>(storeService.getTitbits(id, storeId));
    }


    @ApiOperation(value = "(管理接口) 获取店铺花絮列表")
    @Authorization
    @RequestMapping(value = "/store/titbits/list/get", method = RequestMethod.POST)
    public Result getTitbitsList(
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
        PageInfo pageInfo = storeService.getTitbitsList(storeId, pageNum, pageSize);
        Map<String, Object> map = new HashMap<>();
        map.put("total", pageInfo.getTotal());
        map.put("list", pageInfo.getList());
        return new DataResult<>(map);
    }


}
