package cn.zpc.common.plugins.image;

import cn.zpc.common.plugins.oss.OssService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;


/**
 * Description:
 * Author: Simon
 * Date: 2017-08-30
 */

@Service
@Lazy(false)
public class ImageService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final OssService ossService;


    @Autowired
    public ImageService(OssService ossService) {
        this.ossService = ossService;
    }


}
