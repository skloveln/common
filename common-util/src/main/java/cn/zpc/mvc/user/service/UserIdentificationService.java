package cn.zpc.mvc.user.service;

import cn.zpc.common.handler.exception.GlobalExceptionResult;
import cn.zpc.common.serivce.BaseService;
import cn.zpc.common.utils.PropertiesLoader;
import cn.zpc.common.web.result.MessageResult;
import cn.zpc.common.web.result.Result;
import cn.zpc.mvc.user.dao.CompanyIdentificationDao;
import cn.zpc.mvc.user.dao.PersonIdentificationDao;
import cn.zpc.mvc.user.dao.UserDao;
import cn.zpc.mvc.user.entity.CompanyIdentification;
import cn.zpc.mvc.user.entity.PersonIdentification;
import cn.zpc.mvc.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserIdentificationService extends BaseService{

    private final static PropertiesLoader loader = new PropertiesLoader("application.properties");
    private final static String env = loader.getProperty("env");


    @Autowired
    private PersonIdentificationDao personIdentificationDao;
    @Autowired
    private CompanyIdentificationDao companyIdentificationDao;
    @Autowired
    private UserDao userDao;


    /**
     * 检查某用户认证过没有
     * @param userId
     * @return
     */
    private Boolean checkExist(Integer userId){
        if(personIdentificationDao.getByUserId(userId) != null){
            return true;
        }
        if(companyIdentificationDao.getByUserId(userId) != null) {
            return true;
        }
        return false;
    }


    /**
     * 个人认证提交
     * @param person
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Result personCommit(PersonIdentification person, Integer userId){

        // 看之前认证过没有
        if(checkExist(userId)){
           throw new GlobalExceptionResult("user.identification.already",Result.EXCEPTION);
        }else {
            person.setCreateTime(new Date());
            person.setStatus(0); // 待审核
            person.setUserId(userId);
            if(personIdentificationDao.insert(person) > 0){
                User user  = userDao.get(userId);
                // 发短信
                smsService.sendIdentificationSms(user.getPhone(), "智景用户：" + user.getNickname());
                user.setStatus(2);
                // 更改用户状态
                userDao.update(user);

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO 500 错误需要发邮件通知管理员
                        emailService.sendEmail("bluetingsky@163.com", env + "有一个个人商户待审核", "个人商户待审核");
                        emailService.sendEmail("sukai@locationbox.cn", env + "有一个个人商户待审核", "个人商户待审核");
                    }
                });
                t.start();

                return new MessageResult();
            }
        }
        return MessageResult.getExceptionMessage("认证信息提交失败");
    }


    /**
     * 企业认证提交
     * @param company
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Result companyCommit(CompanyIdentification company, Integer userId){

        // 看之前认证过企业没有
        if(companyIdentificationDao.getByUserId(userId) != null){
            throw new GlobalExceptionResult("user.identification.already", Result.EXCEPTION);
        }else {
            company.setStatus(0); // 待审核
            company.setUserId(userId);
            company.setCreateTime(new Date());
            if(companyIdentificationDao.insert(company) > 0){
                User user  = userDao.get(userId);
                // 更改用户状态，如果之前是个人商户用户，不更新状态
                if(user.getStatus() == 0){
                    user.setStatus(2);
                    userDao.update(user);
                }
                // 发短信
                smsService.sendIdentificationSms(user.getPhone(), "智景用户：" + user.getNickname());

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO 500 错误需要发邮件通知管理员
                        emailService.sendEmail("lihaoyi@locationmore.com", "有一个企业商户待审核", "企业商户待审核");
                        emailService.sendEmail("sukai@locationbox.cn", "有一个企业商户待审核", "企业商户待审核");
                    }
                });
                t.start();

                return new MessageResult();
            }
        }
        return MessageResult.getExceptionMessage("认证信息提交失败");
    }


}
