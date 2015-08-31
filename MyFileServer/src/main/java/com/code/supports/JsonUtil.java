package com.code.supports;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * json工具类. 使用 jackson 的 json 处理库
 * 
 * @ClassName: JsonUtil
 * @Description:
 * @date 2014-5-10 上午10:35:32
 */
public class JsonUtil {

	private static ObjectMapper	mapper	= new ObjectMapper();	// can reuse,
																// share
																// globally

	/**
	 * 把 java 对象转化为 json 对象
	 */
	public static <T> String java2json(T t) {
		String json = null;
		try {
			json = mapper.writeValueAsString(t); // 把 java 对象转化为 json 对象
		} catch (JsonGenerationException ex) {
			Logger.getLogger(JsonUtil.class.getName()).log(Level.SEVERE, null, ex);
		} catch (JsonMappingException ex) {
			Logger.getLogger(JsonUtil.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(JsonUtil.class.getName()).log(Level.SEVERE, null, ex);
		}

		return json;
	}

}
