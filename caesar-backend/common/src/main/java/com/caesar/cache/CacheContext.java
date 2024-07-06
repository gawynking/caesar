package com.caesar.cache;


public class CacheContext<K, V> {
    private CacheStrategy<K, V> cacheStrategy;

    public CacheContext(CacheStrategy<K, V> cacheStrategy) {
        this.cacheStrategy = cacheStrategy;
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

}

