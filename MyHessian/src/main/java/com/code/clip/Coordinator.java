package com.code.clip;

public class Coordinator {
	public static String channel_push = "serviceChannel_push";
	public static String channel_pull = "serviceChannel_pull";
	public static String channel_heartbeat = "serviceChannel_heartbeat";
	public static int dbIdx = 0;
	
	
	public static Integer retries = 1;// 远程调用失败后，最大的重试次数，重次时可能在不同的节点上重试
}
