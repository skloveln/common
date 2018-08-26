import cn.zpc.common.plugins.email.EmailService;
import cn.zpc.common.plugins.mq.RabbitMqService;
import cn.zpc.common.plugins.oss.OssPathConfig;
import cn.zpc.common.plugins.oss.OssService;
import cn.zpc.common.redis.Cache;
import cn.zpc.common.redis.Redis;
import cn.zpc.mvc.scene.dao.SceneExtraDao;
import cn.zpc.mvc.scene.dao.SceneNewsDao;
import cn.zpc.mvc.scene.entity.SceneInfo;
import cn.zpc.mvc.scene.entity.SceneServ;
import cn.zpc.mvc.scene.service.SceneService;
import cn.zpc.mvc.store.dao.StoreAdvertDao;
import cn.zpc.mvc.store.dao.StoreDao;
import cn.zpc.mvc.store.dao.StoreServicesDescDao;
import cn.zpc.mvc.store.entity.Store;
import cn.zpc.mvc.store.entity.StoreServicesDesc;
import cn.zpc.mvc.store.service.StoreManageService;
import cn.zpc.mvc.store.service.StoreService;
import cn.zpc.mvc.sys.service.NotificationService;
import cn.zpc.mvc.user.service.UserService;
import cn.zpc.mvc.user.utils.UserUtils;
import com.aliyun.oss.OSSClient;
import com.github.pagehelper.PageInfo;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import org.junit.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import java.io.File;
import java.util.*;

/**
 * Description:
 * Author: sukai
 * Date: 2017-08-15
 */
public class MethodTest extends JUnitTest{

    @Autowired
    private EmailService emailService;
    @Autowired
    private UserService userService;
    @Autowired
    private OssService ossService;
    @Autowired
    private SceneService sceneService;
    @Autowired
    private ReloadableResourceBundleMessageSource messageSource;
    @Autowired
    private RabbitMqService rabbitMqService;
    @Autowired
    private SceneExtraDao sceneExtraDao;
    @Autowired
    private SceneNewsDao sceneNewsDao;
    @Autowired
    private StoreDao storeDao;
    @Autowired
    private StoreService storeService;
    @Autowired
    private StoreServicesDescDao storeServicesDescDao;
    @Autowired
    private StoreAdvertDao storeAdvertDao;
    @Autowired
    private NotificationService notificationService;
    @Autowired


    @Test
    public void testCache(){
        Cache use = Redis.use();
        System.out.println("accessToken:   token_10001000   " + use.get("token_10001000"));
        System.out.println("accessToken:   token_10001001   " + use.get("token_10001001"));
    }

    @Test
    public void testRabbitmq(){

        AbstractApplicationContext ctx = new ClassPathXmlApplicationContext(
                "classpath:spring/spring-context-rabbitmq.xml");
        //RabbitMQ模板
        RabbitTemplate template = ctx.getBean(RabbitTemplate.class);
        //发送消息
        template.convertAndSend("Hello, world!");
        try {
            Thread.sleep(1000);// 休眠1秒
        }catch (Exception e){

        }
        ctx.destroy(); //容器销毁
    }

    @Test
    public void testSend() throws Exception{
        String QUEUE_NAME = "test_queue";
        // 获取到连接以及mq通道
        Connection connection = rabbitMqService.getConnection();
        // 从连接中创建通道
        Channel channel = connection.createChannel();
        // 声明（创建）队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        // 消息内容
        String message = "Hello World!";
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");
        //关闭通道和连接
        channel.close();
        connection.close();
    }

    @Test
    public void testReceive() throws Exception{
        String QUEUE_NAME = "test_queue";
        // 获取到连接以及mq通道
        Connection connection = rabbitMqService.getConnection();
        Channel channel = connection.createChannel();
        // 声明队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        // 定义队列的消费者
        QueueingConsumer consumer = new QueueingConsumer(channel);
        // 监听队列
        channel.basicConsume(QUEUE_NAME, true, consumer);
        // 获取消息
        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String message = new String(delivery.getBody());
            System.out.println(" [x] Received '" + message + "'");
        }
    }

    @Test
    public void addStoreService(){
        // 获取全部店铺列表
        List<Store> list = storeDao.getList();
        // 获取全部场景
        for(Store store : list){
            List<SceneInfo> sceneInfos = storeService.getSceneInfo(store.getId(), null, 0, 0);
            Set<Integer> set = new HashSet<>();
            for(SceneInfo info : sceneInfos){
                List<SceneServ> ser = sceneService.getSceneServiceIcon(info.getId());
                for(SceneServ s : ser){
                    set.add(s.getTypeId());
                }
            }
            StoreServicesDesc desc = new StoreServicesDesc();
            desc.setStoreId(store.getId());
            String g = "";
            for(Integer a : set){
                g += a + " ";
            }
            desc.setGeneral(g);
            if(storeServicesDescDao.getServices(store.getId()) == null){
                storeServicesDescDao.insert(desc);
            }else {
                storeServicesDescDao.update(desc);
            }
        }
    }

    @Autowired
    private StoreManageService storeManageService;

    @Test
    public void addTitbits(){
        String path = "F:\\\\花絮\\04片场";
        File fileDir = new File(path);
        String files = "";
        for(File file : fileDir.listFiles()){
            String fileName = UserUtils.generateToken(path);  // 文件名
            if (file.getName().contains("")) { // 加文件后缀
                String fileExt = file.getName().substring(file.getName().lastIndexOf(""));
                fileName += fileExt;
            }
            file.renameTo(new File(path+"/"+fileName));
            File newFile = new File(path+"/"+fileName);
            ossService.putFile(OssPathConfig.getStoreTitbitsPath(fileName), newFile);
            files += " " + fileName;
        }
        storeManageService.addTitbits(10035, "04片场", new Date(), files);
    }

    @Test
    public void changePassword(){
        String p = userService.encryptPassword("57489521211", "zpc123456", "e3kICbdoCD");
        System.out.println(p);
    }

    @Test
    public void asd(){
         PageInfo<SceneInfo> pageInfo=   sceneService.getHotScenes(10001001,0,Integer.MAX_VALUE);
        System.out.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
        System.out.println(  pageInfo.getList().toString());

    }
}
