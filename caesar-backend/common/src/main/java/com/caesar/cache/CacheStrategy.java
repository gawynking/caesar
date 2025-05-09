package com.caesar.cache;

import java.util.List;
import java.util.Map;

public interface CacheStrategy<K, V> {
    void put(K key, V value);
    V get(K key);
    void remove(K key);
    Map<K, V> getAll();
}
