package cn.zpc.common.web.controller;

import cn.zpc.common.web.result.MessageResult;
import cn.zpc.common.web.result.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理控制器
 * Author: sukai
 * Date: 2017/8/4.
 */
@Controller
public class ExceptionController {

    /**
     * 请求异常
     */
    @RequestMapping(value = "/404")
    @ResponseBody
    public Result error404() throws Exception {

        MessageResult messageResult = new MessageResult();
        messageResult.setMessage("not found");
        messageResult.setCode(Result.EXCEPTION);
        return messageResult;
    }

    /**
     * 服务器异常
     */
    @RequestMapping(value ="/500")
    @ResponseBody
    public Result error500() {
        MessageResult messageResult = new MessageResult();
        messageResult.setMessage("internal error");
        messageResult.setCode(Result.EXCEPTION);
        return messageResult;
    }

}
