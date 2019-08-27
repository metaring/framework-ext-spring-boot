package com.metaring.framework.ext.spring.boot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;

import org.springframework.beans.BeansException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.ea.async.Async;
import com.metaring.framework.Core;
import com.metaring.framework.FrameworkFunctionalitiesManager;
import com.metaring.framework.Tools;
import com.metaring.framework.util.ObjectUtil;

@ComponentScan(basePackages = {"com.metaring"})
@SpringBootApplication
public class MetaRingSpringBootApplication implements ApplicationContextAware {

    public static final String CFG_EXT = "ext";
    public static final String CFG_SPRING = "spring";
    public static final String CFG_INSTANCES = "instances";

    public static SpringApplication SPRING_APPLICATION;
    private static ApplicationContext APPLICATION_CONTEXT;

    public static final Executor EXECUTOR;

    static {
        Async.init();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int corePoolSize = 7;
        try {
            corePoolSize = Core.SYSKB.get(CFG_EXT).get(CFG_SPRING).getDigit(CFG_INSTANCES).intValue();
        } catch(Exception e) {
        }
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(42);
        executor.setQueueCapacity(11);
        executor.setThreadNamePrefix("MRSBAE - ");
        executor.initialize();
        EXECUTOR = executor;
    }

    public static final void main(String[] args) {
        run(args, new Class<?>[0]);
    }

    public static final void run(String[] args, String... classesString) {
        Class<?>[] classes = new Class<?>[0];
        try {
            classes = Arrays.asList(Arrays.asList(classesString).stream().map(it -> {
                Class<?> c = null;
                try {
                    c = Class.forName(it);
                } catch(Exception e) {
                }
                return c;
            }).toArray()).toArray(new Class<?>[classesString.length]);
        } catch(Exception e) {
        }
        run(args, classes);
    }

    public static final void run(String[] args, List<?> list) {
        Class<?> [] classes = new Class[0];
        if(ObjectUtil.isNullOrEmpty(list)) {
            run(args, classes);
            return;
        }
        if(list.get(0) instanceof Class) {
            run(args, list.toArray(new Class<?>[list.size()]));
        }
    }

    public static final void run(String[] args, Class<?>... classes) {
        List<Class<?>> list = new ArrayList<>();
        list.add(MetaRingSpringBootApplication.class);
        LinkedList<Class<?>> l = classes == null ? new LinkedList<>() : new LinkedList<>(Arrays.asList(classes));
        l.remove(MetaRingSpringBootApplication.class);
        l.stream().distinct().forEach(list::add);
        SPRING_APPLICATION = new SpringApplication(list.toArray(new Class<?>[list.size()]));
        try {
            SPRING_APPLICATION.setDefaultProperties(ObjectUtil.toProperties(Tools.FACTORY_DATA_REPRESENTATION.create().add(CFG_SPRING, Core.SYSKB.get(CFG_EXT).get(CFG_SPRING))));
        } catch(Exception e) {
        }
        SPRING_APPLICATION.run(args);
    }

    @Bean
    public CommandLineRunner completed() {
        return args -> FrameworkFunctionalitiesManager.reinit();
    }

    @Override
    public final void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(APPLICATION_CONTEXT != null) {
            return;
        }
        APPLICATION_CONTEXT = applicationContext;
    }

    public static final <T> T getBean(Class<T> clazz) {
        if(APPLICATION_CONTEXT == null) {
            return null;
        }
        return APPLICATION_CONTEXT.getBean(clazz);
    }
}