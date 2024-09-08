package com.caesar.none.engine;

import com.caesar.engine.Engine;
import com.caesar.runner.ExecutionResult;
import com.caesar.params.TaskInfo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class TextEngine implements Engine {

    private String codeDir;

    public TextEngine(String dir){
        if(!dir.endsWith("/")){
            this.codeDir = dir + "/";
        }
    }

    @Override
    public ExecutionResult execute(TaskInfo task) {

        String scriptPath = this.codeDir + task.getDbLevel() + "/" + task.getTaskName() + ".sql";
        File scriptFile = new File(scriptPath);

        try {
            if (scriptFile.exists()) {
                new FileWriter(scriptFile, false).close();
            } else {
                Files.createDirectories(Paths.get(scriptFile.getParent()));
                scriptFile.createNewFile();
            }

            try (FileWriter writer = new FileWriter(scriptFile)) {
                writer.write(task.getCode());
            }

            return new ExecutionResult(true, "File written successfully.", scriptPath);
        } catch (IOException e) {
            e.printStackTrace();
            return new ExecutionResult(false, "Error writing file: " + e.getMessage());
        }

    }

}
