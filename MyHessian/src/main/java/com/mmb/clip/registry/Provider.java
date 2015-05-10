package com.mmb.clip.registry;

import com.mmb.clip.Describer;

public interface Provider {

	public void register(Describer desc);
	public void subscribe(String channel);
	public void down();
	public void up();

}
