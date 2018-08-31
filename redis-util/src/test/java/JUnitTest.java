import com.github.sky.RedisUtil;
import org.junit.Test;


/**
 * Description:
 * Author: sukai
 * Date: 2017-08-16
 */
public class JUnitTest {

    @Test
    public void testSetValue(){
        RedisUtil.set("a", 1);
        int a = RedisUtil.get("a");
    }
}

