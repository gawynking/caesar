package com.caesar.none.engine;

import com.caesar.engine.Engine;
import com.caesar.runner.ExecutionResult;
import com.caesar.params.TaskInfo;
import com.caesar.util.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class TextEngine implements Engine {

    private String fileSystem;
    private String codeDir;
    private String SEP = File.separator;

    public TextEngine(String fileSystem, String dir){
        this.fileSystem = fileSystem;
        if(!dir.endsWith("/")){
            this.codeDir = dir + "/";
        }
    }

    @Override
    public String buildCodeScript(String dbLevel, String taskName, String code) {
        /**
         * 项目路径设计类似如下风格: code-dir 指定绝对路径
         *  - dw-project/
         *      - sbin/
         *          - ods/
         *          - dim/
         *          - dwd/
         *      - sql/
         *          - ods/
         *          - dim/
         *          - dwd/
         */
        String sqlDirPath = this.codeDir+"sql"+ SEP +dbLevel;
        String sqlFilePath = sqlDirPath+ SEP +"tmp__"+taskName+".sql";
        FileUtils.createDirectoryIfNotExists(sqlDirPath);
        FileUtils.createFile(sqlFilePath);
        FileUtils.writeToFile(sqlFilePath,code); // 更新临时脚本文件，带${xxx}或${hivevar:xxx}参数SQL脚本

        return sqlFilePath;
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
