<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no"/> 
	<title>登录</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/Product/Admin/css/common.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/Product/Admin/css/index.css">
</head>
<body>
	<div class="viewport">
		<div class="logo-box">
			<img src="${pageContext.request.contextPath}/Product/images/logo_q.png" alt="" class="logo">
		</div>
		<div class="content">
			<h4>后台管理员登录</h4>
			<div class="login-box">
				<form action="${pageContext.request.contextPath}/Product/loginBackground!login.action" method="post">
					<input type="text" class="username" name="users.username" placeholder="用户名">
					<input type="password" class="password" name="users.password" placeholder="密码">
					<input type="submit" class="login-btn" value="登录">
				</form>
			</div>
		</div>
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