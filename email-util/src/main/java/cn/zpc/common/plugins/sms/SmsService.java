package cn.zpc.common.plugins.sms;

import cn.zpc.common.plugins.email.EmailService;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * Description:短信服务
 * Author: sukai
 * Date: 2017-08-14
 */
@Service
public class SmsService {

    @Autowired
    private EmailService emailService;

    private final static String product = "Dysmsapi";
    private final static String domain = "dysmsapi.aliyuncs.com";
    private final static String accessKeyId = "LTAIxG0rYOqJnzJO";
    private final static String accessKeySecret = "UAGEp2M2cJNhAZWBR0kdPMCqlpATh6";
    private final static String registerTemplate = "SMS_122292626";
    private final static String loginTemplate = "SMS_122282654";
    private final static String resetpwdTemplate = "SMS_122282657";
    private final static String bindphoneTemplate = "SMS_127158163";
    private final static String changephoneTemplate = "SMS_129742880";
    private final static String identifyCommitTemplate = "SMS_117521541";

    private static IAcsClient acsClient;

    public SmsService() throws ClientException{
        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        this.acsClient = new DefaultAcsClient(DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret));
    }

    /**
     * 发送验证码通用方法
     * @param phone 电话
     * @param code 验证码
     * @param type 类型
     * @throws ClientException
     */
    private void sendSms(String phone, String code, String type) throws ClientException{
        //组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        //必填:待发送手机号
        request.setPhoneNumbers(phone);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName("智景");
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(type);
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        request.setTemplateParam("{\"code\":\"" + code  + "\"}");

        acsClient.getAcsResponse(request);
    }


    /**
     * 发送登录码
     * @param phone
     * @param code
     * @throws Exception
     */
    public void sendLoginCodeSms(String phone, String code) throws Exception{
        sendSms(phone, code, loginTemplate);
    }


    /**
     * 发送注册码
     * @param phone
     * @param code
     * @throws Exception
     */
    public void sendRegisterCodeSms(String phone, String code) throws Exception {
        sendSms(phone, code, registerTemplate);
    }

    /**
     * 修改密码验证码
     * @param phone
     * @param code
     * @throws Exception
     */
    public void sendResetPasswordCodeSms(String phone, String code) throws Exception{
        sendSms(phone, code, resetpwdTemplate);
    }

    /**
     * 绑定手机验证码
     * @param phone
     * @param code
     * @throws Exception
     */
    public void sendBindPhoneCodeSms(String phone, String code) throws Exception{
        sendSms(phone, code, bindphoneTemplate);
    }

    /**
     * 绑定手机验证码
     * @param phone
     * @param code
     * @throws Exception
     */
    public void sendChangePhoneCodeSms(String phone, String code) throws Exception{
        sendSms(phone, code, changephoneTemplate);
    }

    /**
     * 发送认证提交短信
     * @param phone
     * @param name
     * @throws Exception
     */
    public void sendIdentificationSms(String phone, String name){
        //组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        //必填:待发送手机号
        request.setPhoneNumbers(phone);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName("智景");
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(identifyCommitTemplate);
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        request.setTemplateParam("{\"name\":\"" + name  + "\"}");

        try {
            acsClient.getAcsResponse(request);
        }catch (Exception e){
            //todo: 发邮件通知异常信息
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            emailService.sendException("短信发送失败", sw.toString());
        }
    }

}
