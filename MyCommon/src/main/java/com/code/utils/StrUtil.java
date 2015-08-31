package com.code.utils;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class StrUtil {
	
	public static String long2Hex(Long num, int fixLen) {
		String hex = Long.toHexString(num).toUpperCase();
		return fillHex(hex,fixLen);
	}
	
	public static String dec2Hex(int num, int fixLen) {
		String hex = Integer.toHexString(num).toUpperCase();
		return fillHex(hex,fixLen);
	}
	
	private static String fillHex(String hex,int fixLen){
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < fixLen - hex.length(); i++) {
			sb.append("0");
		}
		sb.append(hex);
		return sb.toString();
	}
	
	public static int hex2Dec(String num) {
		return Integer.parseInt(num, 16);
	}
	
	public static long hex2Dec64(String num) {
		return Long.parseLong(num, 16);
	}
	
	public static byte[] uint2Binary(long num) {
		return long2Binary(num, 4);
	}
	
	public static byte[] ushort2Binary(long num) {
		return long2Binary(num, 2);
	}
	
	public static byte[] ubyte2Binary(long num) {
		return long2Binary(num, 1);
	}
	
	public static long binary2Long(byte[] btBuf) { //用long解决无符号int
		long num = 0;
		for(int i = 0; i < btBuf.length; i++) {
			long ni = btBuf[i] & 0xff;
			num = num | (ni << (8*i));
		}
		return num;
	}

	private static byte[] long2Binary(long num, int byteLen) { //用long解决无符号int
		long temp = num;
		byte[] btBuf = new byte[byteLen];
		for(int i = 0; i < byteLen; i++) {
			btBuf[i] = new Long(temp & 0xff).byteValue();
			temp >>= 8;
		}
		return btBuf;
	}	
	/**
	 * 将List转成字符串
	 * @param list
	 * @return
	 */
	public static String list2String(List<String> list){
		if(list.isEmpty()){
			return null;
		}
		return list.toString().replace("[", "").replace("]", "").replace(", ", "|");
	}
	/**
	 * 过滤为空的Map请求参数
	* @Description:
	* @param params void
	* @throws
	 */
	public static void filterMapParams(Map<String,Object> params){
		Object[] keys = params.keySet().toArray();
		for (Object key : keys) {
			Object value = params.get(key);
			if (value == null || StringUtils.isEmpty(value.toString())) {
				params.remove(key);
			}
		}
	}
	
	public static String calcJedisKey(String system,String module, String key) {
		String str = system + ":" + module + "&" + key;
		return str;
	}
}
