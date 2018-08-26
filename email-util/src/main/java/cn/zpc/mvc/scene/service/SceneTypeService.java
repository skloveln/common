package cn.zpc.mvc.scene.service;

import cn.zpc.common.plugins.oss.OssPathConfig;
import cn.zpc.common.serivce.BaseService;
import cn.zpc.mvc.scene.dao.SceneTypeDao;
import cn.zpc.mvc.scene.entity.SceneType;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SceneTypeService extends BaseService {

    @Autowired
    private SceneTypeDao sceneTypeDao;

    /**
     * 获取指定场景类型下多有类型Id集合
     * @param type
     * @return
     */
    public Set<Integer> getChildrenTypeIds(Integer type){
        Set<Integer> types = new HashSet<>();
        if(type != null && type != 0){
            types.add(type);
            Set<SceneType> typeList = getChildren(type);
            for(SceneType sceneType : typeList){
                types.add(sceneType.getId());
            }
        }
        return types;
    }

    /**
     * 获取指定场景类型的所有子节点（递归）,层叠型
     * @param list
     * @return
     */
    private List<SceneType> getChildren(List<SceneType> list){
        for(SceneType child : list){
            List<SceneType> childList = sceneTypeDao.getChildTypes(child.getId());
            if(childList.size() > 0){
                getChildren(childList);
                child.setHasChild(true);
                child.setChildList(childList);
            }else {
               child.setHasChild(false);
            }
        }
        return list;
    }

    /**
     * 获取指定场景的所有子场景（递归），平行型，包含自身
     * @param typeId
     * @return
     */
    public Set<SceneType> getChildren(Integer typeId){
        Set set = new HashSet();
        List<SceneType> typeList = sceneTypeDao.getChildTypes(typeId);
        for(SceneType sceneType : typeList){
            set.add(sceneType);
            List<SceneType> child = sceneTypeDao.getChildTypes(sceneType.getId());
            if(child.size() > 0){
                set.addAll(getChildren(sceneType.getId()));
            }
        }

        return set;
    }

    /**
     * 获取所有的场景类型
     * @return
     */
    public List<SceneType> getAllTypes(){

        List<SceneType> top =  sceneTypeDao.getTopTypes();
        getChildren(top);

        return top;
    }

    /**
     * 得到场景类型的名称
     * @param id
     * @return
     */
    public String getSceneTypeName(Integer id){

        return sceneTypeDao.get(id).getSceneTypeName();
    }

    /**
     * 得到热门场景类型
     * @return
     */
    public List<SceneType> hotTypes(){

        List<SceneType> list = sceneTypeDao.getHotTypes();
        for(SceneType sceneType : list){
            sceneType.setImageUrl(ossService.getSimpleUrl(OssPathConfig.getSceneTypeIconPath(sceneType.getImageUrl())));
        }

        return list;
    }

}
