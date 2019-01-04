import org.junit.Test;

/**
 * Description:
 * Author: sukai
 * Date: 2017-08-16
 */
public class JUnitTest {

    @Test
    public void test(){
        EmailUtil.sendEmail("490763125@qq.com", "邮件自动化测试", "你好，邮件测试，请忽略");
    }
}

