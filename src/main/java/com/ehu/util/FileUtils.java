package com.ehu.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * @author AlanSun
 * @Date 2017年8月10日 下午9:29:41
 */
@Slf4j
public class FileUtils {
    /**
     * 文件拷贝
     *
     * @param in
     * @param desPath
     */
    public static void streamHandler(InputStream in, String desPath) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {

            File file = new File(desPath);
            if (file.exists()) {
                return;
            }
            File file1 = new File(desPath.substring(0, desPath.lastIndexOf("/")));
            if (!file1.exists()) {
                boolean mkdirs = file1.mkdirs();
            }
            bis = new BufferedInputStream(in);
            OutputStream out = new FileOutputStream(file, true);
            bos = new BufferedOutputStream(out);
            byte[] buff = new byte[1024];
            int length = 0;
            while ((length = bis.read(buff)) != -1) {
                bos.write(buff, 0, length);
            }
        } catch (IOException e) {
            log.error("从inputStream获取数据失败", e);
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                log.error("从inputStream获取数据失败", e);
            }
        }
    }

    /**
     * 获取文件流
     *
     * @param path 文件路径
     * @return content
     */
    public static String readFile(String path) {
        StringBuilder key = new StringBuilder();
        File file = new File(path);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String tempString;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                key.append(tempString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return key.toString();
    }
}
