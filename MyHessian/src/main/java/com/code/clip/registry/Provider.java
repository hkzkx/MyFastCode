package com.code.clip.registry;

import java.util.List;

import com.code.clip.Describer;
import com.code.clip.Node;

public interface Provider {

	public void register(Describer desc);
	public void subscribe(String... channel);
	public Node getNode();
	public List<Object> getServices();
	public void down();
	public void up();

}
