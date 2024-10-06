package com.caesar.util;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;


public class FileUtils {

    /**
     * 获取当前目录下的文件列表
     *
     * @param filePath 目录路径
     * @return 文件路径列表
     */
    public static List<String> listFiles(String filePath) {
        List<String> list = new ArrayList<>();
        File file = new File(filePath);

        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                list.add(f.getPath());
            }
        }

        return list;
    }

    /**
     * 递归打印所有文件信息
     *
     * @param filePath 文件路径
     */
    public static void printFiles(String filePath) {
        File file = new File(filePath);
        print(file, 1);
    }

    private static void print(File file, int level) {
        if (file.exists()) {
            for (int i = 1; i < level; i++) {
                System.out.print("\t");
            }
            System.out.println(file.getName());

            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    print(f, level + 1);
                }
            }
        }
    }

    /**
     * 创建目录
     *
     * @param filePath 目录路径
     * @return 是否成功创建
     */
    public static boolean mkdir(String filePath) {
        File file = new File(filePath);
        return file.mkdirs();
    }

    /**
     * 创建文件
     *
     * @param filePath 文件路径
     * @return 是否成功创建
     */
    public static boolean createFile(String filePath) {
        if(!existsFile(filePath)) {
            File file = new File(filePath);
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return 是否成功删除
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        return file.delete();
    }

    /**
     * 重命名文件
     *
     * @param oldFile 旧文件路径
     * @param newFile 新文件路径
     * @return 新文件路径
     */
    public static String renameFile(String oldFile, String newFile) {
        File srcFile = new File(oldFile);
        File tgtFile = new File(newFile);
        if (srcFile.renameTo(tgtFile)) {
            return newFile;
        } else {
            System.err.println("重命名失败: " + oldFile);
            return oldFile;
        }
    }

    /**
     * 检查文件是否为空
     *
     * @param filePath 文件路径
     * @return 文件是否为空
     */
    public static boolean isFileEmpty(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.length() == 0;
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath
     * @return
     */
    public static boolean existsFile(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }


    /**
     * 获取文件大小
     *
     * @param filePath 文件路径
     * @return 文件大小（字节）
     */
    public static long getFileSize(String filePath) {
        File file = new File(filePath);
        return file.exists() ? file.length() : -1;
    }

    /**
     * 复制文件
     *
     * @param sourcePath 源文件路径
     * @param destPath   目标文件路径
     * @return 是否成功复制
     */
    public static boolean copyFile(String sourcePath, String destPath) {
        try {
            Files.copy(Paths.get(sourcePath), Paths.get(destPath), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            System.err.println("复制文件失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 移动文件
     *
     * @param sourcePath 源文件路径
     * @param destPath   目标文件路径
     * @return 是否成功移动
     */
    public static boolean moveFile(String sourcePath, String destPath) {
        try {
            Files.move(Paths.get(sourcePath), Paths.get(destPath), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            System.err.println("移动文件失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 如果操作系统不存在对应路径，则递归创建目录
     *
     * @param path 目录路径
     */
    public static void createDirectoryIfNotExists(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            try {
                Files.createDirectories(Paths.get(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
        }
    }


    /**
     * 将字符串内容写入指定文件
     *
     * @param filePath 文件路径
     * @param content  要写入的内容
     */
    public static void writeToFile(String filePath, String content) {
        // 使用 try-with-resources 自动关闭资源
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
