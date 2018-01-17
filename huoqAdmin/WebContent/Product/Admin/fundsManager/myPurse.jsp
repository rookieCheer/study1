<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>   
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt"%>
<%@ taglib uri="/WEB-INF/zlqEl.tld" prefix="myel"%>   
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>我的账户详情</title>
		<link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css" />
		<link href="${pageContext.request.contextPath}/Product/Admin/css/product.css" rel="stylesheet" type="text/css" />
		<script src="${pageContext.request.contextPath}/Product/Admin/js/jquery-1.9.1.min.js"></script>
</head>
<body>
<div class="center">
<jsp:include page="/Product/Admin/common/head.jsp"/>
			<div class="main">
			<h3 align="center">我的账户详情</h3><br>
				<h3 align="center">用户ID：${users.id} &nbsp;&nbsp;&nbsp;&nbsp;
					用户名：${myel:jieMiUsername(users.username)} &nbsp;&nbsp;&nbsp;&nbsp;
					真实姓名：
					<c:choose>
						<c:when test="${users.usersInfo.realName ne '[]' &&users.usersInfo.realName ne '' && users.usersInfo.realName ne null }">${users.usersInfo.realName}</c:when>
						<c:otherwise>未实名认证</c:otherwise>
					</c:choose>
					&nbsp;&nbsp;&nbsp;&nbsp;性别：
					<c:choose>
						<c:when test="${users.usersInfo.sex  ne '[]' &&users.usersInfo.sex ne '' && users.usersInfo.sex ne null }">${users.usersInfo.sex}</c:when>
						<c:otherwise>未填写</c:otherwise>
					</c:choose>&nbsp;&nbsp;&nbsp;&nbsp;
					是否为黑名单用户：
					<c:if test="${isBlack eq 0 }">不是</c:if>
					<c:if test="${isBlack eq 1 }"><font color="#ff0000"><strong>是</strong></font></c:if>
					&nbsp;&nbsp;&nbsp;&nbsp;
					<c:if test="${isBlack eq 0 }"><button id="addBlank" onclick="addBlack()">添加黑名单</button></c:if>
					<c:if test="${isBlack eq 1 }"><button  disabled="disabled">已经是黑名单用户</button></c:if>
					
				</h3>
				
				<h3 align="center">
				<c:if test="${isBlack eq 1}">
					<c:forEach items="${list}" var="item" varStatus="i">
						<span>备注：${item.note}</span><br/>
						<span>描述：${item.description}</span>
					</c:forEach>
				</c:if>
				</h3>
			
				<div class="top">
					<dl>
						<dd>总资产：${totalMoney} 元</dd>
						<dd>可用余额：${leftMoney} 元</dd>
						<dd>账户冻结资金：${freezeMoney} 元</dd>
						<dd>产品冻结资金：${sumInvestor} 元</dd>
						<dd>冻结资金是否一致：
							<c:if test="${isEqals eq 0 }">一致</c:if>
							<c:if test="${isEqals eq 1 }"><font color="#ff0000"><strong>不一致</strong></font></c:if>
						</dd>
						<dd>充值总次数：${czCount }</dd>
						<dd>充值总金额：${czSum } 元</dd>
						<dd>成功投资产品：${invCountString } 个</dd>
						<dd>已结算投资产品：${accountString } 个</dd>
						<dd>未结算投资产品：${unAccountString } 个</dd>
						<dd>总利息：${sumInterest} 元</dd>
						<dd>已支付利息：${payInterest} 元</dd>
						<dd>未支付利息：${unPayInterest} 元</dd>
						<dd>邀请奖励：${sumInviteEarn} 元</dd>
						<dd>已过期的红包的总金额：${pastHongbao} 元</dd>
					</dl>
					<dl>
						<dd>是否为异常账户：
							<c:if test="${isDiff eq 0 }">不是</c:if>
							<c:if test="${isDiff eq 1 }"><font color="#ff0000"><strong>是</strong></font></c:if>
						</dd>
						<dd>账户偏差值：${diff} 元</dd>
						<dd>零钱包余额：${coinPurseMoney}</dd>
						<dd>零钱包收益：${lqgSum}</dd>
						<dd>提现总次数：${txCount }</dd>
						<dd>提现总金额：${txSum } 元</dd>
						<dd>提现待审核总金额：${txCheckSum } 元</dd>
						<dd>可用总积分：${totalPoint}</dd>
						<dd>已使用的总积分：${usedPoints}</dd>
						<dd>已使用的投资券总金额：${usedCouponString} 元</dd>
						<dd>未使用的投资券的总金额：${unusedCouponString} 元</dd>
						<dd>已过期的投资券的总金额：${pastCouponString} 元</dd>
						<dd>已使用的红包总金额：${usedHongbao} 元</dd>
						<dd>未使用的红包的总金额：${unusedHongbao} 元</dd>
						
					</dl>
					<div class="clea"></div>
					<br>
					<h3 style="margin-left: 30px;">1.账户偏差值:"+"代表平台少给钱了;"-"代表多拿了平台的钱;</h3>
					<h3 style="margin-left: 30px;">2.如果不是异常账户;但冻结资金不一致,如果是产品冻结资金比账户冻结资金要多,则说明此用户当时提现没有扣款,继续拿那笔钱进行投资;(建议把可用余额设置为多拿那笔钱的负数,)</h3>
				</div>
				
			</div>
			
		</div>	
</body>


<script type="text/javascript">

function addBlack(){
	
	var url="${pageContext.request.contextPath}/Product/Admin/account!addBlack.action?";
	var formData = "users.id="+${users.id};
	
	$.post(url,formData,function(data){
		/* alert(formData);  */
 		 if(data.status=="ok"){
				alert(data.json);
				location.reload();
		 }else{
			 alert(data.json);
			 return false;
		 }
	 });
	
} 

</script>
</html>