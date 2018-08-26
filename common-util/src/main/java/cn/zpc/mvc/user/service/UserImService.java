package cn.zpc.mvc.user.service;

import cn.zpc.common.plugins.im.io.rong.RongCloud;
import cn.zpc.common.plugins.im.io.rong.RongCloudService;
import cn.zpc.common.utils.StringUtils;
import cn.zpc.mvc.user.dao.UserImDao;
import cn.zpc.common.plugins.im.io.rong.models.response.TokenResult;
import cn.zpc.common.plugins.im.io.rong.models.user.UserModel;
import cn.zpc.mvc.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 * User: sukai
 * Date: 2018-04-09   17:41
 */
@Service
public class UserImService {

    @Autowired
    private UserService userService;
    @Resource
    private UserImDao userImDao;
    @Resource
    private RongCloudService rongCloudService;

    /**
     * 换取融云用户Token
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = SQLException.class)
    public Map<String, Object> userRegisterToken(Integer userId){
        Map<String, Object> map = new HashMap<>();

        RongCloud rongCloud = rongCloudService.getRongCloud();
        User user = userService.getUser(userId);
        map.put("id", userId);
        map.put("nickName", user.getNickname());
        map.put("avatar", user.getAvatar());

        UserModel userIm = new UserModel()
                .setId(userId+"")
                .setName(user.getNickname())
                .setPortrait(user.getAvatar());

        String token  = userImDao.getToken(userId);
        if(StringUtils.isEmpty(token)){
            TokenResult result;
            try {
                result = rongCloud.user.register(userIm);
                map.put("token", result.getToken());
                userImDao.insert(userId, result.getToken());
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            map.put("token", token);
        }

        return map;
    }

}
