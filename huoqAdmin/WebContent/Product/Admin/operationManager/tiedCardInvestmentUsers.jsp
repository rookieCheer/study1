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

<title>注册未投资用户信息表</title>
<style type="text/css">
.a {
  color: blue;
  text-decoration: underline;
}

.sereach {
  width: 200px;
  height: 32px;
  line-height: 32px;
  text-align: center;
  border: 1px solid #009DDA;
  border-radius: 5px;
}

.exportReport {
  width: 500px;
  height: 32px;
  line-height: 32px;
  text-align: center;
  border: 1px solid gray;
}

</style>

<script type="text/javascript">

//导出报表
function ireportDo() {
    var list = '${table}';
    if (list != '1') {
        alert("无数据");
        return false;
    }
    var url = '${pageContext.request.contextPath}/Product/Admin/usersInvInfo!iportData.action?';
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
//根据用户名与注册时间查询
function queryProduct(){
	var insertTime = $("#insertTime").val();
	var username = $("#username").val();
	var url="${pageContext.request.contextPath}/Product/Admin/usersInvInfo!tiedCardInvestmentUsers.action?insertTime="+insertTime+"&username="+username;
		
	window.location.href=url;
	}
</script>

</head>
<body>
<div class="center">		
	<jsp:include page="/Product/Admin/common/head.jsp"/>	
	<div class="main" align="center">
	<h3>注册未投资用户信息表</h3>
	
<form class="form"  id="frm" method="post">
	<span>用户名:</span>   <input id="username" name="username" type="text" maxlength="11" placeholder="请输入用户名" value="${username }"/>
	
	<span>注册时间:</span> <input id="insertTime" name="insertTime" type="text" value="${insertTime}"/>
	
	
<!-- 	<span>等级</span>
	<select id="level">
		<option value="">所有</option>
		<option value="0">0</option>
		<option value="1">1</option>
		<option value="2">2</option>
		<option value="3">3</option>
		<option value="4">4</option>
		<option value="5">5</option>
		<option value="6">6</option>
	</select> -->
	
	<a class="sereach" href="javascript:queryProduct();" id="sereach">查询</a>
	
	<input class="input-button" type="button" value="导出报表" onclick="ireportDo()">
	
</form>	

	<table border="0.5" cellspacing="0" cellpadding="0" align="center">

				<tr>
					<td width="50px" style="text-align: center;">编号</td>
					<td width="100px" style="text-align: center;">用户ID</td>
					<td width="100px" style="text-align: center;">用户名</td>
					<td width="100px" style="text-align: center;">姓名</td>
					<td width="100px" style="text-align: center;">性别</td>
					<td width="100px" style="text-align: center;">年龄</td>
					<!-- <td width="100px" style="text-align: center;">生日</td> -->
					<td width="100px" style="text-align: center;">是否绑卡</td>
					<td width="100px" style="text-align: center;">所属省份</td>
					<td width="100px" style="text-align: center;">所属城市</td>
					<!-- <td width="100px" style="text-align: center;">电话类型</td> -->
					<td width="200px" style="text-align: center;">注册时间</td>
					<!-- <td width="100px" style="text-align: center;">注册平台</td> -->
					<td width="100px" style="text-align: center;">渠道号</td>
				</tr>
			<c:forEach items="${list}"  var="item" varStatus="i">
			
				<tr>
			<!-- 编号 -->
			<td width="50px" style="text-align: center;">${i.count + (pageUtil.currentPage-1)*pageUtil.pageSize}</td>
			<!-- 用户ID -->
			<td width="100px" style="text-align: center;">
				<c:if test="${item[0] eq null }">--</c:if>
				<c:if test="${item[0] ne null }">${item[0] } </c:if>
			</td>
			<!-- 用户名 -->
			<td width="100px" style="text-align: center;">
				<c:if test="${item[1] eq null || item[1] eq 'null' }">--</c:if>
				<c:if test="${item[1] ne null && item[1] ne 'null' }"><a class="a" target="_blank"  href="${pageContext.request.contextPath}/Product/Admin/userStat!loadUserInfo.action?username=${myel:jieMiUsername(item[1])}">${myel:jieMiUsername(item[1])}</a></c:if>
				</td>
			<!-- 姓名 -->
			<td width="100px" style="text-align: center;">
			<c:if test="${item[2] eq null || item[2] eq 'null' }">--</c:if>
			<c:if test="${item[2] ne null && item[2] ne 'null' }">${item[2]}</c:if>
			</td>
			<!-- 性别 -->
			<td width="100px" style="text-align: center;">
				<c:if test="${item[3] eq null || item[3] eq 'null' }">--</c:if>
				<c:if test="${item[3] ne null && item[3] ne 'null' }">${item[3] }</c:if>
			</td>	
			<!-- 年龄 -->
			<td width="100px" style="text-align: center;">
				<c:if test="${item[4] eq null }">--</c:if>
				<c:if test="${item[4] ne null }">${item[4] }</c:if>
			</td>
			<!-- 绑卡 -->
			<td width="100px" style="text-align: center;">
				<c:if test="${item[5] eq null }">--</c:if>
				<c:if test="${item[5] ne null }">
					<c:if test="${item[5] eq 0 }">未绑卡</c:if>
					<c:if test="${item[5] eq 1 }">已绑卡</c:if>
				</c:if>
			</td>
			<!-- 所属省份 -->
			<td width="100px" style="text-align: center;">
				<c:if test="${item[6] eq null || item[6] eq 'null'}">--</c:if>
				<c:if test="${item[6] ne null && item[6] ne 'null'}">${item[6] }</c:if>
			</td>
			<!-- 所属城市 -->
			<td width="100px" style="text-align: center;">
				<c:if test="${item[7] eq null || item[7] eq 'null' }">--</c:if>
				<c:if test="${item[7] ne null && item[7] ne 'null' }">${item[7] }</c:if>
			</td>
			<!-- 电话类型 -->
			<%-- <td width="100px" style="text-align: center;">
				<c:if test="${item[8] eq null || item[8] eq 'null' }">--</c:if>
				<c:if test="${item[8] ne null && item[8] ne 'null' }">${item[8] }</c:if>
			</td> --%>
			<!-- 注册时间 -->
			<td width="200px" style="text-align: center">
				<c:if test="${item[8] eq null || item[8] eq 'null' }">--</c:if>
				<c:if test="${item[8] ne null && item[8] ne 'null' }">${item[8] }</c:if>
			</td>
			<!-- 注册平台 -->
			<%-- <td width="100px" style="text-align: center">
				<c:if test="${item[10] == 0}">手机注册</c:if>
				<c:if test="${item[10] == 1}">邮箱注册</c:if>
				<c:if test="${item[10] == 2}">其它</c:if>
				<c:if test="${item[10] eq null }">其他</c:if>
			</td> --%>
			
			<td width="100px" style="text-align: center">
				<c:if test="${item[9] eq null || item[9] eq 'null' }">--</c:if>
				<c:if test="${item[9] ne null && item[9] ne 'null' }">${item[9] }</c:if>
			</td>
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
//显示日历
	var k4 = new Kalendae.Input("insertTime", {
		attachTo:document.body,
		months:1,//多少个月显示出来,即看到多少个日历
		mode:'range'
		/* selected:[Kalendae.moment().subtract({d:7}), Kalendae.moment().add({d:0})] */
	});
	
	$(function(){
		$("#insertTime,#username").keydown(function(event){
			if(event.keyCode==13){
				queryProduct(); 
			}
		});
		
	});
	

</script>

</body>

</html>
