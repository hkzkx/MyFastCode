package com.code.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class IP {
	private static Log	log	= LogFactory.getLog(IP.class);

	public static String getLocalIp() throws SocketException {

		Enumeration<NetworkInterface> interfaces = java.net.NetworkInterface.getNetworkInterfaces();
		while (interfaces.hasMoreElements()) {
			NetworkInterface interface_ = interfaces.nextElement();
			List<InterfaceAddress> ias = interface_.getInterfaceAddresses();
			if (ias != null && !ias.isEmpty()) {
				for (InterfaceAddress interfaceAddress : ias) {
					InetAddress ia = interfaceAddress.getAddress();
					if (ia != null && ia instanceof Inet4Address) {
						if (ia.isLoopbackAddress()) {
							log.debug("跳过Loopback地址绑定");
							continue;
						}
						String addr = ia.getHostAddress();
						return addr;
					}
				}
			}
		}
		return "";
	}
}
