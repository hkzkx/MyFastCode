package com.code.utils;

public class StarUtil {

	/**
	 * 计算打分星标
	 * @param score
	 * @return
	 * fulei May 4, 2014 6:04:15 PM
	 */
	public static String starNum(Double score) {
		if(null==score){
			return "0";
		}
		int star = score.intValue();
		Double halfScore = DoubleMathUtils.mul(score-star,10);
		int half = (int)DoubleMathUtils.round(halfScore);
		if(half==0){
			return star+"";
		}
		return star+"_"+half;
	}
	
}
