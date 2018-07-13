package com.ehu.util;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.digest.DigestUtils;

public class StringUtils {

	/**
	 * 检查对象是否为空
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(Object str) {
		return (str == null || "".equals(str) || "".equals(str.toString().trim()));
	}
	
	/**
	 * 把对象转换成字符串
	 * @param obj
	 * @return
	 */
	public static String toString(Object obj) {
		return (obj == null ? null : obj.toString());
	}

	/**
	 * 对指定字符串进行转码
	 * @param str
	 * @param charset
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static String transCharset(String str, String charset) throws UnsupportedEncodingException {
		return new String(str.getBytes("ISO-8859-1"), charset);
	}
	
	/**
	 * 隐藏一个字符串的前部分
	 * @param str 	待处理字符串
	 * @param part	保留后面的字符数量
	 * @return	隐藏部分用*号代替的字符串
	 */
	public static String hidePartString(String str, int part) {
		int length = str.length() - part;
		StringBuffer prestr = new StringBuffer();
		for (int i=0; i<length; i++) {
			prestr.append("*");
		}
		prestr.append(str.substring(length, str.length()));
		
		return prestr.toString();
	}
	
	/**
	 * 把字符串转为32位MD5串
	 * @param str
	 * @return
	 */
	public static String md5Hex(String str) {
		return DigestUtils.md5Hex(str);
	}
	
	/**
	 * 把字符串转为2次加密的32位MD5串
	 * @param str
	 * @return
	 */
	public static String md5Hex2(String str) {
		String s1 = DigestUtils.md5Hex(str);
		String s2 = DigestUtils.md5Hex(s1.substring(20));
		return s2;
	}
	
	/**
	 * 把字符串转为16位MD5串
	 * @param str
	 * @return
	 */
	public static String md5Hex16(String str) {
		String s1 = DigestUtils.md5Hex(str);
		String s2 = s1.substring(8, 24);
		return s2;
	}
	/**
	 * 将有符号隔开的字符串用符号分割
	 * @param str
	 * @param splitType
	 * @return
	 */
	public static String stringTransform(String str,String splitType){
		String[] s = str.split(splitType);
		StringBuffer sb = new StringBuffer();
		for(String ss :s){
			sb.append("'"+ss+"',");
		}
		sb.delete(sb.length()-1, sb.length());
		return sb.toString();
	}
}
