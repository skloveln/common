package cn.zpc.mvc.sys.service;

import cn.zpc.common.plugins.oss.OssPathConfig;
import cn.zpc.common.serivce.BaseService;
import cn.zpc.mvc.scene.dao.SceneInfoDao;
import cn.zpc.mvc.scene.service.SceneService;
import cn.zpc.mvc.store.entity.Store;
import cn.zpc.mvc.store.service.StoreService;
import cn.zpc.mvc.sys.dao.NotificationDao;
import cn.zpc.mvc.sys.entity.Notification;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class NotificationService extends BaseService{

    @Autowired
    private NotificationDao notificationDao;
    @Autowired
    private SceneService sceneService;
    @Resource
    private SceneInfoDao sceneInfoDao;
    @Resource
    private StoreService storeService;

    /**
     * 获取通过列表
     * @param pageNum
     * @param pageSize
     * @param userId
     * @return
     */
    public PageInfo getPass(Integer pageNum, Integer pageSize, Integer userId){
        PageHelper.startPage(pageNum, pageSize, true);
        List<Notification> list = notificationDao.findPass(userId);
        for(Notification notification : list){
            if(notification.getType() == 2){
                notification.setImageUrl(sceneService.getSceneMainImage(Integer.parseInt(notification.getExt())));
                notification.setSceneName(sceneInfoDao.getSceneNameById(Integer.parseInt(notification.getExt())));
            }
        }
        return new PageInfo(list);
    }

    /**
     * 获取未通过列表
     * @param pageNum
     * @param pageSize
     * @param userId
     * @return
     */
    public PageInfo getNoPass(Integer pageNum, Integer pageSize, Integer userId){
        PageHelper.startPage(pageNum, pageSize, true);
        List<Notification> list = notificationDao.findNoPass(userId);
        for(Notification notification : list){
            if(notification.getType() == 2){
                notification.setImageUrl(sceneService.getSceneMainImage(Integer.parseInt(notification.getExt())));
                notification.setSceneName(sceneInfoDao.getSceneNameById(Integer.parseInt(notification.getExt())));
            }
        }
        return new PageInfo(list);
    }

    /**
     * 获取系统通知列表
     * @param pageNum
     * @param pageSize
     * @param userId
     * @return
     */
    public PageInfo getSystem(Integer pageNum, Integer pageSize, Integer userId){
        PageHelper.startPage(pageNum, pageSize, true);
            List<Notification> list = notificationDao.findSystem(userId);
        for(Notification notification : list){
            notification.setImageUrl(ossService.getSimpleUrl(OssPathConfig.getNoticeImagePath(notification.getImageUrl())));
            if (notification.getType() == 6){
                notification.setSceneTotalCount(sceneInfoDao.countScene(userId));
            }
        }
        return new PageInfo(list);
    }
}
