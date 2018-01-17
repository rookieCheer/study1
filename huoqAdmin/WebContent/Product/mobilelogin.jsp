<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>新华金典-后台登录</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no"/> 
	<title>登录</title>
	<script src="${pageContext.request.contextPath}/Product/js/jquery-1.9.1.min.js"></script>
	<style>
		*{
		    margin:0;
		    padding: 0;
		}
		html,body,p,h1,h2,h3,h4,h5,h6,tr,td,dl,dd,ul,li,ol,span,a{
		    font-family: "微软雅黑";
		}
		input{
		    outline: none;
		}
		a{
		    text-decoration: none;
		}
		li{
		    list-style: none;
		}
		html{
			font-size: 100%;
		}
		body{
			background-color: #f5f5f5;
		}
		.viewport{
			width: 100%;
			min-height: 100%;
		}
		.logo-box{
			width: 100%;
			height: 100px;
			vertical-align:middle;
			text-align: center;
			margin-top: 20px;
		}
		.logo{
		}
		h4{
			color:#4A4A4A;
			font-size:1.6rem;
			font-weight: 700;
			text-align: center;
			letter-spacing: 1px;
		}
		.content{
			width: 100%;
			min-height: 300px;
			margin-top:100px;
		}
		.login-box{
			width: 100%;
			overflow: hidden;
		}	
		.login-box input{
			width: 80%;
			height: 46px;
			line-height: 46px;
			padding-left: 20px;
			display: block;
			margin:20px auto;
			border:1px solid #ccc;
			border-radius:23px;
			box-sizing: border-box;
		}
		.login-box  .login-btn{
			color:#fff;
			font-size:1.2rem;
			letter-spacing: 5px;
			background-color: #f04c38;
			display: block;
			padding:0;
		}
	</style>
</head>
<body>
	<div class="viewport">
		<div class="logo-box">
			<img src="${pageContext.request.contextPath}/Product/images/logo_q.png" alt="" class="logo">
		</div>
		<div class="content">
			<h4>后台管理员登录</h4>
			<div class="login-box">
				<form action="" method="post">
					<input type="text" id="username" class="username" name="users.username" placeholder="用户名">
					<input type="password" id="password" class="password" name="users.password" placeholder="密码">
					<input type="button" class="login-btn" value="登录" onclick="adminLogin();">
				</form>
			</div>
		</div>
	</div>
</body>
<script type="text/javascript">
$(function(){
	$('input').bind('click',function(e){
	    var $this = $(this);
	    e.preventDefault();
	    setTimeout(function(){
	        $(window).scrollTop($this.offset().top - 10);
	    },200)
	})
})

if("${status}"=="not"){
	alert("帐号或密码不正确!");
}else if("${status}"=="err"){
	alert("登录失败!");
}
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
</script>
</html>