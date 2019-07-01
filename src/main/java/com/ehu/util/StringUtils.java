package com.ehu.util;

public class StringUtils {

    /**
     * 检查对象是否为空
     *
     * @param str 字符串
     * @return true or false
     */
    public static boolean isBlank(String str) {
        return (str == null || "".equals(str) || "".equals(str.trim()));
    }

    /**
     * 如果为空则获取默认值
     *
     * @param str        str
     * @param defaultVal 默认值
     * @return str
     */
    public static String getDefaultIfNull(String str, String defaultVal) {
        return isBlank(str) ? defaultVal : str;
    }

    /**
     * 按指定的字节数截取字符串（一个中文字符占3个字节，一个英文字符或数字占1个字节）
     *
     * @param sourceString 源字符串
     * @param cutBytes     要截取的字节数
     * @return 返回截取后String
     */
    public static String subStringSpecifyBytes(String sourceString, int cutBytes) {
        if (StringUtils.isBlank(sourceString)) {
            return "";
        }

        int totalBytes = 0, strTotalBytes = sourceString.getBytes().length;
        if (strTotalBytes <= cutBytes) {
            return sourceString;
        }

        int lastIndex = 0, strLength = sourceString.length();
        int last = strLength - 1;
        for (int i = last; i >= 0; i--) {
            String s = Integer.toBinaryString(sourceString.charAt(i));
            if (s.length() > 8) {
                totalBytes += 3;
            } else {
                totalBytes += 1;
            }

            if (strTotalBytes - totalBytes <= cutBytes) {
                lastIndex = i;
                break;
            }
        }

        return sourceString.substring(0, lastIndex);
    }
}
