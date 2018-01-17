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
<title>已使用的投资券</title>
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
		<h1 style="text-align: center;">已使用的投资券</h1>
		<form action="${pageContext.request.contextPath}/Product/Admin/sendCoupon!couponRecordNote.action" method = 'post' >
		<label>用户名:<input type="text" name="username" id="username" value="${username}" maxlength="11">
		<span>发送投资券时间:</span> <input id="insertTime" name="insertTime" type="text" value="${insertTime}">
		<span>使用投资券时间:</span> <input id="useTime" name="useTime" type="text" value="${useTime}">
		<span>关键字查询:</span> <input id="note" name="note" type="text" value="${note}">
		
		<input type="submit" value="search">
		<input type="button" value="导出报表" onclick="ireportDo()">
		</label>&nbsp;&nbsp;
		</form>
		<table style="width:100%;margin-top: 20px;text-align: center;">
		<tr>
			<td>序号</td>
			<td>用户名</td>
			<td>发送金额</td>
			<td>状态</td>
			<td>发送时间</td>
			<td>到期时间</td>
			<td>使用时间</td>
			<td>注册平台</td>
			<td>注册渠道</td>
			<td>备注</td>
			<td>备注2</td>
			<td>投入金额</td>
		</tr>
		<c:forEach items="${pageUtil.list}" var="item" varStatus="i">
			<tr>
				<td>${i.count + (pageUtil.currentPage-1)*pageUtil.pageSize}</td>
				<td><a class="a"  href="${pageContext.request.contextPath}/Product/Admin/userStat!loadUserInfo.action?username=${myel:jieMiUsername(item.users.username)}">${myel:jieMiUsername(item.users.username)}</a></td> 
				<td><fmt:formatNumber value="${item.money * 0.01}" pattern="#,##0.##"/></td>
				<td>${item.coupon.statusChina}</td>
				<td><fmt:formatDate value="${item.coupon.insertTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td><fmt:formatDate value="${item.coupon.overTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td><fmt:formatDate value="${item.coupon.useTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				
				<td>
                        <c:choose>
                            <c:when test="${item.users.registPlatform == 0}">web端注册</c:when>
                            <c:when test="${item.users.registPlatform == 1}">Android移动端</c:when>
                            <c:when test="${item.users.registPlatform == 2}">IOS移动端</c:when>
                            <c:when test="${item.users.registPlatform == 3}">微信注册</c:when>
                            <c:otherwise>${item.users.registPlatform}</c:otherwise>
                        </c:choose>

                    </td>
				<td>${item.users.registChannel}</td>
				<td>${item.coupon.note}</td>
				<td>${item.note}</td>
				<td><fmt:formatNumber value="${item.investors.inMoney* 0.01}" pattern="#,##0.##"/></td>
			</tr>
		</c:forEach>
	</table>

	<c:choose>
			<c:when test="${pageUtil.list ne '[]' &&  pageUtil.list ne '' && pageUtil.list ne null}">
			<jsp:include page="/Product/page.jsp" /></c:when>
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
</body>
<script type="text/javascript">
	var k4 = new Kalendae.Input("insertTime", {
		attachTo:document.body,
		months:2,//多少个月显示出来,即看到多少个日历
		mode:'range'
		/* selected:[Kalendae.moment().subtract({d:7}), Kalendae.moment().add({d:0})] */
	});
	var k5 = new Kalendae.Input("useTime", {
		attachTo:document.body,
		months:2,//多少个月显示出来,即看到多少个日历
		mode:'range'
		/* selected:[Kalendae.moment().subtract({d:7}), Kalendae.moment().add({d:0})] */
	});
	 $(function(){
		$("#status option[value='${status}']").attr("selected",true);
	 });
	 
	 function ireportDo(){
			var list='${table}';
			if(list!='1'){
				alert("无数据");
				return false;
			}
			var insertTime=$("#insertTime").val();
			var registPlatform=$("#registPlatform option:selected").val();
				//var url='${pageContext.request.contextPath}/Product/Admin/investors!exoprtUserInvertors.action?name='+$("#name").val()+'&insertTime='+$('#insertTime').val()+'&realname='+$('#realname').val();  
				
				var url='${pageContext.request.contextPath}/Product/Admin/sendCoupon!exoprtcouponRecordNote.action?username='+$("#username").val()+'&insertTime='+$("#insertTime").val()+'&useTime='+$("#useTime").val()+'&note='+$("#note").val();
				//name="+$("#name").val()+"&insertTime="+$('#insertTime').val()+"&realname="+realname;
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
			   		    	//mysss.close();
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
			   		    	//mysss.close();
			   		    }
			   		});  
				}
			});

		}
</script>
</html>