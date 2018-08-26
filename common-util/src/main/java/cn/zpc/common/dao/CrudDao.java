package cn.zpc.common.dao;

/**
 * Description:增删改查
 * Author: sukai
 * Date: 2017-08-15
 */
public interface CrudDao<T> extends BaseDao{

    // 根据id获取内容
    T get(Integer id);

    // 更新内容
    Integer update(T entity);

    // 新增内容
    Integer insert(T entity);

    // 删除内容
    Integer delete(Integer id);

}
