package cn.zpc.mvc.user.service;

import cn.zpc.common.plugins.oss.OssService;
import cn.zpc.common.serivce.BaseService;
import cn.zpc.common.utils.StringUtils;
import cn.zpc.common.web.result.DataResult;
import cn.zpc.common.web.result.Result;
import cn.zpc.mvc.user.dao.UserAppDao;
import cn.zpc.mvc.user.dao.UserDao;
import cn.zpc.mvc.user.dao.UserDeviceDao;
import cn.zpc.mvc.user.dao.UserFeedDao;
import cn.zpc.mvc.user.entity.User;
import cn.zpc.mvc.user.entity.UserApp;
import cn.zpc.mvc.user.entity.UserDevice;
import cn.zpc.mvc.user.entity.UserFeed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Date;

/**
 * Description: 应用相关操作
 * Author: sukai
 * Date: 2017-09-11
 */
@Service
public class UserAppService extends BaseService {

    private final UserAppDao userAppDao;
    private final OssService ossService;
    private final UserDao userDao;
    private final UserDeviceDao userDeviceDao;

    @Autowired
    private UserFeedDao userFeedDao;

    @Autowired
    public UserAppService(UserAppDao userAppDao, OssService ossService, UserDeviceDao userDeviceDao, UserDao userDao) {
        this.userAppDao = userAppDao;
        this.ossService = ossService;
        this.userDeviceDao = userDeviceDao;
        this.userDao = userDao;
    }

    /**
     * 获取新的App版成信息
     * @param appOS
     * @return
     */
    public Result getNewVersionInfo(Integer appOS){

        UserApp userApp = userAppDao.findNewestVersionByPlatform(appOS);
        String fileKey = userApp.getUpdateUrl();

        if(ossService.checkFileKeyExists(fileKey)){
            userApp.setUpdateUrl(ossService.getFileUrl(fileKey));
        }

        DataResult<UserApp> dataResult = new DataResult<>();
        dataResult.setData(userApp);
        return dataResult;
    }

    /**
     * 保存设备信息
     * @param userDevice
     */
    @Transactional(rollbackFor = SQLException.class)
    public synchronized void saveDeviceInfo(UserDevice userDevice){
        // 删除
        userDeviceDao.deleteByDeviceToken(userDevice.getDevicePushId());
        userDeviceDao.deleteByUserId(userDevice.getUserId());
        // 新增
        userDevice.setUpdateTime(new Date());
        userDevice.setCreateTime(new Date());
        userDeviceDao.insert(userDevice);
    }

    /**
     * 保存用户反馈信息
     * @param feedContent
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = SQLException.class)
    public Integer saveFeedInfo(String feedContent, Integer userId){

        User user = userDao.get(userId);
        String phone = user.getPhone();

        UserFeed userFeed = new UserFeed();
        userFeed.setContent(feedContent);
        userFeed.setCreateTime(new Date());
        userFeed.setPhone(phone);
        userFeed.setState(0);
        userFeed.setUserId(userId);

        return userFeedDao.saveFeedContent(userFeed);
    }

}
