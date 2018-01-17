<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/zlqEl.tld" prefix="myel"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>头条数据统计表</title>
<link
	href="${pageContext.request.contextPath}/Product/Admin/css/public.css"
	rel="stylesheet" type="text/css" />
<link
	href="${pageContext.request.contextPath}/Product/Admin/css/product_fabu_history.css"
	rel="stylesheet" type="text/css" />
<script
	src="${pageContext.request.contextPath}/Product/Admin/js/jquery-1.9.1.min.js"></script>
<script
	src="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.standalone.js"></script>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.css"
	type="text/css">
<script
	src="${pageContext.request.contextPath}/js/artDialog4.1.7/jquery.artDialog.js?skin=blue"></script>
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
		}
		var insertTime = $("#insertTime").val();
		var phone = $("#phone").val();
		var realname = $("#realname").val();
		var url = "${pageContext.request.contextPath}/Product/buyInfo/userBuy!exportBuyProductInfo.action?currentPage=${currentPage}&insertTime="
				+ insertTime + "&phone=" + phone + "&realname=" + realname;
		var list = "${list}";
		if (list != null && list != "[]") {
			var my = art.dialog({
				title : '提示',
				content : document.getElementById("psi_load"),
				height : 60,
				lock : true,
				cancel : false
			});
			$
					.post(
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

	function queryProduct() {
		var insertTime = $("#insertTime").val();
		var phone = $("#phone").val();
		var realname = $("#realname").val();
		var url = "${pageContext.request.contextPath}/Product/buyInfo/userBuy!toutiaoStatisticsTable.action?insertTime="
				+ insertTime;
		window.location.href = url;
	}
	
	function queryProductInfo(os) {
		var os = os;
		var insertTime = $("#insertTime").val();
		var phone = $("#phone").val();
		var realname = $("#realname").val();
		var url = "${pageContext.request.contextPath}/Product/buyInfo/userBuy!toutiaoStatisticsInfoTable.action?insertTime="
				+ insertTime+"&os="+os;
		window.location.href = url;
	}
</script>
</head>
<body>
	<div class="center">
		<jsp:include page="/Product/Admin/common/head.jsp" />
		<div class="main" align="center">
			<h3>头条统计表初表</h3>
			<p width="80%"><span style="position: relative;
    left: -413px; " class="link">
			<c:choose>
				<c:when test="${os == 0}">
				安卓
				</c:when>
				<c:when test="${os == 1}">
				IOS
				</c:when>
				<c:otherwise>
				
				</c:otherwise>
				</c:choose>
			</span>
				
				<span>统计时间:</span> <input id="insertTime" name="insertTime"
				type="text" value="${insertTime}"> 
				<a class="sereach" href="javascript:queryProduct();" id="sereach">查询</a>
			<input type="button" value="导出报表" onclick="ireportDo()">
				</p>
			<table border="1" width="80%">
				<tr>
					<td>购买日期</td>
					<td>渠道编号</td>
					<td width="70px;">渠道名称</td>
					<td>手机型号</td>
					<td>位置</td>
					<td>广告点击量(点击量)</td>
					<td>下载量</td>
					<td>激活量</td>
					<td>激活注册转化率(%)</td>
					<td>当日注册人数</td>
					<td>认证人数</td>
					<td>注册认证转化率(%)</td>
					<td>认证首投转化率(%)</td>
					<td>首投人数</td>
					<td>首投总金额</td>
					<td>人均首投金额(元)</td>
					<td>复投人数</td>
					<td>复投金额(元)</td>
					<td>人均复投金额(元)</td>
					<td>新增复投用户数</td>
					<td>新增复投用户投资总额(元)</td>
					<td>新增复投率</td>
					<td>投资人数</td>
					<td>投资金额(元)</td>
					<td>人均投资金额(元)</td>
				</tr>
				<tr>
					<td>${toutiao.date}</td>
					<td><a href="">0</a>/<a href="">1</a></td>
					<td><a href="javascript:queryProductInfo(${0})" class="link">安卓</a>/
					<a href="javascript:queryProductInfo(${1})" class="link">IOS</a></td>
					<td>手机型号</td>
					<td>位置</td>
					<td>${toutiao.touTiaoClicks}</td>
					<td>下载量</td>
					<td>${toutiao.activator}</td>
					<td><fmt:formatNumber value="${toutiao.activationRegisterConversion}" pattern="#,##0.##" /></td>
					<td>${toutiao.registerNumber}</td>
					<td>${toutiao.bindingNumber}</td>
					<td><fmt:formatNumber value="${toutiao.registerAuthenticationConversion}" pattern="#,##0.##" /></td>
					<td><fmt:formatNumber value="${toutiao.authenticationBuyConversion}" pattern="#,##0.##" /></td>
					<td>${toutiao.firstInvestmentNumber}</td>
					<td>${toutiao.firstInvestmentMoney}</td>
					<td>${toutiao.perCapitaFirstInvestmentMoney}</td>
					<td>${toutiao.secondInvestmentNumber}</td>
					<td>${toutiao.secondInvestmentMoney}</td>
					<td>${toutiao.perCapitasecondInvestmentMoney}</td>
					<td>${toutiao.newSecondInvestmentNumber}</td>
					<td>${toutiao.newSecondInvestmentMoney}</td>
					<td>${toutiao.newSecondConversion}</td>
					<td>${toutiao.investmentNumber}</td>
					<td>${toutiao.investmentMoney}</td>
					<td><fmt:formatNumber value="${toutiao.perCapitaMoney}" pattern="#,##0.##" /></td>
				</tr>
			</table>
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