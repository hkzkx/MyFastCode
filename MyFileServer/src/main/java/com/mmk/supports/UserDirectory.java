package com.mmk.supports;

import java.security.MessageDigest;

public class UserDirectory {
	public static final int MD5_STRID_SIZE = 16;
	private static final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	
	public static String calc8LByStr(String strBuf) {
		return calcByBt(strBuf.getBytes(), true);
	}
	
	public static String calcByBt(byte[] btBuf, boolean isLeft) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5"); //获得MD5摘要算法的 MessageDigest对象
			byte[] btCode = md5.digest(btBuf); //获得密文
			if(btCode.length != 16) {
				return null;
			}
			
			int len = isLeft ? (btCode.length/2) : btCode.length;
			char[] strCode = new char[len*2];
			int j = 0;
			for(int i = len - 1; i > -1; i--) {
				strCode[j++] = hexDigits[(btCode[i] >>> 4) & 0x0F];
				strCode[j++] = hexDigits[btCode[i] & 0x0F];
			}
			return new String(strCode);
		} catch(Exception e) {
			return null;
		}
	}
	
}

