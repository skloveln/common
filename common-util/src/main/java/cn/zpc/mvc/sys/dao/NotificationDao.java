package cn.zpc.mvc.sys.dao;

import cn.zpc.common.dao.CrudDao;
import cn.zpc.common.dao.annotation.Dao;
import cn.zpc.mvc.sys.entity.Notification;
import org.apache.ibatis.annotations.Param;

import java.util.*;

@Dao
public interface NotificationDao extends CrudDao<Notification>{

    /**
     * 获取通过审核通知
     * @param userId
     * @return
     */
    List<Notification> findPass(@Param("userId") Integer userId);

    /**
     * 获取未通过审核通知
     * @param userId
     * @return
     */
    List<Notification> findNoPass(@Param("userId") Integer userId);

    /**
     * 获取系统通知
     * @param userId
     * @return
     */
    List<Notification> findSystem(@Param("userId") Integer userId);
}
