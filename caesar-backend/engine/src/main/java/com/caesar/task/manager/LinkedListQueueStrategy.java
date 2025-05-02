package com.caesar.task.manager;

import java.util.LinkedList;
import java.util.NoSuchElementException;

public class LinkedListQueueStrategy<T> implements QueueStrategy<T> {
    private static volatile LinkedListQueueStrategy<?> instance;
    private LinkedList<T> list = new LinkedList<>();

    private LinkedListQueueStrategy() {}

    @SuppressWarnings("unchecked")
    public static <T> LinkedListQueueStrategy<T> getInstance() {
        if (instance == null) {
            synchronized (LinkedListQueueStrategy.class) {
                if (instance == null) {
                    instance = new LinkedListQueueStrategy<>();
                }
            }
        }
        return (LinkedListQueueStrategy<T>) instance;
    }

    @Override
    public boolean offer(T element) {
        return list.add(element);
    }

    @Override
    public boolean add(T element) {
        list.add(element);
        return true;
    }

    @Override
    public T poll() {
        return list.pollFirst();
    }

    @Override
    public T remove() {
        return list.removeFirst();
    }

    @Override
    public T peek() {
        return list.peekFirst();
    }

    @Override
    public T element() {
        return list.getFirst();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public int size() {
        return list.size();
    }
}
