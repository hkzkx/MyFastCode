package com.code.clip;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.code.utils.StrUtil;

public class Coordinator {
	public static String nodesKey = StrUtil.calcJedisKey("clip", "nodes", "key");
	
	public static String channel_push = "_service_push_";
	public static String channel_pull = "_service_pull_";
	
	public static String channel_heartbeat = "_service_heartbeat_";
	
	public static String channel_collect_pull = "_service_collect_pull_";
	public static String channel_collect_push = "_service_collect_push_";
	
	public static String channel_collect_pull_logger = "_logger_collect_pull_";
	public static String channel_collect_push_logger = "_logger_collect_push_";
	public static String channel_collect_logger_set = "_logger_set_";
	
	public static String channel_collect_pull_static = "_static_collect_pull_";
	public static String channel_collect_push_static = "_static_collect_push_";
	public static String channel_collect_static_set = "_static_set_";
	
	public static int dbIdx = 0;

	public static Integer retries = 1;// 远程调用失败后，最大的重试次数，重次时可能在不同的节点上重试

	public static String getLocalIp() {
		InetAddress addr = null;
		try {
			addr = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		if (addr != null)
			return addr.getHostAddress().toString();// 获得本机IP

		return "";
	}

}
