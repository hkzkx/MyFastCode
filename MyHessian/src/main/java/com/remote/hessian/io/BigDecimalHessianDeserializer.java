package com.remote.hessian.io;

import java.io.IOException;
import java.math.BigDecimal;

import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.AbstractStringValueDeserializer;

public class BigDecimalHessianDeserializer extends AbstractStringValueDeserializer {

	@Override
	protected Object create(String value) throws IOException {
		 BigDecimal  bigDecimal = new  BigDecimal(value);
		return bigDecimal;
	}


	@Override
	public Object readObject(AbstractHessianInput in) throws IOException {
		Object obj = in.readObject();
		
		return obj;
	}

	@Override
	public Object readObject(AbstractHessianInput in, String[] fieldNames)
			throws IOException {
		return super.readObject(in, fieldNames);
	}

}
