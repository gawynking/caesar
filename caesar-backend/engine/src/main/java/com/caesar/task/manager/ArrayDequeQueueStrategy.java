package com.caesar.task.manager;

import java.util.ArrayDeque;
import java.util.NoSuchElementException;

public class ArrayDequeQueueStrategy<T> implements QueueStrategy<T> {

    private static volatile ArrayDequeQueueStrategy<?> instance;
    private ArrayDeque<T> deque = new ArrayDeque<>();

    private ArrayDequeQueueStrategy() {}

    @SuppressWarnings("unchecked")
    public static <T> ArrayDequeQueueStrategy<T> getInstance() {
        if (instance == null) {
            synchronized (ArrayDequeQueueStrategy.class) {
                if (instance == null) {
                    instance = new ArrayDequeQueueStrategy<>();
                }
            }
        }
        return (ArrayDequeQueueStrategy<T>) instance;
    }

    @Override
    public boolean offer(T element) {
        return deque.add(element);
    }

    @Override
    public boolean add(T element) {
        deque.add(element);
        return true;
    }

    @Override
    public T poll() {
        return deque.pollFirst();
    }

    @Override
    public T remove() {
        return deque.removeFirst();
    }

    @Override
    public T peek() {
        return deque.peekFirst();
    }

    @Override
    public T element() {
        return deque.getFirst();
    }

    @Override
    public boolean isEmpty() {
        return deque.isEmpty();
    }

    @Override
    public int size() {
        return deque.size();
    }
}
