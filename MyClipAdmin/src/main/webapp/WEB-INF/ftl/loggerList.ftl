<#include "/inc/template.ftl">

<@site 
	title="喵喵客 - 服务协调管理" 
	js=[]
	css=[
	]
	keywords=""
	description=""
>
	
	
	
	<table width="100%" align="center" border="1">
		<caption><h1>新增日志配置</h1></caption>
		<tr>
			<td>包</td>
			<td width="40%"><input id="logger" name="logger" size="70"/></td>
			<td>工程</td>
			<td>
				<select id="attach" name="attach">
					<option value="">请选择工程</option>
					<option value="web">web</option>
					<option value="oms">oms</option>
					<option value="market">market</option>
					<option value="wap">wap</option>
					<option value="wechat">wechat</option>
					<option value="timer">timer</option>
				</select>
			</td>
			<td>级别</td>
			<td>
				<select id="level" name="level">
					<option value="">请选择日志级别</option>
					<option value="debug">debug</option>
					<option value="info">info</option>
					<option value="warn">warn</option>
					<option value="error">error</option>
				</select>
			</td>
		</tr>
		<tr>
			<td colspan="6" align="center">
				<input id="addLogger" type="button" value="增加"/>
			</td>
		</tr>
	</table>
	<table width="100%" align="center" border="1">
		<caption><h1>日志配置管理</h1></caption>
		<tr>
			<th>节点</th><th>日志设置</th><th>日志级别</th><th>操作</th>
		</tr>
		<#list loggers?keys as host>
			<#list loggers[host] as loggerList>
				<tr>
					<td>${host}</td>
					<td>
						<#assign loggerOne="">
						<#assign attachOne="">
						<#list loggerList as logger>
							<#if logger_index == 0 >
								<#assign loggerOne="${logger.k}">
								<#assign attachOne="${logger.attach}">
							</#if>
							${logger.k}<br>
						</#list>
					</td>
					<td>
						<#list loggerList as logger>
							${logger.v}<br>
						</#list>
					</td>
					<td width="5%">
						<select class="logger" name="logger" logger="${loggerOne}" attach="${attachOne}">
							<option value="">请选择</option>
							<option value="debug">debug</option>
							<option value="info">info</option>
							<option value="warn">warn</option>
							<option value="error">error</option>
						</select>
					</td>
				</tr>
			</#list>
		</#list>
	</table>
	
	
	<script>
		jQuery(".logger").change(function(){
			var logger = $(this).attr('logger');
			var level = $(this).val();
			jQuery.ajax({
				type: "POST",
				url: "/logger/set.htm",
				data: "logger="+logger+"&level="+level+"&attach="+attach,
				success: function(msg){
					if(msg == "ok"){
						location.reload();
					}else{
						alert(msg);
					}
				}
			});
		});
		
		
		jQuery('#addLogger').click(function(){
			var logger = jQuery('#logger').val();
			var level = jQuery('#level').val();
			var attach = jQuery('#attach').val();
			jQuery.ajax({
				type: "POST",
				url: "/logger/set.htm",
				data: "logger="+logger+"&level="+level+"&attach="+attach,
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