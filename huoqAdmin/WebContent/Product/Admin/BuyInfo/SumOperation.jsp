<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/zlqEl.tld" prefix="myel"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>运营总表</title>
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
		var url = "${pageContext.request.contextPath}/Product/buyInfo/userBuy!exportSumOperationInfo.action?insertTime=" + insertTime;
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
					var ssss = "导出成功&nbsp;&nbsp;&nbsp;&nbsp;<a href='" + data + "' style='color:red;'>点击下载</a>";
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
		var url = "${pageContext.request.contextPath}/Product/buyInfo/userBuy!sumOperation.action?insertTime=" + insertTime;
		window.location.href = url;
	}
</script>
</head>
<body>
	<div class="center">
		<jsp:include page="/Product/Admin/common/head.jsp" />
		<div class="main" align="center">
			<h3>运营总表</h3>
			<span>查询时间:</span> <input id="insertTime" name="insertTime"
				type="text" value="${insertTime}"> <a class="sereach"
				href="javascript:queryProduct();" id="sereach">查询</a> <input
				type="button" value="导出报表" onclick="ireportDo()">
			<table border="1" width="80%">
				<tr>
                    <td>日期</td>
					<td>累计资金流入</td>
					<td>资金存量</td>
					<td>当日交易金额</td>
					<td>资金流入</td>
					<td>提现金额</td>
					<td>预留资金</td>
					<td>定期预留资金</td>
					<td>宝付资金存量</td>
					<td>宝付手续费</td>
					<td>交易类别</td>
					<td>交易金额</td>
					<td>交易后资金余额</td>
					<td>累计划出</td>
					<td>收益</td><!-- 平台收益 -->
					<td>成本合计</td>
					<td>盈亏</td>
					<td>零钱罐发息</td>
					<td>定期标发息</td>
					<td>好友返利成本</td>
					<td>提现交易</td>
					<td>注册人数</td>
					<td>绑卡用户</td>
					<td>累计注册</td>
					<td>累计绑卡</td>
					<td>购买交易</td>
				</tr>
				<tr>
					<td>${list.todayDate}</td>
					<td>${list.allMoneyinflowA}</td>
					<td></td><!-- 资金存量 无 -->
					<td></td><!--当日交易金额 无 -->
					<td></td><!--资金流入 无 -->
					<td>${list.txMoney}</td>
					<td></td><!-- 预留资金 无 -->
					<td></td><!-- 定期预留资金 -->
					<td>${list.baofusaveMoney}</td>
					<td></td><!-- 宝付手续费 无-->
					<td>${list.dealtype}</td>
					<td>${list.dealMoney}</td>
					<td>${list.afterdealremainMoney}</td>
					<td>${list.alllayoff}</td>
					<td>${list.platearnings}</td>
					<td>${list.allcost}</td>
					<td></td><!-- 盈亏 -->
					<td>${list.lingqianfaxi}</td>
					<td>${list.regularbiaofaxi}</td>
					<td>${list.friendreturnMoney}</td>
					<td>${list.txDeal}</td>
					<td>${list.rigistpersonCount}</td>
					<td>${list.tieCard}</td>
					<td>${list.allRigist}</td>
					<td>${list.allallRigist}</td>
					<td>${list.buyDeal}</td>
				</tr>
			</table>
		</div>
	</div>
	<script type="text/javascript">
		var k4 = new Kalendae.Input("insertTime", {
			attachTo : document.body,
			months : 2, //多少个月显示出来,即看到多少个日历
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