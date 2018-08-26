package cn.zpc.common.plugins.oss;

/**
 * Oss路径配置
 */
public class OssPathConfig {

    /**
     * 获取商家认证图片
     * @param fileName
     * @return
     */
    public static String getIdentificationPath(String fileName){
        return "store/identification/" + fileName;
    }

    /**
     * 获取店铺的封面图
     * @param fileName
     * @return
     */
    public static String getStoreImagePath(String fileName){
        return "store/image/" + fileName;
    }

    /**
     * 获取店铺的logo图
     * @param fileName
     * @return
     */
    public static String getStoreLogoPath(String fileName){
        return "store/logo/" + fileName;
    }

    /**
     * 得到场景的图片
     */
    public static String getSceneImagePath(String fileName){
        return "scene/images/" + fileName;
    }

    /**
     * 得到场景类型的Icon图
     */
    public static String getSceneTypeIconPath(String fileName){
        return "scene/types/" + fileName;
    }

    /**
     * 得到广告的图片
     */
    public static String getAdvertPath(String fileName){
        return "sys/advert/" + fileName;
    }

    /**
     * 得到用户头像
     */
    public static String getUserAvatarPath(String fileName){
        return  "user/avatar/"  + fileName;
    }

    /**
     * 得到通知小图
     */
    public static String getNoticeImagePath(String fileName){
        return  "notice/image/"  + fileName;
    }


    /**
     * 得到场景服务图标的Key
     */
    public static String getServiceIconPath(String fileName){
        return "scene/service/" + fileName;
    }

    /**
     * 获取场景拍摄过的作品图片
     * @param fileName
     * @return
     */
    public static String getSceneWorksPath(String fileName){
        return "scene/works/" + fileName;
    }

    /**
     * 获取店铺周边的场景图片
     * @param fileName
     * @return
     */
    public static String getStorePeripheryPath(String fileName){
        return "store/periphery/" + fileName;
    }

    /**
     * 获取店铺花絮
     * @param fileName
     * @return
     */
    public static String getStoreTitbitsPath(String fileName){
        return "store/titbits/" + fileName;
    }

    /**
     * 获取店铺服务
     * @param fileName
     * @returns
     */
    public static String getStoreServicesPath(String fileName){
        return "store/services/" + fileName;
    }

    /**
     * 获取场景资讯、快报
     * @param fileName
     * @returns
     */
    public static String getSceneExtraPath(String fileName){
        return "scene/extra/" + fileName;
    }

    /**
     * 获取图集照片
     * @param fileName
     * @return
     */
    public static String getAlbumImagePath(String fileName){
        return "album/images/" + fileName;
    }

    /**
     * 获取店铺广告图
     * @param fileName
     * @returns
     */
    public static String getStoreAdvertPath(String fileName){
        return "store/advert/" + fileName;
    }
}
