<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/zlqEl.tld" prefix="myel"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>每日明细汇总表</title>
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
	function exportExcel() {
		var insertTime = $("#insertTime").val();

		var form = $("<form>");
		form.attr('target', 'iframe');
		form.attr('method', 'post');
		form.attr('action', 'userBuy!exportExcelDayDetailList.action');
		var input1 = $('<input>');
		input1.attr('type', 'hidden');
		input1.attr('name', 'insertTime');
		input1.attr('value', insertTime);


		var iframe = $("<iframe>")
		iframe.attr('id', 'iframe');
		iframe.attr('name', 'iframe');
		iframe.attr('src', 'about:blank');
		iframe.attr('style', 'display:none;');
		$('body').append(iframe);
		$('body').append(form);
		form.append(input1);

		form.submit();
	}

	function queryProduct() {
		var insertTime = $("#insertTime").val();
		//var phone=$("#phone").val();
		//var realname=$("#realname").val();
		var url = "${pageContext.request.contextPath}/Product/buyInfo/userBuy!summaryTable.action?insertTime=" + insertTime;
		window.location.href = url;
	}
</script>
</head>
<body>
	<div class="center">
		<jsp:include page="/Product/Admin/common/head.jsp" />
		<div class="main" align="center">
			<h3>每日明细汇总表</h3>
			<span>查询时间:</span> <input id="insertTime" name="insertTime"
				type="text" value="${insertTime}"> <a class="sereach"
				href="javascript:queryProduct();" id="sereach">查询</a> <input
				type="button" value="导出报表" onclick="exportExcel()">
			<table border="1" width="80%">
				<tr>
					<td>日期</td>
					<td>新增注册用户数A</td>
					<td>新增绑卡客户B</td>
					<td>今日首投人数</td>
					<td>累计注册客户数</td>
					<td>累计绑卡客户</td>
					<td>资金存量E</td>
					<td>当日提现金额E</td>
					<td>当日资金流入</td>
					<td>首投总额</td>
					<td>复投总额D</td>
					<td>新增注册IOS客户A</td>
					<td>新增注册Android客户A</td>
					<td>新增注册微信客户A</td>
					<td>新增绑卡IOS客户B</td>
					<td>新增绑卡Android客户B</td>
					<td>新增绑卡微信客户B</td>
					<td>当日购买交易笔数C</td>
					<td>新客户部分C</td>
					<td>老客户部分C</td>
					<td>活期产品部分D</td>
					<td>定期产品部分D</td>
					<td>累计资金流入</td>
					<td>当日可提现金额E</td>

				</tr>
				<tr>
					<td>${list.tadayDate}</td>
					<td>${list.NEnrollUser}</td>
					<td>${list.NAutUser}</td>
					<td>${list.todayFirstInvestPeople}</td>
					<td>${list.allEnUser}</td>
					<td>${list.allAutUser}</td>
					<td>${list.capitalStock}</td>
                    <td>${list.todayoutMoney}</td>
					<td>${list.todayincapital}</td>
					<td>${list.NDealMoney}</td>
					<td>${list.ODealMoney}</td>
					<td>${list.NEnrollIosUser}</td>
					<td>${list.NEnrollAndroidUser}</td>
					<td>${list.NEnrollWeChatUser}</td>
					<td>${list.NAutIosUser}</td>
					<td>${list.NAutAndroidUser}</td>
					<td>${list.NAutWeChatUser}</td>
					<td>${list.todayDeal}<td>
					<td>${list.NUnserDeal}</td>
					<td>${list.OUserDeal}</td>
					<td>${list.currentProduct}</td>
					<td>${list.regularProduct}</td>
					<td>${list.allinMoney}</td>
					<td></td>
					<!-- ${list.todayCash} 暂时不显示 -->
				</tr>
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