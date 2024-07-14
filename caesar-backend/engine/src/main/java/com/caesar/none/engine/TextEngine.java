package com.caesar.none.engine;

import com.caesar.engine.Engine;
import com.caesar.runner.ExecutionResult;
import com.caesar.task.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class TextEngine implements Engine {

    private String code_dir;

    public TextEngine(String dir){
        if(!dir.endsWith("/")){
            this.code_dir = dir + "/";
        }
    }

    @Override
    public ExecutionResult execute(Task task) {

        String scriptPath = this.code_dir + task.getDbLevel() + "/" + task.getTaskName() + ".sql";
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

            return new ExecutionResult(true, "File written successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            return new ExecutionResult(false, "Error writing file: " + e.getMessage());
        }

    }

}