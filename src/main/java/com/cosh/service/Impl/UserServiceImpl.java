package com.cosh.service.Impl;


import com.cosh.service.UserService;
import com.spring.*;

@Component()
@Scope()
public class UserServiceImpl implements UserService {

    /*
    关于Autowired注入，在创建UserServiceBean的过程中会遍历UserService的所有出现的
     */
    @Autowired
    OrderServiceImpl orderService;

    String beanName;

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void Test()
    {
        System.out.println(beanName);
    }

    @Override
    public void AopTest(String ans) {
        System.out.println("执行业务逻辑：" + ans);
    }

}
