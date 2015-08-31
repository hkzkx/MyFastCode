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
		<caption><h1>静态域配置管理</h1></caption>
		<tr>
			<th>节点</th><th>静态域</th><th>静态域名</th><th>切换</th>
		</tr>
		<#list statics?keys as host>
			<#list statics[host] as staticList>
				<tr>
					<td>${host}</td>
					<td>
						<#assign sdOne="">
						<#assign sdAttach="">
						<#list staticList as sd>
							<#assign sdOne="${sd.k}">
							<#assign sdOneV="${sd.v}">
							<#assign sdAttach="${sd.attach}">
							
							<#if sdOne!='staticVersion'>
								${sdOne}
							<#else>
								<span style="color:red;">${sdOne}</span>
							</#if>
						</#list>
					</td>
					<td>
						<#list staticList as sd>
							${sdOneV}
						</#list>
					</td>
					<td width="5%">
						<#if sdOneV !="" && sdOne!='staticVersion'>
						<select class="sd" name="sd" sd="${sdOne}" attach="${sdAttach}">
							<option value="">请选择</option>
							<option value="http://localhost:8083">本地(http://localhost:8083)</option>
							<!-- oms 要使用http 协议，不能使用https-->
							<#if sdOne == 'libsDomain' && sdAttach=='oms'>
									<option value="http://dn-guanqun-libs.qbox.me/resources/libs">七牛(http://dn-guanqun-libs.qbox.me/resources/libs)</option>
									<option value="http://static.miaomiaobank.com/resources/libs">喵喵客(http://static.miaomiaobank.com/resources/libs)</option>
							<#elseif sdOne == 'libsDomain' && sdAttach!='oms'>
									<option value="https://dn-guanqun-libs.qbox.me/resources/libs">七牛(https://dn-guanqun-libs.qbox.me/resources/libs)</option>
									<option value="https://static.miaomiaobank.com/resources/libs">喵喵客(https://static.miaomiaobank.com/resources/libs)</option>
							<#else>
								<option value="https://dn-miaomiaobank.qbox.me">七牛(https://dn-miaomiaobank.qbox.me)</option>
								<option value="https://static.miaomiaobank.com">喵喵客(https://static.miaomiaobank.com)</option>
							</#if>
						</select>
						<#elseif sdOne=='staticVersion'>
							<input name="staticVersion" k="${sdOne}" attach="${sdAttach}" id="${sdOne}-${sdAttach}"/>
							<input type="button" value="设置" onclick="setStaticVersion('${sdOne}-${sdAttach}');"/>
						</#if>
					</td>
				</tr>
			</#list>
		</#list>
	</table>
	
	
	<script>
		jQuery(".sd").change(function(){
			var sd = $(this).attr('sd');
			var attach = $(this).attr('attach');
			var domain = $(this).val();
			jQuery.ajax({
				type: "POST",
				url: "/static/set.htm",
				data: "key="+sd+"&domain="+domain+"&attach="+attach,
				success: function(msg){
					if(msg == "ok"){
						location.reload();
					}else{
						alert(msg);
					}
				}
			});
		});
		
		function setStaticVersion(id){
			var k = jQuery('#'+id).attr('k');
			var v = jQuery('#'+id).val();
			var attach = jQuery('#'+id).attr('attach');
			jQuery.ajax({
				type: "POST",
				url: "/static/set.htm",
				data: "key="+k+"&domain="+v+"&attach="+attach,
				success: function(msg){
					if(msg == "ok"){
						location.reload();
					}else{
						alert(msg);
					}
				}
			});
		}
	</script>
</@site>