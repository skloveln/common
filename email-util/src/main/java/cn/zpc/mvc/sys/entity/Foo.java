package cn.zpc.mvc.sys.entity;

/**
 * 消费者
 * @author zhijun
 *
 */
public class Foo {

    //具体执行业务的方法
    public void listen(String foo) {
        System.out.println("****************************************   消费者： " + foo + "      *******************************************");
    }
}