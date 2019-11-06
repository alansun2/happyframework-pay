import bean.TestProxy;
import com.alan344happyframework.core.proxy.ValidationCglibProxy;
import org.junit.jupiter.api.Test;

/**
 * @author AlanSun
 * @date 2019/11/6 14:59
 */
public class CglibTest {

    static TestProxy user;

    @Test
    public static void main(String[] args) {
        ValidationCglibProxy cglib = new ValidationCglibProxy();//实例化CglibProxy对象
        user = (TestProxy) cglib.getCglibProxy(new TestProxy());//获取代理对象
        user.getA("A");
        user.getA("B");
        user.getA("C");
    }
}
