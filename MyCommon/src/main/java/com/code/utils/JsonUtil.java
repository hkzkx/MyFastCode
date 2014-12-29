package com.code.utils;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * json工具类. 使用 jackson 的 json 处理库
* @ClassName: JsonUtil 
* @Description: 
* @date 2014-5-10 上午10:35:32
 */
public class JsonUtil {

    private static ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally

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
    
    /**
     * 先把 java 对象转化为 json 对象, 然后编码返回。
     * 
     * 例如, 编码成: pmrggyloinqw4y3fnqrduztbnrzwk7i
     * 
     * 用途：
     * 在  front 或者 backend 里面的复杂表单查询，把 N 个查询条件封装到一个 dto 里面，
     * 通过调用本方法，把整个 dto 对象 转成 一个 “由小写字母和数字组成的”字符串，然后，把这个字符串作为一个参数传递给  web-core 的某个 URL 来处理。
     * 
     * 好处：简单，方便，彻底避免乱码
     */
    public static <T> String java2json_last_encode(T t) {
        String json = java2json(t); // {"canCancel":false}
        return Codec.encode(json); // pmrggyloinqw4y3fnqrduztbnrzwk7i
    }
    
    /**
     * 先把 "字符串"(例如: pmrggyloinqw4y3fnqrduztbnrzwk7i)  解码为 json 串，然后把 json 串转化为 java 对象
     * 
     * 用途：
     * web-core 接收到一个 http 请求，里面有一个参数，是一个 "字符串"(例如: pmrggyloinqw4y3fnqrduztbnrzwk7i)，
     * 通过调用本方法，直接把这个"字符串" 转成 dto 查询条件对象
     */
    public static <T> T json2java_first_decode(String json, Class<T> valueType) {
    	json = Codec.decode(json);
    	return json2java(json, valueType);
    }
    
    /**
     * 把 json 对象转化为 java 对象
     *
     * -----------------------------------------------------------------------
     * 说明(1):
     * 1. json 对象中的属性 在 java 对象中 必须存在, 否则会报错;
     * 2. java 对象中 可以 包含 json 对象中 没有的属性.
     *
     * 以上两点, 可以总结如下: json 对象中的属性列表 必须是 java 对象中的属性列表 的一个子集 (子集的特例是: 自己是自己的子集)
     * 
     * 用途: 把简单的 json 对象 转成 简单的 JavaBean 对象, 例如: Apple apple = JsonUtil.json2java(json, Apple.class);
     * -----------------------------------------------------------------------
     * 
     * 说明(2):
     * 如果 json 对象比较复杂, 可以先转成 Map 对象, 再操作 Map 对象.
     * 这种方式不太 OO, 但可以说是 "万能的" 转换方法
     * 
     * 用途: 把复杂的 json 对象 转成 Map 对象, 例如: Map<String, Object> map = JsonUtil.json2java(json, Map.class);
     * -----------------------------------------------------------------------
     *
     * 说明(3):
     * 如果 json 对象比较复杂, 也可以 转成 复杂的 JavaBean 对象.
     * 这种方式比较 OO, 但要求写一个复杂的 JavaBean 对象, 来与 json 对象 匹配
     *
     * 用途: 把复杂的 json 对象 转成 复杂的 JavaBean 对象, 例如: JsonExampleVo vo = JsonUtil.json2java(json, JsonExampleVo.class);
     *
     * 具体用法: 见 main() 方法, 以及 JsonExampleVo 类
     */
    public static <T> T json2java(String json, Class<T> valueType) {
        T obj = null;
        try {
            obj = mapper.readValue(json, valueType); // 把 json 对象转化为 java 对象
            //User user = mapper.readValue(json, User.class); // 把 json 对象转化为 java 对象
        } catch (JsonGenerationException ex) {
            Logger.getLogger(JsonUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JsonMappingException ex) {
            Logger.getLogger(JsonUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JsonUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        return obj;
    }


}
