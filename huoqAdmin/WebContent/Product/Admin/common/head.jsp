<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="s" uri="/struts-tags"%> 
<%@ taglib uri="/WEB-INF/zlqEl.tld" prefix="myel"%>
<!-- <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd"> -->
<html>
<head>
<script type="text/javascript" src="${pageContext.request.contextPath}/Product/js/alert.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/Product/css/alert.css">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<s:action name="loginBackground!getRolesRight" namespace="/Product" />
</head>
<body>
    <div class="logo">
        <img src="${pageContext.request.contextPath}/Product/images/newLogo.png"/><span>新华金典财富管理后台</span> 
         <p align="right" style="margin-right: 20px;padding-right: 20px;font-size: 16px;font-weight: 1">你好,${myel:jieMiUsername(usersAdmin.username)}	</p>
        
    </div>
    <div class="head">
        <ul class="head_list">
        <%-- <li><a href="${pageContext.request.contextPath}/Product/Admin/login/welcome.jsp">首页</a></li> --%>
        <!-- 遍历一级标题 -->
        <c:forEach items="${firstModul}" var="fm">
                <!-- 一级标题 -->
                <li>
                <c:choose>
                    <c:when  test="${'' ne fm.modulPath && fm.modulPath ne '#'}">
                        <a href="${pageContext.request.contextPath}${fm.modulPath}">${fm.modulName}</a>
                    </c:when>
                    <c:otherwise>
                        <a href="#">${fm.modulName}</a>
                    </c:otherwise>
                </c:choose>
                <ul class="hide_list">
                <c:forEach items="${userModul}" var="um">
                    <c:choose>
                        <c:when test="${fm.id eq um.parentId && um.type eq 2 && um.modulPath eq '#' }">
                        <!-- 二级标题 -->
                            <li>
                                <a href="#">${um.modulName}</a>
                                <ul class="hide2_list" style="z-index: 10;">
                                <c:forEach items="${userModul}" var="um2">
                                    <c:if test="${um2.type eq 3 && um2.parentId eq um.id}">
                                    <!-- 三级标题 -->
                                    <li><a href="${pageContext.request.contextPath}${um2.modulPath}">${um2.modulName}</a></li>
                                    </c:if>
                                </c:forEach>
                                </ul>
                            </li>
                        </c:when>
                        <c:when test="${fm.id eq um.parentId && um.type eq 2 && um.modulPath ne '#' }">
                            <li><a href="${pageContext.request.contextPath}${um.modulPath}">${um.modulName}</a></li>
                        </c:when>
                    </c:choose>
                </c:forEach>
                    <div class="clea"></div>
                </ul>
                </li>
        </c:forEach>
            <div class="clea"></div>
        </ul>
        <a href="${pageContext.request.contextPath}/Product/loginBackground!exitLogin.action">退出</a>
        <div class="bread">我的位置：首页&gt;</div>				
        <script>
            $(function(){
                $(".head_list > li").mouseover(function(){
                    $(this).children(".hide_list").css("display","block");
                }).mouseout(function(){
                    $(this).children(".hide_list").css("display","none");
                });
                
                $(".hide_list > li").mouseover(function(){
                    $(this).children(".hide2_list").css("display","block");
                }).mouseout(function(){
                    $(this).children(".hide2_list").css("display","none");
                });
                
            })
            $("a").click(function(){
                i=0;
                var href =$(this).attr("href");
                if(href!='#'){
                    var text = "<img src='${pageContext.request.contextPath}/Product/images/progress.gif' width='24' height='24'> <span style='font-family:楷体;font-size:22px;' id='my_alter_span'>正在加载,请稍等...</span>";
                    var id = chuangkou(text,"正在加载...","",0,true);
                    setInterval("sTime()",100);
                }
            });
            var i=0;
            function sTime(){
                i++;
                $("#my_alter_span").html("正在加载,请稍等...<br>耗时"+(i/10)+"秒");
                //console.log("i: "+i);
            }
        </script>
    </div>	
</body>
</html>