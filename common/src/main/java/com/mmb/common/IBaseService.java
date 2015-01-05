package com.mmb.common;

/**
 * 接口公用方法
 * 
 */
import java.util.List;
import java.util.Map;
public interface IBaseService<T> {
	/**
	 * 插入数据
	 * @param obj
	 */
	 T insert(T obj); 
	 /**
	  * 根据主键更新数据中不为空的属
	  * @param obj
	  * @return
	  */
	 int updateNotNullById(T obj);
	 
	 /**
	  * 根据主键更新数据的全部属
	  * @param obj
	  * @return
	  */
	 int updateById(T obj);
	 
	 /**
	  * 根据主键删除数据
	  * @param id
	  * @return
	  */
	 int deleteById(Number id);
	 
	 /**
	  * 根据对象中不为空的条件删除数据
	  * @param id
	  * @return
	  */
	 int deleteByObject(T obj);
	 
	 /**
	  * 根据参数中不为空的条件删除数据,key对应dto中的属性
	  * @param id
	  * @return
	  */
	 int deleteByParamNotEmpty(Map<String,Object> param);
	 
	 /**
	  * 根据参数中条件删除数据,key对应dto中的属性
	  * @param id
	  * @return
	  */
	 int deleteByParam(Map<String,Object> param);
	 
	 /**
	  * 根据主键查询数据
	  * @param id
	  * @return
	  */
	 T queryById(Number id);
	 
	 
	 /**
	  * 根据对象中不为空的属性查询列表
	  * @param id
	  * @return
	  */
	 List<T> queryByObject(T obj);
	 
	 /**
	  * 返回单个对象
	  * @param obj
	  * @return
	  */
	 T queryUniqueByObject(T obj);
	 /**
	  * 返回单个对象
	  * @param obj
	  * @return
	  */
	 T queryUniqueByParams(Map<String,Object> params);
	 /**
	  * 根据参数中不为空的属性查询列表，key对应dto中的属性
	  * @param id
	  * @return
	  */
	 List<T> queryByParamNotEmpty(Map<String,Object> params);
	 /**
	  * 根据参数查询列表，key对应dto中的属性
	  * @param id
	  * @return
	  */
	 List<T> queryByParam(Map<String,Object> params);
	 
	 /**
	  * 根据对象中不为空的属性查询总数
	  * @param id
	  * @return
	  */
	 Integer queryByObjectCount(T obj);
	 /**
	  * 根据参数中不为空的属性查询总数，key对应dto中的属性
	  * @param id
	  * @return
	  */
	 Integer queryByParamNotEmptyCount(Map<String,Object> params);
	 /**
	  * 根据参数查询总数，key对应dto中的属性
	  * @param id
	  * @return
	  */
	 Integer queryByParamCount(Map<String,Object> params);
	 
	 /**
	  * 根据对象中不为空的属性查询列表
	  * @param id
	  * @return
	  */
	 RemotePage queryPageByObject(T obj,PageInfo info);
	 /**
	  * 根据参数中不为空的属性查询列表，key对应dto中的属性
	  * @param id
	  * @return
	  */
	 RemotePage queryPageByParamNotEmpty(Map<String,Object> params,PageInfo info);
	 /**
	  * 根据参数查询列表，key对应dto中的属性
	  * @param id
	  * @return
	  */
	 RemotePage queryPageByParam(Map<String,Object> params,PageInfo info);
}
