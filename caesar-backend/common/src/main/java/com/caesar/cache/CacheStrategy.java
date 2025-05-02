package com.caesar.cache;

public interface CacheStrategy<K, V> {
    void put(K key, V value);
    V get(K key);
    void remove(K key);
}
