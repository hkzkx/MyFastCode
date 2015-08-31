<#assign ctxPath="${rc.contextPath}" />
<#macro site
	title="" 
	js=[] 
	css=[] 
	keywords=""
	description=""
>
<!DOCTYPE html>
<html>
	<head>
		<link rel="icon" href="https://dn-miaomiaobank.qbox.me/favicon.ico" type="image/x-icon" /> 
		<meta http-equiv="Content-type" content="text/html; charset=utf-8">
		<meta name="viewport" content="width=device-width"/>
		<meta name="keywords" content="${keywords}">
		<meta name="description" content="${description}">
		<meta http-equiv="Access-Control-Allow-Origin" content="*">
		<title>${title}</title>
		<script src="../resources/js/jquery-1.11.1.js"></script>
	</head>
	<body>
		<a href="${ctxPath}/node/list.htm">服务管理</a>
		<a href="${ctxPath}/logger/list.htm">日志级别管理</a>
		<a href="${ctxPath}/static/list.htm">静态资源切换管理</a>
		<hr>
		<#nested>
	</body>
	
</html>
</#macro>