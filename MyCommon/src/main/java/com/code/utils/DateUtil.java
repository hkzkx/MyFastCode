package com.code.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @ClassName: DateUtil
 * @date 2014年4月15日 下午11:28:43
 * 
 */
public class DateUtil {

	
      
	/**
	 * 英文简写（默认）如：2014-12-01
	 */
	public static String FORMAT_SHORT = "yyyy-MM-dd";
	/**
	 * 英文全称 如：2014-12-01 23:15:06
	 * 
	 */
	public static String FORMAT_LONG = "yyyy-MM-dd HH:mm:ss";
	/**
	 * 精确到毫秒的完整时间 如：yyyy-MM-dd HH:mm:ss.S
	 */
	public static String FORMAT_FULL = "yyyy-MM-dd HH:mm:ss.S";
	/**
	 * 中文简写 如：2014年12月01日
	 */
	public static String FORMAT_SHORT_CN = "yyyy年MM月dd";
	/**
	 * 中文全称 如：2014年12月01日 23时15分06秒
	 */
	public static String FORMAT_LONG_CN = "yyyy年MM月dd日  HH时mm分ss秒";
	/**
	 * 精确到毫秒的完整中文时间
	 */
	public static String FORMAT_FULL_CN = "yyyy年MM月dd日  HH时mm分ss秒SSS毫秒";

	private static Map<String, SimpleDateFormat> formaterCache = new HashMap<String, SimpleDateFormat>();

	private static Object lock = new Object();

	public static String now(String format) {
		SimpleDateFormat sdf = formaterCache.get(format);

		if (sdf == null) {
			synchronized (lock) {
				sdf = formaterCache.get(format);
				if (null == sdf) {
					sdf = new SimpleDateFormat(format);
					formaterCache.put(format, sdf);
				}
			}
		}

		return sdf.format(new Date());
	}

	public static String format(Date date, String format) {
		SimpleDateFormat sdf = formaterCache.get(format);

		if (sdf == null) {
			synchronized (lock) {
				sdf = formaterCache.get(format);
				if (null == sdf) {
					sdf = new SimpleDateFormat(format);
					formaterCache.put(format, sdf);
				}
			}
		}

		return sdf.format(date);
	}

	public static Date parse(String dateStr, String format) {
		SimpleDateFormat sdf = formaterCache.get(format);
		if (sdf == null) {
			synchronized (lock) {
				sdf = formaterCache.get(format);
				if (null == sdf) {
					sdf = new SimpleDateFormat(format);
					formaterCache.put(format, sdf);
				}
			}
		}
		try {
			return sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Date addDays(Date date, int step) {
		java.util.Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, step);
		return c.getTime();
	}

	public static Date addMonth(Date date,int step){
		java.util.Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MONTH, step);

		return c.getTime();
	}
	public static int interval(Date date1, Date date2) {
		Long l = date1.getTime() - date2.getTime();
		Long day = l / (24 * 60 * 60 * 1000);
		if (date1.compareTo(date2) > 0)
			return day.intValue() + 1;
		return Math.abs(day.intValue());
	}

	public static long getNowTimes() {
		Calendar now = Calendar.getInstance();
		return now.getTimeInMillis();
	}

	public static Date getDateAddHours(Date date, int hour) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		ca.add(Calendar.HOUR_OF_DAY, hour);
		return ca.getTime();
	}
	
	public static Date getDateAddMinute(Date date, int minute) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		ca.add(Calendar.MINUTE, minute);
		return ca.getTime();
	}

	// 计算两个日期之间有多少分钟
	public static int getMinutesBetween(Date startDate, Date endDate) {
		Calendar d1 = Calendar.getInstance();
		d1.setTime(startDate);
		Calendar d2 = Calendar.getInstance();
		d2.setTime(endDate);
		if (d1.after(d2)) { // swap dates so that d1 is start and d2 is end
			java.util.Calendar swap = d1;
			d1 = d2;
			d2 = swap;
		}
		int days = d2.get(Calendar.MINUTE) - d1.get(Calendar.MINUTE);

		return days;
	}
	// 2个时间的倒计时
		public static Long[] getCountDown(Date startDate, Date endDate) {
			long between = (startDate.getTime() - endDate.getTime()) / 1000;// 除以1000是为了转换成秒
			long day = between / (24 * 3600);
			long hour = between % (24 * 3600) / 3600;
			long minute = between % 3600 / 60;
			long second = between % 60;
			Long[] long_ = new Long[4];
			long_[0] = day;
			long_[1] = hour;
			long_[2] = minute;
			long_[3] = second;
			return long_;
		}

		// 距今倒计时
		public static String getCountDownStr(Date startDate) {
			Calendar now = Calendar.getInstance();
			return getCountDownStr(startDate, now.getTime(), null);
		}

		// 距今倒计时
		public static String getCountDownStr(Date startDate, Integer index) {
			Calendar now = Calendar.getInstance();
			return getCountDownStr(startDate, now.getTime(), index);
		}

		// 2个时间的倒计时
		public static String getCountDownStr(Date startDate, Date endDate,
				Integer index) {
			Long[] longs = getCountDown(startDate, endDate);
			StringBuffer stringBuffer = new StringBuffer();
			if (longs[0] > 0) {
				stringBuffer.append(longs[0] + "天  ");
			} else {
				stringBuffer.append("0天  ");
			}
			int size = longs.length;
			if (index != null && index <= size) {
				size = index;
			}
			for (int i = 1; i < size; i++) {
				Long num = longs[i];
				if (i != 1) {
					stringBuffer.append(":" + (num == 0 ? "00" : num));
				} else {
					stringBuffer.append(num == 0 ? "00" : num);
				}
			}
			// stringBuffer.append((longs[1] == 0 ? "00" : longs[1]) + ":" +
			// (longs[2] == 0 ? "00" : longs[2]) + ":" + (longs[3] == 0 ? "00" :
			// longs[3]));
			return stringBuffer.toString();
		}

		// 判读是否是今天
		public static boolean isToday(Date startDate) {
			Calendar today = Calendar.getInstance();
			if (format(startDate, "yyyyMMdd").equals(
					format(today.getTime(), "yyyyMMdd"))) {
				return true;
			}
			return false;
		}
	// 距今倒计时
	public static Long[] getCountDown(Date startDate) {
		Calendar d1 = Calendar.getInstance();
		d1.setTime(startDate);
		long between = (d1.getTimeInMillis() - getNowTimes()) / 1000;// 除以1000是为了转换成秒
		long day = between / (24 * 3600);
		long hour = between % (24 * 3600) / 3600;
		long minute = between % 3600 / 60;
		long second = between % 60 / 60;
		Long[] long_ = new Long[4];
		long_[0] = day;
		long_[1] = hour;
		long_[2] = minute;
		long_[3] = second;
		return long_;
	}
	/**
	 * 计算两个日期之间相差的天数
	 * 
	 * @param smdate
	 *            较小的时间
	 * @param bdate
	 *            较大的时间
	 * @return 相差天数
	 * @throws ParseException
	 */
	public static int daysBetween(Date smdate, Date bdate)
			throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (bdate == null) {
			bdate = new Date();
		}
		smdate = sdf.parse(sdf.format(smdate));
		bdate = sdf.parse(sdf.format(bdate));
		Calendar cal = Calendar.getInstance();
		cal.setTime(smdate);
		long time1 = cal.getTimeInMillis();
		cal.setTime(bdate);
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);
		return Integer.parseInt(String.valueOf(between_days));
	}

	/**
	 * 是否有效时间
	 * 
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	public static boolean isValidTime(Date beginTime, Date endTime) {
		Date now = new Date();
		if (beginTime == null && endTime == null) {
			return true;
		} else if (beginTime != null && endTime == null) {
			if(beginTime.compareTo(now)<=0){
				return true;
			}else{
				return false;
			}
		} else if (beginTime == null && endTime != null) {
			if(endTime.compareTo(now)>=0){
				return true;
			}else{
				return false;
			}
		} else if (beginTime != null && endTime != null) {
			if (beginTime.compareTo(now)<=0&&endTime.compareTo(now)>=0) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * 是否还未过期
	 * 
	 * @param time
	 * @return
	 */
	public static boolean checkValidTime(Date time) {
		if (time != null) {
			Date now = new Date();
			if (time.compareTo(now)>=0) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	
	
	/**
	 * 
	 * @Title: getBetweenMinute   
	 * @Description: 得到2个时间的分钟数
	 * @param: @param endTime  结束时间
	 * @param: @param now 当前时间
	 * @param: @return      
	 * @return: int      
	 * @throws
	 */
	public static int getBetweenMinute(Date endTime, Date now) {
		Long time = endTime.getTime() - now.getTime();
		Long day = time / (60 * 1000);
		if (endTime.compareTo(now) > 0)
			return day.intValue() + 1;
		return Math.abs(day.intValue());
	}
	
	
    /**
     * 
     * @Title: getHoursAfterTime   
     * @Description: TODO
     * @param: @param hour
     * @param: @param date
     * @param: @return      
     * @return: Date      
     * @throws
     */
    public static Date getHoursAfterTime(int hour,Date date) {
            String hoursAgoTime = "";
            Calendar cal = Calendar.getInstance();         
            cal.setTime(date);
            cal.set(Calendar.HOUR, cal.get(Calendar.HOUR)+hour);
            hoursAgoTime = new SimpleDateFormat(DateUtil.FORMAT_LONG).format(cal.getTime());
            return  parse(hoursAgoTime, DateUtil.FORMAT_LONG);
        }
	

	public static void main(String[] args) throws ParseException {
		// System.out.println(format(new Date(), "yyyy-MM-dd"));
		// System.out.println(format(new Date(), "yyyy-MM-dd"));
		// System.out.println(format(new Date(), "yyyy-MM-dd"));
		//
		// System.out.println(format(new Date(), "yyyy年MM月dd日"));
		 System.out.println(getDateAddMinute(new Date(), 3));
		// System.out.println(addDays(new Date(), -7));

		/*String aa = getCountDownStr(parse("2014-05-16 17:10:11",
				"yyyy-MM-dd HH:mm:ss"));
		System.out.println(aa);*/
	/*	 SimpleDateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");  
	        String dateString = "20140516140101";  
	        try {  
	            Date date = df.parse(dateString);  
	            System.out.println(getBetweenMinute(date, new Date()));
	            System.out.println(df.format(date));  
	        } catch (Exception ex) {  
	            System.out.println(ex.getMessage());  
	        } */ 
		 
		//System.err.println(getHoursAgoTime(2));
		 
		 System.out.println(getTodayEnd());
	
	}

	public static long getSeconds(Date beginTime, Date endTime) {
		if(null==beginTime||endTime==null){
			return 0;
		}
		Calendar begin = Calendar.getInstance();
		begin.setTime(beginTime);
		Long beginMi= begin.getTimeInMillis();
		Calendar end = Calendar.getInstance();
		end.setTime(endTime);
		Long endMi=end.getTimeInMillis();
		return (endMi-beginMi)/1000;
	}
	
	
	/**
	 * 取当天零点零分零秒 
	 * @return Date
	 */
    public static Date getTodayStart() {  
        Calendar calendar = Calendar.getInstance();  
        //如果没有这种设定的话回去系统的当期的时间  
        calendar.set(Calendar.HOUR_OF_DAY, 0);  
        calendar.set(Calendar.MINUTE, 0);  
        calendar.set(Calendar.SECOND, 0);  
        calendar.set(Calendar.MILLISECOND, 0);  
        Date date = new Date(calendar.getTimeInMillis());  
         return date;
    }     
  
    
    
     /**
      *  取当天23点59分59秒   
      * @return Date
      */
    public static Date getTodayEnd () {  
        Calendar calendar = Calendar.getInstance();  
        calendar.set(Calendar.HOUR_OF_DAY, 23);   
        calendar.set(Calendar.MINUTE, 59);  
        calendar.set(Calendar.SECOND, 59);  
        Date date = new Date(calendar.getTimeInMillis());  
        return date;
    }     
}
