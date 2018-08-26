import cn.zpc.mvc.scene.entity.SceneInfo;
import cn.zpc.mvc.sys.entity.Notification;
import cn.zpc.mvc.sys.service.NotificationService;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Description:
 * Author: sukai
 * Date: 2017-08-16
 */
@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试
@WebAppConfiguration
@ContextConfiguration(
        locations = {
                "classpath*:spring/spring-*.xml"
        }
)
//@Transactional
public class JUnitTest
//        extends AbstractTransactionalJUnit4SpringContextTests
{


        public static void main(String[] args) {
                List<Integer> list = new ArrayList<>();
                list.add(1);
                list.add(2);
               System.out.print(Collections.max(list));

        }
}

