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

//	@Override
//	protected void writeDefinition20(Class<?> cl, AbstractHessianOutput out)
//			throws IOException {
//		out.writeInt(72);//H
//		
//	}

//	@Override
//	protected void writeInstance(Object obj, AbstractHessianOutput out)
//			throws IOException {
//		out.writeString(obj.toString());
//	}

//	public static void main(String[] args) {
//		byte[] bs = "H".getBytes();
//		System.out.println();
//	}
	

}
