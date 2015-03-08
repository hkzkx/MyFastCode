package com.basedao.interceptor;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.log4j.Logger;

import com.basedao.common.BaseDao;
import com.basedao.utils.DtoUtil;
import com.code.annotation.Id;
import com.code.annotation.RegisterDto;

@Intercepts({
		@Signature(
				type = Executor.class, method = "update", 
				args = { MappedStatement.class,Object.class }),
		@Signature(
				type = Executor.class, method = "query", 
				args = { MappedStatement.class,Object.class, RowBounds.class, ResultHandler.class }) 
		})
public class BaseDaoInterceptor implements Interceptor {
	
	private static final Logger LOGGER = Logger.getLogger(BaseDaoInterceptor.class);
	private static Method[] methods = BaseDao.class.getMethods();
	private static Map<String, Class<?>> classMap = new ConcurrentHashMap<String, Class<?>>();
	private final static Lock lock = new ReentrantLock();

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		try {
			MappedStatement statement = (MappedStatement) invocation.getArgs()[0];
			Object parameter = (Object) invocation.getArgs()[1];
			String sqlId = statement.getId();
			String className = sqlId.substring(0, sqlId.lastIndexOf("."));
			Class<?> currentClass = getClass(className);
			String methodId = getMethodid(sqlId);

			if (!isBaseMethod(currentClass, methodId)) {
				return invocation.proceed();
			}
			String[] keys = statement.getKeyProperties();
			String[] keyColumns = statement.getKeyColumns();

			setObject(statement, parameter, keys, keyColumns);
			setTableName(parameter, invocation, currentClass, methodId);
			setResultClass(statement, currentClass, methodId);
			setClass(parameter, currentClass);

			Object obj = invocation.proceed();
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			return invocation.proceed();
		}
	}

	@Override
	public Object plugin(Object target) {
		if (target instanceof Executor) {
			return Plugin.wrap(target, this);
		} else {
			return target;
		}
	}

	@Override
	public void setProperties(Properties properties) {

	}

	private void setObject(final MappedStatement statement, final Object parameter,
			final String[] keys, String[] keyColumns) {
		if (statement.getSqlCommandType().equals(SqlCommandType.INSERT)) {
			keys[0] = DtoUtil.id((Serializable) parameter);
			if (keyColumns == null) {
				keyColumns = new String[1];
			}
			keyColumns[0] = keys[0].replaceAll("([A-Z])", "_$1").toLowerCase();
		}
	}

	private void setTableName(Object parameter, Invocation invocation, Class<?> currentClass,
			String methodId) throws InstantiationException, IllegalAccessException {
		if (methodId.equals("queryById") || methodId.equals("deleteById")) {
			Annotation a = currentClass.getAnnotation(RegisterDto.class);
			if (a == null) {
				LOGGER.error(currentClass.getName()
						+ " not find annotation RegisterDto,please set it");
			}
			Class<?> paramClass = ((RegisterDto) a).value();
			Object param = paramClass.newInstance();
			Field[] fields = paramClass.getDeclaredFields();
			Boolean hasId = Boolean.FALSE;
			for (Field field : fields) {
				Id id = field.getAnnotation(Id.class);
				if (null != id) {
					hasId = Boolean.TRUE;
					field.setAccessible(true);
					if (field.getType().getSimpleName().equals(Long.class.getSimpleName())) {
						if (parameter instanceof Integer) {
							field.set(param, ((Integer) parameter).longValue());
						} else {
							field.set(param, parameter);
						}
					} else {
						if (parameter instanceof Long) {
							field.set(param, ((Long) parameter).intValue());
						} else {
							field.set(param, parameter);
						}
					}
					invocation.getArgs()[1] = param;
					parameter = param;
					break;					//新加
				}
			}
			if (!hasId) {
				LOGGER.error(currentClass.getName() + " RegisterDto is not @Id");
			}
		}
	}

	private void setResultClass(MappedStatement statement, Class<?> currentClass, String methodId)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException, ClassNotFoundException {
		if (methodId.matches("^query.*") && !methodId.matches(".*Count$")) {
			List<ResultMap> resultMaps = statement.getResultMaps();
			ResultMap resultMap = resultMaps.get(0);
			Class<?> clazz = resultMap.getType();
			Annotation a = currentClass.getAnnotation(RegisterDto.class);
			if (a == null) {
				LOGGER.error(currentClass.getName()
						+ " not find annotation RegisterDto,please set it");
			}
			clazz = ((RegisterDto) a).value();

			Field rf = resultMap.getClass().getDeclaredField("type");
			rf.setAccessible(Boolean.TRUE);
			rf.set(resultMap, clazz);
		} else if (methodId.matches(".*Count$")) {
			ResultMap resultMap = statement.getResultMaps().get(0);
			Field rf = resultMap.getClass().getDeclaredField("type");
			rf.setAccessible(Boolean.TRUE);
			rf.set(resultMap, Integer.class);
		}
	}

	@SuppressWarnings("unchecked")
	private void setClass(Object parameter, Class<?> currentClass) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		if (parameter instanceof Map) {
			Annotation a = currentClass.getAnnotation(RegisterDto.class);
			if (a == null) {
				LOGGER.error(currentClass.getName()
						+ " not find annotation RegisterDto,please set it");
			}
			Class<?> clazz = ((RegisterDto) a).value();
			((Map) parameter).put(DtoUtil.SPACE_TABLE_NAME, Class.forName(clazz.getName())
					.newInstance());
		}
	}

	private boolean isBaseMethod(Class<?> currentClass, String methodId)
			throws ClassNotFoundException {

		Boolean isBaseMethod = Boolean.FALSE;
		for (Method method : methods) {
			String methodName = method.getName();
			if (methodId.equals(methodName)) {
				isBaseMethod = Boolean.TRUE;
			}
		}

		if (!isBaseMethod) {
			return Boolean.FALSE;
		}

		isBaseMethod = Boolean.FALSE;
		for (Class<?> clazz : currentClass.getInterfaces()) {
			if (clazz.equals(BaseDao.class)) {
				isBaseMethod = Boolean.TRUE;
			}
		}
		return isBaseMethod;
	}

	private String getMethodid(String methodFullName) {
		String[] strs = methodFullName.split("\\.");
		String methodId = strs[strs.length - 1];
		return methodId;
	}

	private Class<?> getClass(String className) throws ClassNotFoundException {

		Class<?> currentClass = classMap.get(className);
		if(currentClass!=null)
			return currentClass;

		currentClass = Class.forName(className);
		for (Class<?> clazz : currentClass.getInterfaces()) {
			if (clazz.equals(BaseDao.class)) {
				if (!classMap.containsKey(className)) {
					lock.lock();
					classMap.put(className, currentClass);
					lock.unlock();
				}
				break;
			}
		}
		
		return currentClass;
	}
}
