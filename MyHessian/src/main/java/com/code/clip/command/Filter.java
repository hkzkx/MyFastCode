package com.code.clip.command;

public interface Filter {
	public void doFilter(String channel, String message, FilterChain chain);
}
