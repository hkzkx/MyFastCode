package com.my.redis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

public class SerialUtil {
	private static Logger log = Log.getLogger(SerialUtil.class);

	public static byte[] encode(Object object) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		byte[] data = null;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			data = baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (oos != null) {
					oos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return data;
	}

	public static Object decode(byte[] data) {
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		ClassLoadingObjectInputStream ois = null;
		Object object = null;
		try {
			ois = new ClassLoadingObjectInputStream(bais);
			object = ois.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ois != null) {
					ois.close();
				}
			} catch (IOException e) {
				log.warn(e.getMessage());
			}
		}
		return object;
	}

	private static class ClassLoadingObjectInputStream extends ObjectInputStream {
		public ClassLoadingObjectInputStream(java.io.InputStream in) throws IOException {
			super(in);
		}

		public ClassLoadingObjectInputStream() throws IOException {
			super();
		}

		@Override
		public Class<?> resolveClass(java.io.ObjectStreamClass cl) throws IOException,
				ClassNotFoundException {
			try {
				return Class.forName(cl.getName(), false, Thread.currentThread().getContextClassLoader());
			} catch (ClassNotFoundException e) {
				return super.resolveClass(cl);
			}
		}
	}
}