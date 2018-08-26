package cn.zpc.mvc.user.dao;

import cn.zpc.common.dao.CrudDao;
import cn.zpc.common.dao.annotation.Dao;
import cn.zpc.mvc.user.entity.UserShare;
import org.apache.ibatis.annotations.Param;

@Dao
public interface UserShareDao extends CrudDao<UserShare>{

    UserShare getByTarget(@Param("userId") Integer userId, @Param("targetId") Integer targetId, @Param("targetType") Integer targetType);




}
