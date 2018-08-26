package cn.zpc.mvc.user.dao;

import cn.zpc.common.dao.CrudDao;
import cn.zpc.common.dao.annotation.Dao;
import cn.zpc.mvc.user.entity.UserWechat;
import org.apache.ibatis.annotations.Param;

/**
 * Description:
 * User: sukai
 * Date: 2018-03-20   14:48
 */
@Dao
public interface UserWeChatDao extends CrudDao<UserWechat>{

    /**
     * 根据openId查找用户信息
     * @param openId
     * @return
     */
    UserWechat findByOpenId(@Param("openId") String openId);

    /**
     * 根据用户Id获取微信信息
     * @param userId
     * @return
     */
    UserWechat findByUserId(@Param("userId") Integer userId);

    /**
     * 根据用户手机号获取微信信息
     * @param phone
     * @return
     */
    UserWechat findByUserPhone(@Param("phone") String phone);

    /**
     * 解除微信绑定
     * @param userId
     * @return
     */
    Integer deleteWechat(@Param("userId") Integer userId);
}
