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

<title>情人节活动首投金额</title>

<script type="text/javascript">
$(function () {
    $("#sumMoney option[value='${sumMoney}']").attr("selected", true);
});  

function Byname(){
	var username = $("#username").val();
	var sumMoney = $("#sumMoney option:selected").val();
	
	var url="${pageContext.request.contextPath}/Product/Admin/bannerActivity!loadFirstInvValentine.action?username="+username+"&sumMoney="+sumMoney;
		
	window.location.href=url;
	}
</script>


</head>
<body>
<div class="center">			
	<jsp:include page="/Product/Admin/common/head.jsp"/>
	<div class="main" align="center">
		<h3 style="text-align: center;">情人节活动首投金额</h3><br/>
		<div id="div_condition" style="text-align: center;" >
		</div>
		<span>用户名:</span> <input id="username" name="username" type="text" value="${username}"/>
		<select id="sumMoney" name="sumMoney">
				<option value = "">全部</option>
				<option value = "0">3-5千</option>
				<option value = "1">5千-1万</option>
				<option value = "2">1-3万</option>
				<option value = "3">3万以上</option>
		</select>
		<a class="sereach" href="javascript:Byname();" id="sereach">查询</a>
		<table style="margin-top: 20px;width: 80%;align:center " border="1" >
		
		<tr>
		<td>序号</td>
		<td>用户ID</td>
		<td>用户名</td>
		<td>首笔投资金额</td>
		<td>支付时间</td>
		</tr>
		
		<c:forEach items="${list}" var="item" varStatus="i">
		
		<tr>
		<td>${i.count + (pageUtil.currentPage-1)*pageUtil.pageSize}</td>
		<td>${item[0] }</td>
		<td><a class="a"  href="${pageContext.request.contextPath}/Product/Admin/userStat!loadUserInfo.action?username=${myel:jieMiUsername(item[1])}">${myel:jieMiUsername(item[1])}</a></td>
		<td>
		<fmt:formatNumber value="${item[2]*0.01 }" pattern="0.00"/>
		</td>
		<td>${item[3] }</td>
		</tr>
		</c:forEach>
		</table>
		<c:choose>
			<c:when test="${list ne '[]' && list ne '' && list ne null}"><jsp:include page="/Product/page.jsp" /></c:when>
			<c:otherwise>
				<div style="text-align: center;margin-top: 15px;"><!-- <img src="images/lh.jpg"> -->
			  		<img src="../images/no_record.png" />
			  	</div>
			</c:otherwise>
		</c:choose>
	</div>
</div> 

<script type="text/javascript">
	var k4 = new Kalendae.Input("insertTime", {
		attachTo:document.body,
		months:1,//多少个月显示出来,即看到多少个日历
		mode:'range'
		/* selected:[Kalendae.moment().subtract({d:7}), Kalendae.moment().add({d:0})] */
	});
	
	$(function(){
		$("#username").keydown(function(event){
			if(event.keyCode==13){
				queryProduct(); 
			}
		});
		
	});
	
</script>
</body>
</html>	