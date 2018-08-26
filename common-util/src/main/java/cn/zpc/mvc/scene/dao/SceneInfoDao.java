package cn.zpc.mvc.scene.dao;

import cn.zpc.common.dao.CrudDao;
import cn.zpc.common.dao.annotation.Dao;
import cn.zpc.mvc.scene.entity.SceneInfo;
import cn.zpc.mvc.scene.entity.SceneMap;
import cn.zpc.mvc.sys.entity.SearchResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Dao
public interface SceneInfoDao extends CrudDao<SceneInfo> {

    /**
     * 按发布时间获取场景
     * @return
     */
    List<SceneInfo> getListByTime();

    /**
     * 获取某店主自家的VR场景
     * @param userId
     * @return
     */
    List<SceneInfo> getVrListByUser(@Param("userId") Integer userId);

    /**
     * 获取全部VR场景
     * @return
     */
    List<SceneInfo> getVrList();

    /**
     * 获取某店主自家场景
     * @param userId
     * @param status
     * @return
     */
    List<SceneInfo> getListByUser(@Param("userId") Integer userId, @Param("status") Integer status);


    /**
     * 按热度获取场景
     * @return
     */
    List<SceneInfo> getHotScene();

    /**
     * 按场景类型搜索场景
     * @param typeIds
     * @return
     */
    List<SceneInfo> getListByType(@Param("typeId") Set<Integer> typeIds);

    /**
     * 按关键词搜索场景
     * @param keyword
     * @return
     */
    List<SceneInfo> search(@Param("keyword") String keyword, @Param("address") String address,
                           @Param("type") Set<Integer> typeIds, @Param("priceType") Integer priceType,
                           @Param("priceMin") Integer priceMin, @Param("priceMax") Integer priceMax,
                           @Param("areaMin") Integer areaMin, @Param("areaMax")Integer areaMax);

    /**
     * 获取场景信息
     * @param userId
     * @param sceneId
     * @return
     */
    SceneInfo getManage(@Param("userId") Integer userId, @Param("id") Integer sceneId);

    /**
     * 获取指定场景信息
     * @param sceneId
     * @return
     */
    SceneInfo manageGet(@Param("id") Integer sceneId);


    /**
     * 清空排序
     * @param userId
     * @return
     */
    Integer clearWeight(@Param("userId") Integer userId);

    /**
     * 获取最新场景按用户分组
     * @return
     */
    List<SceneInfo> getNewSceneGroupUser();

    /**
     * 获取APP首页推荐的场景Id
     * @return
     */
    List<Integer> getRecommendScene();


    /**
     * 获取场景Tab页
     * @return
     */
    List<SearchResult> getSceneTab(@Param("userId") Integer userId);

    /**
     * 获取坐标范围内的场景（包括图集）
     * @param topLon
     * @param topLat
     * @param bottomLon
     * @param bottomLat
     * @return
     */
    List<SceneMap> getSceneMap(@Param("topLon") Double topLon, @Param("topLat") Double topLat,
                               @Param("bottomLon") Double bottomLon, @Param("bottomLat") Double bottomLat);


    /**
     * 获取指定点附近的场景（按远近排）
     * @param lon
     * @param lat
     * @return
     */
    List<SceneMap> getNearestScene(@Param("lon") Double lon, @Param("lat") Double lat);


    /**
     * 获取指定坐标的场景
     * @param lon
     * @param lat
     * @return
     */
    List<SceneMap> getLonLatScene(@Param("lon") Double lon, @Param("lat") Double lat);

    /**
     * 获取场景总数
     * @return
     */
    Integer countScene(@Param("userId") Integer userId);

    /**
     * 获取城市情况
     * @return
     */
    List<String> selectCity();

    /**
     * 获取城市情况
     * @return
     */
    Integer deleteScene(@Param("userId") Integer userId);

    
    /** 
    * Description: 根据权重和创建时间对场景信息进行排序 
    * Param:  
    * return:  
    * Author: W
    * Date: 2018/4/17 14:45 
    */ 
    List<SceneInfo> getSceneList(@Param("userId") Integer userId);
    
    /** 
     * @description:  根据id获取场景名称
     * @param id	
     * @return: java.lang.String
     * @author: W  
     * @date: 2018/4/26  14:28  
     */ 
    String getSceneNameById(@Param("id") Integer id);

}
