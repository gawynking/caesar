package com.caesar.shell;


import com.caesar.cache.CacheContext;
import com.caesar.cache.LocalCacheStrategy;
import com.caesar.runner.ExecutionResult;
import com.caesar.task.Task;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
public class TaskManager {

    public static final CacheContext<String,Task> tasks = new CacheContext<>(new LocalCacheStrategy<String,Task>());


    public ExecutionResult<Task> submitTask(String[] command) {
        Task task = new ShellTask();
        task.setId(UUID.randomUUID().toString());
        task.setCommand(command);
        tasks.put(task.getId(), task);

        CompletableFuture taskRunResult = CompletableFuture.runAsync(() -> {
            try {
                task.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return;
        });

//        taskRunResult.thenRunAsync((task)->{
//            tasks.remove();
//        });

        return new ExecutionResult<Task>(true, "Submit shell command.", task);
    }


    public Task getTask(String taskId) {
        return tasks.get(taskId);
    }

    public ExecutionResult<Boolean> terminateTask(String taskId) {
        Task task = tasks.get(taskId);
        if (task != null && task.isRunning()) {
            task.getProcess().destroy();
        }
        return new ExecutionResult<Boolean>(true, "Terminate shell command.");
    }

}
