package cn.zpc.mvc.user.controller;

import cn.zpc.common.plugins.oss.OssPathConfig;
import cn.zpc.common.serivce.BaseService;
import cn.zpc.common.utils.FileUtils;
import cn.zpc.common.web.result.DataResult;
import cn.zpc.common.web.result.Result;
import cn.zpc.common.web.validators.sequence.First;
import cn.zpc.common.web.validators.sequence.Second;
import cn.zpc.mvc.store.dao.StoreDao;
import cn.zpc.mvc.store.entity.Store;
import cn.zpc.mvc.user.entity.CompanyIdentification;
import cn.zpc.mvc.user.entity.PersonIdentification;
import cn.zpc.mvc.user.entity.User;
import cn.zpc.mvc.user.security.UserContext;
import cn.zpc.mvc.user.security.annotation.Authorization;
import cn.zpc.mvc.user.security.annotation.CurrentUser;
import cn.zpc.mvc.user.service.UserIdentificationService;
import cn.zpc.mvc.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Api(description = "用户认证管理", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@Validated({First.class, Second.class, Default.class})
public class UserIdentificationController extends BaseService{


    @Autowired
    private UserIdentificationService userIdentificationService;
    @Autowired
    private UserService userService;
    @Autowired
    private StoreDao storeDao;

    @ApiOperation(value = "用户认证状态", notes = "认证状态<br>status（0-普通用户 1-店铺用户 2-认证中）" +
            "<br>storeType（1-个人店铺用户 2-商家店铺用户 3-地接店铺用户）" +
            "<br>expireTime 店铺到期时间" +
            "<br>isExpire 店铺是否到期  true-到期 false-正常")
    @Authorization
    @RequestMapping(value = "/user/identification/status", method = RequestMethod.POST)
    public Result Identification(
            @ApiIgnore
            @CurrentUser UserContext userContext){
        User user = userService.get(userContext.getUserId());
        Map<String, Object> result = new HashMap<>();
        result.put("status", user.getStatus());
        // 查找商户类型
        if(user.getStatus() == 1){
            Store store = storeDao.getByUser(user.getId());
            result.put("storeId", store.getId());
            result.put("storeType", store.getType());
            result.put("expireTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(store.getExpireTime()));
            result.put("isExpire", store.getExpireTime().before(new Date()));
            result.put("detailAddress", store.getDetailAddress());
            result.put("address", store.getAddress());
            result.put("province", store.getProvince());
            result.put("city", store.getCity());
            result.put("logo", ossService.getSimpleUrl(OssPathConfig.getStoreLogoPath(store.getLogo())));
            result.put("storeName", store.getName());
            result.put("mainImage", ossService.getSimpleUrl(OssPathConfig.getStoreImagePath(store.getMainImage())));
        }

        return DataResult.getNormal(result);
    }


    @ApiOperation(value = "个人认证", notes = "个人认证信息上传")
    @Authorization
    @RequestMapping(value = "/user/identification/person", method = RequestMethod.POST)
    public Result PersonIdentification(
            @ApiParam(allowMultiple = true, required = true)
            @NotEmpty
            @RequestParam    String name,

            @ApiParam(required = true)
            @NotNull
            @RequestParam    MultipartFile idcardFront,

            @ApiParam(required = true)
            @NotNull
            @RequestParam    MultipartFile idcardBack,

            HttpServletRequest request,

            @ApiIgnore
            @CurrentUser UserContext userContext) {

        Integer userId = userContext.getUserId();
        // 文件保存路径
        String savePath = request.getSession().getServletContext().getRealPath("upload");
        // 重命名文件并上传
        File frontFile = FileUtils.transferFile(idcardFront, userId, savePath);
        ossService.putFile(OssPathConfig.getIdentificationPath(frontFile.getName()), frontFile);
        File backFile = FileUtils.transferFile(idcardBack, userId, savePath);
        ossService.putFile(OssPathConfig.getIdentificationPath(backFile.getName()), backFile);
        // 封装数据
        PersonIdentification person = new PersonIdentification();
        person.setName(name);
        person.setIdcardFront(frontFile.getName());
        person.setIdcardBack(backFile.getName());
        // 清缓存文件
        frontFile.deleteOnExit();
        backFile.deleteOnExit();
        return userIdentificationService.personCommit(person, userId);
    }


    @ApiOperation(value = "企业认证", notes = "企业认证信息上传")
    @Authorization
    @RequestMapping(value = "/user/identification/company", method = RequestMethod.POST)
    public Result CompanyIdentification(
            @ApiParam(allowMultiple = true, required = true)
            @NotEmpty
            @RequestParam String name,

            @ApiParam(allowMultiple = true, required = true)
            @NotEmpty
            @RequestParam String address,

            @ApiParam(allowMultiple = true, required = true, value = "认证类型（1-普通企业  2-地接企业）")
            @NotNull
            @RequestParam Integer type,

            @ApiParam(required = true)
            @NotNull
            @RequestParam MultipartFile businessLicence,

            @ApiParam(required = true)
            @NotNull
            @RequestParam    MultipartFile idcardFront,

            @ApiParam(required = true)
            @NotNull
            @RequestParam    MultipartFile idcardBack,

            HttpServletRequest request,

            @ApiIgnore
            @CurrentUser UserContext userContext) {

        Integer userId = userContext.getUserId();
        // 文件保存路径
        String savaPath = request.getSession().getServletContext().getRealPath("upload");
        // 重命名文件并上传
        File frontFile = FileUtils.transferFile(idcardFront, userId, savaPath);
        ossService.putFile(OssPathConfig.getIdentificationPath(frontFile.getName()), frontFile);
        File backFile = FileUtils.transferFile(idcardBack, userId, savaPath);
        ossService.putFile(OssPathConfig.getIdentificationPath(backFile.getName()), backFile);
        File licenceFile = FileUtils.transferFile(businessLicence, userId, savaPath);
        ossService.putFile(OssPathConfig.getIdentificationPath(licenceFile.getName()), licenceFile);
        // 封装数据
        CompanyIdentification company = new CompanyIdentification();
        company.setUserId(userId);
        company.setCompanyName(name);
        company.setCompanyAddress(address);
        company.setType(type);
        company.setIdcardFront(frontFile.getName());
        company.setIdcardBack(backFile.getName());
        company.setBusinessLicence(licenceFile.getName());
        // 清缓存文件
        frontFile.delete();
        backFile.delete();
        licenceFile.delete();
        return userIdentificationService.companyCommit(company, userId);
    }


}
