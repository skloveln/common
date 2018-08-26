package cn.zpc.common.plugins.im.io.rong;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Description: 融云即时通讯服务
 * User: sukai
 * Date: 2018-04-17   16:53
 */
@Service
public class RongCloudService {

    private RongCloud rongCloud;

    public RongCloudService(@Value("${rongCloud.AppKey}") String appKey, @Value("${rongCloud.AppSecret}") String appSecret){
        rongCloud = RongCloud.getInstance(appKey, appSecret);
    }

    public RongCloud getRongCloud(){
        return rongCloud;
    }

}
