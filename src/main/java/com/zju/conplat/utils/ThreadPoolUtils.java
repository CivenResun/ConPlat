package com.zju.conplat.utils;

import com.zju.conplat.config.ThreadPoolConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 返回一个自动或手动创建的线程池的实例，执行一些调度策略
 * @author civeng
 */
public class ThreadPoolUtils {
    private volatile static ExecutorService THREAD_POOL;
    private ThreadPoolUtils(){};

    public static ExecutorService getInstance(){
        if(THREAD_POOL==null){
            synchronized (ThreadPoolUtils.class){
                if(THREAD_POOL==null){
                    THREAD_POOL= Executors.newFixedThreadPool(10);
//                    new ThreadPoolConfig().taskExecutor().execute();
                }
            }
        }
        return THREAD_POOL;
    }
}
