package com.code.controller.clip;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.code.clip.Coordinator;
import com.code.clip.Describer;
import com.code.clip.KV;
import com.code.clip.Node;
import com.code.clip.Report;
import com.code.clip.ServiceStub;
import com.code.clip.Type;
import com.code.clip.command.PubSub;
import com.code.redis.PooledJedis;
import com.code.utils.JsonUtil;

@Component
public class Kernel implements InitializingBean {
	private static Log							log		= LogFactory.getLog(Kernel.class);

	@Autowired
	protected PooledJedis						pooledJedis;

	/**
	 * 服务端服务存根
	 */
	protected Map<String, List<ServiceStub>>	stubs	= new ConcurrentHashMap<String, List<ServiceStub>>();

	/**
	 * 服务端日志器设置
	 */
	protected Map<String, List<KV>>				loggers	= new ConcurrentHashMap<String, List<KV>>();

	/**
	 * 静态域设置
	 */
	protected Map<String, List<KV>>				statics	= new ConcurrentHashMap<String, List<KV>>();

	@Override
	public void afterPropertiesSet() throws Exception {
		new Thread(new Runnable() {
			@Override
			public void run() {
				pooledJedis.getJedisProxy().subscribe(
						new PubSub() {

							@Override
							public void onMessage(String channel, String message) {
								try {
									Report report = JsonUtil.json2java(message, Report.class);

									// 处理服务收集
									if (channel.equals(Coordinator.channel_collect_push)) {
										List<ServiceStub> list = new ArrayList<ServiceStub>();
										report.getStubs().forEach(new Consumer<Object>() {

											@Override
											public void accept(Object t) {
												list.add((ServiceStub) t);
											}
										});
										stubs.put(report.getNode().getHost(), list);
									}
									// 处理日志配置收集
									else if (channel.equals(Coordinator.channel_collect_push_logger)) {
										Node node = report.getNode();
										loggers.put(node.getHost()+"<br><span style='color:red;'>"+node.getRemark()+"</span>", report.getList());
									}
									// 处理静态域收集
									else if (channel.equals(Coordinator.channel_collect_push_static)) {
										Node node = report.getNode();
										List<KV> list = report.getList();
										if (node != null && list != null && !list.isEmpty()) {
											KV kv = list.get(0);
											String host = node.getHost();
											Object attach = kv.getAttach();
											if (StringUtils.isNotBlank(host) && attach != null)
												statics.put(host + " , " + attach, list);
										}
									}

								} catch (Exception ex) {
									log.info("消息处理错误" + message);
									ex.printStackTrace();
								}
							}

						},
						new String[] { Coordinator.channel_collect_push, Coordinator.channel_collect_push_logger,
								Coordinator.channel_collect_push_static });
			}
		}, "channel_collect_push monitor").start();

		heartbeat();
	}

	public void heartbeat() {
		new Timer("soa nodes monitor", true).schedule(new TimerTask() {

			@Override
			public void run() {
				List<Object> nodes = pooledJedis.getJedisProxy().hvals(Coordinator.nodesKey);
				if (!nodes.isEmpty()) {
					for (Object o : nodes) {
						Node host = (Node) o;

						Describer desc = new Describer();
						desc.setType(Type.NODE_HEARTBEAT);

						Long replay = pooledJedis.getJedisProxy().publish(Coordinator.channel_heartbeat + host.getHost(), JsonUtil.java2json(desc));
						// 目标节点没有回复心跳测试
						if (replay < 1) {
							// 删除节点服务存根信息
							stubs.put(host.getHost(), new ArrayList<ServiceStub>());
						}
					}
				}
			}
		}, 1000, 1000);
	}

}
