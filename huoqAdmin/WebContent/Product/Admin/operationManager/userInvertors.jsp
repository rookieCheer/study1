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
<script src="${pageContext.request.contextPath}/js/artDialog4.1.7/jquery.artDialog.js?skin=blue"></script>
<style type="text/css">
.a {
  color: blue;
  text-decoration: underline;
}
</style>

<title>用户投资统计</title>
</head>
<body>
<div class="center">			
	<jsp:include page="/Product/Admin/common/head.jsp"/>
	<div class="main" align="center">
		<h3 style="text-align: center;">用户投资统计</h3><br/>
		<div id="div_condition" style="text-align: center;" >
		<label>用户名:<input type="text" name="name" id="name" value="${name}" maxlength="11"  onkeypress="if(event.keyCode==13) {frm.click();}">
		真实姓名:<input type="text" name="realname" id="realname" value="${realname}" maxlength="6"  onkeypress="if(event.keyCode==13) {frm.click();}">
		<span>注册时间:</span> <input id="insertTime" name="insertTime" type="text" value="${insertTime}"  onkeypress="if(event.keyCode==13) {frm.click();}">
		<input type="button" value="search" id="frm" onclick="Byname()"></label>&nbsp;&nbsp;
		<input type="button" value="导出报表" onclick="ireportDo()">
		</div>
		
		<table style="margin-top: 20px;width: 80%;" >
		<tr align="center">
			<td>序号</td>
			<td>用户id</td>
			<td>用户名</td>
			<td>真实姓名</td>
			<td>注册日期</td>
			<td>绑卡日期</td>
			<td>首投日期</td>
			<td>投资总额(元)</td>
			<td>现存资金(元)</td>
			<td>在贷金额(不含零钱罐)</td>
			<td>零钱罐金额</td>
			<td>账户余额</td>
			<td>投资券金额(元)</td>
			<td>红包金额(元)</td>
			<td>邀请好友人数</td>
			<td>好友总资金</td>
		</tr>
		<c:forEach items="${pageUtil.list}" var="item" varStatus="i">
		<tr align="center">
			<td>${i.count + (pageUtil.currentPage-1)*pageUtil.pageSize}</td>
			<td>${item.id}</td>
			<td><a class="a"  target="_blank" href="${pageContext.request.contextPath}/Product/Admin/userStat!loadUserInfo.action?username=${myel:jieMiUsername(item.username)}">${myel:jieMiUsername(item.username)}</a></td>
			<td>${item.real_name}</td>
			<td><fmt:formatDate value="${item.insertTime}" pattern="yyyy-MM-dd"/></td>
			<td><fmt:formatDate value="${item.bandCardTime}" pattern="yyyy-MM-dd"/></td>
			<td><fmt:formatDate value="${item.fristBuyTime}" pattern="yyyy-MM-dd"/></td>
			<td>${item.copies}</td>
			<td><fmt:formatNumber value="${item.allMoney}" pattern="#,##0.##"/></td>
			<td><fmt:formatNumber value="${item.buyInMoney}" pattern="#,##0.##"/></td>
			<td><fmt:formatNumber value="${item.coinPurseMoney}" pattern="#,##0.##"/></td>
			<td><fmt:formatNumber value="${item.leftMoney}" pattern="#,##0.##"/></td>
			<td><fmt:formatNumber value="${item.coupon}" pattern="#,##0.##"/></td>
			<td><fmt:formatNumber value="${item.hongbao}" pattern="#,##0.##"/></td>
			<td>${item.friendNumber}</td>
			<td><fmt:formatNumber value="${item.friendMoney}" pattern="#,##0.##"/></td>
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
<div id="psi_load" style="display:none;">
<img width="18" src="${pageContext.request.contextPath}/Product/images/progress.gif"><span  id="psi_tip">正在导出,请稍等...</span>
</div>	
<script type="text/javascript">
function ireportDo(){
	var list='${table}';
	if(list!='1'){
		alert("无数据");
		return false;
	}

	var url='${pageContext.request.contextPath}/Product/Admin/investors!exoprtUserInvertors.action?name='+$("#name").val()+'&insertTime='+$('#insertTime').val()+'&realname='+$('#realname').val();
	var my= art.dialog({
	    title: '提示',
	    content:document.getElementById("psi_load"),
	    height: 60,
	    lock: true,
	    cancel: false
	});
	$.post(url,$("#frm").serialize(),function(data){
		my.close();
		if("ok"==data.status){
			var mydata = '${pageContext.request.contextPath}'+data.json;
				var ssss="导出成功&nbsp;&nbsp;&nbsp;&nbsp;<a href='"+mydata+"' style='color:red;'>点击下载</a>";
	       		art.dialog({
	   		    title: '提示',
	   		    content:ssss,
	   		    height: 60,
	   		    lock: true,
	   		    ok:function(){
	   		    }
	   		});   
		}else{
			var mydata = '${pageContext.request.contextPath}'+data.json;
	       		art.dialog({
	   		    title: '提示',
	   		    content:data.json,
	   		    height: 60,
	   		    lock: true,
	   		    ok:function(){
	   		    }
	   		});  
		}
	});

}
	function Byname(){
	var realname = $('#realname').val();
		var url="${pageContext.request.contextPath}/Product/Admin/investors!userInvertors.action?name="+$("#name").val()+"&insertTime="+$('#insertTime').val()+"&realname="+realname;
		window.location.href=url;
	}
</script>
<script type="text/javascript">
	var k4 = new Kalendae.Input("insertTime", {
		attachTo:document.body,
		months:2,//多少个月显示出来,即看到多少个日历
		mode:'range'
		/* selected:[Kalendae.moment().subtract({d:7}), Kalendae.moment().add({d:0})] */
	});
</script>
</body>
</html>	