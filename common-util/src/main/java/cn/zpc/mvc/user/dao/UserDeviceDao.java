package cn.zpc.mvc.user.dao;

import cn.zpc.common.dao.CrudDao;
import cn.zpc.common.dao.annotation.Dao;
import cn.zpc.mvc.user.entity.UserDevice;
import org.apache.ibatis.annotations.Param;

/**
 * Description:设备信息
 * Author: sukai
 * Date: 2017-09-06
 */
@Dao
public interface UserDeviceDao extends CrudDao<UserDevice>{

    /**
     * 通过deviceToken查询数据
     * @param deviceToken
     * @return
     */
    UserDevice findByDeviceToken(@Param("devicePushId") String deviceToken);

    /**
     * 把对应的用户或token删除
     * @param deviceToken
     * @param userId
     * @return
     */
    Integer delete(@Param("devicePushId") String deviceToken, @Param("userId") Integer userId);

    /**
     * 把对应的token用户信息删除
     * @param deviceToken
     * @return
     */
    Integer deleteByDeviceToken(@Param("devicePushId") String deviceToken);


    /**
     * 把对应的用户删除
     * @param userId
     * @return
     */
    Integer deleteByUserId(@Param("userId") Integer userId);

}
