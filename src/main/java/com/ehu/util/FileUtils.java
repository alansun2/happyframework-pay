package com.ehu.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lombok.extern.slf4j.Slf4j;

/**
 * @author AlanSun
 * @Date 2017年8月10日 下午9:29:41
 */
@Slf4j
public class FileUtils {
	/**
	 * 文件拷贝
	 * @param in
	 * @param des
	 */
	public static void streamHandler(InputStream in, String desPath){
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			
			File file = new File(desPath);
			if(file.exists()){
				return;
			}
			File file1 = new File(desPath.substring(0, desPath.lastIndexOf("/")));
			if(!file1.exists()){
				file1.mkdirs();
			}
			bis = new BufferedInputStream(in);
			OutputStream out = new FileOutputStream(file, true);
			bos = new BufferedOutputStream(out);
			byte[] buff = new byte[1024];
			int length = 0;
			while((length = bis.read(buff)) != -1){
				bos.write(buff, 0, length);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("从inputstream获取数据失败", e);
		}finally{
			try {
				if(bis != null){
					bis.close();
				}
				if(bos != null){
					bos.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error("从inputstream获取数据失败", e);
			}
		}
	}
}
