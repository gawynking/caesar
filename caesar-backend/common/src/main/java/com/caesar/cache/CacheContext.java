package com.caesar.cache;


import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class CacheContext<K, V> {

    private static final Logger logger = Logger.getLogger(CacheContext.class.getName());

    private CacheStrategy<K, V> cacheStrategy;

    private final ScheduledExecutorService monitorExecutor = Executors.newSingleThreadScheduledExecutor();

    public CacheContext(CacheStrategy<K, V> cacheStrategy) {
        this.cacheStrategy = cacheStrategy;
        this.monitor(cacheStrategy);
    }

    public void setCacheStrategy(CacheStrategy<K, V> cacheStrategy) {
        this.cacheStrategy = cacheStrategy;
    }

    public void put(K key, V value) {
        cacheStrategy.put(key, value);
    }

    public V get(K key) {
        return cacheStrategy.get(key);
    }

    public void remove(K key) {
        cacheStrategy.remove(key);
    }

    private void monitor(CacheStrategy<K, V> cacheStrategy){
        monitorExecutor.scheduleAtFixedRate(() -> {
            logger.info("开始审计运行任务.");
            Map<K, V> internalMap = cacheStrategy.getAll();
            internalMap.forEach((key, value) -> logger.info("运行任务 " + key + " => " + value.toString()));
        }, 0, 1, TimeUnit.MINUTES);
    }

}

