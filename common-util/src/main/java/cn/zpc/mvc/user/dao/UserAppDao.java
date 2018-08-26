package cn.zpc.mvc.user.dao;

import cn.zpc.common.dao.BaseDao;
import cn.zpc.common.dao.annotation.Dao;
import cn.zpc.mvc.user.entity.UserApp;

/**
 * Description: APP版本更新
 * Author: sukai
 * Date: 2017-09-11
 */
@Dao
public interface UserAppDao extends BaseDao {

    /**
     * 根据各种信息获取版本更新信息
     * @param appOs
     * @return
     */
    UserApp findNewestVersionByPlatform(Integer appOs);


}
