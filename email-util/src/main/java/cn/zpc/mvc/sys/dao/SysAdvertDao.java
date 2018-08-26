package cn.zpc.mvc.sys.dao;

import cn.zpc.common.dao.CrudDao;
import cn.zpc.common.dao.annotation.Dao;
import cn.zpc.mvc.sys.entity.SysAdvert;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Dao
public interface SysAdvertDao extends CrudDao<SysAdvert>{

    List<SysAdvert> getAdByType(@Param("type") Integer type);

    List<SysAdvert> getAdList();
}
