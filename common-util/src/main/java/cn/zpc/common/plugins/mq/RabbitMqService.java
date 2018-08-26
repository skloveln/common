package cn.zpc.common.plugins.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMqService {

    private static Connection connection;
    private RabbitTemplate template;

    public RabbitMqService(@Value("${mq.host}") String host, @Value("${mq.userName}") String userName, @Value("${mq.password}") String password){
        try{
            //定义连接工厂
            ConnectionFactory factory = new ConnectionFactory();
            //设置服务地址
            factory.setHost(host);
            //端口
            factory.setPort(5672);
            //设置账号信息，用户名、密码、vhost
            factory.setVirtualHost("/");
            factory.setUsername(userName);
            factory.setPassword(password);
            // 通过工程获取连接
            this.connection = factory.newConnection();
        }catch (Exception e){

        }
    }

    public Connection getConnection() {
        return connection;
    }

    /**
     * 发送消息
     * @param queueName 队列名
     * @param message 消息体
     * @throws Exception
     */
    public static void sendMessage(String queueName, String message) throws Exception{
        // 从连接中创建通道
        Channel channel = connection.createChannel();
        // 声明（创建）队列
        channel.queueDeclare(queueName, false, false, false, null);
        // 消息内容
        channel.basicPublish("", queueName, null, message.getBytes());
        //关闭通道和连接
        channel.close();
    }


}
