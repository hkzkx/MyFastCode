package ${dto.package_};

import javax.validation.constraints.NotNull;

import com.mmb.annotation.Id;
import java.io.Serializable;
<#if dto.hasDate>
import java.util.Date;
</#if>
<#if dto.hasBigDecimal>
import java.math.BigDecimal;
</#if>

${dto.comment}
public class ${dto.className} implements Serializable {

    private static final long serialVersionUID = ${dto.serialNum}L;

<#list dto.fields as field>
	${field.comment}
	<#if (field.nullable == "NO" && field.defaultValue??)>
	@NotNull(message="${dto.className?lower_case}.${field.name?lower_case}.notnull")
	</#if>
	<#switch field.type>
	<#case "Long">
	<#if field.primary>
	@Id
	</#if>
	private Long ${field.name};
	<#break>
	
	<#case "String">
	<#if field.primary>
	@Id
	</#if>
	private String ${field.name};
	<#break>
	
	<#case "BigDecimal">
	<#if field.primary>
	@Id
	</#if>
	private BigDecimal ${field.name};
	<#break>
	
	<#case "Date">
	<#if field.primary>
	@Id
	</#if>
	private Date ${field.name};
	<#break>
	
	<#case "Integer">
	<#if field.primary>
	@Id
	</#if>
	private Integer ${field.name};
	<#break>
	
	<#case "Float">
	<#if field.primary>
	@Id
	</#if>
	private Float ${field.name};
	<#break>
	
	<#case "Double">
	<#if field.primary>
	@Id
	</#if>
	private Double ${field.name};
	<#break>
	
	<#default>
	</#switch>
</#list>

	
<#list dto.fields as field>
	${field.setter}
	${field.getter}
</#list>
}