<#include "/inc/template.ftl">

<@site 
	title="喵喵客 - 服务协调管理" 
	js=[]
	css=[
	]
	keywords=""
	description=""
>
	
	<table width="70%" align="center">
		<caption><h1>节点管理</h1></caption>
		<tr>
			<td width='30%'><b>节点</b></td><td width='50%'><b>状态</b></td><td width='20%'><b>操作</b></td>
			<#list nodes as node>
			<tr>
				<td>${node.host}</td>
				<td>
					<#if node.status ==2>
						节点存活，但暂时不接受新的请求或暂时不对外服务
					<#else>
						正常服务
					</#if>
				</td>
				<td>
					<#if node.status ==2>
						<a href="javascript:void(null);" data="${node.host}" class="start">启用</a>
					<#else>
						<a href="javascript:void(null);" data="${node.host}" class="pause">暂停</a>
					</#if>
				 </td>
			</tr>
			</#list>
		</tr>
	</table>
	
	<br>
	<br>
	<hr/>
	<br>
	<br>
	<input type="button" value="刷新服务列表" id="refresh"/>
	<table width="100%" align="center">
		<caption><h1>服务管理</h1></caption>
		<tr>
			<th>服务名称</th><th>远程地址</th><th>状态</th><th>操作</th>
		</tr>
		<#list stubs?keys as host>
			<#list stubs[host] as stub>
				<tr>
					<td>${stub.serviceName}</td>
					<td>${stub.node}${stub.serviceUri}</td>
					<td width="5%">${stub.status}</td>
					<td width="9%">
						<a href="javascript:void(null);">启用</a>
						<a href="javascript:void(null);">暂停</a>
					</td>
				</tr>
			</#list>
		</#list>
	</table>
	
	
	<script>
		jQuery(".start").click(function(){
			var node = $(this).attr('data');
			jQuery.ajax({
				type: "POST",
				url: "/node/start.htm",
				data: "host="+node,
				success: function(msg){
					if(msg == "ok"){
						location.reload();
					}else{
						alert(msg);
					}
				}
			});
		});
		jQuery(".pause").click(function(){
			var node = $(this).attr('data');
			jQuery.ajax({
				type: "POST",
				url: "/node/pause.htm",
				data: "host="+node,
				success: function(msg){
					if(msg == "ok"){
						location.reload();
					}else{
						alert(msg);
					}
				}
			});
		});
		
		jQuery("#refresh").click(function(){
			jQuery.ajax({
				type: "POST",
				url: "/services/refresh.htm",
				success: function(msg){
					if(msg == "ok"){
						location.reload();
					}else{
						alert(msg);
					}
				}
			});
		});
		
	</script>
</@site>