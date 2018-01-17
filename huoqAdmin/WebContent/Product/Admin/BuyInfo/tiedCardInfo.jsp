<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/zlqEl.tld" prefix="myel"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>绑卡情况统计表</title>
<link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css" />
<link href="${pageContext.request.contextPath}/Product/Admin/css/product_fabu_history.css" rel="stylesheet" type="text/css" />
<script src="${pageContext.request.contextPath}/Product/Admin/js/jquery-1.9.1.min.js"></script>
<script src="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.standalone.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.css" type="text/css">
<script src="${pageContext.request.contextPath}/js/artDialog4.1.7/jquery.artDialog.js?skin=blue"></script>
<style type="text/css">
.sereach {
	width: 200px;
	height: 32px;
	line-height: 32px;
	text-align: center;
	border: 1px solid #009DDA;
	border-radius: 5px;
}

.select1 {
	border-radius: 5px;
	border-color: #009DDA;
	margin-right: 5px;
}

.a {
	color: blue;
	text-decoration: underline;
}
</style>
<script type="text/javascript">
	function ireportDo() {
		var interval = $("#insertTime").val();
		if (interval == null || interval == '' || interval.length == 0) {
			alert("请选择要导出报表日期！");
			return false;
		}
		if (interval.indexOf("-") != -1) {
			var startDate = interval.split("-")[0];
			var endDate = interval.split("-")[1];
			var startTime = new Date(Date.parse(startDate.replace(/-/g, "/")))
					.getTime();
			var endTime = new Date(Date.parse(endDate.replace(/-/g, "/")))
					.getTime();
			var dates = Math.abs((startTime - endTime)) / (1000 * 60 * 60 * 24);
			if (31 - dates <= 0) {
				alert("请选择日期间隔为31天的数据导出！")
				return false;
			}
			//alert(dates);
		}
		var insertTime=$("#insertTime").val();
		var phone=$("#phone").val();
		var url = "${pageContext.request.contextPath}/Product/buyInfo/userBuy!exportTiedCardInfo.action?currentPage=${currentPage}&insertTime="+insertTime
			+ "&phone="+phone;
		var list = "${list}";
		if (list != null && list != "[]") {
			var my = art.dialog({
				title : '提示',
				content : document.getElementById("psi_load"),
				height : 60,
				lock : true,
				cancel : false
			});
			$.post(
					url,
					$("#sereach").serialize(),
					function(data) {
						my.close();
						data = '${pageContext.request.contextPath}'
								+ data;
						var ssss = "导出成功&nbsp;&nbsp;&nbsp;&nbsp;<a href='"+data+"' style='color:red;'>点击下载</a>";
						art.dialog({
							title : '提示',
							content : ssss,
							height : 60,
							lock : true,
							ok : function() {
							//mysss.close();
							}
						});
					});
		}
	}
	
	function queryProduct(){
		var insertTime=$("#insertTime").val();
		var phone=$("#phone").val();
		/* var username=$("#username").val();
		var isbindbank=$("#isbindbank option:selected").val();
		var level=$("#level option:selected").val();
		var inMoney1=$("#inMoney1").val();
		var inMoney2=$("#inMoney2").val();
		var channel=$("#channel").val(); */

		var url = "${pageContext.request.contextPath}/Product/buyInfo/userBuy!tiedCardInfo.action?insertTime="+insertTime+"&phone="+phone;
		window.location.href=url;
	}
</script>
</head>
<body>
	<div class="center">
		<jsp:include page="/Product/Admin/common/head.jsp" />
		<div class="main" align="center">
			<h3>绑卡情况统计表</h3>
			<span>绑卡时间:</span> 
			<input id="insertTime" name="insertTime"type="text" value="${insertTime}">
			<span>手机号:</span>
			<input id="phone" name="phone" type="text" value="${phone}">
			<a class="sereach" href="javascript:queryProduct();" id="sereach">查询</a> 
			<input type="button" value="导出报表" onclick="ireportDo()">
			<table border="1" width="80%">
					<tr>
						<td>序号</td>
						<td>手机号</td>
						<td>姓名</td>
						<td>所属省份</td>
						<td>所属城市</td>
						<td>持卡人好友</td>
						<td>渠道</td>
						<td>开户银行</td>
						<td>银行卡号</td>
						<td>注册平台</td>
						<td>注册日期</td>
						<td>绑定日期</td>
						<td>用户id</td>
						<td>电话类型</td>
						<td>身份证号</td>
						<td>性别</td>
						<td>年龄</td>
						<td>生日</td>
					</tr>
					<c:forEach items="${list}" var="mylist" varStatus="number">
						<tr>
							<td>${number.index + 1}</td>
							<td>${mylist.phone}</td>
							<td>${mylist.realName}</td>
							<td>${mylist.province}</td>
							<td>${mylist.city}</td>
							<td>${mylist.cardFriend}</td>
							<td>${mylist.channel}</td>
							<td>${mylist.bankName}</td>
							<td>${mylist.bankAccount}</td>
							<td>${mylist.registPlatform}</td>
							<td>${mylist.zinsertTime}</td>
							<td>${mylist.insertTime}</td>
							<td>${mylist.id}</td>
							<td>${mylist.cardType}</td>
							<td>${mylist.idCard}</td>
							<td>${mylist.gender}</td>
							<td>${mylist.age}</td>
							<td>${mylist.birthday}</td>
						</tr>
					</c:forEach>
				</table>
			<c:choose>
				<c:when
					test="${pageUtil.list ne '[]' &&  pageUtil.list ne '' && pageUtil.list ne null}"><jsp:include
						page="/Product/page.jsp" />
			跳转页面:<input id="goPage" name="goPage" type="text" value="${goPage}"
						style="width: 50px">
					<a class="sereach" href="javascript:goqueryProduct();">跳转</a>
				</c:when>
				<c:otherwise>
					<div style="text-align: center; margin-top: 15px;">
						<!-- <img src="images/lh.jpg"> -->
						<img src="../images/no_record.png" />
					</div>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
	<script type="text/javascript">
		var k4 = new Kalendae.Input("insertTime", {
			attachTo : document.body,
			months : 2,//多少个月显示出来,即看到多少个日历
			mode : 'range'
		/* selected:[Kalendae.moment().subtract({d:7}), Kalendae.moment().add({d:0})] */
		});
		$(function() {
			$("#isbindbank option[value='${isbindbank}']").attr("selected",
					true);
			$("#level option[value='${level}']").attr("selected", true);
		});
	</script>
</body>
</html>