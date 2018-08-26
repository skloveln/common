package cn.zpc.mvc.user.service;

import cn.zpc.common.serivce.BaseService;
import cn.zpc.mvc.user.dao.UserAccountDao;
import cn.zpc.mvc.user.dao.UserShareDao;
import cn.zpc.mvc.user.entity.UserAccount;
import cn.zpc.mvc.user.entity.UserShare;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

@Service
public class UserAccountService extends BaseService{

    @Autowired
    private UserShareDao userShareDao;
    @Autowired
    private UserAccountDao userAccountDao;

    /**
     * 检测是否分享过该场景
     * @param userId
     * @param targetType
     * @param targetId
     * @return true ：分享过    false： 没分享过
     */
    public Boolean checkShare(Integer userId, Integer targetType, Integer targetId){

        UserShare userShare = userShareDao.getByTarget(userId, targetId, targetType);

        return userShare != null;
    }


    /**
     * 增加积分
     * @param userId
     * @param targetType
     * @param targetId
     * @return
     */
    @Transactional(rollbackFor = SQLException.class)
    public Boolean addIntegral(Integer userId, Integer targetType, Integer targetId){

        // 先检查是否分享过该目标, 分享过更新分享次数， 没分享增加积分并增加记录
        if(checkShare(userId, targetType, targetId)){ // 分享过
            UserShare userShare = userShareDao.getByTarget(userId, targetId, targetType);
            userShare.setCount(userShare.getCount() + 1);
            return userShareDao.update(userShare) > 0;
        }else { // 没分享
            UserShare userShare = new UserShare();
            userShare.setCount(1);
            userShare.setUserId(userId);
            userShare.setTargetType(targetType);
            userShare.setTargetId(targetId);
            userShareDao.insert(userShare);
            // 增加积分
            UserAccount userAccount = userAccountDao.getByUserId(userId);
            userAccount.setIntegral(userAccount.getIntegral() + 2);
            return userAccountDao.update(userAccount) > 0;
        }

    }


    /**
     * 获取积分
     * @param userId
     */
    public Integer getIntegral(Integer userId) {
        UserAccount account = userAccountDao.getByUserId(userId);
        if(account == null){
            return 0;
        }
        return account.getIntegral();
    }
    
}
