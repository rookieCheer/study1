<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%> 	
<!doctype html>
<html>
<head>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/Product/images/favicon.ico" type="image/x-icon"/>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>新华金典-后台登录</title>		
<link href="${pageContext.request.contextPath}/Product/css/public.css" rel="stylesheet" type="text/css" />		
<script src="${pageContext.request.contextPath}/Product/js/jquery-1.9.1.min.js"></script>
<script type="text/javascript">
$(function(){
	browserRedirect("${pageContext.request.contextPath}/Product/mobilelogin.jsp");
})
  function adminLogin(){
	  var url="${pageContext.request.contextPath}/Product/loginBackground!adminLogin.action?";
	  var username=$("#username").val();
	  var password=$("#password").val();
	  if(username==null||""==username){
		  alert("用户名不能为空");
		  return false;
	  }
	  if(password==null||""==password){
			 alert("密码不能为空");
			 return false;
		 }
	  var formData="users.username="+username;
	  formData+="&users.password="+password;
	  $.post(url,formData,function(data){
			 if("ok"==data.status){			 
				 window.location.href="${pageContext.request.contextPath}/Product/Admin/login/welcome.jsp";
			 }else{			 
				 	alert(data.json);
				 	//$("#username").val("");
				 	//$("#password").val("");
				 	return false;
			 }
		 });
	  
  }
  function browserRedirect(url) {
		//只读的字符串，声明了浏览器用于 HTTP 请求的用户代理头的值
		var sUserAgent = navigator.userAgent.toLowerCase();
		var bIsIphoneOs = sUserAgent.match(/iphone os/i) == "iphone os";
		var bIsMidp = sUserAgent.match(/midp/i) == "midp";
		var bIsUc7 = sUserAgent.match(/rv:1.2.3.4/i) == "rv:1.2.3.4";
		var bIsUc = sUserAgent.match(/ucweb/i) == "ucweb";
		var bIsAndroid = sUserAgent.match(/android/i) == "android";
		var bIsCE = sUserAgent.match(/windows ce/i) == "windows ce";
		var bIsWM = sUserAgent.match(/windows mobile/i) == "windows mobile";
		if (bIsIphoneOs || bIsMidp || bIsUc7 || bIsUc || bIsAndroid || bIsCE || bIsWM) {
			window.location.replace(url);
		}
	}
  
</script>
</head>
<body class="bg">
<div class="center">
	<div class="fl">
	 	<div class="logo">
		 	<img src="${pageContext.request.contextPath}/Product/images/logo_w.png"/>
		 	<p class="company-name">新华金典财富管理股份有限公司</p>
		 </div> 
	</div> 
	<div class="fr">
		<div class="login-form">
			<h1 class="login-title">后台管理员登录</h1>
			<form action="" method="post" id="loginForm">
				<p class="icon">
					<input type="text" id="username" class="input" name="users.username" placeholder="请输入用户名"/>
				</p>
				<p class="icon">	
					<input type="password" id="password" class="input" name="users.password" placeholder="请输入登录密码"/>
				</p>
				<p>	
					<input type="button" class="input login-btn" value="登录" onclick="adminLogin();"/>
				</p>	
			</form>
		</div>
	</div>
</div>
</body>
</html>