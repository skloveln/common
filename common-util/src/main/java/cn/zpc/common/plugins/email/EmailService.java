package cn.zpc.common.plugins.email;

import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailService{

    private final static String sendEmail = "13717649591@163.com";
    private final static String emailPassword = "ZJ328**";


    /**
     * 发送异常通知
     * @param title
     * @param body
     * @throws Exception
     */
    public void sendException(String title, String body){
        Properties pro = new Properties();
        pro.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        pro.put("mail.smtp.host", "smtp.163.com");  //smtp.163.com 是你用到的邮件服务器
        pro.setProperty("mail.transport.protocol", "smtp");
        pro.put("mail.smtp.auth", true);
        pro.put("mail.smtp.port", "465");
        pro.put("mail.smtp.socketFactory.port", "465");  //Linux下需要设置此项，Windows默认localhost为127.0.0.1
        pro.put("mail.smtp.localhost", "127.0.0.1");
        Session mailConnection = Session.getInstance(pro, null);
        try {
            InternetAddress send = new InternetAddress(sendEmail);
            InternetAddress receiver = new InternetAddress("sukai@locationbox.cn");
            InternetAddress receiver2 = new InternetAddress("wangpengju@locationmore.com");

            Message msg = new MimeMessage(mailConnection);
            msg.setFrom(send);
            msg.addRecipient(MimeMessage.RecipientType.TO, receiver);
            msg.setSubject(title);//设置邮件的主题
            msg.setText(body);//设置邮件的内容

            Message msg2 = new MimeMessage(mailConnection);
            msg2.setFrom(send);
            msg2.addRecipient(MimeMessage.RecipientType.TO, receiver2);
            msg2.setSubject(title);//设置邮件的主题
            msg2.setText(body);//设置邮件的内容

            Transport tr = mailConnection.getTransport();
            tr.connect(sendEmail, emailPassword);//填写你的邮箱地址和密码
            tr.sendMessage(msg, msg.getAllRecipients());
            tr.sendMessage(msg2, msg2.getAllRecipients());
            tr.close();//发送成功后关闭链接
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 发送邮件通知
     * @param email 邮件接受者
     * @param title
     * @param body
     * @throws Exception
     */
    public void sendEmail(String email, String title, String body){
        try{
            Properties pro = new Properties();
            pro.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            pro.put("mail.smtp.host", "smtp.163.com");  //smtp.163.com 是你用到的邮件服务器
            pro.setProperty("mail.transport.protocol", "smtp");
            pro.put("mail.smtp.auth", true);
            pro.put("mail.smtp.port", "465");
            pro.put("mail.smtp.socketFactory.port", "465");  //Linux下需要设置此项，Windows默认localhost为127.0.0.1
            pro.put("mail.smtp.localhost", "127.0.0.1");
            Session mailConnection = Session.getInstance(pro, null);
            Message msg = new MimeMessage(mailConnection);
            InternetAddress send = new InternetAddress(sendEmail);
            InternetAddress receiver = new InternetAddress(email);
            msg.setFrom(send);
            msg.addRecipient(MimeMessage.RecipientType.TO, receiver);
            msg.setSubject(title);//设置邮件的主题
            msg.setText(body);//设置邮件的内容
            Transport tr = mailConnection.getTransport();
            tr.connect(sendEmail, emailPassword);//填写你的邮箱地址和密码
            tr.sendMessage(msg, msg.getAllRecipients());
            tr.close();//发送成功后关闭链接
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
