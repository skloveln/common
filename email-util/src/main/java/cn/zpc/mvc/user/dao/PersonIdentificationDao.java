package cn.zpc.mvc.user.dao;

import cn.zpc.common.dao.CrudDao;
import cn.zpc.common.dao.annotation.Dao;
import cn.zpc.mvc.user.entity.PersonIdentification;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PathVariable;

@Dao
public interface PersonIdentificationDao extends CrudDao<PersonIdentification>{

    /**
     * 通过用户Id获取认证信息
     * @param userId
     * @return
     */
    PersonIdentification getByUserId(@Param("userId") Integer userId);

    /**
     * 删除用户认证信息
     * @param userId
     * @return
     */
    Integer delete(@Param("userId") Integer userId);
}
