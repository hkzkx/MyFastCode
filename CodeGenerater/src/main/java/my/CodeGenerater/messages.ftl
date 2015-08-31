<#list messages.dto.fields as field>
	<#if (field.nullable == "NO" && field.defaultValue??)>
${messages.dto.className?lower_case}.${field.name?lower_case}.notnull=字段不能为空
	</#if>
</#list>