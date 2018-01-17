<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>群发短信</title>
<link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css" />
<link href="${pageContext.request.contextPath}/Product/Admin/css/product.css" rel="stylesheet" type="text/css" />
<script src="${pageContext.request.contextPath}/Product/Admin/js/jquery-1.9.1.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/qwyUtilJS/qwyUtil.js"></script>
<script>
//禁用或启用
function send(){
    var smsContent=$("#smsContent").val();
/*     if(!isPhoneNumber(mobile)){
		 alert("填入的用户帐号不是手机号格式！");
		 return false;
	 } */
    if(smsContent==null||""==smsContent){
		 alert("填入短信内容！");
		 return false;
    }
	 
	var userType = $('#type input[name="userType"]:checked').val();
	//alert(userType);
	
    var formData="smsRecord.mobile="+userType;
        formData+="&smsRecord.smsContent="+smsContent;
	 var url="${pageContext.request.contextPath}/Product/Admin/querybalance!sendMassMessage.action";
	// alert(formData);
		$.post(url,formData,function(data){
			 if("ok"==data.status){
				 alert("短信发送成功");
				 location.reload();
			 }else if("error"==data.status){			 
				 	alert(data.json);
				 	return false;
			 }
		 }); 
}
</script>
</head>
<body>
<%-- <jsp:include page="top.jsp" /> --%>
<div class="center">			
	<jsp:include page="/Product/Admin/common/head.jsp"/>
	<div class="main" align="center">
	</div>
			<ul align="center">
				<li id="type">
					发送对象：
					<!-- <select>
						<option value = "0" selected="selected">平台所有用户</option>
						<option value = "1">绑卡用户</option>
						<option value = "2">绑卡未投资用户</option>
						<option value = "3">投资用户</option>
					</select> -->
					<label><input type="radio" name="userType" checked="checked" value="0">平台所有用户</label>
					<label><input type="radio" name="userType" value="1">绑卡用户</label>
					<label><input type="radio" name="userType" value="2">绑卡未投资用户</label>
					<label><input type="radio" name="userType" value="3">投资用户</label>
				</li>
			</ul>
			<ul id="tab" align="center">
				<!-- <li>
					<label>手机号</label>
					<label><textarea id="mobile" name= "smsRecord.mobile" cols= "40 " rows= "10 " placeholder="请输入正确的手机号码"></textarea>
				</li> -->
				<li>
					<label>短信内容</label>
					
					<label><textarea id="smsContent" name= "smsRecord.smsContent" maxlength="500" cols= "40 " rows= "10 " placeholder="一条短信67个长度,最前面默认追加'【新华金典网】'"></textarea>
					</label>
					  *目前长度：<span id="contentLenght">5</span>
				</li>				
				<li>
					<label colspan="2"><input type="button" onclick="send()" value="发送"></label>
				</li>
			</ul>
</div>
</body>
<script type="text/javascript">
$(document).ready(function() { 
$("#smsContent").change(function() {//发生改变时触发
	var smsContent=$("#smsContent").val();
	var contentLenght=smsContent.length+5;
	    $("#contentLenght").text(contentLenght);
	});
});
</script>
</html>