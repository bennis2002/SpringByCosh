package com.cosh;

import com.cosh.service.UserService;
import com.spring.AppConfig;
import com.spring.CoshApplicationContext;

public class Main {
    public static void main(String[] args) {
        CoshApplicationContext applicationContext = new CoshApplicationContext(AppConfig.class);

        UserService userService = (UserService) applicationContext.getBean("userService");
        userService.Test();
    }
}