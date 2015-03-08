package com.code.utils;

import org.apache.commons.lang.StringUtils;

public class RegexUtil {

	private static final String int_regex = "(\\d)+";

	private static final String float_regex = "(\\d)+(\\.){1,1}(\\d){0,2}";

	public static boolean isInt(String str) {
		return StringUtils.isNotEmpty(str) && str.matches(int_regex);
	}

	public static boolean isFloat(String str) {
		return StringUtils.isNotEmpty(str) && str.matches(float_regex);
	}
	
}
