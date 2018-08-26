import com.alibaba.fastjson.JSON;
import com.github.sky.serializer.ISerializer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Description:
 * Author: sukai
 * Date: 2017-08-16
 */
public class JUnitTest {

    public static class User{

        String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Test
    public void testJson(){
        User user = new User();
        user.setName("李四");
        String str = JSON.toJSONString(user);
        System.out.println(str);
        User newUser = JSON.parseObject(str, User.class);
        Assert.assertEquals(user.getName(),newUser.getName());
    }

    @Test
    public void testJsonSerializer() {
        String str = "测试对象";
        ISerializer serializer = JsonSerializer.jsonSerializer;
        String newStr = (String) serializer.valueFromBytes(serializer.valueToBytes(str));
        Assert.assertEquals(str, newStr);

        User user = new User();
        user.setName("zhang三");
        Object newUser = serializer.valueFromBytes(serializer.valueToBytes(user));
        User newUser1 = (User) newUser;
                Assert.assertEquals(user, newUser1);
    }
}

