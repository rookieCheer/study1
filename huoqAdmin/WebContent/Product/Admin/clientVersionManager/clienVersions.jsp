<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/Product/images/favicon.ico" type="image/x-icon">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/artDialog4.1.7/artDialog.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/qwyUtilJS/qwyUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dropzone/dropzone.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/js/dropzone/dropzone.css">
<link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css" />		
<link href="${pageContext.request.contextPath}/Product/Admin/css/product_fabu_history.css" rel="stylesheet" type="text/css" />
<script src="${pageContext.request.contextPath}/Product/Admin/js/jquery-1.9.1.min.js"></script>
<style type="text/css">
	.rightText{
		text-align: right;
	}
	.leftText{
		text-align: left;
	}
	.divEdit {
	  width: 1200px;
	  overflow: auto;
	  min-height: 500px;
	  height: auto;
	  _height: 120px;
	  max-height: 160px;
	  cursor: text;
	  outline: none;
	  white-space: normal;
	  padding: 1px 2px 1px 2px;
	  font-family: SimSun,Verdana,sans-serif;
	  font-size: 12px;
	  line-height: 16px;
	  /*border: 1px solid black;*/
	}
	.a{
		color: blue;
		text-decoration: underline ;
	}
</style>
<script type="text/javascript">
	function updateStatus(value,title,status){
		$.ajax({
			type : "get",
			url : "${pageContext.request.contextPath}/Product/Admin/clientVersion!updateStatuById.action?versions.id="+value+"&versions.status="+status,
			/* data:"inv.productId=${product.id}", */
			success : function(data) {
				if(data.status == 'ok'){
					alert(title+"成功");
				}else if(data.status == 'err'){
					alert(title+"失败,至少要保证android版本一个启用!");
				}else{
					alert(title+"失败");
				}
					window.location.href="${pageContext.request.contextPath}/Product/Admin/clientVersion!loadClientVersion.action"
				
			}
		});
	}
</script>
<title>版本信息</title>
</head>
<body>
<div class="center">		
	<jsp:include page="/Product/Admin/common/head.jsp"/>	
	<div style="margin-bottom: 20px;width: 900px;margin: 0 auto 20px; color: red;font-size: 14px;">
		标注:
		<ul>
			<li>1 客户端类型是android的记录，强制更新时，要满足isUpdate为需要更新 version版本号大于当前版本 记录状态为启动状态 path不能为空（即必须上传当前需要更新的版本）。</li>
			<li>2 客户端类型是ios的记录，强制更新时，要满足isUpdate为需要更新 version版本号大于当前版本 记录状态为启动状态。 </li>
			<li>3 h5强制更新时，使用的也是android的记录，所以必须保证有一条android的记录为启动状态。</li>
		</ul>	
		
	</div>
	<div class="main" align="center">
		<h3>版本信息</h3>
		<a href="${pageContext.request.contextPath}/Product/Admin/clientVersion!deleteRedis.action" style="margin-left: 1400px;color: red;text-decoration: underline;"> 删除缓存 </a>
		<table border="0.5">
			<tr>
				<td width="50px" style="text-align: center;">序号</td>
				<td width="100px" style="text-align: center;">版本</td>
				<td width="100px" style="text-align: center;">客户端类型</td>
				<td width="200px" style="text-align: center;">更新功能</td>
				<td width="200px" style="text-align: center;">更新时间</td>
				<td width="200px" style="text-align: center;">path</td>
				<td width="200px" style="text-align: center;">isUpdate</td>
				<td width="200px" style="text-align: center;">当前状态</td>
				<td width="200px" style="text-align: center;">修改</td>
				<td width="200px" style="text-align: center;">操作</td>
			</tr>
		<c:forEach items="${list}"  var="item" varStatus="i">
			<tr>
				<td style="text-align: center;">${i.index+1}</td>
				<td style="text-align: center;">${item.versions}</td>
				<td style="text-align: center;">${item.clientType}</td>
				<td style="text-align: center;">${item.content}</td>
				<td style="text-align: center;">${item.insertTime}</td>
				<td style="text-align: center;">${url}${downLoadPath}${item.versions}${folder}${item.path}</td>
				<td style="text-align: center;">
					<c:if test="${item.isUpdate eq 0}">不需要更新</c:if>
					<c:if test="${item.isUpdate eq 1}">需要更新</c:if>
				</td>
				<td style="text-align: center;">
					<c:if test="${item.status eq 0}">启用</c:if>
					<c:if test="${item.status eq 1}">停用</c:if>
				
				</td>
				<td style="text-align: center;">
					<a href="${pageContext.request.contextPath}/Product/Admin/clientVersion!updateVersionsById.action?id=${item.id}" class="a">修改</a>
				</td>
				<td style="text-align: center;">
					 <c:choose>
						<c:when test="${item.status != null && item.status == '0' }"><a href="javascript:updateStatus('${item.id}','停用','${item.status}')" class="a">启用</a></c:when>
						<c:otherwise><a href="javascript:updateStatus('${item.id}','启用','${item.status}')" class="a">停用</a></c:otherwise>
					</c:choose>
				</td>
			</tr>
		</c:forEach>

		</table>
	</div>
</div>


</body>

<script type="text/javascript">
		$(function(){
			var isDelete = "${isDelete}";
			if(isDelete == "ok"){
				alert("删除缓存成功！");
				window.location.href="${pageContext.request.contextPath}/Product/Admin/clientVersion!loadClientVersion.action";
			}
			
		});
		</script>
</html>