package cn.zpc.mvc.user.dao;

import cn.zpc.common.dao.CrudDao;
import cn.zpc.common.dao.annotation.Dao;
import cn.zpc.mvc.user.entity.UserToken;

/**
 * Description:用户身份令牌
 * Author: sukai
 * Date: 2017-08-15
 */
@Dao
public interface UserTokenDao extends CrudDao<UserToken>{

    // 根据用户id获取用户令牌信息
    UserToken findUserTokenByUserId(int userId);

    // 根据长效令牌查找用户
    UserToken findUserBySwapToken(String swapToken);
}
