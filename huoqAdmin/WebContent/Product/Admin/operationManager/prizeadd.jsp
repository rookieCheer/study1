<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/zlqEl.tld" prefix="myel"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>添加奖品</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.7.2.min.js"></script>
<link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css" />
<link href="${pageContext.request.contextPath}/Product/Admin/css/product.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/Product/js/alert.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/Product/css/alert.css">
<script src="${pageContext.request.contextPath}/js/artDialog4.1.7/jquery.artDialog.js?skin=default"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/qwyUtilJS/qwyUtil.js"></script>
<script type="text/javascript">
function isSubmit(){
	var prizeName=$("#prizeName").val();
	var type=$("#type").val();
	var winningRate=$("#winningRate").val(); 
	var payRate=$("#payRate").val(); 
	var serialNum=$("#serialNum").val(); 
	if(prizeName==''){
		alert("请输入奖品名称");
		return false;
	}
	if(type==''){
		alert("请输入类型");
		return false;
	}
	if(!isPrice(winningRate)){
		alert("请输入正确的百分百比");
		return false;
	}else{
		if(winningRate>100){
			alert("请输入正确的百分百比");
			return false;
		}
	}
	if(!isPrice(payRate)){
		alert("请输入正确的百分百比");
		return false;
	}else{
		if(payRate>100){
			alert("请输入正确的百分百比");
			return false;
		}
	}
	if(!isOnlyNumber(serialNum)){
		alert("请输入正确的排序");
		return false;
	}
	

}
</script>
</head>
<body>

<div class="center">	
	<jsp:include page="/Product/Admin/common/head.jsp"/>		
	<div class="main" align="center" style="text-align: center;">
	  <h3 align="center">添加奖品</h3>
		<form method="post" action="${pageContext.request.contextPath}/Product/Admin/prize!addPrize.action" onsubmit="return isSubmit();">
			<ul id="tab">
				<li>
					<label>奖品名称:</label>
					<input type="text" id="prizeName" name="prize.prizeName" maxlength="10">*必填
				</li>
								
				<li>
					<label>类        型:</label>
					<input type="text" id="type" name="prize.type" maxlength="10">*必填
				</li>
				<li>
					<label>免费中奖概率:</label>
					<input type="text" id="winningRate" name="prize.winningRate">% *必填
				</li>
				<li>
					<label>付费中奖概率:</label>
					<input type="text" id="payRate" name="prize.payRate">% *必填
				</li>
				<li>
					<label>排        序:</label>
					<input type="text" id="serialNum" name="prize.serialNum">
				</li>				

				<li>
					<label colspan="2"><input type="submit" value="提交"></label>
				</li>
			</ul>
		</form>
	</div>
	</div>
</body>
<script type="text/javascript">
	$(function(){
		var message='${message}';
		if(message!=''){
			alert(message);
            window.location.href="${pageContext.request.contextPath}/Product/Admin/operationManager/prizeadd.jsp";

		}
	});
</script>
</html>