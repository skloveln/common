package cn.zpc.mvc.album.controller;

import cn.zpc.common.entity.UrlEntity;
import cn.zpc.common.plugins.oss.OssPathConfig;
import cn.zpc.common.serivce.BaseService;
import cn.zpc.common.utils.FileUtils;
import cn.zpc.common.utils.PropertiesLoader;
import cn.zpc.common.web.result.DataResult;
import cn.zpc.common.web.result.MessageResult;
import cn.zpc.common.web.result.Result;
import cn.zpc.mvc.album.entity.Album;
import cn.zpc.mvc.album.entity.AlbumComment;
import cn.zpc.mvc.album.param.AlbumParam;
import cn.zpc.mvc.album.service.AlbumService;
import cn.zpc.mvc.user.security.UserContext;
import cn.zpc.mvc.user.security.annotation.Authorization;
import cn.zpc.mvc.user.security.annotation.CurrentUser;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(description = "图集信息控制器")
@RestController
public class AlbumController extends BaseService{

    private final static PropertiesLoader loader = new PropertiesLoader("application.properties");
    private final static String env = loader.getProperty("env");


    @Autowired
    private AlbumService albumService;


    @ApiOperation(value = "上传图集照片")
    @Authorization
    @RequestMapping(value = "/album/images/upload", method = RequestMethod.POST)
    public Result uploadImages(
            @ApiParam(required = true)
            @RequestParam MultipartFile file,

            HttpServletRequest request,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();

        String str = request.getSession().getServletContext().getRealPath("upload");
        File newFile = FileUtils.transferFile(file, userId, str);
        String url = ossService.putFile(OssPathConfig.getAlbumImagePath(newFile.getName()), newFile);
        newFile.deleteOnExit();

        return new DataResult<>(new UrlEntity(newFile.getName(), url));
    }


    @ApiOperation(value = "编辑保存一个图集")
    @Authorization
    @RequestMapping(value = "/album/edit/save", method = RequestMethod.POST)
    public Result saveAlbu图m(
            @Validated AlbumParam albumParam,

            @ApiParam(value = "增加的fileKeys  多个空格隔开")
            @RequestParam(required = false) String addFileKeys,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        Album album =  albumParam.getAlbum(userId);
        albumService.addAlbum(album, addFileKeys);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO 500 错误需要发邮件通知管理员
                emailService.sendEmail("bluetingsky@163.com", env + "有一个图集待审核", "图集待审核");
                emailService.sendEmail("sukai@locationbox.cn", env + "有一个图集待审核", "图集待审核");
            }
        });
        t.start();

        return new MessageResult();
    }


    @ApiOperation(value = "删除图集")
    @Authorization
    @RequestMapping(value = "/album/delete", method = RequestMethod.POST)
    public Result saveAlbum(
            @ApiParam(value = "图集Id, 多个空格隔开")
            @RequestParam String albumIds,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();

        albumService.deleteAlbums(userId, albumIds);

        return new MessageResult();
    }


    @ApiOperation(value = "获取图集热门标签")
    @Authorization
    @RequestMapping(value = "/album/hot/tags", method = RequestMethod.POST)
    public Result getHotTags(){

        List<String> list = new ArrayList<>();

        list.add("实景");
        list.add("影棚");
        list.add("年代");
        list.add("科幻");
        list.add("医院");
        list.add("办公室");
        list.add("家居");
        list.add("民国风");
        list.add("古镇");
        list.add("废墟");

        return new DataResult<>(list);
    }


    @ApiOperation(value = "获取图集列表")
    @Authorization
    @RequestMapping(value = "/album/list", method = RequestMethod.POST)
    public Result getAlbumList(
            @ApiParam(value = "页码")
            @RequestParam(defaultValue = "1") Integer pageNum,

            @ApiParam(value = "每页大小")
            @RequestParam(defaultValue = "20") Integer pageSize,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        Map<String, Object> map = new HashMap<>();
        PageInfo pageInfo = albumService.getList(userId, pageNum, pageSize);
        map.put("list", pageInfo.getList());
        map.put("total", pageInfo.getTotal());

        return new DataResult<>(map);
    }


    @ApiOperation(value = "获取某一图集详情", notes = "给纠错10条， 评论20条，剩余分页获取" +
            "</br>commentNum 总评论数   普通评论数自己算一下（总评论数减去纠错数）" +
            "</br>errorNum 纠错数" +
            "</br>storeType 1-个人店铺 2-商家店铺 3-地接店铺")
    @Authorization
    @RequestMapping(value = "/album/info", method = RequestMethod.POST)
    public Result getAlbumList(
            @ApiParam(value = "图集Id")
            @RequestParam Integer albumId,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Map<String, Object> map = albumService.getInfo(albumId, userContext.getUserId());

        return new DataResult<>(map);
    }


    @ApiOperation(value = "图集详情获取更多评论")
    @Authorization
    @RequestMapping(value = "/album/more/comment", method = RequestMethod.POST)
    public Result getMoreAlbumComment(
            @ApiParam(value = "图集Id")
            @RequestParam Integer albumId,

            @ApiParam(value = "页码")
            @RequestParam(defaultValue = "2") Integer pageNum,

            @ApiParam(value = "每页大小")
            @RequestParam(defaultValue = "20") Integer pageSize){

        Map<String, Object> map = new HashMap<>();
        PageInfo pageInfo = albumService.getComments(albumId, pageNum, pageSize);
        map.put("list", pageInfo.getList());
        map.put("total", pageInfo.getTotal());

        return new DataResult<>(map);
    }


    @ApiOperation(value = "图集详情获取更多纠错")
    @Authorization
    @RequestMapping(value = "/album/more/error", method = RequestMethod.POST)
    public Result getMoreErrorComment(
            @ApiParam(value = "图集Id")
            @RequestParam Integer albumId,

            @ApiParam(value = "页码")
            @RequestParam(defaultValue = "2") Integer pageNum,

            @ApiParam(value = "每页大小")
            @RequestParam(defaultValue = "10") Integer pageSize){

        Map<String, Object> map = new HashMap<>();
        PageInfo pageInfo = albumService.getErrors(albumId, pageNum, pageSize);
        map.put("list", pageInfo.getList());
        map.put("total", pageInfo.getTotal());

        return new DataResult<>(map);
    }


    @ApiOperation(value = "对某一图集评论")
    @Authorization
    @RequestMapping(value = "/album/comment", method = RequestMethod.POST)
    public Result albumComment(
            @ApiParam(value = "图集Id", required = true)
            @RequestParam Integer albumId,

            @ApiParam(value = "回复谁的评论  没有不传")
            @RequestParam(required = false) Integer replyUserId,

            @ApiParam(value = "回复内容")
            @RequestParam String content,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        AlbumComment albumComment = albumService.commentAlbum(AlbumComment.Comment, albumId, userId, replyUserId, content);
        return new DataResult<>(albumComment);
    }


    @ApiOperation(value = "删除某一评论")
    @Authorization
    @RequestMapping(value = "/album/comment/delete", method = RequestMethod.POST)
    public Result deleteComment(
            @ApiParam(value = "评论Id", required = true)
            @RequestParam Integer id,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        albumService.deleteComment(AlbumComment.Comment, userId, id);
        return new MessageResult();
    }


    @ApiOperation(value = "删除某一纠错")
    @Authorization
    @RequestMapping(value = "/album/error/delete", method = RequestMethod.POST)
    public Result deleteErrorComment(
            @ApiParam(value = "纠错评论Id", required = true)
            @RequestParam Integer id,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        albumService.deleteComment(AlbumComment.Error, userId, id);
        return new MessageResult();
    }


    @ApiOperation(value = "对某一图集纠错")
    @Authorization
    @RequestMapping(value = "/album/error", method = RequestMethod.POST)
    public Result albumError(
            @ApiParam(value = "图集Id", required = true)
            @RequestParam Integer albumId,

            @ApiParam(value = "回复谁的评论  没有不传")
            @RequestParam(required = false) Integer replyUserId,

            @ApiParam(value = "回复内容")
            @RequestParam String content,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        AlbumComment albumComment = albumService.commentAlbum(AlbumComment.Error, albumId, userId, replyUserId, content);
        return new DataResult<>(albumComment);
    }


    @ApiOperation(value = "对某一图集点赞", notes = "每个用户对一个图集最多点10次赞")
    @Authorization
    @RequestMapping(value = "/album/like", method = RequestMethod.POST)
    public Result albumLike(
            @ApiParam(value = "图集Id", required = true)
            @RequestParam Integer albumId,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer userId = userContext.getUserId();
        albumService.likeAlbum(albumId, userId);

        return new MessageResult();
    }


    @ApiOperation(value = "对某一图集转发", notes = "转发成功调用")
    @Authorization
    @RequestMapping(value = "/album/repost", method = RequestMethod.POST)
    public Result albumRepost(
            @ApiParam(value = "图集Id", required = true)
            @RequestParam Integer albumId){

        albumService.repostAlbum(albumId);

        return new MessageResult();
    }


    @ApiOperation(value = "图集时间流")
    @Authorization
    @RequestMapping(value = "/album/user/list", method = RequestMethod.POST)
    public Result albumUserList(
            @ApiParam(value = "用户Id", required = true)
            @RequestParam Integer userId,

            @ApiParam(value = "页码")
            @RequestParam(defaultValue = "1") Integer pageNum,

            @ApiParam(value = "每页大小，指获取有数据的日期的个数")
            @RequestParam(defaultValue = "10") Integer pageSize,

            @ApiIgnore
            @CurrentUser UserContext userContext){

        Integer viewId = userContext.getUserId();
        PageInfo pageInfo = albumService.getGroupByTime(pageNum, pageSize, userId, viewId);
        Map<String, Object> map = new HashMap<>();
        map.put("list", pageInfo.getList());
        map.put("total", pageInfo.getTotal());

        return new DataResult<>(map);
    }


}

