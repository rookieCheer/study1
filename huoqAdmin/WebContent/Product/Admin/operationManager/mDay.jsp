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
<script src="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.standalone.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.css" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/Product/js/alert.js"></script>
<script src="${pageContext.request.contextPath}/js/artDialog4.1.7/jquery.artDialog.js?skin=blue"></script>
<style type="text/css">
.a {
  color: blue;
  text-decoration: underline;
}
table{border-collapse:collapse;empty-cells:show;}
table tr td{border:1px solid #555;}
</style>

<title>母亲节留言记录</title>

	<script type="text/javascript">
        function ireportDo() {
            var list = '${table}';
            if (list != '1') {
                alert("无数据");
                return false;
            }
            var url = '${pageContext.request.contextPath}/Product/Admin/bannerActivity!iportMdayData.action?';
            var list = "${list}";
            var my = art.dialog({
                title: '提示',
                content: document.getElementById("psi_load"),
                height: 60,
                lock: true,
                cancel: false,
            });
            
            $.post(url, $("#frm").serialize(), function (data) {
                my.close();
                data = '${pageContext.request.contextPath}'+data;
                var ssss = "导出成功&nbsp;&nbsp;&nbsp;&nbsp;<a href='" + data + "' style='color:red;'>点击下载</a>";
                art.dialog({
                    title: '提示',
                    content: ssss,
                    height: 60,
                    lock: true,
                    ok: function () {
                        //mysss.close();
                    },
                });
            });

        }
    </script>

<script type="text/javascript">
function Byname(){
	var username = $("#username").val();
	var insertTime = $("#insertTime").val();
	var url="${pageContext.request.contextPath}/Product/Admin/bannerActivity!getMDay.action?username="+username+"&insertTime="+insertTime;
		
	window.location.href=url;
	}
</script>


</head>
<body>

<div class="center">			
	<jsp:include page="/Product/Admin/common/head.jsp"/>
	
	<div class="main" align="center">
		<h3 style="text-align: center;">母亲节留言记录</h3><br/>
		<div id="div_condition" style="text-align: center;" >
		</div>
		 <form class="form" id="frm" method="post">
		<span>用户名:</span> <input id="username" name="username" type="text" value="${username}"/>
		<span>注册时间:</span> <input id="insertTime" name="insertTime" type="text" value="${insertTime}"/>
		<a class="sereach" href="javascript:Byname();" id="sereach">查询</a>
		<input class="input-button" type="button" value="导出报表" onclick="ireportDo()"><br/><br/>
		<br/><br/>
		</form>
		<table  cellpadding="0" cellspacing="0" >
		
		<tr>
		<td width="50px" height="20" style="text-align: center;">序号</td>
		<td width="200px" style="text-align: center;">用户名</td>
		<td width="100px" style="text-align: center;">真实姓名</td>
		<td width="200px" style="text-align: center;">留言时间</td>
		<td width="500px" style="text-align: center;">留言内容</td>
		</tr>
		
		<c:forEach items="${list}" var="item" varStatus="i">
		
		<tr>
		<td width="50px" height="20" style="text-align: center;">${i.count + (pageUtil.currentPage-1)*pageUtil.pageSize}</td>
		<td width="200px" style="text-align: center;"><a class="a"  href="${pageContext.request.contextPath}/Product/Admin/userStat!loadUserInfo.action?username=${myel:jieMiUsername(item[0])}">${myel:jieMiUsername(item[0])}</a></td>
		<td width="100px" style="text-align: center;">${item[1]}</td>
		<td width="100px" style="text-align: center;">${item[2]}</td>
		<td width="100px" style="text-align: center;">${item[3]}</td>
		</tr>
		</c:forEach>
		</table>
		<c:choose>
			<c:when test="${list ne '[]' && list ne '' && list ne null}"><jsp:include page="/Product/page.jsp" /></c:when>
			<c:otherwise>
				<div style="text-align: center;margin-top: 15px;"><!-- <img src="images/lh.jpg"> -->
			  		<img src="../images/no_record.png" />
			  	</div>
			</c:otherwise>
		</c:choose>
	</div>
</div> 

<script type="text/javascript">
	var k4 = new Kalendae.Input("insertTime", {
		attachTo:document.body,
		months:1,//多少个月显示出来,即看到多少个日历
		mode:'range'
		/* selected:[Kalendae.moment().subtract({d:7}), Kalendae.moment().add({d:0})] */
	});
	
	$(function(){
		$("#insertTime,#username").keydown(function(event){
			if(event.keyCode==13){
				Byname(); 
			}
		});
		
	});
	
</script>
</body>
</html>	