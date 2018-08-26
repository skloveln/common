package cn.zpc.mvc.user.dao;

import cn.zpc.common.dao.CrudDao;
import cn.zpc.common.dao.annotation.Dao;
import cn.zpc.mvc.user.entity.CompanyIdentification;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PathVariable;

@Dao
public interface CompanyIdentificationDao extends CrudDao<CompanyIdentification>{

    /**
     * 通过Id获取认证信息
     * @param userId
     * @return
     */
    CompanyIdentification getByUserId(@Param("userId") Integer userId);

    /**
     * 删除用户认证信息
     * @param userId
     * @return
     */
    Integer delete(@Param("userId") Integer userId);
}
