package cn.zpc.mvc.user.dao;

import cn.zpc.common.dao.annotation.Dao;
import org.apache.ibatis.annotations.Param;

/**
 * Description:
 * User: sukai
 * Date: 2018-04-10   10:02
 */
@Dao
public interface UserImDao{

    /**
     * 获取用户token
     * @param userId
     * @return
     */
    String getToken(@Param("userId") Integer userId);

    /**
     * 插入token
     * @param userId
     * @param token
     * @return
     */
    Integer insert(@Param("userId") Integer userId, @Param("token") String token);

}
