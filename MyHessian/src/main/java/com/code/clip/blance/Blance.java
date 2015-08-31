package com.code.clip.blance;

import java.util.List;
import java.util.Map;

import com.code.clip.ServiceStub;

public interface Blance {

	ServiceStub getNextNode(List<ServiceStub> stubs,Map<String,Integer> statistics);
}
