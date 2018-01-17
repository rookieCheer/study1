<%--
  Created by IntelliJ IDEA.
  User: yks
  Date: 2016/11/8
  Time: 13:22
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<!doctype html>
<html>
<head>
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/Product/images/favicon.ico" type="image/x-icon"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>新华网-访客登录</title>
    <link href="${pageContext.request.contextPath}/Product/css/public.css" rel="stylesheet" type="text/css" />
    <script src="${pageContext.request.contextPath}/Product/js/jquery-1.9.1.min.js"></script>
    <script type="text/javascript">
        function adminLogin(){
            var url="${pageContext.request.contextPath}/Product/Guest/guestLoginBackground!adminLogin.action?";
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
            var formData="guest.username="+username;
            formData+="&guest.password="+password;
            $.post(url,formData,function(data){
                if("ok"==data.status){
                    toUrl();
                }else{
                    alert(data.json);
                    return false;
                }
            });
        }
        function  toUrl() {
            var url = "${pageContext.request.contextPath}/Product/Guest/investorsGuest!investChannel.action";
            window.location.href = url;
        }
    </script>
</head>
<body>
<div class="center">
    <div class="logo">
        <img src="${pageContext.request.contextPath}/Product/images/logo.png"/><span>杭州奥发金融服务外包有限公司</span>
    </div>
    <div class="main" align="center" >
        <h1>访客登录</h1>
        <form action="" method="post">
            <table border="1">
                <tr>
                    <td>用户名：</td>
                    <td><input type="text" id="username" name="guest.username"/></td>
                </tr>
                <tr>
                    <td>密码：</td>
                    <td><input type="password" id="password" name="guest.password" /></td>
                <tr>
                    <td colspan="2" style="text-align: center;"><input type="button" value="登录" onclick="adminLogin();" style="width:200px;height:30px;"/></td>
                </tr>
            </table>
        </form>
    </div>
</div>

</body>
</html>
