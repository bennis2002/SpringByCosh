package com.cosh.service;


import com.spring.*;

@Component("userService")
@Scope()
public class UserService implements InitializingBean {

    /*
    关于Autowired注入，在创建UserServiceBean的过程中会遍历UserService的所有出现的
     */
    @Autowired
    OrderService orderService;

    String beanName;

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public void Test()
    {
        System.out.println(beanName);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println(("初始化"));
    }
}
