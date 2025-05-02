package com.caesar.util;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/* 依赖:
        <dependency>
            <groupId>net.lingala.zip4j</groupId>
            <artifactId>zip4j</artifactId>
            <version>2.9.1</version>
        </dependency>
 */
public class ZipUtils {


    /**
     * 单元测试
     *
     * @param args
     */
    public static void main(String[] args) {

        // 定义ZIP文件输出路径
        String zipFilePath = "D:\\test\\output\\output222.zip";

        // 定义压缩文件的密码
        String password = "1234567890";

        String content = IOUtils.readFileToBuffer("D:\\test\\output\\tmp_0.json");

        writeEncryptZipFile(content,"json",zipFilePath,password);

        String s = readEncryptZipFile(zipFilePath, password);
        System.out.println(s);

    }


    /**
     * @param zipFilePath
     * @param password
     */
    public static String readEncryptZipFile(String zipFilePath, String password) {
        StringBuilder content = new StringBuilder();
        try {
            // 创建ZipFile对象
            ZipFile zipFile = new ZipFile(zipFilePath, password.toCharArray());
            List<FileHeader> fileHeaders = zipFile.getFileHeaders();
            for (FileHeader fileHeader : fileHeaders) {
                if (fileHeader != null) {
                    try (InputStream is = zipFile.getInputStream(fileHeader);
                         ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = is.read(buffer)) > 0) {
                            os.write(buffer, 0, length);
                        }
                        String fileContent = os.toString(StandardCharsets.UTF_8.name());
                        content.append(fileContent);
                    }
                } else {
                    System.out.println("文件 'string.txt' 不存在于ZIP文件中。");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return content.toString();
    }

    /**
     * @param content
     * @param fileType    文件类型：json
     * @param zipFilePath
     * @param password
     * @return
     */
    public static Boolean writeEncryptZipFile(String content, String fileType, String zipFilePath, String password) {

        String fileNamePrefix = "invoce";
        String fileName = fileNamePrefix + "." +("json".equals(fileType)?"json":"txt");

        try {
            // 创建ZipFile对象
            ZipFile zipFile = new ZipFile(zipFilePath, password.toCharArray());

            // 创建Zip参数
            ZipParameters zipParameters = new ZipParameters();
            zipParameters.setCompressionMethod(CompressionMethod.DEFLATE);
            zipParameters.setEncryptFiles(true);
            zipParameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);
            zipParameters.setFileNameInZip(fileName); // 设置文件名

            // 将字符串转换为字节数组输入流
            try (InputStream is = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))) {
                zipFile.addStream(is, zipParameters);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

}