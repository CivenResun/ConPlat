package com.zju.conplat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 手动创建线程池，设置各个参数
 * 要开启对异步任务的支持
 * @author civeng
 */
@Configuration
@EnableAsync
public class ThreadPoolConfig {

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程数     CPU密集型：cores+1  IO密集型：cores*2
        executor.setCorePoolSize(5);
        //最大线程数
        executor.setMaxPoolSize(5);
        //队列大小
        executor.setQueueCapacity(30);
        //线程池中的线程的前缀名
        executor.setThreadNamePrefix("async-service-");

        // 拒绝策略Rejection-policy：当前线程数等于最大线程数时，拒绝新增加的任务
        // Caller_Runs：由调用线程池的线程执行新任务    还有丢弃/忽视等策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //执行初始化
        executor.initialize();

        return executor;
    }

}
