package cn.zpc.mvc.user.dao;

import cn.zpc.mvc.user.entity.User;
import cn.zpc.common.dao.CrudDao;
import cn.zpc.common.dao.annotation.Dao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Author: sukai
 * Date: 2017/8/3.
 */
@Dao
public interface UserDao extends CrudDao<User>{

    /**
     * 看某用户是否为新用户
     * @param userId
     * @return
     */
    Integer isNewUser(@Param("userId") Integer userId);

    /**
     * 获取某用户邀请码
     * @param userId
     * @return
     */
    String getUserCode(@Param("userId") Integer userId);

    /**
     * 插入邀请码记录
     * @param oldUserId
     * @param newUserId
     * @return
     */
    Integer insertCodeLog(@Param("oldUserId") Integer oldUserId, @Param("newUserId") Integer newUserId);

    /**
     * 获取某用户的邀请人数
     * @param userId
     * @return
     */
    Integer countInvitatoryNum(@Param("userId") Integer userId);

    /**
     * 根据验证码获取邀请人
     * @param userId
     * @param code
     * @return
     */
    Integer getInviteUser(@Param("userId") Integer userId, @Param("code") String code);

    /**
     * 获取用户积分
     * @param userId
     * @return
     */
    Integer getUserIntegral(Integer userId);

    /**
     * 增加用户积分
     * @param userId
     * @return
     */
    Integer updateIntegral(@Param("userId") Integer userId);

    /**
     * 兑换奖励，减少积分
     * @param userId
     * @return
     */
    Integer deleteIntegral(@Param("userId") Integer userId);

    /**
     * 兑换奖品记录
     * @return
     */
    Integer insertPrize(@Param("userId") Integer userId);

    /**
     * 增加某用户邀请码
     * @param userId
     * @param code
     * @return
     */
    Integer insertUserCode(@Param("userId") Integer userId, @Param("code") String code);

    /**
     * 根据手机号查询登录验证码发送次数
     */
    int countLoginCodeByPhone(String phone);

    /**
     * 根据手机号查询绑定验证码发送次数
     */
    int countBindCodeByPhone(String phone);

    /**
     * 根据手机号查询注册验证码发送次数
     */
    int countRegisterCodeByPhone(String phone);

    /**
     * 根据手机号查询修改密码验证码发送次数
     */
    int countResetPasswordCodeByPhone(String phone);

    /**
     * 根据手机号查询修改密码验证码发送次数
     */
    int countChangePhoneCodeByPhone(String phone);

    /**
     * 通过手机号查找到相关用户
     */
    User getUserByPhone(String phone);

    /**
     * 根据令牌信息查找用户
     */
    User findUserBySwapToken(String token);

    /**
     * 上传头像
     */
    void updateAvatar(@Param("fileKey") String fileKey, @Param("userId") Integer userId);

    /**
     * 上传背景图
     */
    void updateCardBg(@Param("fileKey") String fileKey, @Param("userId") Integer userId);

    /**
     * 获取职业选择列表
     * @return
     */
    List<String> selectProfession();


    /**
     * 增加新用户记录
     * @param userId
     */
    Integer insertNewUser(@Param("userId")Integer userId);

    /**
     * 删除新用户记录
     * @param userId
     * @return
     */
    Integer deleteNewUser(@Param("userId")Integer userId);
}
