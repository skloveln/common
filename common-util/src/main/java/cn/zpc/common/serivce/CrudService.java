package cn.zpc.common.serivce;

import cn.zpc.common.dao.CrudDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description:基础数据库操作服务
 * Author: sukai
 * Date: 2017-08-15
 */
@Transactional(readOnly = true)
public class CrudService<T> extends BaseService {

    @Autowired
    private CrudDao<T> crudDao;


    /**
     * 获取单条数据
     * @param id 编号
     */
    public T get(int id){
        return crudDao.get(id);
    }


    /**
     * 插入数据
     * @param entity 实体
     */
    @Transactional(rollbackFor = Exception.class)
    public int insert(T entity){
        return crudDao.insert(entity);
    }


    /**
     * 更新数据
     * @param entity 实体
     */
    @Transactional(rollbackFor = Exception.class)
    public int update(T entity){
        return crudDao.update(entity);
    }


    /**
     * 删除数据，逻辑删除
     * @param id 实体
     */
    @Transactional(rollbackFor = Exception.class)
    public int delete(Integer id){
        return crudDao.delete(id);
    }

}
