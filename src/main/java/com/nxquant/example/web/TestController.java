package com.nxquant.example.web;

import com.nxquant.example.entity.UserBean;
import com.nxquant.example.service.UserService;
import io.micrometer.core.instrument.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class TestController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserService userService;


    @GetMapping(value = "/test")
    public String getHistoryKline(@RequestParam(value = "symbol") String symbol,
                                  HttpServletRequest request) {
        if(!StringUtils.isEmpty(symbol)){
            return symbol;
        }
        return "error";
    }

    /**
     * 注册控制方法
     * @param user 用户对象
     * @return
     */
    @RequestMapping(value = "/register")
    public String register(UserBean user) {
        //调用注册业务逻辑
        userService.register(user);
        return "注册成功.";
    }
}
