package cn.zpc.mvc.user.dao;

import cn.zpc.common.dao.annotation.Dao;
import cn.zpc.mvc.user.entity.PageAdvert;

@Dao
public interface AdvertDao {

    PageAdvert getAdvert();

}
