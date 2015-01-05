package com.basedao.interceptor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.ibatis.executor.parameter.DefaultParameterHandler;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;

import com.mmb.common.PageInfo;

/*
 * 物理分页：在StatementHandler执行sql之前进行拦截，改写Sql
 * 两种使用模式：
 * 人工模式：
 *   要求：
 *     需要人工计算记录条数，即单独写个计算记录条数的方法。然后把记录条数设置给YnPageInfo。
 *     Dao接口要，加上"RowBounds pageInfo"参数
 *   参见：
 *     DictionaryService的getListByParentCode方法
 *   建议：
 *     用该模式，性能高，适合各种复杂查询。
 * 自动模式：
 *    要求：
 *     不需要人工计算记录条数。
 *     Dao接口用HashMap，把YnPageInfo设置进去。
 *   参见：
 *     ContentService的getList方法
 *   建议：
 *     用该模式，性能低。简单的查询比较适合。特别复杂的查询，如嵌套子查询里进行分页，慎用。
 */

@Intercepts({@Signature(type=StatementHandler.class, method="prepare", args={Connection.class})})
public class PageInterceptor implements Interceptor {	
	private static String databasetype;
	Logger logger = Logger.getLogger("YnPageInterceptor");
	public Object intercept(Invocation invocation) throws Throwable {
		StatementHandler statementHandler = (StatementHandler)invocation.getTarget();
		
		MetaObject metaStatement = MetaObject.forObject(statementHandler); //获取meta元数据
		while (metaStatement.hasGetter("h")) {
			Object object = metaStatement.getValue("h");
			metaStatement = MetaObject.forObject(object);
		}
		while (metaStatement.hasGetter("target")) {
			Object object = metaStatement.getValue("target");
			metaStatement = MetaObject.forObject(object);
		}
		
		if(!databasetype.equals("mysql")) { //目前只支持mysql
			return invocation.proceed();
		}		
				
		BoundSql boundSql = (BoundSql)metaStatement.getValue("delegate.boundSql");
		String oldSql = boundSql.getSql(); //老的sql
		String newSql = null;
		RowBounds rowBounds = (RowBounds)metaStatement.getValue("delegate.rowBounds");
		if(rowBounds == RowBounds.DEFAULT) { //没有分页信息RowBounds
			Object parameterObject = boundSql.getParameterObject();
			if(parameterObject == null || !metaStatement.hasGetter("delegate.boundSql.parameterObject.pageInfo")) { //无分页参数YnPageInfo
				return invocation.proceed();
			}
			PageInfo pageInfo = (PageInfo)metaStatement.getValue("delegate.boundSql.parameterObject.pageInfo");
			if(pageInfo == null) {
				return invocation.proceed();
			}
			
			if(pageInfo.getRecordCount() == null) {
				MappedStatement mappedStatement = (MappedStatement)metaStatement.getValue("delegate.mappedStatement");
				Configuration config = (Configuration)metaStatement.getValue("delegate.configuration");
				
				/*
				 * 下面两行是获取MyBatis事务内正在用的连接，事务结束后，连接自动关闭，
				 * 在这里无须手动关闭连接
				 */
				SqlSession session = new DefaultSqlSessionFactory(config).openSession();
				Connection connection = session.getConnection();
				
				if(!setRecordCount(connection, mappedStatement, boundSql, pageInfo)) {
					Object object = invocation.proceed();
					return object;
				}else{
				}
			}
			
//			rowBounds = pageInfo.getPageInfo();
		}
	
		newSql = oldSql + " limit " + rowBounds.getOffset() + ", " + rowBounds.getLimit();
		metaStatement.setValue("delegate.boundSql.sql", newSql); //设置新sql
		metaStatement.setValue("delegate.rowBounds.offset", RowBounds.NO_ROW_OFFSET); //取消原有的内存分页
		metaStatement.setValue("delegate.rowBounds.limit", RowBounds.NO_ROW_LIMIT);

		return invocation.proceed();
	}
	
	public Object plugin(Object target) {
  		if (target instanceof StatementHandler) {
  			return Plugin.wrap(target, this);
  		} else {
  			return target;
  		}
	}

	public void setProperties(Properties properties) {
		databasetype = properties.getProperty("databasetype");
	}
	
	private boolean setRecordCount(Connection connection, MappedStatement mappedStatement, BoundSql boundSql, PageInfo pageInfo) {
		String cntSql = "SELECT COUNT(0) FROM (" + boundSql.getSql() + ") recordCount";
		BoundSql cntBndSql = new BoundSql(mappedStatement.getConfiguration(), cntSql, boundSql.getParameterMappings(), boundSql.getParameterObject());

		boolean ok = true;
		PreparedStatement cntStmt = null;
		ResultSet rs = null;
		try {
			cntStmt = connection.prepareStatement(cntSql);
			ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, boundSql.getParameterObject(), cntBndSql);
			parameterHandler.setParameters(cntStmt); //对SQL绑定参数设值
			
			rs = cntStmt.executeQuery();
			int recordCount = 0;
			if(rs.next()) {
				recordCount = rs.getInt(1);
			}
			pageInfo.setRecordCount(recordCount);
		} catch (SQLException e) {
			ok = false;
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if(cntStmt != null) {
				try {
					cntStmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return ok;
	}
}
