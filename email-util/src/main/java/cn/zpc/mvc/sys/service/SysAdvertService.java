package cn.zpc.mvc.sys.service;

import cn.zpc.common.plugins.oss.OssPathConfig;
import cn.zpc.common.plugins.oss.OssService;
import cn.zpc.mvc.sys.dao.SysAdvertDao;
import cn.zpc.mvc.sys.entity.SysAdvert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SysAdvertService{

    @Autowired
    private SysAdvertDao sysAdvertDao;
    @Autowired
    private OssService ossService;

    /**
     * 获取本地场景推广
     */
    public List<SysAdvert> getBanner(){

        List<SysAdvert> list = sysAdvertDao.getAdList();
        for(SysAdvert ad : list){
            ad.setImage(ossService.getSimpleUrl(OssPathConfig.getAdvertPath(ad.getImage())));
        }

        return list;
    }

    /**
     * 首页Icon分类
     * @return
     */
    public List<Map> getTypesIcon(){

        List list = new ArrayList();
        Map<String, Object> map = new HashMap();
        map.put("sceneTypeName", "年代");
        map.put("imageUrl", ossService.getSimpleUrl(OssPathConfig.getSceneTypeIconPath("index_niandai@3x.png")));
        list.add(map);
        map = new HashMap<>();
        map.put("sceneTypeName", "现代");
        map.put("imageUrl", ossService.getSimpleUrl(OssPathConfig.getSceneTypeIconPath("index_xiandai@3x.png")));
        list.add(map);
        map = new HashMap<>();
        map.put("sceneTypeName", "科幻");
        map.put("imageUrl", ossService.getSimpleUrl(OssPathConfig.getSceneTypeIconPath("index_kehuan@3x.png")));
        list.add(map);
        map = new HashMap<>();
        map.put("sceneTypeName", "古装");
        map.put("imageUrl", ossService.getSimpleUrl(OssPathConfig.getSceneTypeIconPath("index_guzhuang@3x.png")));
        list.add(map);
        map = new HashMap<>();
        map.put("sceneTypeName", "影棚");
        map.put("imageUrl", ossService.getSimpleUrl(OssPathConfig.getSceneTypeIconPath("index_yingpeng@3x.png")));
        list.add(map);
        map = new HashMap<>();
        map.put("sceneTypeName", "实景");
        map.put("imageUrl", ossService.getSimpleUrl(OssPathConfig.getSceneTypeIconPath("index_yingcheng@3x.png")));
        list.add(map);
        map = new HashMap<>();
        map.put("sceneTypeName", "外景");
        map.put("imageUrl", ossService.getSimpleUrl(OssPathConfig.getSceneTypeIconPath("index_waijing.png")));
        list.add(map);
        map = new HashMap<>();
        map.put("sceneTypeName", "其他");
        map.put("imageUrl", ossService.getSimpleUrl(OssPathConfig.getSceneTypeIconPath("index_gengduo.png")));
        list.add(map);

        return list;
    }



}
