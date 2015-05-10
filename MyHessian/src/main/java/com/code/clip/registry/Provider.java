package com.code.clip.registry;

import com.code.clip.Describer;

public interface Provider {

	public void register(Describer desc);
	public void subscribe(String channel);
	public void down();
	public void up();

}
