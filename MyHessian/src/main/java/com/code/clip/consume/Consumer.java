package com.code.clip.consume;

import com.code.clip.Describer;

public interface Consumer {

	public void subscribe(String... host);

	public void publish(Describer desc, String channel);
}
