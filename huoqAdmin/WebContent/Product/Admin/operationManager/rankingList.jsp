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

<title>悦享双十一投资总额排行榜</title>
</head>
<body>
<div class="center">			
	<jsp:include page="/Product/Admin/common/head.jsp"/>
	<div class="main" align="center">
		<h3 style="text-align: center;">悦享双十一投资总额排行榜</h3><br/>
		<div id="div_condition" style="text-align: center;" >
		
		<%-- <label>手机号:<input type="text" name="mobile" id="mobile" value="${mobile}" maxlength="11"  onkeypress="if(event.keyCode==13) {frm.click();}">
		时间:<input type="text" name="insertTime" id="insertTime" value="${insertTime}" maxlength="20"  onkeypress="if(event.keyCode==13) {frm.click();}">
		<input t ype="button" value="search" id="frm" onclick="Byname()"></label>&nbsp;&nbsp;--%>
		</div>
		
		<table style="margin-top: 20px;width: 80%;" >
		
		<tr>
		<td>排名</td>
		<td>用户名</td>
		<td>投资金额</td>
		</tr>
		
		<c:forEach items="${list}" var="item" varStatus="i">
		
		<tr>
		<td>${i.count + (pageUtil.currentPage-1)*pageUtil.pageSize}</td>
		<td><a class="a"  href="${pageContext.request.contextPath}/Product/Admin/userStat!loadUserInfo.action?username=${myel:jieMiUsername(item[0])}">${myel:jieMiUsername(item[0])}</a></td>
		<td>${item[1] }</td>
		
		</tr>
		</c:forEach>
		</table>
		<c:choose>
			<c:when test="${list ne '[]' &&  list ne '' && list ne null}"><jsp:include page="/Product/page.jsp" /></c:when>
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
		var url="${pageContext.request.contextPath}/Product/Admin/bannerActivity!rankingList.action";

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