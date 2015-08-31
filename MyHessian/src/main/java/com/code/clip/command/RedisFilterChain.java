package com.code.clip.command;



public class RedisFilterChain implements FilterChain {

	private Filter		filter;

	private FilterChain	next;

	public RedisFilterChain(Filter filter, FilterChain nextFilterChain) {
		this.filter = filter;
		this.next = nextFilterChain;
	}

	@Override
	public void doFilter(String channel, String message) {
		filter.doFilter(channel, message, next);
	}

	@Override
	public Context getContext() {
		return Context.getInstance();
	}
	
}
