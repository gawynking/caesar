package com.caesar.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class LocalCacheStrategy<K, V> implements CacheStrategy<K, V> {
    private static LocalCacheStrategy instance;
    private final Map<K, V> cache = new ConcurrentHashMap<>();

    private LocalCacheStrategy() {
        // 私有构造函数防止外部实例化
    }

    public static synchronized <K, V> LocalCacheStrategy<K, V> getInstance() {
        if (instance == null) {
            instance = new LocalCacheStrategy<>();
        }
        return instance;
    }

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
    }

    @Override
    public V get(K key) {
        return cache.get(key);
    }

    @Override
    public void remove(K key) {
        cache.remove(key);
    }

}
