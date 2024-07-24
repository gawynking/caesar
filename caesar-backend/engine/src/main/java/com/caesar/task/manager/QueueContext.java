package com.caesar.task.manager;

public class QueueContext<T> {
    private QueueStrategy<T> strategy;

    public QueueContext(QueueStrategy<T> strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(QueueStrategy<T> strategy) {
        this.strategy = strategy;
    }

    public boolean offer(T element) {
        return strategy.offer(element);
    }

    public boolean add(T element) {
        return strategy.add(element);
    }

    public T poll() {
        return strategy.poll();
    }

    public T remove() {
        return strategy.remove();
    }

    public T peek() {
        return strategy.peek();
    }

    public T element() {
        return strategy.element();
    }

    public boolean isEmpty() {
        return strategy.isEmpty();
    }

    public int size() {
        return strategy.size();
    }
}
