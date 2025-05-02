package com.caesar.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class LocalCacheStrategy<K, V> implements CacheStrategy<K, V> {
    private Map<K, V> cache;

    public LocalCacheStrategy() {
        this.cache = new ConcurrentHashMap<>();
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
