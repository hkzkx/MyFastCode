package com.code.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;

public class DoubleMathUtils {
	/**
	 * 对double数据进行取精度.
	 * 
	 * @param value
	 *            float数据.
	 * @param scale
	 *            精度位数(保留的小数位数).
	 * @param roundingMode
	 *            精度取值方式. roundingMode 枚举
	 * @return 精度计算后的数据.
	 */
	public static double round(double value, int scale,
			RoundingMode roundingMode) {
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(scale, roundingMode);
		double d = bd.doubleValue();
		bd = null;
		return d;
	}

	/**
	 * 保留指定小数位数 四舍五入
	 * 
	 * @param value
	 * @param scale
	 * @return fulei 2014-4-4 上午9:18:58
	 */
	public static double round(double value, int scale) {
		return round(value, scale, RoundingMode.HALF_EVEN);
	}
	
	/**
	 * 保留两位小数位数 四舍五入
	 * 
	 * @param value
	 * @param scale
	 * @return fulei 2014-4-4 上午9:18:58
	 */
	public static double round(double value) {
		return round(value, 2);
	}
	/**
	 * 格式化，保留两位小数输出，不组两位补零，超过两位四舍五入保留两位
	 * @param value
	 * @return
	 * fulei 2014-4-21 下午6:54:22
	 */
	public static String format(double value){
		value = round(value);
		NumberFormat format = NumberFormat.getNumberInstance();
		format.setMinimumFractionDigits(2);
		format.setMaximumFractionDigits(2);
		return format.format(value).replaceAll(",", "");
	}
	
	public static void main(String[] args) {
		// 两位小数 舍弃
		System.out.println(round(1.155d, 2, RoundingMode.DOWN));
		// 三位小数 四舍五入
		System.out.println(round(1.155d, 2, RoundingMode.HALF_EVEN));
		// 乘法
		System.out.println(mul(4.1f, 100));
		// 减法之后执行加法
		System.out.println(sum(sub(0.4234f, 0.2111f), 0.1f));
		// 相除不保留小数 舍弃
		System.out.println(div(6.8f, 2.3f));
		// 相除四舍五入
		System.out.println(div(6.8f, 2.3f, 2));
	}

	/**
	 * float 相加
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static double sum(double d1, double d2) {
		BigDecimal bd1 = new BigDecimal(d1);
		BigDecimal bd2 = new BigDecimal(d2);
		return bd1.add(bd2).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * float 相减
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static double sub(double d1, double d2) {
		BigDecimal bd1 = new BigDecimal(d1);
		BigDecimal bd2 = new BigDecimal(d2);
		return bd1.subtract(bd2).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * double 乘法
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static double mul(double d1, double d2) {
		BigDecimal bd1 = new BigDecimal(Double.toString(d1));
		BigDecimal bd2 = new BigDecimal(Double.toString(d2));
		return bd1.multiply(bd2).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * double 除法
	 * 
	 * @param d1
	 * @param d2
	 * @param scale
	 *            四舍五入 小数点位数
	 * @return
	 */
	public static double div(double d1, double d2, int scale) {
		// 当然在此之前，你要判断分母是否为0， 为0你可以根据实际需求做相应的处理
		if (d2 == 0) {
			return 0;
		}
		BigDecimal bd1 = new BigDecimal(d1);
		BigDecimal bd2 = new BigDecimal(d2);
		return bd1.divide(bd2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * double 除法
	 * 
	 * @param d1
	 * @param d2
	 * @param scale
	 *            四舍五入 小数点位数
	 * @return
	 */
	public static int div(double d1, double d2) {
		// 当然在此之前，你要判断分母是否为0， 为0你可以根据实际需求做相应的处理
		if (d2 == 0) {
			return 0;
		}
		BigDecimal bd1 = new BigDecimal(d1);
		BigDecimal bd2 = new BigDecimal(d2);
		BigDecimal result = bd1.divide(bd2, RoundingMode.DOWN);
		return result.intValue();
	}
	/**
	 * 将货币元-转为-分分会
	 * @param price
	 * @return
	 * fulei 2014-4-24 下午8:50:08
	 */
	public static Long toLongCurr(Double price) {
		price = mul(price, 100);
		return price.longValue();
	}
}
