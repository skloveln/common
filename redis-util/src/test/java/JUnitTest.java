import com.alibaba.fastjson.JSON;
import com.github.sky.Redis;
import com.github.sky.RedisUtil;
import com.github.sky.serializer.ISerializer;
import org.junit.Assert;
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
        System.out.println(a);
    }
}

