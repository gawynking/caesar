package com.caesar.task.manager;

import com.caesar.task.Task;

public class QueueOperator {

    private static QueueContext<Task> queueContext;

    public QueueOperator(){
        this.queueContext.setStrategy(LinkedListQueueStrategy.getInstance());
    }

    public QueueContext<Task> getQueueContext(){
        return this.queueContext;
    }

}
