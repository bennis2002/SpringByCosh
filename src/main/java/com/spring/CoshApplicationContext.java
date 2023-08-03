package com.spring;

import java.io.File;
import java.net.URL;

public class CoshApplicationContext {

    private Class configClass;

    public CoshApplicationContext(Class configClass){

        this.configClass = configClass;

        //解析配置类
        //解析ComponentScan注解 --> 扫描路径 --> 扫描
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
            File[] files = file.listFiles();
            for (File f : files) {
                String fileName = f.getAbsolutePath();
                if (fileName.endsWith(".class")) {
                    String className = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class"));
                    className = className.replace("\\", ".");
                    try {
                        //类加载器
                        Class<?> aClass = classLoader.loadClass(className);
                        if (aClass.isAnnotationPresent(Component.class)) {      //判断是否存在Component注解
                            //..
                        }
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

    }

    public Object getBean(String beanName){

        return null;
    }
}
