package com.caesar.engine;

import com.caesar.runner.ExecutionResult;
import com.caesar.runner.params.TaskInfo;
import com.caesar.shell.ShellTask;
import com.caesar.text.model.ScriptInfo;

public interface Engine {

    ScriptInfo buildCodeScript(TaskInfo task);

    ExecutionResult<ShellTask> execute(TaskInfo task);

}
