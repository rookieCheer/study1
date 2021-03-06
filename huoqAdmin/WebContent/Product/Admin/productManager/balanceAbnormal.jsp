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
<link href="${pageContext.request.contextPath}/Product/Admin/css/product_fabu_history.css" rel="stylesheet" type="text/css" />
<script src="${pageContext.request.contextPath}/Product/Admin/js/jquery-1.9.1.min.js"></script>
<script src="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.standalone.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.css" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/Product/js/alert.js"></script>
<script src="${pageContext.request.contextPath}/js/artDialog4.1.7/jquery.artDialog.js?skin=blue"></script>

<style type="text/css">
.a {
  color: blue;
  text-decoration: underline;
}
</style>

<title>账户余额异常</title>
</head>
<body>
<div class="center">			
	<jsp:include page="/Product/Admin/common/head.jsp"/>
	<div class="main" align="center">
		<h1 style="text-align: center;">账户余额异常</h1>
		<div id="div_condition" style="text-align: center;" >
		<label>关键字:<input type="text" name="name" id="name" value="${name}" maxlength="11">
		<input type="button" value="search" id="frm" onclick="Byname()"></label>&nbsp;&nbsp;
		</div>
		
		<table style="width:1200px;margin-top: 20px;width: 100%;" border="1" >
		<tr>
		<td>序号</td>
		<td>用户ID</td>
		<td>用户名</td>
		<td>用户总金额</td>
		<td>当前可用余额</td>
		<td>冻结资金</td>
		<td>真实姓名</td>
		<td>插入时间</td>
		<td>更新时间</td>
		
		</tr>
		<c:forEach items="${pageUtil.list}" var="item" varStatus="i">
		<tr>
		<td>${i.count + (pageUtil.currentPage-1)*pageUtil.pageSize}</td>
		<td>${item.usersId}</td> 
		<td>
			<a class="a" href="${pageContext.request.contextPath}/Product/Admin/userStat!loadUserInfo.action?username=${myel:jieMiUsername(item.phone)}">
				${myel:jieMiUsername(item.phone)}
			</a>
		</td>
		<td>${item.totalMoney*0.01}</td> 
		<td>${item.leftMoney*0.01}</td>
		<td>${item.freezeMoney*0.01}</td>
		<td>${item.realName}</td>
		<td>${item.insertTime}</td>
		<td>${item.updateTime}</td>
		
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

$(function () {
    $("#status option[value='${status}']").attr("selected", true);
});  

	function Byname(){
		var stau = $("#status option:selected").val();
		var url="${pageContext.request.contextPath}/Product/Admin/moneyProblem!balanceAbnormalList.action?name="+$('#name').val();
		
		window.location.href=url;
	}
	

	var k4 = new Kalendae.Input("insertTime", {
		attachTo:document.body,
		months:2,//多少个月显示出来,即看到多少个日历
		mode:'range'
		/* selected:[Kalendae.moment().subtract({d:7}), Kalendae.moment().add({d:0})] */
	});
	
	//回车绑定
    $("#name,#insertTime").keydown(function (event) {
        if (event.keyCode == 13) {
            Byname();
        }
    });
</script>
</body>
</html>	