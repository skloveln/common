package cn.zpc.mvc.scene.controller;

import cn.zpc.common.entity.UrlEntity;
import cn.zpc.common.handler.exception.GlobalExceptionResult;
import cn.zpc.common.plugins.oss.OssPathConfig;
import cn.zpc.common.serivce.BaseService;
import cn.zpc.common.utils.FileUtils;
import cn.zpc.common.utils.PropertiesLoader;
import cn.zpc.common.utils.StringUtils;
import cn.zpc.common.web.result.DataResult;
import cn.zpc.common.web.result.MessageResult;
import cn.zpc.common.web.result.Result;
import cn.zpc.mvc.scene.dao.SceneImageDao;
import cn.zpc.mvc.scene.entity.SceneImage;
import cn.zpc.mvc.scene.entity.SceneInfo;
import cn.zpc.mvc.scene.param.SceneBasicParam;
import cn.zpc.mvc.scene.service.SceneManageService;
import cn.zpc.mvc.scene.service.SceneService;
import cn.zpc.mvc.user.security.UserContext;
import cn.zpc.mvc.user.security.annotation.Authorization;
import cn.zpc.mvc.user.security.annotation.CurrentUser;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.*;

@Api(description = "场景管理控制器")
@RestController
public class SceneManageController extends BaseService{

    private final static PropertiesLoader loader = new PropertiesLoader("application.properties");
    private final static String env = loader.getProperty("env");


    @Autowired
    private SceneManageService sceneManageService;
    @Autowired
    private SceneService sceneService;
    @Autowired
    private SceneImageDao sceneImageDao;


    @ApiOperation(value = "(管理接口)获取各类状态场景列表",
            notes = "status:  -1.草稿 0.审核中 1.正常状态 2.审核未通过 3.主动下架")
    @Authorization
    @RequestMapping(value = "/scene/manage/list",method = RequestMethod.POST)
    public Result getScenePass(
            @ApiParam(value = "页码")
            @RequestParam(defaultValue = "1") Integer pageNum,

            @ApiParam(value = "每页大小")
            @RequestParam(defaultValue = "20") Integer pageSize,

            @ApiParam(required = true, value = "-1.草稿 0.审核中 1.正常状态 2.审核未通过 3.主动下架")
            @RequestParam(defaultValue = "1") Integer status,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();

        PageInfo<SceneInfo> pageInfo = sceneManageService.getStatusList(userId, pageNum, pageSize, status);
        Map<String, Object> map = new HashMap<>();
        map.put("list", pageInfo.getList());
        map.put("total", pageInfo.getTotal());

        return new DataResult<>(map);
    }


    @ApiOperation(value = "(管理接口)对指定场景进行操作", notes = "1-上架 2-下架 3-撤回 4-删除 5-发布")
    @Authorization
    @RequestMapping(value = "/scene/manage/operate",  method = RequestMethod.POST)
    public Result offScene(
            @ApiParam(required = true, value = "场景Id")
            @RequestParam   Integer sceneId,

            @ApiParam(required = true, value = "1-上架 2-下架 3-撤回 4-删除 5-发布")
            @RequestParam Integer operate,

            @ApiIgnore
            @CurrentUser    UserContext userContext){

        Integer userId = userContext.getUserId();
        // 校验场景是否属于用户
        if(!sceneManageService.isExist(userId, sceneId)){
            throw new GlobalExceptionResult("scene.notBelongTo.user", Result.EXCEPTION);
        }

        switch (operate){
            case 1:
                sceneManageService.changeStatus(userId, sceneId, 1);
                break;
            case 2:
                sceneManageService.changeStatus(userId, sceneId, 3);
                break;
            case 3:
                sceneManageService.changeStatus(userId, sceneId, -1);
                break;
            case 4:
                sceneManageService.deleteScene(userId, sceneId);
                break;
            case 5:
                sceneManageService.changeStatus(userId, sceneId, 0);
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO 500 错误需要发邮件通知管理员
                        emailService.sendEmail("bluetingsky@163.com", env + "有一个专业场景待审核", "专业场景待审核");
                        emailService.sendEmail("sukai@locationbox.cn", env + "有一个专业场景待审核", "专业场景待审核");
                    }
                });
                t.start();
                break;
            default:
                throw new GlobalExceptionResult("api.param.error", Result.EXCEPTION);
        }

        return new MessageResult();
    }


    @ApiOperation(value = "(管理接口)判断场景模块的完整性", notes = "编辑模式下检验4个模块是否编辑完整")
    @Authorization
    @RequestMapping(value = "/scene/completeness", method = RequestMethod.POST)
    public Result SceneCompleteness(
            @ApiParam(required = true, value = "场景Id")
            @RequestParam   Integer sceneId,

            @ApiIgnore
            @CurrentUser    UserContext userContext){

        Integer userId = userContext.getUserId();
        // 校验场景是否属于用户
        if(!sceneManageService.isExist(userId, sceneId)){
            throw new GlobalExceptionResult("scene.notBelongTo.user", Result.EXCEPTION);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("image", sceneManageService.checkImageComplement(sceneId));
        map.put("information", sceneManageService.checkInfoComplement(sceneId));
        map.put("service", sceneManageService.checkServiceComplement(sceneId));
        map.put("production", sceneManageService.checkProductionComplement(sceneId));

        return new DataResult<>(map);
    }


    @ApiOperation(value = "(管理接口)删除场景图片")
    @Authorization
    @RequestMapping(value = "/scene/image/delete",method = RequestMethod.POST)
    public Result deleteSceneImage(
            @ApiParam(required = true, value = "场景Id")
            @RequestParam   Integer sceneId,

            @ApiParam(required = true, value = "图片fileKey, 多个以空格隔开")
            @RequestParam String fileKey,

            @ApiIgnore
            @CurrentUser    UserContext userContext){

        Integer userId = userContext.getUserId();
        // 校验场景是否属于用户
        if(!sceneManageService.isExist(userId, sceneId)){
            throw new GlobalExceptionResult("scene.notBelongTo.user", Result.EXCEPTION);
        }

        List<String> list = StringUtils.splitString(fileKey);
        for(String imageName : list) {
            sceneManageService.deleteSceneImage(userId, sceneId, imageName);
        }

        return new MessageResult();
    }


    @ApiOperation(value = "(管理接口)获取指定场景图片集")
    @Authorization
    @RequestMapping(value = "/scene/image/get",method = RequestMethod.POST)
    public Result deleteSceneImage(
            @ApiParam(required = true, value = "场景Id")
            @RequestParam   Integer sceneId,

            @ApiIgnore
            @CurrentUser    UserContext userContext){

        Integer userId = userContext.getUserId();
        // 校验场景是否属于用户
        if(!sceneManageService.isExist(userId, sceneId)){
            throw new GlobalExceptionResult("scene.notBelongTo.user", Result.EXCEPTION);
        }

        return new DataResult<>(sceneService.getSceneImages(sceneId, false));
    }


    @ApiOperation(value = "(管理接口)上传场景图片")
    @Authorization
    @RequestMapping(value = "/scene/image/upload",method = RequestMethod.POST)
    public Result deleteSceneImage(
            @ApiParam(required = true, value = "场景Id")
            @RequestParam   Integer sceneId,

            @ApiParam(required = true, value = "上传的图片")
            @NotNull
            @RequestParam MultipartFile file,

            HttpServletRequest request,

            @ApiIgnore
            @CurrentUser    UserContext userContext){

        Integer userId = userContext.getUserId();
        // 校验场景是否属于用户
        if(!sceneManageService.isExist(userId, sceneId)){
            throw new GlobalExceptionResult("scene.notBelongTo.user", Result.EXCEPTION);
        }

        // 文件保存路径
        String savePath = request.getSession().getServletContext().getRealPath("upload");
        // 重命名文件并上传
        File newFile = FileUtils.transferFile(file, userId, savePath);
        String url = ossService.putFile(OssPathConfig.getSceneImagePath(newFile.getName()), newFile);
        sceneManageService.addSceneImage(userId, sceneId, newFile.getName());

        return new DataResult<>(new UrlEntity(newFile.getName(), url));
    }


    @ApiOperation(value = "(管理接口)获取场景资料")
    @Authorization
    @RequestMapping(value = "/scene/basicInfo/get",method = RequestMethod.POST)
    public Result basicSceneGet(
            @ApiParam(required = true, value = "场景Id")
            @RequestParam   Integer sceneId,

            @ApiIgnore
            @CurrentUser    UserContext userContext){

        Integer userId = userContext.getUserId();
        // 校验场景是否属于用户
        if(!sceneManageService.isExist(userId, sceneId)){
            throw new GlobalExceptionResult("scene.notBelongTo.user", Result.EXCEPTION);
        }

        SceneInfo sceneInfo = sceneManageService.getBasicInfo(userId, sceneId);

        return new DataResult<>(sceneInfo);
    }


    @ApiOperation(value = "(管理接口)编辑场景资料")
    @Authorization
    @RequestMapping(value = "/scene/basicInfo/edit",method = RequestMethod.POST)
    public Result basicSceneEdit(
            @Validated SceneBasicParam sceneBasicParam,

            @ApiIgnore
            @CurrentUser    UserContext userContext){

        Integer userId = userContext.getUserId();
        // 校验场景是否属于用户
        if(!sceneManageService.isExist(userId, sceneBasicParam.getId())){
            throw new GlobalExceptionResult("scene.notBelongTo.user", Result.EXCEPTION);
        }

        sceneManageService.editBasicInfo(userId, sceneBasicParam);

        return new MessageResult();
    }


    @ApiOperation(value = "(管理接口)新增场景资料")
    @Authorization
    @RequestMapping(value = "/scene/add",method = RequestMethod.POST)
    public Result basicSceneAdd(
            @ApiIgnore
            @CurrentUser    UserContext userContext){

        Integer userId = userContext.getUserId();

        SceneInfo sceneInfo = sceneManageService.addBasicInfo(userId);

        return new DataResult<>(sceneInfo);
    }


    @ApiOperation(value = "(管理接口)获取指定场景所能提供的服务")
    @Authorization
    @RequestMapping(value = "/scene/service/get",method = RequestMethod.POST)
    public Result serviceSceneGet(
            @ApiParam(required = true, value = "场景Id")
            @RequestParam   Integer sceneId,

            @ApiIgnore
            @CurrentUser    UserContext userContext){

        Integer userId = userContext.getUserId();
        // 校验场景是否属于用户
        if(!sceneManageService.isExist(userId, sceneId)){
            throw new GlobalExceptionResult("scene.notBelongTo.user", Result.EXCEPTION);
        }

        return new DataResult<>(sceneService.getSceneServiceIcon(sceneId));
    }


    @ApiOperation(value = "(管理接口)获取全部场景服务资源")
    @Authorization
    @RequestMapping(value = "/scene/serviceResource/get",method = RequestMethod.POST)
    public Result serviceSceneGet(){

        return new DataResult<>(sceneManageService.getAllService());
    }


    @ApiOperation(value = "(管理接口)编辑指定场景的服务")
    @Authorization
    @RequestMapping(value = "/scene/service/edit",method = RequestMethod.POST)
    public Result serviceSceneEdit(
            @ApiParam(required = true, value = "场景Id")
            @RequestParam   Integer sceneId,

            @ApiParam(required = true, value = "场景服务类型Id, 中间空格隔开")
            @RequestParam   String typeId,

            @ApiIgnore
            @CurrentUser    UserContext userContext){

        Integer userId = userContext.getUserId();
        // 校验场景是否属于用户
        if(!sceneManageService.isExist(userId, sceneId)){
            throw new GlobalExceptionResult("scene.notBelongTo.user", Result.EXCEPTION);
        }

        List<String> stringList = StringUtils.splitString(typeId);

        sceneManageService.editSceneService(sceneId, stringList);

        return new MessageResult();
    }


    @ApiOperation(value = "(管理接口)上传曾经拍摄图片")
    @Authorization
    @RequestMapping(value = "/scene/works/image/upload",method = RequestMethod.POST)
    public Result uploadWorksImage(
            @ApiParam(required = true, value = "场景Id")
            @RequestParam   Integer sceneId,
            @ApiParam(required = true, value = "上传的图片")
            @NotNull
            @RequestParam MultipartFile file,
            HttpServletRequest request,
            @ApiIgnore
            @CurrentUser    UserContext userContext){
        Integer userId = userContext.getUserId();
        // 校验场景是否属于用户
        if(!sceneManageService.isExist(userId, sceneId)){
            throw new GlobalExceptionResult("scene.notBelongTo.user", Result.EXCEPTION);
        }
        // 文件保存路径
        String savePath = request.getSession().getServletContext().getRealPath("upload");
        // 重命名文件并上传
        File newFile = FileUtils.transferFile(file, userId, savePath);
        String url = ossService.putFile(OssPathConfig.getSceneWorksPath(newFile.getName()), newFile);
        sceneManageService.addWorksImage(sceneId, newFile.getName());
        return new DataResult<>( new UrlEntity(newFile.getName(), url));
    }

    
    
    @ApiOperation(value = "(管理接口)获取指定场景的拍摄作品")
    @Authorization
    @RequestMapping(value = "/scene/works/get",method = RequestMethod.POST)
    public Result worksSceneGet(
            @ApiParam(required = true, value = "场景Id")
            @RequestParam   Integer sceneId,

            @ApiIgnore
            @CurrentUser    UserContext userContext){

        Integer userId = userContext.getUserId();
        // 校验场景是否属于用户
        if(!sceneManageService.isExist(userId, sceneId)){
            throw new GlobalExceptionResult("scene.notBelongTo.user", Result.EXCEPTION);
        }

        return new DataResult<>( sceneService.getSceneWorks(sceneId));
    }


    @ApiOperation(value = "(管理接口)编辑指定场景的拍摄作品")
    @Authorization
    @RequestMapping(value = "/scene/works/edit",method = RequestMethod.POST)
    public Result worksSceneGet(
            @ApiParam(required = true, value = "场景Id")
            @RequestParam   Integer sceneId,

            @ApiParam(required = true, value = "场景作品描述")
            @RequestParam   String worksDesc,

            @ApiParam(value = "删除的fileKey集合")
            @RequestParam(required = false) String fileKeys,

            @ApiIgnore
            @CurrentUser    UserContext userContext){

        Integer userId = userContext.getUserId();
        // 校验场景是否属于用户
        if(!sceneManageService.isExist(userId, sceneId)){
            throw new GlobalExceptionResult("scene.notBelongTo.user", Result.EXCEPTION);
        }

        sceneManageService.editSceneWorks(sceneId, worksDesc, fileKeys);

        return new MessageResult();
    }




    @ApiOperation(value = "(管理接口)场景信息预览")
    @Authorization
    @RequestMapping(value = "/scene/preview",method = RequestMethod.POST)
    public Result previewScene(
            @ApiParam(required = true, value = "场景Id")
            @RequestParam   Integer sceneId,

            @ApiIgnore
            @CurrentUser    UserContext userContext){

        Integer userId = userContext.getUserId();
        // 校验场景是否属于用户
        if(!sceneManageService.isExist(userId, sceneId)){
            throw new GlobalExceptionResult("scene.notBelongTo.user", Result.EXCEPTION);
        }

        Map<String, Object> result = new HashMap<>();

        // 场景基本信息
        result.put("sceneInfo", sceneManageService.getBasicInfo(userId, sceneId));
        // 场景图片
        result.put("sceneImage", sceneService.getSceneImages(sceneId, false));
        // 场景服务
        result.put("sceneService", sceneService.getSceneServiceIcon(sceneId));
        // 场景联系人
        result.put("contacts", sceneService.getSceneContacts(sceneId));
        // 场景拍过的电影
        result.put("works", sceneService.getSceneWorks(sceneId));

        return new DataResult<>(result);
    }






///**
//* Description: 根据id 获取场景图片集
//* Param:
//* return:
//* Author: W
//* Date: 2018/4/18 13:19
//*/
//    @ApiOperation(value = "(管理接口)获取指定场景图片集")
//    @Authorization
//    @RequestMapping(value = "/scene/image/get",method = RequestMethod.POST)
//    public Result getSceneImages(
//            @ApiParam(required = true, value = "场景Id")
//            @RequestParam   Integer sceneId,
//
//            @ApiIgnore
//            @CurrentUser    UserContext userContext){
//
//        Integer userId = userContext.getUserId();
//        // 校验场景是否属于用户
//        if(!sceneManageService.isExist(userId, sceneId)){
//            throw new GlobalExceptionResult("scene.notBelongTo.user", Result.EXCEPTION);
//        }
//        List<SceneImage> images =  sceneImageDao.getImages(sceneId);
//        if (images.isEmpty()){
//                    return  new DataResult<>();
//        }
//        return new DataResult<>(sceneService.getSceneImageList(images, false));
//    }


    @ApiOperation(value = "场景图片更新", notes = "场景图片编辑以及封面图选择")
    @Authorization
    @RequestMapping(value = "scene/images/update", method = RequestMethod.POST)
    public Result editStoreSceneOrder(
            @ApiParam(value = "场景封面图片名称，eg: 游泳池 下水道 ")
            @NotEmpty
            @RequestParam String fileKeys,

            @ApiParam(required = true, value = "场景Id")
            @NotEmpty
            @RequestParam   Integer sceneId,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        // 校验场景是否属于用户
        if(!sceneManageService.isExist(userId, sceneId)){
            throw new GlobalExceptionResult("scene.notBelongTo.user", Result.EXCEPTION);
        }
        sceneManageService.updateSceneImage(sceneId,userId,fileKeys);
        return new MessageResult();
    }
}


