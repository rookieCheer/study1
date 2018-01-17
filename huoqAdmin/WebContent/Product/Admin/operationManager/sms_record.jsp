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
<style type="text/css">
.a {
  color: blue;
  text-decoration: underline;
}
</style>

<title>短信列表</title>
</head>
<body>
<div class="center">			
	<jsp:include page="/Product/Admin/common/head.jsp"/>
	<div class="main" align="center">
		<h3 style="text-align: center;">短信列表</h3><br/>
		<div id="div_condition" style="text-align: center;" >
		
		<label>手机号:<input type="text" name="mobile" id="mobile" value="${mobile}" maxlength="11"  onkeypress="if(event.keyCode==13) {frm.click();}">
		时间:<input type="text" name="insertTime" id="insertTime" value="${insertTime}" maxlength="20"  onkeypress="if(event.keyCode==13) {frm.click();}">
		<input type="button" value="search" id="frm" onclick="Byname()"></label>&nbsp;&nbsp;
		</div>
		
		<table style="table-layout: fixed;width:100%;">
		
		<tr>
		<td width="5%">序号</td>
		<td width="40%" style="overflow-x: auto;">手机号</td>
		<td width="5%">状态</td>
		<td width="20%">内容</td>
		<td width="20%">时间</td>
		</tr>
		
		<c:forEach items="${pageUtil.list}" var="item" varStatus="i">
		
		<tr>
		<td width="5%">${i.count + (pageUtil.currentPage-1)*pageUtil.pageSize}</td>
		<td width="40%" style="overflow-x: auto;">${item.mobile}</td> 
		<td width="5%">${item.status}</td> 
		<td width="20%">${item.smsContent}</td> 
	    <td width="20%">${item.insertTime}</td>
		</tr>
		</c:forEach>
		</table>
		<c:choose>
			<c:when test="${pageUtil.list ne '[]' &&  pageUtil.list ne '' && pageUtil.list ne null}"><jsp:include page="/Product/page.jsp" /></c:when>
			<c:otherwise>
				<div style="text-align: center;margin-top: 15px;"><!-- <img src="images/lh.jpg"> -->
			  		<img src="../images/no_record.png" />
			  	</div>
			</c:otherwise>
		</c:choose>
	</div>
</div> 
<script type="text/javascript">
	function Byname(){
	var realname = $('#mobile').val();
	//var  realnames = encodeURI(encodeURI(realname,"utf-8"),"utf-8");
		var url="${pageContext.request.contextPath}/Product/Admin/smsRecord!getSmsRecord.action?mobile="+$("#mobile").val()+"&insertTime="+$('#insertTime').val();

		window.location.href=url;
	}
</script>
<script type="text/javascript">
	var k4 = new Kalendae.Input("insertTime", {
		attachTo:document.body,
		months:1,//多少个月显示出来,即看到多少个日历
		mode:'range'
		/* selected:[Kalendae.moment().subtract({d:7}), Kalendae.moment().add({d:0})] */
	});
</script>
</body>
</html>	