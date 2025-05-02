package com.caesar.util;

import java.io.*;

public class IOUtils {

    public static String readFileToBuffer(String file) {

        StringBuilder sb = new StringBuilder();

        BufferedReader objReader = null;
        try {
            String strCurrentLine = "";
            objReader = new BufferedReader(new FileReader(file));
            while ((strCurrentLine = objReader.readLine()) != null) {
//                sb.append(strCurrentLine.replaceAll("\\s",""));
                sb.append(strCurrentLine).append("\n");
            }

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {
                if (objReader != null)
                    objReader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return sb.toString();

    }


    /**
     * copy 文件
     *      FileInputStream 和 FileOutputStream 方式 效率较低
     *
     * @param source
     * @param target
     */
    public static void copy(String source,String target){

        File input = new File(source);
        File output = new File(target);

        if(!input.exists()){
            System.out.println("源文件不存在!");
            System.exit(1);
        }

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = new FileInputStream(input);
        }catch (IOException e){
            e.printStackTrace();
        }

        try {
            outputStream = new FileOutputStream(output);
        } catch (IOException e){
            e.printStackTrace();
        }

        if(inputStream != null && outputStream != null){
            try {
                int size = 1024*1024; // 开辟1M空间内存
                byte[] b = new byte[size];
                int tmp;
                while ((tmp = inputStream.read(b)) != -1) {

                    // 快
                    outputStream.write(b,0,tmp);

                }
                outputStream.flush();

            }catch (IOException e){
                e.printStackTrace();
            }
        }

        try{
            inputStream.close();
            outputStream.close();
        }catch (IOException e){
            e.printStackTrace();
        }

    }






}
