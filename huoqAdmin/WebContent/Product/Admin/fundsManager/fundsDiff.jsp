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
<title>资金管理-资金偏差记录</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.7.2.min.js"></script>
<script type="text/javascript">
	
</script>
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
		<h1 style="text-align: center;">资金偏差记录</h1>
		<input type="hidden" name="totalCount" id="totalCount" value="${totalCount}">
		<input type="hidden" name="pageCount" id="pageCount" value="${pageCount}">
		<table  style="width:100%;margin-top: 20px;text-align: center;">
		<tr>
		<td>序号</td>
		<td>用户id</td>
		<td>手机号</td>
			<td>姓名</td>
		<td>冻结资金（单位：元）</td>
		<td>投资金额（单位：元）</td>
		<td>差额（投资金额 -冻结资金 单位：元）</td>
		<td>是否异常</td>
		</tr>
		<c:forEach items="${list}" var="list" varStatus="i">
		<tr>
			<td>${i.count + (currentPage-1)*100}</td>
			<td>
			${list.usersId}
			</td>
			<td>
				<a class="a" target="_blank" href="${pageContext.request.contextPath}/Product/Admin/userStat!loadUserInfo.action?username=${list.phone}">${list.phone}</a>
			</td>
			<td>${list.realName}</td>
		<td>
			<fmt:formatNumber value="${list.freezeMoney* 0.01}" pattern="#,##0.##"/>
		</td>
			<td><fmt:formatNumber value="${list.productMoney* 0.01}" pattern="#,##0.##"/></td>
			<td><fmt:formatNumber value="${list.diff* 0.01}" pattern="#,##0.##"/></td>
			<td>${list.isDiff}</td>
		</tr>
		</c:forEach>
		</table>
		<%--<c:choose>
			<c:when test="${list ne '[]' &&  list ne '' && list ne null}"><jsp:include page="/Product/page.jsp" /></c:when>
			<c:otherwise>
				<div style="text-align: center;margin-top: 15px;"><!-- <img src="images/lh.jpg"> -->
			  		<img src="../images/no_record.png" />
			  	</div>
			</c:otherwise>
		</c:choose>--%>
		<c:choose>
			<c:when test="${list ne '[]' &&  list ne '' && list ne null}">
				<span>当前第${currentPage}页</span>
				<a onclick="Byname('1')">首页</a>
				<a onclick="Byname('${currentPage-1}')">上一页</a>
				<a onclick="Byname('${currentPage+1}')">下一页</a>
			</c:when>
			<c:otherwise>
				<div style="text-align: center;margin-top: 15px;"><!-- <img src="images/lh.jpg"> -->
					<img src="../images/no_record.png" />
				</div>
			</c:otherwise>
		</c:choose>
	</div>
</div> 
<script type="text/javascript">
	function Byname(currentPage){
	   /* var pageCount = $("#pageCount").val();
	    var totalCount = $("#totalCount").val();
	    if ( currentPage > pageCount){
	        alert(currentPage);
			return false;
		}*/
	   if (currentPage <=0 ){
	       return false;
	   }
		window.location.href="${pageContext.request.contextPath}/Product/Admin/userFundsDiff!listFundDiff.action?currentPage="+currentPage;
	}
</script>
</body>
</html>	