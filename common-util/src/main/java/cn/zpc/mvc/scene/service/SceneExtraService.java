package cn.zpc.mvc.scene.service;

import cn.zpc.common.plugins.oss.OssPathConfig;
import cn.zpc.common.serivce.BaseService;
import cn.zpc.common.utils.StringUtils;
import cn.zpc.mvc.scene.dao.SceneExtraDao;
import cn.zpc.mvc.scene.dao.SceneNewsDao;
import cn.zpc.mvc.scene.entity.SceneExtra;
import cn.zpc.mvc.scene.entity.SceneNews;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 外部场景服务
 */
@Service
public class SceneExtraService extends BaseService{

    @Autowired
    private SceneNewsDao sceneNewsDao;
    @Autowired
    private SceneExtraDao sceneExtraDao;


    /**
     * 分页获取快报列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageInfo getNewsList(Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum, pageSize, true);
        List<SceneNews> list = sceneNewsDao.getList();

        return new PageInfo(list);
    }


    /**
     * 获取场景资讯
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageInfo getMoreScene(Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum, pageSize, true);
        List<SceneExtra> list = sceneExtraDao.getList();
        for(SceneExtra more : list){
            if(StringUtils.isNotEmpty(more.getImage())){
                String imageUrl = ossService.getSimpleUrl(OssPathConfig.getSceneExtraPath(more.getImage()));
                more.setImage(imageUrl);
            }else{
                more.setImage(more.getExtUrl());
            }
        }
        return new PageInfo(list);
    }


    /**
     * 按日期获取场景快报
     * @param date
     * @return
     */
    public PageInfo getMoreNews(Date date){
        List<SceneNews> list = sceneNewsDao.getListByDate(date);
//        for(SceneNews news : list){
//            String imageUrl = ossService.getSimpleUrl(OssPathConfig.getSceneNewsPath(news.getImage()));
//            news.setImage(imageUrl);
//        }
        return new PageInfo(list);
    }

    /**
     * 根据Id获取信息
     * @param id
     * @return
     */
    public SceneExtra getInfo(Integer id){
        SceneExtra sceneExtra = sceneExtraDao.get(id);
        if(StringUtils.isNotEmpty(sceneExtra.getImage())){
            String imageUrl = ossService.getSimpleUrl(OssPathConfig.getSceneExtraPath(sceneExtra.getImage()));
            sceneExtra.setImage(imageUrl);
        }else{
            sceneExtra.setImage(sceneExtra.getExtUrl());
        }
        return sceneExtra;
    }
}
