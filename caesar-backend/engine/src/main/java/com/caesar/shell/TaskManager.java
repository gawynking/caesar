package com.caesar.shell;


import com.caesar.cache.CacheContext;
import com.caesar.cache.LocalCacheStrategy;
import com.caesar.runner.ExecutionResult;
import com.caesar.task.Task;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class TaskManager {

    public static final CacheContext<String,Task> tasks = new CacheContext<>(new LocalCacheStrategy<String,Task>());


    public ExecutionResult<ShellTask> submitTask(ShellTask task) throws ExecutionException, InterruptedException {

        tasks.put(task.getFullTaskName(), task);

        CompletableFuture<ExecutionResult<ShellTask>> taskRunResult = CompletableFuture.supplyAsync(() -> {
            try {
                ExecutionResult<ShellTask> result = task.run();
                // 你可以在这里记录日志或状态
                return new ExecutionResult<ShellTask>(true, "Shell command executed successfully.", task);
            } catch (Exception e) {
                // 处理异常
                e.printStackTrace();
                return new ExecutionResult<ShellTask>(false, "Shell command execution failed: " + e.getMessage(), task);
            }
        });

        return taskRunResult.get();
    }


    public Task getTask(String fullTaskName) {
        return tasks.get(fullTaskName);
    }


    public ExecutionResult<ShellTask> terminateTask(String fullTaskName) {
        Task task = tasks.get(fullTaskName);
        if (task != null && task.getStatus().isRunning()) {
            task.getProcess().destroy();
        }
        return new ExecutionResult<ShellTask>(true, "Terminate shell command.", (ShellTask) task);
    }

}
