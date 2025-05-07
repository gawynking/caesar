package com.caesar.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类：用于处理包含 @{{ varName }} 模板变量的 shell 脚本
 */
public class ShellTemplateProcessorUtils {

    // 支持变量名包含字母、数字、下划线、点、横杠（不能以数字开头）
    private static final Pattern VAR_PATTERN = Pattern.compile("@\\{\\{\\s*([a-zA-Z_][a-zA-Z0-9_\\-\\.]*)\\s*\\}\\}");

    /**
     * 替换模板内容中的变量
     *
     * @param templateContent 模板文本
     * @param variables        替换用的变量 map
     * @return 替换完成的文本
     */
    public static String processTemplate(String templateContent, Map<String, String> variables) {
        Matcher matcher = VAR_PATTERN.matcher(templateContent);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String varName = matcher.group(1);
            String replacement = variables.containsKey(varName) ? variables.get(varName) : "";
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * 从模板文件生成最终脚本文件
     *
     * @param templatePath 模板文件路径
     * @param outputPath   输出文件路径
     * @param variables    替换变量
     * @throws IOException 文件读写异常
     */
    public static void generateScriptFromTemplate(String templatePath, String outputPath, Map<String, String> variables) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(templatePath)), StandardCharsets.UTF_8);
        String processedContent = processTemplate(content, variables);
        Files.write(Paths.get(outputPath), processedContent.getBytes(StandardCharsets.UTF_8));
    }


    /**
     * 单元测试
     *
     * @param args
     */
    public static void main(String[] args) {
        String script = "#!/bin/bash\n" +
                "source ~/.bash_profile\n" +
                "\n" +
                "# 打印开始时间\n" +
                "echo \"Task start at: $(date '+%Y-%m-%d %H:%M:%S')\"\n" +
                "\n" +
                "# 自定义参数\n" +
                "@{{ customParamsDefine }}\n" +
                "\n" +
                "# 打印传入的参数\n" +
                "echo \"Received parameters: @{{ customParamsDefine }}\"\n" +
                "\n" +
                "# 任务逻辑部分\n" +
                "hive @{{ hiveParams }} -f @{{ sqlFile }}\n" +
                "\n" +
                "# 打印结束时间\n" +
                "echo \"Task end at: $(date '+%Y-%m-%d %H:%M:%S')\"\n" +
                "\n" +
                "# 获取 hive 的退出状态码\n" +
                "status=$?\n" +
                "\n" +
                "# 根据状态码判断任务执行结果\n" +
                "if [ ${status} -eq 0 ]; then\n" +
                "    echo \"Spark job completed successfully.\"\n" +
                "    # 可以在这里添加成功时需要执行的操作\n" +
                "else\n" +
                "    echo \"Spark job failed with exit code ${status}.\"\n" +
                "    # 可以在这里添加失败时需要执行的操作\n" +
                "fi\n" +
                "\n" +
                "# 退出\n" +
                "exit ${status}";

        Map<String, String> map = new HashMap<>();
        map.put("customParamsDefine","etl_date = ${etl_date} start_date = ${start_date}");
        map.put("hiveParams","--hivevar etl_date='2025-01-01' --hivevar start_date='2026-05-05'");
        map.put("sqlFile","/tmp/1.sql");
        String result = processTemplate(script, map);
        System.out.println(result);
    }

}
