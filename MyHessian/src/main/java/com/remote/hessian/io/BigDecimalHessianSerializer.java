package com.remote.hessian.io;

import java.io.IOException;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;

public class BigDecimalHessianSerializer extends AbstractSerializer {

	@Override
	public void writeObject(Object obj, AbstractHessianOutput out)
			throws IOException {
		out.writeString(obj.toString());
	}
}
