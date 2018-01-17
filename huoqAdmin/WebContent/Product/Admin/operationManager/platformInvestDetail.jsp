<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css" />		
<link href="${pageContext.request.contextPath}/Product/Admin/css/product_fabu_history.css" rel="stylesheet" type="text/css" />
<script src="${pageContext.request.contextPath}/Product/Admin/js/jquery-1.9.1.min.js"></script>
<script src="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.standalone.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.css" type="text/css">
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

table{border-collapse:collapse;empty-cells:show;}
table tr td{border:1px solid #555;}
</style>
<script type="text/javascript">
function queryProduct(){
	var payTime=$("#payTime").val();
    var platform=$("#platform").val();
//    alert(platform);
	var insertTime=$("#insertTime").val();
	var status=$("#status").val();
	var url = "${pageContext.request.contextPath}/Product/Admin/platfromInversors!loadPlatfromInversors.action?payTime="+payTime+"&platform="+platform;
	window.location.href=url;
}
$(function(){
	 var status='${status}';
		// alert(tj1);
		// alert(annualEarnings);
		 if(platform!=''){
		 	$("#platform").find("option[value='"+platform+"']").attr("selected",true);
		 }
         if(status!=''){
           $("#status").find("option[value='"+status+"']").attr("selected",true);
        }
	$("#payTime,#status,#platform").keydown(function(event){
		if(event.keyCode==13){
			queryProduct(); 
		}
	});
});
</script>
<title>平台投资情况</title>
</head>
<body>
<div class="center">		
	<jsp:include page="/Product/Admin/common/head.jsp"/>	
	<div class="main" align="center">
	<h3>平台投资情况</h3>
	<form class="form" id="frm" method="post">
		<%-- <span class="select1" >注册时间:</span> <input id="insertTime" name="insertTime" type="text" value="${insertTime}"> --%>
		<span class="select1" >查询时间:</span> <input id="payTime" name="payTime" type="text" value="${payTime}">
			<select name="platform" id="platform">
				<option value="" <c:if test="${platform eq '' }">selected</c:if>>请选择投资平台</option>
				<option value="0" <c:if test="${platform eq '0' }">selected</c:if>>web</option>
				<option value="1" <c:if test="${platform eq '1' }">selected</c:if>>android</option>
				<option value="2" <c:if test="${platform eq '2' }">selected</c:if>>IOS</option>
				<option value="3" <c:if test="${platform eq '3' }">selected</c:if>>微信</option>

				<!-- <option value="" >请选择投资平台</option>
            <option value="0">web</option>
            <option value="1">android</option>
            <option value="2">IOS</option>
            <option value="3">微信</option>    -->
        </select>
    <!-- 投资状态：
    <select class="select1" id="status" name="status">
        <option value="">全部</option>
        <option value="1">已付款</option>
        <option value="3">已结算</option>
    </select> -->
		<a class="sereach" href="javascript:queryProduct();" id="sereach">查询</a>
		<input type="button" value="更新当天数据" id="updateQdtjPlatformToday">
	</form>
	<table cellpadding="0" cellspacing="0">
				<tbody >
					<tr>
						<td width="100px" style="text-align: center;">序号</td>
						<td width="200px" style="text-align: center;">投资平台</td>
						<td width="200px" style="text-align: center;">激活人数(人)</td>
						<td width="200px" style="text-align: center;">注册人数(人)</td>
						<td width="200px" style="text-align: center;">首投人数(人)</td>
						<td width="200px" style="text-align: center;">首投金额（元）</td>
						<td width="200px" style="text-align: center;">投资金额（元）</td>
						<td width="300px" style="text-align: center;">累计存量（充值 - 提现）</td>
					</tr>
					
					<c:forEach items="${list}"  var="item" varStatus="i">
					<tr>
						<td style="text-align: center;">${i.index+1}</td>
						<td style="text-align: center;">
							<c:choose>
								<c:when test="${item[0] eq '0'}">web</c:when>
								<c:when test="${item[0] eq '1'}">android</c:when>
								<c:when test="${item[0] eq '2'}">IOS</c:when>
								<c:when test="${item[0] eq '3'}">微信</c:when>
								<c:otherwise>${item[0]}</c:otherwise>
							</c:choose>
						</td>
						<td style="text-align: center;"><fmt:formatNumber value="${item[1]}" pattern="#,##0" /></td>
						<td style="text-align: center;"><fmt:formatNumber value="${item[2]}" pattern="#,##0" /></td>
						<td style="text-align: center;"><fmt:formatNumber value="${item[3]}" pattern="#,##0" /></td>
						<td style="text-align: center;"><fmt:formatNumber value="${item[4]*0.01}" pattern="#,##0.#" /> </td>
						<td style="text-align: center;"><fmt:formatNumber value="${item[5]*0.01}" pattern="#,##0.#" /> </td>
						<td style="text-align: center;"><fmt:formatNumber value="${item[6]*0.01}" pattern="#,##0.#" /> </td>
					</tr>
				</c:forEach>
					<tr>
						<td style="text-align: center;">合计</td>
						<td style="text-align: center;"></td>
						<td style="text-align: center;"><fmt:formatNumber value="${hj[0]}" pattern="#,##0" /></td>
						<td style="text-align: center;"><fmt:formatNumber value="${hj[1]}" pattern="#,##0" /></td>
						<td style="text-align: center;"><fmt:formatNumber value="${hj[2]}" pattern="#,##0" /></td>
						<td style="text-align: center;"><fmt:formatNumber value="${hj[3]*0.01}" pattern="#,##0.#" /></td>
						<td style="text-align: center;"><fmt:formatNumber value="${hj[4]*0.01}" pattern="#,##0.#" /></td>
						<td style="text-align: center;"><fmt:formatNumber value="${hj[5]*0.01}" pattern="#,##0.#" /></td>
					</tr>
					
					<%-- <tr>
						<td width="50px" style="text-align: center;">序号</td>
						<td width="200px" style="text-align: center;">投资平台</td>
						<td width="100px" style="text-align: center;">投资金额</td>
					</tr>
				<c:forEach items="${list}"  var="item" varStatus="i">
					<tr>
						<td style="text-align: center;">${i.index+1}</td>
						<td style="text-align: center;">${item.registPlatform}</td>
						<td style="text-align: center;"><fmt:formatNumber value="${item.platformInvest}" pattern="#,##0.#" /> </td>
					</tr>
				</c:forEach>
				</tbody>
				<tr>
					<td colspan="2" style="text-align: left;">总计</td>
					<td ><fmt:formatNumber value="${collectMoney}" pattern="#,##0.#" /></td>
				</tr> --%>
			</table>
	</div>
</div>
</body>
<script type="text/javascript">
	var k4 = new Kalendae.Input("payTime", {
		attachTo:document.body,
		months:2,//多少个月显示出来,即看到多少个日历
		mode:'range'
		/* selected:[Kalendae.moment().subtract({d:7}), Kalendae.moment().add({d:0})] */
	});
	 
	
	 $("#updateQdtjPlatformToday").click(function () {
		
	        var url = "${pageContext.request.contextPath}/Product/Admin/qdtjPlatform!updateQdtjPlatformToday.action";
	        $.post(url, null, function (data) {
	            alert(data.json);//结果!
	            window.location.reload();
	        });
	    });
	 
	 
</script>
</html>