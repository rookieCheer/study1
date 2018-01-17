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
<title>新华金典理财-新闻列表</title>
</head>
<body>
<div class="center">		
	<jsp:include page="/Product/Admin/common/head.jsp"/>	
	<div class="main" align="center">
		<h3>新闻</h3>
		<table border="1"  style="text-align: center;table-layout:fixed;width: 1500px;word-break:break-all; word-wrap:break-all;">
			<tr>
				<td style="width: 50px;">序号</td>
				<td style="width: 200px;">新闻ID</td>
				<td style="width: 200px;">新闻标题</td>
				<td style="width: 200px;">新闻链接</td>
				<td style="width: 500px;">新闻描述</td>
				<td style="width: 100px;">状态</td>
				<td style="width: 50px;">操作</td>
			</tr>
		<c:forEach items="${list}"  var="item" varStatus="i">
			<tr>
				<td >${i.index+1}</td>
				<td >${item.id}</td>
				<td >${item.newsTitle}</td>
				<td >${item.httpUrl}</td>
				<td >${item.note}</td>
				<td >
					<c:choose>
						<c:when test="${item.status eq 0}">
							可用
						</c:when>
						<c:when test="${item.status eq 1}">
							<span style="color: red">已过期</span>
						</c:when>
					</c:choose>
				</td>
				<td >
					<a href="${pageContext.request.contextPath}/Product/Admin/newsManager!toEditNews.action?news.id=${item.id}" class="a">修改</a>
				</td>
			</tr>
		</c:forEach>

		</table>
	</div>
</div>
</body>
</html>