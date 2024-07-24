package com.caesar.task.manager;

public interface QueueStrategy<T> {
    boolean offer(T element);    // 尝试将元素插入队列
    boolean add(T element);      // 添加元素到队列
    T poll();                    // 从队列头部检索并删除一个元素
    T remove();                  // 从队列头部检索并删除一个元素
    T peek();                    // 查看队列头部的元素但不删除
    T element();                 // 查看队列头部的元素但不删除
    boolean isEmpty();           // 检查队列是否为空
    int size();                  // 获取队列大小
}