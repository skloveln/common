package cn.zpc.mvc.store.dao;

import cn.zpc.common.dao.CrudDao;
import cn.zpc.common.dao.annotation.Dao;
import cn.zpc.mvc.store.entity.StoreAdvertTemplate;

import java.util.List;

@Dao
public interface StoreAdvertTemplateDao extends CrudDao<StoreAdvertTemplate>{

    List<StoreAdvertTemplate> getAll();

}
