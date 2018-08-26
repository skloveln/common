package cn.zpc.mvc.user.listener;

import cn.zpc.common.config.Global;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;

/**
 * Description:容器初始化监听器
 * Author: sukai
 * Date: 2017-08-14
 */
public class WebContextListener extends ContextLoaderListener {


    @Override
    public WebApplicationContext initWebApplicationContext(ServletContext servletContext) {

        printLoadMessage();

        return super.initWebApplicationContext(servletContext);
    }

    private void printLoadMessage(){
        String sb = "\r\n===============================================\r\n" +
                "\r\n  "  + Global.getConfig("projectName") + "@Copyright " + Global.getConfig("copyrightYear") + " Loading...\r\n" +
                "\r\n===============================================\r\n";
        System.out.println(sb);
    }

}
