package com.code.clip.blance;

import java.util.List;
import java.util.Map;

import com.code.clip.Describer;

public interface Blance {

	Describer getNextNode(List<Describer> nodes,Map<String,Integer> statistics);
}
