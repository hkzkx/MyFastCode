package com.mmb.clip.consume;

import com.mmb.clip.Describer;

public interface Consumer {

	public void subscribe(String... host);

	public void publish(Describer desc, String channel);
}
