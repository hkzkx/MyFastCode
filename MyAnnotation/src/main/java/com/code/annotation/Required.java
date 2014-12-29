package com.code.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @ClassName: Required
 * @Description: 用于HTTP POST数据到服务端时，校验数据对象的必填字段<br/>
 *               不进行数据规则判断
 * 
 * @date 2014年4月2日 下午6:18:55
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Required {

}
