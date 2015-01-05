
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${mapper.dao.package_}.${mapper.dao.className}">
	<resultMap type="BaseResultMap" id="${mapper.dao.dto.package_}.${mapper.dao.dto.className}">
	<#list mapper.dao.dto.fields as field>
		<#if field.primary>
		<id column="${field.columnName}" property="${field.name}" />
		</#if>
	</#list>
	<#list mapper.dao.dto.fields as field>
		<#if !(field.primary)>
		<result column="${field.columnName}" property="${field.name}" />
		</#if>
	</#list>
	</resultMap>
</mapper>
