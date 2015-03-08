package com.code.common;

/***
 * 传递系统消息变量定义
 *
 */

public class EmailSmsParamsConstant {
	
	/**
	 * 发送用户ID
	 */
	public static final String FROM_USERID = "fromUserId";
	
	/**
	 * 接收用户ID 
	 */
	public static final String TO_USERID = "toUserId";
	
	/***
	 * 接收用户会员类型  会员类型：0-卖家；1-服务商；2-买家；3-货源商
	 */
	public static final String MEMBER_TYPE = "memberType";
	
	/**
	 * 模板条目CODE
	 */
	public static final String TEMPLATECODE_ENUM = "templateCodeEnum";
	
	/**
	 * 模板内容变量值
	 */
	public static final String CONTENT_MAP = "contentMap";
	
	/**
	 * 是否english
	 */
	public static final String IS_ENGLISH = "isEnglish";
	
	/**
	 * 是否发送email
	 */
	public static final String IS_SENDEMAIL = "isSendEmail";
	
	/**
	 * 是否发送站内信
	 */
	public static final String IS_SENDSITEMAIL = "isSendSitemail";
	
	/**
	 * 是否发送短信
	 */
	public static final String IS_SENDSMS = "isSendSms";

}
