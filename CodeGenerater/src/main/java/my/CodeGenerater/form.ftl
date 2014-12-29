<!doctype html>
<html>
<head>
	<meta charset="utf-8"/>
	<meta name="viewport" content="width=device-width"/>
	<title></title>
</head>

<body class="page-index ">
	<form action="/${form.module?lower_case}/${form.dto.className?lower_case}/save.htm" method="post">
		<#list form.dto.fields as field>
			${field.name}：<input name="${field.name}" id="${field.name}"/><br/>
		</#list>
		<input type="submit" value="保存"/>
	</form
</body>
</html>