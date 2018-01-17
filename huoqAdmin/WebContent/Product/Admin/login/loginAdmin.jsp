<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css" />
<link href="${pageContext.request.contextPath}/Product/Admin/css/login.css" rel="stylesheet" type="text/css" />
<script src="${pageContext.request.contextPath}/Product/Admin/js/jquery-1.9.1.min.js"></script>
<script src="${pageContext.request.contextPath}/Product/Admin/js/login.js"></script>
<title>新华金典后台登陆</title>
<script type="text/javascript">
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
browserRedirect("${pageContext.request.contextPath}/Product/Admin/login/login.jsp")
</script>

</head>
<body>
	<div class="logo">
			<img src="${pageContext.request.contextPath}/Product/Admin/img/logo.png"/><span>新华金典财富管理有限公司</span>
		</div>
		<div class="nam">
			<p>后台管理系统</p>
			<div><span></span></div>
		</div>
		<div class="login" style="text-align: center;">
			<form action="${pageContext.request.contextPath}/Product/loginBackground!login.action" method="post">
				<p>登陆</p>
				<ul class="user">
					<li><span>请输入登陆账号</span><input type="text" name="users.username"  id="username"/></li>
					<li><span>请输入登陆密码</span><input type="password"  name="users.password" id="password"/></li>
				</ul>
				<!-- <div><a href="#">忘记密码?</a></div> -->
				<input type="submit" value="登录" style="width:200px;height:30px;"/>
			</form>			
		</div>
</body>
<script type="text/javascript">

if("${status}"=="not"){
	alert("帐号或密码不正确!");
}else if("${status}"=="err"){
	alert("登录失败!");
}

</script>
</html>