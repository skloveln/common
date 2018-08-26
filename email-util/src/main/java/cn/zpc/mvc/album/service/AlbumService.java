package cn.zpc.mvc.album.service;

import cn.zpc.common.entity.UrlEntity;
import cn.zpc.common.handler.exception.GlobalExceptionResult;
import cn.zpc.common.plugins.oss.OssPathConfig;
import cn.zpc.common.serivce.BaseService;
import cn.zpc.common.utils.DateUtils;
import cn.zpc.common.utils.StringUtils;
import cn.zpc.mvc.album.dao.*;
import cn.zpc.mvc.album.entity.Album;
import cn.zpc.mvc.album.entity.AlbumComment;
import cn.zpc.mvc.album.entity.AlbumImage;
import cn.zpc.mvc.user.dao.UserDao;
import cn.zpc.mvc.user.service.UserCollectionService;
import cn.zpc.mvc.user.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class AlbumService extends BaseService{

    @Autowired
    private AlbumDao albumDao;
    @Autowired
    private AlbumImageDao albumImageDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private AlbumErrorDao albumErrorDao;
    @Autowired
    private AlbumCommentDao albumCommentDao;
    @Autowired
    private UserService userService;
    @Autowired
    private AlbumLikeDao albumLikeDao;
    @Autowired
    private UserCollectionService userCollectionService;

    /**
     * 增加一个图集
     * @param album 图集信息
     * @param addFileKeys 图片
     */
    @Transactional(rollbackFor = Exception.class)
    public void addAlbum(Album album, String addFileKeys) {
        albumDao.insert(album);
        List<String> list = StringUtils.splitString(addFileKeys);
        for(int i=0; i < list.size(); i++) {
            AlbumImage image = new AlbumImage();
            image.setAlbumId(album.getId());
            image.setUserId(album.getUserId());
            image.setImage(list.get(i));
            image.setWeight(10-i);
            albumImageDao.insert(image);
        }
    }

    /**
     * 删除一个图集
     * @param albumIds 图集Id
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteAlbums(Integer userId, String albumIds) {
        for(Integer albumId : StringUtils.splitInteger(albumIds)) {
            Album album = albumDao.get(albumId);
            if (album == null) {
                throw new GlobalExceptionResult("album.notExist", 1002);
            }
            if (album.getUserId().equals(userId)) {
                albumDao.deletedAlbum(albumId);
            } else {
                throw new GlobalExceptionResult("accessDenied", 1002);
            }
        }
    }


    /**
     * 评论补全信息
     * @param albumComments
     * @return
     */
    public List<AlbumComment> addInfo(List<AlbumComment> albumComments){
        for(AlbumComment comment : albumComments){
            comment.setToReply(comment.getReplyUserId() != null);
            if(comment.getReplyUserId() != null) {
                comment.setReplyUser(userService.getUser(comment.getReplyUserId()));
            }
            comment.setUser(userService.getUser(comment.getUserId()));
        }
        return albumComments;
    }


    /**
     * 评论补全信息
     * @param comment
     * @return
     */
    public AlbumComment addInfo(AlbumComment comment){

        comment.setToReply(comment.getReplyUserId() != null);
        if(comment.getReplyUserId() != null) {
            comment.setReplyUser(userService.getUser(comment.getReplyUserId()));
        }
        comment.setUser(userService.getUser(comment.getUserId()));

        return comment;
    }


    /**
     * 获取某一图集详情
     * @param albumId
     * @return
     */
    public Map<String, Object> getInfo(Integer albumId, Integer userId){
        Map<String, Object> map = new HashMap<>();
        Album album = albumDao.get(albumId);
        if(album == null){
            throw new GlobalExceptionResult("album.notExist", 1002);
        }
        album.setErrorNum(albumErrorDao.getErrorCount(albumId));
        album.setCommentNum(getCommentNum(albumId));
        if(userId != null) {
            album.setLiked(hasliked(albumId, userId));
            album.setPraiseCount(praiseCount(albumId, userId));
        }
        album.setHot(getHot(getCommentNum(album.getId()), album.getLikeNum(), album.getRepostNum()));
        album.setImages(getImages(albumId));
        album.setUserCollect(userCollectionService.checkCollection(userId, 2, albumId));
        map.put("album", album);
        map.put("comment", getComments(albumId,1, 20).getList());
        map.put("error", getErrors(albumId, 1, 10).getList());
        map.put("user", userService.getUser(album.getUserId()));
        return map;
    }

    /**
     * 获取某一图集(app中列表封面展示)
     * @param albumId
     * @return
     */
    public Map<String, Object> getSimpleInfo(Integer albumId, Integer userId){
        Map<String, Object> map = new HashMap<>();
        Album album = albumDao.get(albumId);
        if(album == null){
            throw new GlobalExceptionResult("album.notExist", 1002);
        }
        if(userId != null) {
            album.setUserCollect(userCollectionService.checkCollection(userId, 2, albumId));
        }
        album.setMainImage(getMainImage(albumId));
        map.put("album", album);
        map.put("user", userService.getUser(album.getUserId()));
        return map;
    }


    /**
     * 分享多个图集
     * @param albumIds 图集Id
     * @return
     */
    public List<Album> getMoreInfo(String albumIds){
        List<Album> list = new ArrayList<>();
        for(Integer albumId  : StringUtils.splitInteger(albumIds)) {
            Album album = albumDao.get(albumId);
            if (album == null) {
                throw new GlobalExceptionResult("album.notExist", 1002);
            }
            album.setImages(getImages(albumId));
            list.add(album);
        }
        return list;
    }


    /**
     * 获取某一图集的所有图片
     * @return
     */
    public List<UrlEntity> getImages(Integer albumId){
        List<UrlEntity> result = new ArrayList<>();
        List<String> images = albumImageDao.getAlbumImages(albumId);
        for(String imageName : images){
            String simpleUrl = ossService.getSimpleWaterMarkUrl(OssPathConfig.getAlbumImagePath(imageName));
            String originUrl = ossService.getOriginWaterMarkUrl(OssPathConfig.getAlbumImagePath(imageName));
            result.add(new UrlEntity(imageName, simpleUrl, originUrl));
        }
        return result;
    }


    /**
     * 分页获取评论
     * @param albumId
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageInfo<AlbumComment> getComments(Integer albumId, Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum, pageSize, true);
        List<AlbumComment> comments = albumCommentDao.getAllComment(albumId);
        return new PageInfo<>(addInfo(comments));
    }


    /**
     * 分页获取纠错
     * @param albumId
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageInfo<AlbumComment> getErrors(Integer albumId, Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum, pageSize, true);
        List<AlbumComment> errors = albumErrorDao.getAllError(albumId);
        return new PageInfo<>(addInfo(errors));
    }


    /**
     * 分页获取列表信息
     * @param userId 用户
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return
     */
    public PageInfo getList(Integer userId, Integer pageNum, Integer pageSize){
        List<Map> list = new ArrayList<>();
        PageHelper.startPage(pageNum, pageSize, true);
        List<Album> albums = albumDao.getListByCreateTime(userId);
        Long total = new PageInfo<>(albums).getTotal();
        for(Album album : albums){
            Map<String, Object> map = new HashMap<>();
            album.setMainImage(getMainImage(album.getId()));
            album.setCommentNum(getCommentNum(album.getId()));
            album.setLiked(hasliked(album.getId(), userId));
            album.setPraiseCount(praiseCount(album.getId(), userId));
            map.put("albums", album);
            map.put("user", userService.getUser(album.getUserId()));
            list.add(map);
        }
        PageInfo pageInfo = new PageInfo<>(list);
        pageInfo.setTotal(total);
        return pageInfo;
    }


    /**
     * 获取封面图
     * @return
     */
    public UrlEntity getMainImage(Integer albumId){

        UrlEntity urlEntity = new UrlEntity();
        PageHelper.startPage(1, 1,true);
        List<String> list = albumImageDao.getAlbumImages(albumId);
        if(list.size() > 0){
            String fileName = list.get(0);
            urlEntity.setFileKey(fileName);
            urlEntity.setSimpleUrl(ossService.getSimpleUrl(OssPathConfig.getAlbumImagePath(fileName)));
            urlEntity.setOriginUrl(ossService.getOriginWaterMarkUrl(OssPathConfig.getAlbumImagePath(fileName)));
        }
        return urlEntity;
    }


    /**
     * 获取某一图集的总评论数
     * @param albumId 图集Id
     * @return
     */
    public Integer getCommentNum(Integer albumId){

        return albumCommentDao.getCommentCount(albumId) + albumErrorDao.getErrorCount(albumId);
    }


    /**
     * 对某一图集点赞
     * @param albumId
     */
    @Transactional(rollbackFor = SQLException.class)
    public void likeAlbum(Integer albumId, Integer userId){

        if(hasliked(albumId, userId)){
            if(albumLikeDao.getCount(userId, albumId) <= 10){
                albumDao.likeAlbum(albumId);
                albumLikeDao.addLikedCount(userId, albumId);
            }else {
                throw new GlobalExceptionResult("liked.count.tooMuch", 1002);
            }
        }else {
            albumDao.likeAlbum(albumId);
            albumLikeDao.insert(userId, albumId);
        }
    }


    /**
     * 某一用户是否对某一图集点过赞
     * @param albumId
     * @param userId
     * @return
     */
    public Boolean hasliked(Integer albumId, Integer userId){

        return albumLikeDao.get(userId, albumId) > 0;
    }

    /**
     * 某一用户对某一图集点赞次数
     * @param albumId
     * @param userId
     * @return
     */
    public Integer praiseCount(Integer albumId, Integer userId){

        return albumLikeDao.getCount(userId, albumId);
    }

    /**
     * 对某一图集进行转发
     * @param albumId
     */
    @Transactional(rollbackFor = SQLException.class)
    public void repostAlbum(Integer albumId){
        albumDao.repostAlbum(albumId);
    }


    /**
     * 对某一图集进行评论/纠错
     * @param albumId
     * @param type 1-纠错 2-评论
     */
    @Transactional(rollbackFor = SQLException.class)
    public AlbumComment commentAlbum(Integer type, Integer albumId, Integer userId, Integer replyUserId, String content){
        AlbumComment albumComment = new AlbumComment();
        albumComment.setAlbumId(albumId);
        albumComment.setContent(content);
        albumComment.setUserId(userId);
        albumComment.setCreateTime(new Date());
        albumComment.setReplyUserId(replyUserId);
        if(type.equals(AlbumComment.Error)){
            albumErrorDao.insert(albumComment);
        }else if(type.equals(AlbumComment.Comment)){
            albumCommentDao.insert(albumComment);
        }

        return addInfo(albumComment);
    }


    /**
     * 删除某一评论/纠错
     * @param userId
     * @param id
     * @param type 1-纠错 2-评论
     */
    @Transactional(rollbackFor = SQLException.class)
    public void deleteComment(Integer type, Integer userId, Integer id){
        if(type.equals(AlbumComment.Error)){
            if(albumErrorDao.delete(id, userId) <= 0){
                throw new GlobalExceptionResult("comment.notExist.or.accessDenied" ,1002);
            }
        }else if(type.equals(AlbumComment.Comment)){
            if(albumCommentDao.delete(id, userId) <=0){
                throw new GlobalExceptionResult("comment.notExist.or.accessDenied" ,1002);
            }
        }
    }


    /**
     * 计算某一图集的热度
     * @return
     */
    public Integer getHot(Integer commentNum, Integer likeNum, Integer repostNum){
        Integer hot = 0;

        if(commentNum <= 3){
            hot += 10;
        }else if(commentNum <= 5){
            hot += 20;
        }else if(commentNum <= 8){
            hot += 30;
        }else if(commentNum <= 10){
            hot += 50;
        }

        if(likeNum <= 10){
            hot += 10;
        }else if(likeNum <= 50){
            hot += 20;
        }else if(likeNum <= 100){
            hot += 30;
        }

        if(repostNum <= 3){
            hot += 5;
        }else if(repostNum <= 8){
            hot += 12;
        }else if(repostNum <= 15){
            hot += 20;
        }

        return hot;
    }


    /**
     * 获取某一用户的图集
     * @param pageNum
     * @param pageSize
     * @param userId 查看谁的信息
     * @param viewId 查看者Id
     * @return
     */
    public PageInfo<Album> getUserAlbum(Integer pageNum, Integer pageSize, Integer userId, Integer viewId){

        PageHelper.startPage(pageNum, pageSize, true);
        List<Album> albums = albumDao.getByUser(userId, viewId);
        for(Album album : albums){
            album.setMainImage(getMainImage(album.getId()));
            album.setImages(getImages(album.getId()));
        }
        return new PageInfo<>(albums);
    }


    /**
     * 获取某一用户的图集时间流
     * @param userId 查看谁的信息
     * @param viewId 查看者Id
     */
    public PageInfo<List<Map>> getGroupByTime(Integer pageNum, Integer pageSize, Integer userId, Integer viewId){
        List<Map> result = new ArrayList<>();
        PageHelper.startPage(pageNum, pageSize, true);
        List<Album> list =  albumDao.getUserAlbumGroupByTime(userId, viewId);
        Long total = new PageInfo<>(list).getTotal();
        for(Album album : list){
            Map<String, Object> map = new HashMap<>();
            album.setImages(getImages(album.getId()));
            if(album.getGroupNum() > 1){
                List<Album> groupList = albumDao.getUserByDate(userId, viewId, DateUtils.getStartTime(album.getCreateTime()));
                for(Album more : groupList){
                    more.setImages(getImages(more.getId()));
                }
                map.put("date", new SimpleDateFormat("yyyy-MM-dd").format(album.getCreateTime()));
                map.put("list", groupList);
            }else {
                map.put("date", new SimpleDateFormat("yyyy-MM-dd").format(album.getCreateTime()));
                List<Album> albums = new ArrayList<>();
                albums.add(album);
                map.put("list", albums);
            }
            result.add(map);
        }
        PageInfo pageInfo = new PageInfo(result);
        pageInfo.setTotal(total);
        return pageInfo;
    }
}
