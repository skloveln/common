package cn.zpc.mvc.user.service;

import cn.zpc.common.handler.exception.GlobalExceptionResult;
import cn.zpc.common.plugins.oss.OssPathConfig;
import cn.zpc.common.plugins.sms.SmsService;
import cn.zpc.common.plugins.oss.OssService;
import cn.zpc.common.redis.RedisService;
import cn.zpc.common.utils.CodeUtils;
import cn.zpc.common.utils.IdGen;
import cn.zpc.common.utils.RandomUtils;
import cn.zpc.common.utils.StringUtils;

import cn.zpc.common.web.result.DataResult;
import cn.zpc.common.web.result.MessageResult;
import cn.zpc.common.web.result.Result;

import cn.zpc.mvc.album.dao.AlbumDao;
import cn.zpc.mvc.scene.dao.SceneInfoDao;
import cn.zpc.mvc.store.dao.StoreDao;
import cn.zpc.mvc.store.entity.Store;
import cn.zpc.mvc.user.dao.*;
import cn.zpc.mvc.user.entity.User;
import cn.zpc.common.serivce.CrudService;
import cn.zpc.mvc.user.entity.UserAccount;
import cn.zpc.mvc.user.entity.UserToken;
import cn.zpc.mvc.user.entity.UserWechat;
import cn.zpc.mvc.user.log.UserLog;
import cn.zpc.mvc.user.log.UserLogUtils;
import cn.zpc.mvc.user.security.UserContext;
import cn.zpc.mvc.user.security.exception.AuthFailureException;
import cn.zpc.mvc.user.security.factory.AuthTokenFactory;
import cn.zpc.mvc.user.security.model.BaseToken;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.sql.SQLException;
import java.util.Date;

import static cn.zpc.mvc.user.utils.UserUtils.THIRTY_DAYS;

/**
 * Description:用户服务
 * Author: sukai
 * Date: 2017-08-14
 */
@Service
@Transactional(readOnly = false)
@Scope("singleton")
public class UserService extends CrudService<User> {

    private final static Logger logger = LoggerFactory.getLogger(UserService.class);

    // 验证码缓存时间，十分钟
    private final static int CACHE_TIME = 60 * 10;
    // 注册验证码 缓存时间
    private final static String PREFIX_REGISTER_CODE = "code_register_expired_";
    // 登录验证码 缓存时间
    private final static String PREFIX_LOGIN_CODE = "code_login_expired_";
    // 修改密码验证码
    private final static String PREFIX_RESET_CODE = "code_reset_expired_";
    // 绑定手机验证码
    private final static String PREFIX_BIND_CODE = "code_bind_expired";
    // 绑定手机验证码
    private final static String PREFIX_CHANGE_CODE = "code_change_expired";

    private final UserDao userDao;
    private final UserTokenDao userTokenDao;
    private final SmsService smsService;
    private AuthTokenFactory tokenFactory;
    private final RedisService redisService;
    private final MessageSource messageSource;
    private final OssService ossService;


    @Autowired
    private UserAccountDao userAccountDao;
    @Autowired
    private UserWechatService userWechatService;
    @Autowired
    private UserWeChatDao userWeChatDao;
    @Autowired
    private StoreDao storeDao;
    @Autowired
    private SceneInfoDao sceneInfoDao;
    @Resource
    private CompanyIdentificationDao companyIdentificationDao;
    @Resource
    private PersonIdentificationDao personIdentificationDao;
    @Resource
    private AlbumDao albumDao;

    @Autowired
    public UserService(UserDao userDao, UserTokenDao userTokenDao, SmsService smsService, RedisService redisService, MessageSource messageSource, OssService ossService) {
        this.userDao = userDao;
        this.userTokenDao = userTokenDao;
        this.smsService = smsService;
        this.redisService = redisService;
        this.messageSource = messageSource;
        this.ossService = ossService;
    }


    @Autowired
    public void setTokenFactory(AuthTokenFactory tokenFactory) {
        this.tokenFactory = tokenFactory;
    }


    /**
     * 发送登录验证码
     */
    public Result sendLoginCode(String phone){
        long count = countLoginCodeByPhone(phone);
        if(count > 6){
            throw new GlobalExceptionResult("user.code.limit.maxCount", Result.EXCEPTION);
        } // 检查是否超出次数
        String prefix = PREFIX_LOGIN_CODE;
        if((!redisService.exists(prefix + phone)) || (redisService.exists(prefix + phone) && redisService.ttl(prefix + phone) < 510)){

            String code = IdGen.generate(); // 生成验证码
            sendLoginCodeSms(phone, code);

            // 记录日志
            UserLogUtils.saveLog(UserLog.LogType.LoginCode, "user.log.sendLoginCodeLog", phone, code);
            redisService.setex(prefix + phone, CACHE_TIME, code);

            // 返回结果
            MessageResult messageResult = new MessageResult();
            messageResult.setMessage("user.code.success");
            messageResult.setCode(Result.NORMAL);
            return messageResult;

        } else {
            Long ttl = redisService.ttl(prefix + phone);
            throw new GlobalExceptionResult("user.code.limit.retryTime", Result.EXCEPTION, ttl-510);
        }
    }


    /**
     * 发送注册验证码
     */
    public Result sendRegisterCode(String phone){

        long count = countRegisterCodeByPhone(phone);
        if(count > 6){
            throw new GlobalExceptionResult("user.code.limit.maxCount", Result.EXCEPTION);
        }
        String prefix = PREFIX_REGISTER_CODE;
        if((!redisService.exists(prefix + phone)) || (redisService.exists(prefix + phone) && redisService.ttl(prefix + phone) < 510)){ // 缓存中不存在验证码

            String code = IdGen.generate(); // 生成验证码
            sendRegisterCodeSms(phone, code);

            // 记录日志
            UserLogUtils.saveLog(UserLog.LogType.RegisterCode, "user.log.sendRegisterCodeLog", phone, code);
            redisService.setex(prefix + phone, CACHE_TIME, code); // 缓存

            MessageResult messageResult = new MessageResult();
            messageResult.setMessage("user.code.success");
            messageResult.setCode(Result.NORMAL);

            return messageResult;

        } else {
            Long ttl = redisService.ttl(prefix + phone);
            throw new GlobalExceptionResult("user.code.limit.retryTime", Result.EXCEPTION, ttl-510);
        }
    }


    /**
     * 发送绑定手机验证码
     */
    public Result sendBindPhoneCode(String phone){

        // 检测该手机号是否绑定有微信
        if(userWeChatDao.findByUserPhone(phone) != null){
            return new MessageResult(Result.ALREADY_BIND_PHONE,"该手机已绑定微信");
        }

        long count = countBindCodeByPhone(phone);
        if(count > 6){
            throw new GlobalExceptionResult("user.code.limit.maxCount", Result.EXCEPTION);
        }

        String prefix = PREFIX_BIND_CODE;
        if((!redisService.exists(prefix + phone)) || (redisService.exists(prefix + phone) && redisService.ttl(prefix + phone) < 510)){ // 缓存中不存在验证码

            String code = IdGen.generate(); // 生成验证码
            sendBindPhoneCodeSms(phone, code);

            // 记录日志
            UserLogUtils.saveLog(UserLog.LogType.BindCode, "user.log.sendBindPhoneCodeLog", phone, code);
            redisService.setex(prefix + phone, CACHE_TIME, code); // 缓存

            return new DataResult<>(Result.NORMAL, "user.code.success", getUser(phone)!=null);

        } else {
            Long ttl = redisService.ttl(prefix + phone);
            throw new GlobalExceptionResult("user.code.limit.retryTime", Result.EXCEPTION, ttl-510);
        }
    }


    /**
     * 发送更新密码验证码
     */
    public Result sendResetPasswordCode(String phone){
        long count = countResetPasswordCodeByPhone(phone);
        if(count > 6){
            throw new GlobalExceptionResult("user.code.limit.maxCount", Result.EXCEPTION);
        }
        String prefix = PREFIX_RESET_CODE;
        if((!redisService.exists(prefix + phone)) || (redisService.exists(prefix + phone) && redisService.ttl(prefix + phone) < 510)){

            String code = IdGen.generate(); // 生成验证码
            sendResetPasswordCodeSms(phone, code);

            // 记录日志
            UserLogUtils.saveLog(UserLog.LogType.ResetPasswordCode, "user.log.sendResetPasswordCodeLog", phone, code);
            redisService.setex(prefix + phone, CACHE_TIME, code);
            // 返回结果
            MessageResult messageResult = new MessageResult();
            messageResult.setMessage("user.code.success");
            messageResult.setCode(Result.NORMAL);
            return messageResult;

        } else {
            Long ttl = redisService.ttl(prefix + phone);
            throw new GlobalExceptionResult("user.code.limit.retryTime", Result.EXCEPTION, ttl-510);
        }

    }

    /**
     * 发送更换手机号验证码
     */
    public Result sendChangePhoneCode(String phone){
        long count = countChangePhoneCodeByPhone(phone);
        if(count > 6){
            throw new GlobalExceptionResult("user.code.limit.maxCount", Result.EXCEPTION);
        }
        String prefix = PREFIX_CHANGE_CODE;
        if((!redisService.exists(prefix + phone)) || (redisService.exists(prefix + phone) && redisService.ttl(prefix + phone) < 510)){

            String code = IdGen.generate(); // 生成验证码
            sendChangePhoneCodeSms(phone, code);

            // 记录日志
            UserLogUtils.saveLog(UserLog.LogType.ChangePhoneCode, "user.log.sendChangePhoneCodeLog", phone, code);
            redisService.setex(prefix + phone, CACHE_TIME, code);
            // 返回结果
            MessageResult messageResult = new MessageResult();
            messageResult.setMessage("user.code.success");
            messageResult.setCode(Result.NORMAL);
            return messageResult;

        } else {
            Long ttl = redisService.ttl(prefix + phone);
            throw new GlobalExceptionResult("user.code.limit.retryTime", Result.EXCEPTION, ttl-510);
        }
    }

    /**
     * 根据手机号查询登录验证码发送次数
     */
    private int countLoginCodeByPhone(String phone){
        return userDao.countLoginCodeByPhone(phone);
    }

    /**
     * 根据手机号查询绑定验证码发送次数
     */
    private int countBindCodeByPhone(String phone){
        return userDao.countBindCodeByPhone(phone);
    }

    /**
     * 根据手机号查询注册验证码发送次数
     */
    private int countRegisterCodeByPhone(String phone){
        return userDao.countRegisterCodeByPhone(phone);
    }

    /**
     * 根据手机号查询更新密码验证码发送次数
     */
    private int countResetPasswordCodeByPhone(String phone){
        return userDao.countResetPasswordCodeByPhone(phone);
    }

    /**
     * 根据手机号查询更换手机号验证码发送次数
     */
    private int countChangePhoneCodeByPhone(String phone){
        return userDao.countChangePhoneCodeByPhone(phone);
    }

    /**
     * 生成并发送登录验证码
     */
    private void sendLoginCodeSms(String phone, String code){
        try {
            smsService.sendLoginCodeSms(phone, code);// 发送
        } catch (Exception e) {
            throw new GlobalExceptionResult("user.code.limit.error", Result.EXCEPTION);
        }
    }

    /**
     * 生成并发送注册验证码
     */
    private void sendRegisterCodeSms(String phone, String code){
        try {
            smsService.sendRegisterCodeSms(phone, code);// 发送
        } catch (Exception e) {
            throw new GlobalExceptionResult("user.code.limit.error", Result.EXCEPTION);
        }
    }

    /**
     * 生成并发送更新密码证码
     */
    private void sendResetPasswordCodeSms(String phone, String code){
        try {
            smsService.sendResetPasswordCodeSms(phone, code);// 发送
        } catch (Exception e) {
            throw new GlobalExceptionResult("user.code.limit.error", Result.EXCEPTION);
        }
    }

    /**
     * 生成并发送绑定手机证码
     */
    private void sendBindPhoneCodeSms(String phone, String code){
        try {
            smsService.sendBindPhoneCodeSms(phone, code);// 发送
        } catch (Exception e) {
            throw new GlobalExceptionResult("user.code.limit.error", Result.EXCEPTION);
        }
    }

    /**
     * 生成并发送更换手机证码
     */
    private void sendChangePhoneCodeSms(String phone, String code) {
        try {
            smsService.sendChangePhoneCodeSms(phone, code);// 发送
        } catch (Exception e) {
            throw new GlobalExceptionResult("user.code.limit.error", Result.EXCEPTION);
        }
    }


    /**
     * 根据手机号和验证码校验缓存信息并记录
     */
    private boolean checkCode(String prefix, String phone, String code){
        String cache = redisService.get(prefix + phone);
        return StringUtils.isNotEmpty(cache) && cache.equals(code);
    }

    /**
     * 手机号和密码登陆
     * @param phone
     * @param password
     * @return
     */
    public Result loginWithPassword(String phone, String password){
        User user = userDao.getUserByPhone(phone);
        if(user == null){
            throw new AuthFailureException(Result.NOT_REGISTER, "user.auth.not.exists");
        }
        DataResult<User> userResult = new DataResult<>();
        String originPassword = user.getPassword();
        String salt = user.getSalt();

        if(checkPassword(originPassword, salt, phone, password)){
            putToken(user);
        } else {
            throw new AuthFailureException(Result.PWD_ERROR, "user.auth.error.password");
        }
//        user.setIntegral(userAccountDao.getByUserId(user.getId()).getIntegral());
        user.setAvatar(ossService.getSimpleUrl(OssPathConfig.getUserAvatarPath(user.getAvatar())));
        userResult.setData(user);
        return userResult;

    }

    /**
     * 检查用户手机号和密码
     * @param userId
     * @param password
     * @return
     */
    public Boolean checkPassword(Integer userId, String password){
        User user = userDao.get(userId);
        if(user == null){
            throw new AuthFailureException(Result.NOT_REGISTER, "user.auth.not.exists");
        }
        return checkPassword(user.getPassword(), user.getSalt(), user.getPhone(), password);
    }


    /**
     * 使用手机号，验证码登陆
     * @param phone
     * @param code
     * @return
     */
    public Result loginWithCode(String phone, String code){
        if(!checkCode(PREFIX_LOGIN_CODE, phone, code)){ // 校验验证码
            throw new AuthFailureException(Result.CODE_ERROR, "user.auth.error.code");
        }
        User user = userDao.getUserByPhone(phone);
        if(user == null){ // 校验用户
            throw new AuthFailureException(Result.NOT_REGISTER, "user.auth.not.exists");
        }
        // 设置返回内容
        DataResult<User> userResult = new DataResult<>();
        putToken(user); // 登录成功配置token
        user.setAvatar(ossService.getSimpleUrl(OssPathConfig.getUserAvatarPath(user.getAvatar())));
//        user.setIntegral(userAccountDao.getByUserId(user.getId()).getIntegral());
        userResult.setData(user);
        return userResult;
    }

    /**
     * 检查密码
     */
    private boolean checkPassword(String originPassword, String salt, String phone, String password){
        String key = encryptPassword(phone, password, salt);
        return originPassword.equals(key);
    }

    /**
     * 按照规则构造密码
     */
    public String encryptPassword(String phone, String password, String salt){
        return CodeUtils.getMD5((phone + password + salt).getBytes());
    }

    /**
     * 刷新令牌
     */
    @Transactional(rollbackFor = Exception.class)
    public UserToken refreshToken(UserToken userToken, String token){

        userToken.setToken(token);
        userToken.setExpiredDate(new Date(System.currentTimeMillis() + THIRTY_DAYS));
        userToken.setCount(userToken.getCount() + 1);
        userToken.setAccessTime(new Date());
        userTokenDao.update(userToken);
        return userToken;

    }

    /**
     * 获取令牌
     *      令牌不存在返回空，
     *      令牌存在刷新令牌返回长令牌，
     *      令牌过期重新生成令牌返回
     */
    public UserToken refreshTokenByUserId(int userId, String swapToken){
        UserToken userToken = userTokenDao.findUserTokenByUserId(userId);
        if(userToken == null || !swapToken.equals(userToken.getToken())){
            throw new AuthFailureException();
        } else {
            Date expiredDate = userToken.getExpiredDate();
            if(expiredDate.getTime() < System.currentTimeMillis()){  // token过期
                throw new AuthFailureException("user.auth.token.expired");
            }
            UserContext userContext = UserContext.create(String.valueOf(userId));
            BaseToken refreshToken = tokenFactory.createRefreshToken(userContext);
            return refreshToken(userToken, refreshToken.getToken());
        }
    }

    /**
     * 获得token
     */
    @Transactional(rollbackFor = Exception.class)
    public void putToken(User user){

        UserContext userContext = UserContext.create(String.valueOf(user.getId()));
        BaseToken accessToken = tokenFactory.createAccessJwtToken(userContext);
        BaseToken refreshToken = tokenFactory.createRefreshToken(userContext);

        user.setAccessToken(accessToken.getToken());
        user.setRefreshToken(refreshToken.getToken());
    }


    /**
     * 保存token
     */
    @Transactional(rollbackFor = SQLException.class)
    public UserToken saveToken(String token, UserContext userContext){
        UserToken userToken = userTokenDao.findUserTokenByUserId(userContext.getUserId());
        if(userToken != null){
            return refreshToken(userToken, token);
        }
        UserToken newToken = new UserToken();
        User user = new User();
        user.setId(userContext.getUserId());
        newToken.setUser(user);
        newToken.setToken(token);
        newToken.setExpiredDate(new Date(System.currentTimeMillis() + THIRTY_DAYS));
        newToken.setCreateTime(new Date());
        newToken.setAccessTime(new Date());
        newToken.setCount(1);
        userTokenDao.insert(newToken);
        return newToken;
    }


    /**
     * 用户注册
     * @param phone
     * @param password
     * @param code
     * @param gender
     * @param profession
     * @return
     */
    @Transactional(rollbackFor = SQLException.class)
    public Result register(String phone, String password, String code, Integer gender, String profession) {
        // 验证码错误，返回
        if(!checkCode(PREFIX_REGISTER_CODE, phone, code)){
            throw new AuthFailureException("user.auth.error.code");
        }
        User user = userDao.getUserByPhone(phone);
        // 用户已经存在，返回
        if(user != null) {
            throw new AuthFailureException(Result.USER_EXISTS, "user.auth.exists");
        }
        // 新增用户，生成随机昵称
        user = new User();
        user.setPhone(phone);
        user.setSalt(RandomStringUtils.randomAlphanumeric(10));
        user.setNickname(messageSource.getMessage("user.nickname.prefix",null, LocaleContextHolder.getLocale()) + phone.substring(phone.length() - 4));
        String encryptPassword = encryptPassword(phone, password, user.getSalt());
        user.setPassword(encryptPassword);
        user.setCreateDate(new Date());
        user.setGender(gender);
        user.setAvatar(gender == 0 ? User.DEFAULT_AVATAR_MAN : User.DEFAULT_AVATAR_WOMAN);
        user.setProfession(profession);
        userDao.insert(user);

        putToken(user); // 注册成功，获取令牌

        // 增加用户账户
//        UserAccount account = new UserAccount();
//        account.setUserId(user.getId());
//        account.setIntegral(0);
//        userAccountDao.insert(account);

        // 增加新用户
        userDao.insertNewUser(user.getId());

//        user.setIntegral(userAccountDao.getByUserId(user.getId()).getIntegral());
        user.setAvatar(ossService.getSimpleUrl(user.getAvatar()));

        return new DataResult<>(user);
    }


    /**
     * 根据手机号获取用户
     * @param phone 手机号
     * @return
     */
    public User getUser(String phone){
        return userDao.getUserByPhone(phone);
    }


    /**
     * 更新用户信息
     * @param user
     * @return
     */
    @Transactional(rollbackFor = SQLException.class)
    public Result updateInfo(User user){
        userDao.update(user);
        return MessageResult.getNormalMessage();
    }


    /**
     * 修改密码
     * @param phone
     * @param password
     * @param code
     * @return
     */
    public Result resetPassword(String phone, String password, String code){
        // 校验验证码
        if(!checkCode(PREFIX_RESET_CODE, phone, code)){
            throw new AuthFailureException(Result.CODE_ERROR, "user.auth.error.code");
        }
        User user = userDao.getUserByPhone(phone);
        if(user == null) {
            throw new AuthFailureException(Result.NOT_REGISTER, "user.phone.notExists");
        }
        String salt = RandomStringUtils.randomAlphanumeric(10);
        String newPassword = encryptPassword(phone, password, salt);
        user.setSalt(salt);
        user.setPassword(newPassword);
        update(user);
        MessageResult messageResult = new MessageResult();
        messageResult.setMessage("user.password.update.success");
        return messageResult;
    }


    /**
     * 上传更新用户头像
     */
    @Transactional(rollbackFor = SQLException.class)
    public Result uploadUserAvatar(String fileName, File targetFile, Integer userId){
        // 根据文件名获取文件路径
        String fileKey = OssPathConfig.getUserAvatarPath(fileName);
        String url = ossService.putFile(fileKey, targetFile);
        userDao.updateAvatar(fileName, userId);
        if(targetFile.delete()){
            if(logger.isDebugEnabled()){
                logger.debug("clear test cache");
            }
        }
        DataResult result = new DataResult();
        result.setData(url);
        return result;
    }

    /**
     * 是否是店铺用户
     * @param id
     * @return
     */
    public Boolean isStoreUser(Integer id, Boolean isSelf){
        User user = userDao.get(id);
        if(user.getStatus() == 1){
            Store store = storeDao.getByUser(user.getId());
            if(store.getStatus() == 0 || isSelf){
                return true;
            }
        }
        return false;
    }


    /**
     * 获取用户信息(整合头像)
     * @param userId
     * @return
     */
    public User getUser(Integer userId){
        User user = userDao.get(userId);
        user.setAvatar(ossService.getSimpleUrl(OssPathConfig.getUserAvatarPath(user.getAvatar())));
        // 查找商户类型
        if(user.getStatus() == 1){
            Store store = storeDao.getByUser(user.getId());
            user.setStoreType(store.getType());
            user.setExpireTime(store.getExpireTime());
        }
        return user;
    }

    /**
     * 绑定手机
     * @param openId
     * @param phone
     * @param code
     * @param password
     * @return
     */
    @Transactional(rollbackFor = SQLException.class)
    public Result bindPhone(String openId, String phone, String code, String password) {
        // 校验验证码
        if(!checkCode(PREFIX_BIND_CODE, phone, code)){
            throw new AuthFailureException("user.auth.error.code");
        }

        User user = getUser(phone);
        if(user == null){
            // 新增用户，生成随机昵称
            user = new User();
            user.setPhone(phone);
            user.setSalt(RandomStringUtils.randomAlphanumeric(10));
            UserWechat userWechat = userWechatService.findInfo(openId);
            user.setNickname(userWechat.getNickName());
            String encryptPassword = encryptPassword(phone, password, user.getSalt());
            user.setPassword(encryptPassword);
            user.setCreateDate(new Date());
            user.setGender(userWechat.getSex()-1);
            user.setAvatar(userWechat.getHeadImg());
            user.setProfession("其他");
            userDao.insert(user);

            // 增加新用户记录
            userDao.insertNewUser(user.getId());
        }

        putToken(user); // 注册成功，获取令牌
        UserWechat userWechat = new UserWechat();
        userWechat.setOpenId(openId);
        userWechat.setUserId(user.getId());
        userWechat.setPhone(phone);
        user.setAvatar(ossService.getSimpleUrl(OssPathConfig.getUserAvatarPath(user.getAvatar())));
        userWechatService.updateWechatUser(userWechat);

        return new DataResult<>(user);
    }

    /**
     * 判断某用户是否是新用户
     * @return
     */
    public boolean isNewUser(Integer userId){
        return userDao.isNewUser(userId) != null;
    }

    /**
     * 获取用户邀请码
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = SQLException.class)
    public String getNewCode(Integer userId) {
        String code = userDao.getUserCode(userId);
        if(StringUtils.isEmpty(code)){
            code = RandomUtils.getRandomCode(6);
            userDao.insertUserCode(userId, code);
        }
        return code;
    }

    /**
     * 获取积分
     * @param userId
     * @return
     */
    public Integer getIntegral(Integer userId) {
        return userDao.getUserIntegral(userId);
    }

    /**
     * 获取邀请的人数
     * @param userId
     * @return
     */
    public Integer getInvitatoryNum(Integer userId) {

        return userDao.countInvitatoryNum(userId);
    }

    /**
     * 输入邀请码
     * @param userId
     */
    @Transactional(rollbackFor = SQLException.class)
    public void inputCode(Integer userId, String code) {
        // 根据验证码查询用户
        Integer oldUserId = userDao.getInviteUser(userId, code);
        if(oldUserId == null){
            throw new GlobalExceptionResult("验证码无效", 1002);
        }
        // 双方各集一分
        userDao.updateIntegral(userId);
        userDao.updateIntegral(oldUserId);
        userDao.insertCodeLog(oldUserId, userId);

        // 删除新用户记录
        // 增加新用户
        userDao.deleteNewUser(userId);
    }


    /**
     * 变更绑定的手机
     * @param phone
     * @param code
     * @return
     */
    @Transactional(rollbackFor = SQLException.class)
    public Result changePhone(String phone, String code, Integer userId, String password) {
        // 校验验证码
        if(!checkCode(PREFIX_CHANGE_CODE, phone, code)){
            throw new AuthFailureException("user.auth.error.code");
        }

        if(!checkPassword(userId, password)){
            throw new AuthFailureException("user.auth.error.password");
        }

        User user = userDao.get(userId);
        User newUser = userDao.getUserByPhone(phone);
        if(newUser != null){
            // 删掉该账户下的信息
            deleteUser(newUser);
        }

        // 更新用户信息
        user.setPhone(phone);
        user.setPassword(encryptPassword(user.getPhone(), password, user.getSalt()));
        userDao.update(user);

        return new MessageResult();
    }


    /**
     * 注销账户
     * @param user
     */
    public void deleteUser(User user){
        user.setDeleted(true);
        userDao.update(user);
        // 删除微信绑定
        userWeChatDao.deleteWechat(user.getId());
        // 删除商户
        storeDao.delete(user.getId());
        // 删除认证信息
        personIdentificationDao.delete(user.getId());
        companyIdentificationDao.delete(user.getId());
        // 删除场景
        sceneInfoDao.deleteScene(user.getId());
        // 删除图集
        albumDao.deletedUserAlbum(user.getId());
    }


    /**
     * 兑换邀请码积分奖品
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = SQLException.class)
    public Result exchangePrize(Integer userId) {
        Integer integral = getIntegral(userId);
        if(integral < 5){
            return new GlobalExceptionResult("积分不够", 1002);
        }
        userDao.deleteIntegral(userId);
        userDao.insertPrize(userId);

        return new MessageResult();
    }


}
