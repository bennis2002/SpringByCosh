package com.cosh;

import com.spring.AppConfig;
import com.spring.CoshApplicationContext;

public class Main {
    public static void main(String[] args) {
        CoshApplicationContext applicationContext = new CoshApplicationContext(AppConfig.class);
    }
}