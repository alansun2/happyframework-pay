package com.ehu.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 日期处理工具类
 * 
 * @author AlanSun
 * 
 */
public class DateUtil {
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public static final String TOMORROW_FORMAT = "MM月dd日";

	public static final String HOUR_MINUTES_FORMAT = "HHmm";

	public static final String DATE_SHORT = "yyyy-MM-dd";

	public static final String DATE_LONG_FORMAT = "yyyyMMddHHmmssSSS";

	public static final String DATE_DAY_FORMAT = "yyyy-MM-dd";
	
	public static final String DATE_DAY_FORMAT2 = "yyyyMMdd";
	
	public static final String SECOND = "SECOND";

	/**
	 * 获取指定格式的当前时间
	 * @param format 指定格式
	 * @return
	 */
	public static String getNow(final String format) {
		try {
			return formatDate(new Date(), format);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取当前的小时分钟数
	 * @return
	 */
	public static int getTimeByHourMinute(){
		Date today = new Date();
		SimpleDateFormat f = new SimpleDateFormat(HOUR_MINUTES_FORMAT);
		return Integer.parseInt(f.format(today));
	}

	/**
	 * 获取当前日期
	 * 
	 * @return
	 */
	public static String getCurrentDate() {
		Date today = new Date();
		SimpleDateFormat f = new SimpleDateFormat(DATE_FORMAT);
		return f.format(today);
	}
	
	/**
	 * 根据时间戳转换时间类型
	 * @param time
	 * @return
	 */
	public static Date getDateByTime(int time){
		return new Date((long)time * 1000L);
	}

	/**
	 * 获取当前时间的10位时间戳
	 * 
	 * @return
	 */
	public static int getCurrentTime() {
		return (int) (System.currentTimeMillis() / 1000);
	}
	
	public static Date getDate(Date date, int day) {
		return (Date)getDate(date, day, null, 0);
	}
	
	public static String getDate(Date date, int day, String dateFormat) {
		return (String)getDate(date, day, dateFormat, 0);
	}
	
	/**
	 * 获取明天的日期 格式为（xx月xx日）
	 * 负数表示 往前推 day天
	 * 正数表示 往后推 day天
	 * @return
	 */
	public static Object getDate(Date date, int day, String dateFormat, int e) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);

		// 把日期往后增 加一天.整数往后推,负数往前移动
		calendar.add(Calendar.DATE, day);

		// 这个时间就是日期往后推一天的结果
		date = calendar.getTime();
		if(dateFormat != null){
			SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
			return formatter.format(date);
		}else{
			return date;
		}
	}

	/**
	 * 是否在运营期
	 * @return
	 */
	public static boolean isOperationPeriod(){ 
		Calendar cal = Calendar.getInstance(); 

		//当前时间
		long currentTime = cal.getTimeInMillis();

		cal.set(Calendar.HOUR_OF_DAY, 5); //设置凌晨5点
		cal.set(Calendar.SECOND, 0); 
		cal.set(Calendar.MINUTE, 0); 
		cal.set(Calendar.MILLISECOND, 0); 
		long startTime = cal.getTimeInMillis();

		cal.set(Calendar.HOUR_OF_DAY, 21); //设置晚上21点
		long endTime = cal.getTimeInMillis();

		return startTime < currentTime && currentTime < endTime; 
	} 
	/**
	 * 将Date类型日期格式化为字符串
	 * 
	 * @param date
	 *            Date类型日期
	 * @param format
	 *            目标格式
	 * @return String
	 */
	public static String formatDate(final Date date, final String format) {
		SimpleDateFormat fmt = new SimpleDateFormat(format);
		return fmt.format(date);
	}
	/**
	 * 获取以当天为准的指定日期的开始时间 ， 0表示当前 -1表示昨天
	 * @return
	 */
	public static int getStartTimeByDay(int day){ 
		Calendar cal = Calendar.getInstance(); 
		cal.set(Calendar.HOUR_OF_DAY, day * 24); 
		cal.set(Calendar.SECOND, 0); 
		cal.set(Calendar.MINUTE, 0); 
		cal.set(Calendar.MILLISECOND, 0); 
		return (int) (cal.getTimeInMillis()/1000); 
	}  
}
