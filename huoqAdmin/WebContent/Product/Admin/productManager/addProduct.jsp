<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/zlqEl.tld" prefix="myel"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>添加预约产品规则</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.7.2.min.js"></script>
<link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css" />
<link href="${pageContext.request.contextPath}/Product/Admin/css/product.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/Product/js/alert.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/Product/css/alert.css">
<script src="${pageContext.request.contextPath}/js/artDialog4.1.7/jquery.artDialog.js?skin=default"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/qwyUtilJS/qwyUtil.js"></script>
<style type="text/css">
.a{
		color: blue;
		text-decoration: underline ;
	}
</style>
<script type="text/javascript">
	
	/* 添加关键字时非空检查 */
	function isSubmit(){
		var keyword = $("#keyword").val();
		if(keyword==''){
			alert("请输入预约关键字");
			return false;
		}
	}
	/* 添加关键字时非空检查   end */

	/* 根据ID修改状态 */
	function updateStatus(id,title){
		$.ajax({
			type : "get",
			url : "${pageContext.request.contextPath}/Product/Admin/releaseProduct!updateStatusById.action?bookingKeyword.id="+id,
			success : function(data) {
				if(data.status == 'ok'){
					alert(title+"成功");
					location.reload();
				}else{
					alert(title+"失败");
					return false;
				}
			}
		});
	}
	/* 根据ID修改状态     end  */

</script>

</head>
<body>

<div class="center">	
	<jsp:include page="/Product/Admin/common/head.jsp"/>		
	<div class="main" align="center" style="text-align: center;">
	  <h3 >添加预约产品规则</h3>
		<form action="${pageContext.request.contextPath}/Product/Admin/releaseProduct!addProduct.action" method="post" onsubmit="return isSubmit();">
			<ul id="tab">
				<li>
					<label>预约关键字:</label>
					<input type="text" id="keyword" name="bookingKeyword.keyword" maxlength="10">
					<label><input type="submit" value="提交" ></label><br/>
				</li>
			</ul>
		</form>
		
		<br/> <br/>
		<div align="center" class="main">
		<table border="1">
			<tr>
				<td width="50px" style="text-align: center;">序号</td>
				<td width="100px" style="text-align: center;">关键字</td>
				<td width="100px" style="text-align: center;">状态</td>
				<td width="200px" style="text-align: center;">类型</td>
				<td width="300px" style="text-align: center;">插入时间</td>
				<td width="300px" style="text-align: center;">修改时间</td>
				<td width="100px" style="text-align: center;">操作</td>
			</tr>
		<c:forEach items="${list}"  var="item" varStatus="i">
			<tr>
				<td style="text-align: center;">${i.index + 1}</td>
				<td style="text-align: center;">${item.keyword}</td>
				<td style="text-align: center;">
					<c:if test="${item.status eq 0}">已启动</c:if>
					<c:if test="${item.status eq 1}">未启动</c:if>
				</td>
				<td style="text-align: center;">
					<c:if test="${item.type eq 0}">默认</c:if>
				</td>
				<td style="text-align: center;">${item.insertTime}</td>
				<td style="text-align: center;">${item.updateTime}</td>
				<td style="text-align: center;">
					<c:if test="${item.status eq 0}"><a href="javascript:updateStatus('${item.id}','禁用')" class="a">禁用</a></c:if>
					<c:if test="${item.status eq 1}"><a href="javascript:updateStatus('${item.id}','启动')" class="a">启动</a></c:if>
				</td>
			</tr>
		</c:forEach>
		</table>
		</div>
		
	</div>
	
	</div>
</body>
<script type="text/javascript">
	$(function(){
		var message='${message}';
		if(message!=''){
			alert(message);
            window.location.href="${pageContext.request.contextPath}/Product/Admin/releaseProduct!getBookingKeywordList.action";

		}
	});
</script>
</html>