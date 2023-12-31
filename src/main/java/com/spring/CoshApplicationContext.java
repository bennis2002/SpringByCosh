package com.spring;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CoshApplicationContext {

    private Class configClass;
    private ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();    // 单例池，存的是单例对象
    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    private List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();      //bean后置处理列表

    public CoshApplicationContext(Class configClass) {

        this.configClass = configClass;

        //解析配置类

        //解析ComponentScan注解 --> 扫描路径 --> 扫描
        scan(configClass);

        for (Map.Entry<String , BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition.getScope().equals("singleton")) {
                Object bean = createBean(beanName , beanDefinition);
                singletonObjects.put(beanName, bean);           //存进单例池中
            }
        }

    }

    /*
    由BeanDefinition创建出一个新的对象
    beanDefinition中存的是
    1. bean的类型  [单例bean /  多例bean]
    2. 类 [.class]
     */
    public Object createBean(String beanName , BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getClazz();
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();

            // 依赖注入
            for(Field field : clazz.getDeclaredFields()) {      //获取所有public的字段
                if (field.isAnnotationPresent(Autowired.class)) {
                    //依赖注入
                    Object bean = getBean(field.getName());
                    //判断该bean是否存在
                    if (bean == null) {
                        throw new NullPointerException();
                    }
                    field.setAccessible(true);
                    field.set(instance, bean);
                }
            }

            // 回调Bean（Aware回调）
            if (instance instanceof BeanNameAware) {
                ((BeanNameAware) instance).setBeanName(beanName);
            }

            //遍历后置处理器，采用初始化前的方法
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessBeforeInitialization(instance, beanName);
            }

            //初始化Bean
            if (instance instanceof InitializingBean) {
                ((InitializingBean) instance).afterPropertiesSet();
            }

            //遍历后置处理器，采用初始化后的方法
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessAfterInitialization(instance, beanName);
            }
            //BeanPostProcessor --> Bean的后置处理器
            //在建立Bean的过程中，执行语句

            return instance;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void scan(Class configClass) {
        ComponentScan componentScanAnnotation = (ComponentScan) configClass.getDeclaredAnnotation(ComponentScan.class);
        String path = componentScanAnnotation.value();     //扫描路径
        path = path.replace(".", "/");


        //扫描(类加载器）
        // Bootstrap --> jre/lib
        // Ext --> jre/ext/lib
        // App --> classpath
        ClassLoader classLoader = CoshApplicationContext.class.getClassLoader();    //app类加载器
        URL resource = classLoader.getResource(path);         //得到一个目录
        File file = new File(resource.getFile());
        if (file.isDirectory()) {
            ScanDir(file,classLoader);
        }
    }

    public Object getBean(String beanName) {
//        接收到了一个bean之后判断是否存在beanName(找Map)
        if (beanDefinitionMap.containsKey(beanName)) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition.getScope().equals("singleton")) {
                //如果是单例bean的话就从单例池中提取出bean对象
                Object o = singletonObjects.get(beanName);
                return o;
            } else {
                return createBean(beanName , beanDefinition); //多类bean，要创建出一个新的bean对象
            }
        }else{
            //不存在该bean
            throw new NullPointerException();
        }
    }


    void ScanDir(File file , ClassLoader classLoader) {
        //得到文件
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.isFile()) {
                ScanFile(f,classLoader);
            } else if (f.isDirectory()) {
                ScanDir(f , classLoader);
            }
        }
    }

    void ScanFile(File f , ClassLoader classLoader) {
        String fileName = f.getAbsolutePath();
        if (fileName.endsWith(".class")) {
            String className = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class"));
            className = className.replace("\\", ".");
            try {

                //类加载器
                Class<?> aClass = classLoader.loadClass(className);
                if (aClass.isAnnotationPresent(Component.class)) {      //判断是否存在Component注解
                    //存在Component类
                    //解析类 --> BeanDefinion

                    if (BeanPostProcessor.class.isAssignableFrom(aClass)) {
                        //注意强制转化
                        BeanPostProcessor instance = (BeanPostProcessor)aClass.getDeclaredConstructor().newInstance();
                        beanPostProcessorList.add(instance);
                    }

                    //判断是否当前原型bean 是单例bean，还是prototype（多例bean\原型bean）的bean（解析类）
                    Component declaredAnnotation = aClass.getDeclaredAnnotation(Component.class);

                    String beanName = declaredAnnotation.value();
                    if (beanName.equals("")) {
                        beanName = f.getName().toLowerCase().substring(0,1) + f.getName().substring(1);
                        beanName = beanName.replace(".class", "").replace("Impl","");
                    }
                    //生成一个BeanDefinition对象
                    BeanDefinition beanDefinition = new BeanDefinition();

                    if (aClass.isAnnotationPresent(Scope.class)) {
                        Scope ScopeAnnotation = aClass.getDeclaredAnnotation(Scope.class);
                        beanDefinition.setScope(ScopeAnnotation.value());           //存入bean的类型
                    }
                    else{
                        beanDefinition.setScope("Singleton");   //存入bean的类型（默认为单例bean）
                    }
                    beanDefinition.setClazz(aClass);            //存入class对象
                    beanDefinitionMap.put(beanName, beanDefinition);        //存进对象池中
                    //BeanDefinition
                }

            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
