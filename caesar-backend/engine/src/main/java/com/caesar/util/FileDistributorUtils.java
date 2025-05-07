package com.caesar.util;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class FileDistributorUtils {

    private static final Logger logger = Logger.getLogger(FileDistributorUtils.class.getName());

    private static final String USER = System.getProperty("user.name");

    /**
     * 分发文件到多个目标主机
     *
     * @param filePath    本地文件路径
     * @param remoteHosts 目标主机列表
     * @param remotePath  目标主机保存路径
     */
    public static void distributeFile(String filePath, List<String> remoteHosts, String remotePath) {
        for (String host : remoteHosts) {
            try {
                sendFile(filePath, host, USER, remotePath);
            } catch (IOException | InterruptedException e) {
                logger.info(String.format(" 分发到主机 %s 失败: %s%n", host, e.getMessage()));
            }
        }
    }

    /**
     * 使用 scp 命令发送文件
     *
     * @param filePath    本地文件路径
     * @param host        远程主机
     * @param user        远程用户名
     * @param remotePath  远程路径
     * @throws IOException
     * @throws InterruptedException
     */
    private static void sendFile(String filePath, String host, String user, String remotePath) throws IOException, InterruptedException {
        String remote = String.format("%s@%s:%s", user, host, remotePath);
        ProcessBuilder builder = new ProcessBuilder("scp", "-o", "StrictHostKeyChecking=no", filePath, remote);
        Process process = builder.inheritIO().start();
        int exitCode = process.waitFor();
        if (exitCode == 0) {
            logger.info(String.format(" 文件成功分发到 %s%n", host));
        } else {
            throw new IOException("SCP 命令执行失败，退出码: " + exitCode);
        }
    }
}
