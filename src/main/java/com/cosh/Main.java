package com.cosh;

import com.cosh.other.CoshBeanPostProcessor;
import com.cosh.service.Impl.UserServiceImpl;
import com.cosh.service.UserService;
import com.spring.AppConfig;
import com.spring.CoshApplicationContext;

public class Main {
    public static void main(String[] args) {
        CoshApplicationContext applicationContext = new CoshApplicationContext(AppConfig.class);

        UserService userService = (UserService) applicationContext.getBean("userService");
        CoshBeanPostProcessor coshBeanPostProcessor = (CoshBeanPostProcessor) applicationContext.getBean("coshBeanPostProcessor");

        /*
        AOP方法演示：
        在test前要执行相关逻辑,执行顺序
        1.代理逻辑（Process）
        2. 业务逻辑（Test）
         */
        userService.AopTest("test!");
    }
}