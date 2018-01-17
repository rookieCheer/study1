<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"  deferredSyntaxAllowedAsLiteral="true" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script src="${pageContext.request.contextPath}/Product/Admin/js/jquery-1.9.1.min.js"></script>
<link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css" />
<link href="${pageContext.request.contextPath}/Product/Admin/css/product_fabu.css" rel="stylesheet" type="text/css" />
<title>新华网-发布预约常规产品</title>
<style type="text/css">
	.rightText{
		text-align: right;
	}
	.leftText{
		text-align: left;
	}
	.divEdit {
	  width: 100%;
	  overflow: auto; 
	  min-height: 120px;
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
</style>
</head>
<body>

	<div class="center">	
	<jsp:include page="/Product/Admin/common/head.jsp"/>
		<div class="main">	
			<jsp:include page="../releaseCGYYProduct.jsp"></jsp:include>
		</div>
	</div>
</body>

</html>