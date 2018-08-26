package cn.zpc.mvc.user.dao;

import cn.zpc.common.dao.CrudDao;
import cn.zpc.common.dao.annotation.Dao;
import cn.zpc.mvc.user.entity.UserAccount;
import org.apache.ibatis.annotations.Param;

@Dao
public interface UserAccountDao extends CrudDao<UserAccount>{

    UserAccount getByUserId(@Param("userId") Integer userId);

}
