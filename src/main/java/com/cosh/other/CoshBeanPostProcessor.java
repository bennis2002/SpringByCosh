package com.cosh.other;

import com.cosh.service.Impl.UserServiceImpl;
import com.spring.BeanPostProcessor;
import com.spring.Component;

/*
在processor类中提供了在初始化类时的方法，能在对类进行初始化时提供辅助代码
注意这个类要被spring扫描
 */
@Component
public class CoshBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        System.out.println("初始化       " + beanName);
        if (beanName.equals("userService")) {
            System.out.println("加载UserService");
            ((UserServiceImpl) bean).setBeanName("test!");
        }
        //
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("初始化完毕   " + beanName);
        return bean;
    }
}
