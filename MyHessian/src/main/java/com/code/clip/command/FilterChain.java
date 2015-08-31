package com.code.clip.command;


public interface FilterChain {

	public void doFilter(String channel, String message);
	public Context getContext();
}
