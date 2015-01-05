package com.mmb.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;

import org.apache.commons.lang.StringUtils;

/**
 * 
* @ClassName: PriceUtil 
* @Description: 货币 分/元， 元/分 转换
* @date 2014年4月11日 下午8:38:01 
*
 */
public final class PriceUtil {

	private static final String priceRegex = "(\\d)+(\\.){0,1}(\\d){0,2}";
	
	public static boolean isPrice(String str){
		return StringUtils.isNotEmpty(str) 
				&& str.matches(priceRegex);
	}
	
	public static void main(String[] args) {
		System.out.println(convertToWanYuan(11111110.67d));
		
//		System.out.println(convertToFen("22.14"));
//		System.out.println(convertToYuan(2214));
//		
//		System.out.println(convertToFen("22.15"));
//		System.out.println(convertToYuan(2215));
//		
//		System.out.println(convertToFen("22.22"));
//		System.out.println(convertToYuan(2222));
//		
//		System.out.println(convertToFen("33.33"));
//		System.out.println(convertToYuan(3333));
//		
//		System.out.println(convertToFen("55.55"));
//		System.out.println(convertToYuan(5555));
//		
//		System.out.println(convertToFen("66.66"));
//		System.out.println(convertToYuan(6666));
//		
//		System.out.println(convertToFen("66666666666666666.66"));
//		System.out.println(convertToYuan(6666666666666666666L));
	}

	public static long sum(long src, long target) {
		BigDecimal bd1 = new BigDecimal(src);
		BigDecimal bd2 = new BigDecimal(target);
		return bd1.add(bd2).longValue();
	}

	public static long sub(long src, long target) {
		BigDecimal bd1 = new BigDecimal(src);
		BigDecimal bd2 = new BigDecimal(target);
		return bd1.subtract(bd2).longValue();
	}
	
	public static long mul(long src, long target) {
		BigDecimal bd1 = new BigDecimal(src);
		BigDecimal bd2 = new BigDecimal(target);
		return bd1.multiply(bd2).longValue();
	}
	/**
	 * 转换货币（分）到元，返回值为String为了避免超大数字科学计算法显示
	 * @param src 分
	 * @return 元，字符串形式，无科学计数
	 */
	public static String convertToYuan(long src) {
		if (src == 0) {
			return "0";
		}
		BigDecimal bd1 = new BigDecimal(src);
		BigDecimal bd2 = new BigDecimal(100);
		BigDecimal result = bd1.divide(bd2,2, RoundingMode.HALF_EVEN);
//		DecimalFormat df = new DecimalFormat("###,##0.00");
//		String resultStr = df.format(result);
		return result.toString();
//		return resultStr;
	}
	
	/**
	 * 转换货币（元）到分
	 * 
	 * @param src 元
	 * @return 分
	 */
	public static long convertToFen(String src) {
		BigDecimal bd1 = new BigDecimal(src);
		BigDecimal bd2 = new BigDecimal(100);
		return bd1.multiply(bd2).longValue();
	}
	
	public static Long convertDoubleToLong(Double price){
		return new BigDecimal(price * 100.00D).setScale(2, BigDecimal.ROUND_HALF_DOWN).longValue();
	}
	
	public static Double convertLongToDouble(Long price){
		String temp = new BigDecimal(price).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_DOWN).toPlainString();
		
		return Double.parseDouble(temp);
	}
	/**
	 * 元 转为万元
	 * @param src
	 * @return
	 */
	public static String convertToWanYuan(Double src) {
		if (src == null) {
			return null;
		}
		BigDecimal bd1 = new BigDecimal(src);
		BigDecimal bd2 = new BigDecimal(10000);
		BigDecimal result = bd1.divide(bd2,6,RoundingMode.HALF_DOWN);
		
		NumberFormat nf=NumberFormat.getInstance(); 
		nf.setMaximumFractionDigits(6);
		return nf.format(result.doubleValue()).replace(",", "");
	}
}
