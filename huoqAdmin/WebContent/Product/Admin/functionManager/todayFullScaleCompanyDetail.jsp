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

<link rel="stylesheet" href="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.css" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/Product/Admin/js/jquery-1.9.1.min.js"></script>
<!-- 引入jquery.js -->
<script type="text/javascript" src="${pageContext.request.contextPath}/Product/js/alert.js"></script>
<script src="${pageContext.request.contextPath}/js/artDialog4.1.7/jquery.artDialog.js?skin=blue"></script>
<title>今日满标企业详情</title>
<head>
</head>
<body>

<div class="center">			
	<jsp:include page="/Product/Admin/common/head.jsp"/>
	<div class="main" align="center">
		<h1 style="text-align: center;">今日满标企业详情</h1>
		
		<input type="button" value="导出报表" onclick="exportExcel()">

		</div>

		<table style="width:100%;margin-top: 20px;text-align: center;" border="1px;">
			<tr>
				<td>序号</td>
				<td>借款公司</td>
				<td>法人</td>
				<td>借款额度(元)</td>
				<td>标的类型</td>
				<td>子标数目</td>
				<td>标的编号</td>
				<td>满标时间</td>
				<td>到期时间</td>
				<td>企业到期时间</td>
				<td>企业回款时间</td>
				<td>虚拟投资金额(元)</td>
				<td>实际投资金额(元)</td>
				
			</tr>
			<c:if test="${not empty companyList}">
			<c:forEach items="${companyList}" var="list">
				<tr>
					<td width="5%" >${i.count + (pageUtil.currentPage-1)*pageUtil.pageSize}</td>
					<td width="6%" >
					${list.companyName}
					</td>
					<td width="3%" >${list.legalPerson}</td>
					<td width="3%" >${list.browLimit}</td>
					<td width="3%" >${list.type}</td>
					<td width="3%" >${list.childBidNumber}</td>
					<!-- 四行一列  tr 行 td 列-->
					<td colspan="3">
					  <table style="width:100%;" border="1px;">
					  <c:forEach items="${list.innerMessage}" var="innerList">
					       <tr>
					          <td width="33.3%">${innerList.number}</td>
                              <td width="33.3%">
                                 <fmt:formatDate value="${innerList.expiringDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
                              </td>
                               <td width="33.3%">
                                  <fmt:formatDate value="${innerList.fullTagDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
                               </td>
                           </tr>
                      </c:forEach>
                      </table>
                    </td>
				    <td width="10%" ><fmt:formatDate value="${list.companyDueTime}" pattern="yyyy-MM-dd"/></td>
					<td width="10%" ><fmt:formatDate value="${list.backMoneyTime}" pattern="yyyy-MM-dd"/></td>
					<td>
					 <table style="width:100%;" border="1px;">
                      <c:forEach items="${list.innerMessage}" var="innerList" varStatus="p">
                     
                      <tr>
                          <td>
                             ${innerList.virtualInvest}&nbsp;
                          </td>
                          </tr>
                      </c:forEach>
                       </table>
                      </td>
                    <td width="10%" >${list.realInvest}</td>
				</tr>
			</c:forEach>
			</c:if>
		</table>
		
	</div>



		
		<script type="text/javascript">
	
	
</script>
</body>
</html>