package com.code.clip;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import com.code.utils.IP;

public class Node implements java.io.Serializable, InitializingBean {
	private Log					log					= LogFactory.getLog(Node.class);

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -5240824887130870010L;

	private String				host;

	/**
	 * 当前节点状态<br/>
	 * 1：正常服务<br/>
	 * 2：节点存活，但暂时不接受新的请求或暂时不对外服务<br/>
	 */
	private int					status;

	private String				remark;

	/**
	 * 预计节点激活时间，根据delay计算的
	 */
	private long				activeTime;

	public Node(String host) {
		this.host = host;
	}

	public Node() {
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (!StringUtils.contains(this.host, "*"))
			return;

		boolean isbinded = false;
		String addr = IP.getLocalIp();
		if (StringUtils.isNotBlank(addr)) {
			this.host = StringUtils.replace(this.host, "*", addr);
			isbinded = true;
			log.debug(String.format("服务注册，绑定 %s", this.host));
		}

		if (!isbinded) {
			throw new RuntimeException("服务绑定失败，请检查网口配置");
		}
	}

	// public void calcTime() {
	// this.activeTime = System.currentTimeMillis()+delay;
	// //节点存活，但暂时不接受新的请求或暂时不对外服务
	// this.status = 2;
	// }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return host;
	}

}
