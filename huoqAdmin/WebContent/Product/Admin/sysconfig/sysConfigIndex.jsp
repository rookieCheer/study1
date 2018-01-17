<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/zlqEl.tld" prefix="myel"%>
<!doctype html>
<html>
<meta charset="utf-8" />
<title>管理员列表</title>
<link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css" />		
<link rel="stylesheet" href="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.css" type="text/css">
<link href="${pageContext.request.contextPath}/Product/Admin/css/product_fabu_history.css" rel="stylesheet" type="text/css" />
<script src="${pageContext.request.contextPath}/Product/Admin/js/jquery-1.9.1.min.js"></script>
<script src="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.standalone.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/Product/js/alert.js"></script>
<script src="${pageContext.request.contextPath}/js/artDialog4.1.7/jquery.artDialog.js?skin=blue"></script>
<style type="text/css">
.a{
		color: blue;
		text-decoration: underline ;
	}
</style>
<script type="text/javascript">
 
 //禁用或启用
 function toChange(obj){
	 if(confirm("你确定要修改当前状态?")){
	     var id="button_"+obj;
		 var url="${pageContext.request.contextPath}/Product/Admin/sysConfig!delete.action?id="+obj;
			$.post(url,null,function(data){
				 if("1"==data){	
					 alert("删除成功")
				 }else{			 
					alert("删除失败");
				 }
				 window.location.href="${pageContext.request.contextPath}/Product/Admin/sysConfig!getSysConfig.action";
			 }); 
	 }else{
		 window.location.Reload();
	 }

 }
</script>	 
</head>
	<body>
		<div class="center">		
		<jsp:include page="/Product/Admin/common/head.jsp"/>	
			<div class="main" align="center">
			<a href="${pageContext.request.contextPath}/Product/Admin/sysConfig!reflushRedis.action" style="margin-left: 1400px;color: red;text-decoration: underline;"> 刷新 </a>
			<a href="${pageContext.request.contextPath}/Product/Admin/sysConfig!add.action" style="margin-left: 1400px;color: red;text-decoration: underline;"> 添加 </a>
			<h3 align="center">配置管理</h3> 
				<div class="table">
					<table cellspacing="0" cellpadding="0" style="  width: 100%;">
						<tbody>
						<tr>
							<td style="width: 366px;">编号</td>
							<td style="width: 366px;">编码</td>
							<td style="width: 366px;">名称</td>
                            <td style="width: 366px;">描述</td>
                            <td style="width: 58px;">类型</td>							
							<td style="width: 366px;">状态</td>
							<!--  <td style="width: 366px;">是否删除</td>-->
							<td style="width: 366px;">创建人</td>
							<td style="width: 366px;">添加时间</td>
							<td style="width: 366px;">更新人</td>
							<td style="width: 366px;">更新时间</td>
							<td style="width: 366px;">操作</td>
							
						</tr>
						<c:forEach items="${list}"  var="item" varStatus="i">
						<tr>
								<td>${item.id}</td>
								<td>${item.code}</td>
								<td>${item.name}</td>
								<td>${item.note}</td>
								<td>
									<c:if test="${item.type eq '0'}">web端注册</c:if> 
									<c:if test="${item.type eq '1'}">Android移动端</c:if> 
									<c:if test="${item.type eq '2'}">IOS移动端</c:if> 
									<c:if test="${item.type eq '3'}">微信注册</c:if> 
								</td>
								<td>
									<c:if test="${item.state eq '0'}">停用</c:if> 
									<c:if test="${item.state eq '1'}">启用</c:if> 
								</td>
								<td>
									${myel:jieMiUsername(item.creator)}
								</td>
								<td><fmt:formatDate value="${item.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
								<td>
									${myel:jieMiUsername(item.modifier)}
								</td>
								<td><fmt:formatDate value="${item.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
								<td style="width: 366px;">
									<a href="${pageContext.request.contextPath}/Product/Admin/sysConfig!edit.action?id=${item.id}" style="color: red;text-decoration: underline;margin-right: 5px;" > 编辑 </a>
									<a href="" style="color: red;text-decoration: underline;" onclick="toChange(${item.id})"> 删除</a>
								</td>
								
							</tr>
						</c:forEach>
						</tbody>
					</table>
        <c:choose>
			<c:when test="${list ne '[]' &&  list ne '' && list ne null}">
			     <jsp:include page="/Product/page.jsp" /></c:when>
			<c:otherwise>
				<div style="text-align: center;margin-top: 15px;"><!-- <img src="images/lh.jpg"> -->
			  		<img src="../images/no_record.png" />
			  	</div>
			</c:otherwise>
		</c:choose>
				</div>
			</div>
		</div>
		</body>
		<script type="text/javascript">
		$(function(){
			var isReflush = "${isReflush}";
			if(isReflush == "ok"){
				window.location.href="${pageContext.request.contextPath}/Product/Admin/sysConfig!getSysConfig.action";
				alert("刷新缓存成功！");
			}
			
		});
		</script>
	
	
</html>