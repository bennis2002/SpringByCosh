package com.cosh.other;

import com.cosh.service.UserService;
import com.spring.BeanPostProcessor;
import com.spring.Component;

/*
在processor类中提供了在初始化类时的方法，能在对类进行初始化时提供辅助代码
注意这个类要被spring扫描
 */
@Component()
public class CoshBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        System.out.println("初始化前");
        if (beanName.equals("userService")) {
            ((UserService) bean).setBeanName("test!");
        }
        //
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("初始化后");
        return bean;
    }
}
