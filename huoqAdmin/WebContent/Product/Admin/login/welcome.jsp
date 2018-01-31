<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"  %>
<%@ taglib prefix="sl" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/zlqEl.tld" prefix="myel"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="s1" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="shortcut icon" href="${pageContext.request.contextPath}/Product/images/favicon.ico" type="image/x-icon"/>
<link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css" />		
<link href="${pageContext.request.contextPath}/Product/Admin/css/wecome.css" rel="stylesheet" type="text/css" />
<script src="${pageContext.request.contextPath}/Product/Admin/js/jquery-1.9.1.min.js"></script>
<s:action name="platfromInversors!getPlatformInfo" namespace="/Product/Admin" />
<title>欢迎</title>
<script type="text/javascript">
$(function(){
	//获取当前日期
	var formatDate=function (date) {  
		date=new Date();
	    var y = date.getFullYear();  
	    var m = date.getMonth() + 1;  
	    m = m < 10 ? '0' + m : m;  
	    var d = date.getDate();  
	    d = d < 10 ? ('0' + d) : d;  
	    return m + '/' + d + '/' + y;  
	}; 
	
	var insertTime=formatDate(Date());
	$.each($('.link'),function(){
		var $href=$(this).attr('href');
		$href=$(this).attr('href',$href+insertTime);
	})
})
</script>

</head>
<body>
	<div class="center" >
		<jsp:include page="/Product/Admin/common/head.jsp" />
		<div class="main" >
			<sl:if test="${usersLogin eq '0EA5D6BC23E8EEC78F62546B9F68BABFA96976B775889BA625DB6D764FD0DBD42A1C0F45F85B0DE8'
			|| usersLogin eq '2B794C4221117E6646F80AF1504E5DCC6B62ED14D6E347F17BB1E5E7A678EEE42A1C0F45F85B0DE8'
			|| usersLogin eq '420388530643580F9F86BB19AB8CD57FAA2FFE8AAAEABC29749A0D5C952711B52A1C0F45F85B0DE8'
			|| usersLogin eq '2E713E82D2166EF87B5E5EEC4C2D13F02E3D29DC3A0FC1F83B487DFF5552DBAB2A1C0F45F85B0DE8'
			|| usersLogin eq '9C24FAB253F06322455A054B6AFB7CA823F5CE9BDCDF847AE8953C35ADD206CA2A1C0F45F85B0DE8'
			}">
			<!-- 资金数据概览 -->
			<div class="data-item">
				<h4 class="title-line">今日数据概览</h4>
				<div class="overview fund-data">
					<div class="accumulate-data clearfix">
						<!-- p class="data-msg">累计</p-->
						<ul class="clearfix">
							<li>
								<p class="data-title">今日注册用户数(人)</p>
								<p class="data-value"><a href="${pageContext.request.contextPath}/Product/Admin/userStat!indexUserInfo.action?insertTime=" class="link"><fmt:formatNumber value="${myPlatform.todayregisterCount}" pattern="#,##0" /></a></p>
							</li>
							<li>
								<p class="data-title">今日认证用户数(人)</p>
								<!--p class="data-value"><fmt:formatNumber value="${myPlatform.allOutCashMoney}" pattern="#,##0" /></p-->
								<p class="data-value"><a class="link" href="${pageContext.request.contextPath}/Product/buyInfo/userBuy!tiedCardInfo.action?insertTime="><fmt:formatNumber value="${myPlatform.todaycertificationCount}" pattern="#,##0" /></a></p>

							</li>
							<li>
								<p class="data-title">今日首投人数(人)</p>
								<!--p class="data-value"><fmt:formatNumber value="${myPlatform.rechargeMoney}" pattern="#,##0" /></p-->
								<p class="data-value"><a href="${pageContext.request.contextPath}/Product/buyInfo/userBuy!productInfo.action?isnew=1&insertTime=" class="link"><fmt:formatNumber value="${myPlatform.todayNewBuyNumber}" pattern="#,##0" /></a></p>
								
							</li>
							<li>
                                <p class="data-title">今日购买人数(人)</p>
                                <!--p class="data-value"><fmt:formatNumber value="${myPlatform.rechargeMoney}" pattern="#,##0" /></p-->
                                <p class="data-value"><a href="${pageContext.request.contextPath}/Product/Admin/recharge!rechargeMoneyRecord.action?name=&insertTime=" class="link">${myPlatform.todayBuyNumber}</a></p>
                                
                            </li>
                            <li>
                                <p class="data-title">今日满标企业(家)</p>
                                <!--p class="data-value"><fmt:formatNumber value="${myPlatform.rechargeMoney}" pattern="#,##0" /></p-->
                                <!--p class="data-value"><a href="${pageContext.request.contextPath}/Product/Admin/recharge!rechargeMoneyRecord.action?name=&insertTime=" class="link">0</a></p-->
                                <p class="data-value"><a href="${pageContext.request.contextPath}/Product/Admin/recharge!todayFullScaleUserDetail.action?name=&insertTime=" class="link">${myPlatform.todayFullScaleCompanyNumber}</a></p>
                                
                            </li>
						</ul>
					</div>  
					<div class="today-data clearfix">
						<!--p class="data-msg">今日</p-->
						<ul class="clearfix">
							<li>
								<p class="data-title">今日存量增量(元)</p>
								<p class="data-value"><fmt:formatNumber value="${myPlatform.todayCapitalStock}" pattern="#,##0" /></p>
							</li>
							<li>
								<p class="data-title">今日充值金额(元)</p>
								<p class="data-value"><a href="${pageContext.request.contextPath}/Product/Admin/recharge!rechargeMoneyRecord.action?name=&insertTime=" class="link"><fmt:formatNumber value="${myPlatform.todayrechargeMoney}" pattern="#,##0" /></a></p>
							</li>
							<li>
								<p class="data-title">今日提现金额(元)</p>
								<p class="data-value"><a href="${pageContext.request.contextPath}/Product/Admin/checkTxsq!loadIndexTxsq.action?&insertTime=" class="link"><fmt:formatNumber value="${myPlatform.todayOutCashMoney}" pattern="#,##0" /></a></p>
							</li>
							<!--li>
								<p class="data-title">今日购买金额(元)</p>
								<p class="data-value"><a href="${pageContext.request.contextPath}/Product/buyInfo/userBuy!productInfo.action?insertTime=" class="link"> <fmt:formatNumber value="${myPlatform.todayBuyMoney}" pattern="#,##0" /></a></p>
							</li-->
							<li>
                                <p class="data-title">今日交易金额(元)</p>
                                <p class="data-value">
                                <a href="${pageContext.request.contextPath}/Product/buyInfo/userBuy!productInfo.action?insertTime=" class="link"> <fmt:formatNumber value="${myPlatform.todayBuyMoney}" pattern="#,##0" /></a>
                                </p>
                            </li>
							<li>
								<p class="data-title">未审核提现总额(元)</p>
                                <p class="data-value"><a href="${pageContext.request.contextPath}/Product/Admin/recharge!uAuditiongOutCashTotalMoneyDetail.action" class="linkNew"> <fmt:formatNumber value="${myPlatform.uncheckedOutCashMoney}" pattern="#,##0" /></a></p>
							</li>
							
						</ul>
					</div> 
				</div>
			</div>
			<!-- 累计数据概览 -->
			<div class="data-item">
				<h4 class="title-line">累计数据概览</h4>
				<div class="overview user-data">	
					<div class="accumulate-data clearfix">
						<!--p class="data-msg">累计</p-->
						<ul class="clearfix">
						
						
						      <li>
                                <p class="data-title">平台资金存量</p>
                                <p class="data-value"><fmt:formatNumber value="${myPlatform.allCapitalStock}" pattern="#,##0" /></p>
                            </li>
						     <li>
                                <p class="data-title">平台总交易额(元)</p>
                                <!--p class="data-value"><fmt:formatNumber value="${myPlatform.allCapitalStock}" pattern="#,##0" /></p-->
                                <p class="data-value"><fmt:formatNumber value="${myPlatform.allBuyMoney}" pattern="#,##0" /></p>
                                
                                <!--p class="data-value">0</p-->
                            </li>
						     <li>
                                <p class="data-title">累计充值金额(元)</p>
                                <p class="data-value"><fmt:formatNumber value="${myPlatform.rechargeMoney}" pattern="#,##0" /></p>
                            </li>
                             <li>
                                <p class="data-title">累计提现金额(元)</p>
                                <p class="data-value"><fmt:formatNumber value="${myPlatform.allOutCashMoney}" pattern="#,##0" /></p>
                            </li>
						
						
						
							<li>
								<p class="data-title">累计注册用户数</p>
								<p class="data-value"><fmt:formatNumber value="${myPlatform.registerCount}" pattern="#,##0.##" /></p>
							</li>
							<li>
								<p class="data-title">累计认证用户数</p>
								<p class="data-value"><fmt:formatNumber value="${myPlatform.certificationCount}" pattern="#,##0" /></p>
							</li>
							<!-- li>
                                <p class="data-title">注册用户数</p>
                                <p class="data-value"><a href="${pageContext.request.contextPath}/Product/Admin/userStat!indexUserInfo.action?insertTime=" class="link"><fmt:formatNumber value="${myPlatform.todayregisterCount}" pattern="#,##0" /></a></p>
                                <p class="data-value">147</p>
                            </li>
                            <li>
                                <p class="data-title">认证用户数</p>
                                <p class="data-value"><a class="link" href="${pageContext.request.contextPath}/Product/buyInfo/userBuy!tiedCardInfo.action?insertTime="><fmt:formatNumber value="${myPlatform.todaycertificationCount}" pattern="#,##0" /></a></p>
                            </li>
                            <li>
                                <p class="data-title">购买人数</p>
                                <p class="data-value"><a href="${pageContext.request.contextPath}/Product/buyInfo/userBuy!productInfo.action?insertTime=" class="link"> <fmt:formatNumber value="${myPlatform.todayBuyNumber}" pattern="#,##0" /></a></p>
                            </li>
                            <li>
                                <p class="data-title">首投人数</p>
                                <p class="data-value"><a href="${pageContext.request.contextPath}/Product/buyInfo/userBuy!productInfo.action?isnew=1&insertTime=" class="link"><fmt:formatNumber value="${myPlatform.todayNewBuyNumber}" pattern="#,##0" /></a></p>
                            </li-->
						</ul>
					</div>  
					<!-- div class="today-data clearfix">
						<!--p class="data-msg">今日</p>
						<ul class="clearfix">
							<li>
								<p class="data-title">注册用户数</p>
								<p class="data-value"><a href="${pageContext.request.contextPath}/Product/Admin/userStat!indexUserInfo.action?insertTime=" class="link"><fmt:formatNumber value="${myPlatform.todayregisterCount}" pattern="#,##0" /></a></p>
							</li>
							<li>
								<p class="data-title">认证用户数</p>
								<p class="data-value"><a class="link" href="${pageContext.request.contextPath}/Product/buyInfo/userBuy!tiedCardInfo.action?insertTime="><fmt:formatNumber value="${myPlatform.todaycertificationCount}" pattern="#,##0" /></a></p>
							</li>
							<li>
								<p class="data-title">购买人数</p>
								<p class="data-value"><a href="${pageContext.request.contextPath}/Product/buyInfo/userBuy!productInfo.action?insertTime=" class="link"> <fmt:formatNumber value="${myPlatform.todayBuyNumber}" pattern="#,##0" /></a></p>
							</li>
							<li>
								<p class="data-title">首投人数</p>
								<p class="data-value"><a href="${pageContext.request.contextPath}/Product/buyInfo/userBuy!productInfo.action?isnew=1&insertTime=" class="link"><fmt:formatNumber value="${myPlatform.todayNewBuyNumber}" pattern="#,##0" /></a></p>
							</li>
						</ul>
					</div-->  
				</div>	
			</div>
			</sl:if>
			<s1:else>
				<div align="center"><h1>sfsdfsdfsdfsdfsdf</h1></div>

			</s1:else>

		</div>
	</div>
</body>
</html>