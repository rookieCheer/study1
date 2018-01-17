<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/zlqEl.tld" prefix="myel"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css" />
<link href="${pageContext.request.contextPath}/Product/Admin/css/product.css" rel="stylesheet" type="text/css" />
<script src="${pageContext.request.contextPath}/Product/Admin/js/jquery-1.9.1.min.js"></script>
<script src="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.standalone.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.css" type="text/css">
<title>用户零钱包转出异常记录</title>
<style type="text/css">
.a{
		color: blue;
		text-decoration: underline ;
	}
</style>
</head>
<body>
<div class="center">			
	<jsp:include page="/Product/Admin/common/head.jsp"/>
	<div class="main" align="center">
	<h1 style="text-align: center;">零钱包转出异常记录</h1>
		<form action="${pageContext.request.contextPath}/Product/Admin/usersPurse!loadUsersRollOutException.action">
		<label>用户名:<input type="text" name="username" id="username" value="${username}" maxlength="11">
		<span>插入时间:</span> <input id="insertTime" name="insertTime" type="text" value="${insertTime}">
		<input type="submit" value="search"></label>&nbsp;&nbsp;
		</form>
		<table style="width:100%;margin-top: 20px;text-align: center;"  border="1px;">
		<tr>
			<td>序号</td>
			<td>用户名</td>
			<td>转出金额</td>
			<td>剩余金额</td>
			<td>插入时间</td>
			<td>状态</td>
			<td>类型</td>
		</tr>
		<c:forEach items="${pageUtil.list}" var="item" varStatus="i">
			<tr>
				<td>${i.count + (pageUtil.currentPage-1)*pageUtil.pageSize}</td>
				<td><a class="a"  href="${pageContext.request.contextPath}/Product/Admin/userStat!loadUserInfo.action?username=${myel:jieMiUsername(item[0])}">${myel:jieMiUsername(item[0])}</a></td> 
				<td><fmt:formatNumber value="${item[1]}" pattern="#,##0.##"/></td>
				<td><fmt:formatNumber value="${item[2]}" pattern="#,##0.##"/></td>
				<td><fmt:formatDate value="${item[3]}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td><%-- ${item[4]} --%>
				<c:if test="${item[4] eq 0}">成功</c:if>
				<c:if test="${item[4] eq 1}">失败</c:if>
				</td>
				<td><%-- ${item[5]} --%>
				<c:if test="${item[5] eq 0}">转出</c:if>
				</td>
			</tr>
		</c:forEach>
	</table>

	<c:choose>
			<c:when test="${pageUtil.list ne '[]' &&  pageUtil.list ne '' && pageUtil.list ne null}">
			<jsp:include page="/Product/page.jsp" /></c:when>
			<c:otherwise>
				<div style="text-align: center;margin-top: 15px;">
			  		<img src="../images/no_record.png" />
			  	</div>
			</c:otherwise>
		</c:choose>
				</div>
</div>
</body>
<script type="text/javascript">
	var k4 = new Kalendae.Input("insertTime", {
		attachTo:document.body,
		months:2,//多少个月显示出来,即看到多少个日历
		mode:'range'
		/* selected:[Kalendae.moment().subtract({d:7}), Kalendae.moment().add({d:0})] */
	});

</script>
</html>