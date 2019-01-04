import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;

public class EmailUtil {

    private static String sendEmail;
    private static String emailPassword;
    private static Properties properties = new Properties();

    static {
        ClassLoader loader = EmailUtil.class.getClassLoader();
        try {
            properties.load(loader.getResourceAsStream("email.properties"));
        } catch (IOException e) {
            System.out.println("加载邮件配置文件异常");
            e.printStackTrace();
        }
        sendEmail = properties.getProperty("send.email");
        emailPassword = properties.getProperty("send.email.password");
    }

    /**
     * 发送邮件通知
     * @param email 收件人邮箱
     * @param title 邮件标题
     * @param body  邮件内容
     * @throws Exception
     */
    public static void sendEmail(String email, String title, String body){
        try{
            // 配置邮件参数
            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.put("mail.smtp.auth", true);
            Session mailConnection = Session.getInstance(properties, null);
            Message msg = new MimeMessage(mailConnection);
            // 设置邮件发送人和接收人
            InternetAddress send = new InternetAddress(sendEmail);
            InternetAddress receiver = new InternetAddress(email);
            msg.setFrom(send);
            msg.addRecipient(MimeMessage.RecipientType.TO, receiver);
            //设置邮件的主题
            msg.setSubject(title);
            //设置邮件的内容
            msg.setText(body);
            Transport tr = mailConnection.getTransport();
            tr.connect(sendEmail, emailPassword); //填写发件人邮箱地址和密码
            tr.sendMessage(msg, msg.getAllRecipients());
            tr.close();//发送成功后关闭链接
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
