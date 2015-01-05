package com.basedao.common;

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

public class DateTypeHandler implements TypeHandler<Object> {

	@Override
	public void setParameter(PreparedStatement ps, int i, Object parameter,
			JdbcType jdbcType) throws SQLException {
		MinuteDate date = (MinuteDate) parameter;
		Date time = new Date(date.getTime());
		ps.setDate(i, time);
	}

	@Override
	public Object getResult(ResultSet rs, String columnName)
			throws SQLException {
		Date b = rs.getDate(columnName);
		MinuteDate date = new MinuteDate();
		date.setTime(b.getTime());
		return date;
	}

	@Override
	public Object getResult(ResultSet rs, int columnIndex) throws SQLException {
		Date b = rs.getDate(columnIndex);
		MinuteDate date = new MinuteDate();
		date.setTime(b.getTime());
		return date;
	}

	@Override
	public Object getResult(CallableStatement cs, int columnIndex)
			throws SQLException {
		Date b = cs.getDate(columnIndex);
		MinuteDate date = new MinuteDate();
		date.setTime(b.getTime());
		return date;
	}

}
