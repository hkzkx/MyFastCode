<#list messages.dto.fields as field>
	<#if (field.nullable == "NO" && field.defaultValue??)>
${messages.dto.className?lower_case}.${field.name?lower_case}.notnull=can't be null
	</#if>
</#list>