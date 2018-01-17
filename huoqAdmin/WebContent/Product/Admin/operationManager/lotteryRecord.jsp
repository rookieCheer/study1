<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/zlqEl.tld" prefix="myel"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/Product/images/favicon.ico" type="image/x-icon">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/artDialog4.1.7/artDialog.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/qwyUtilJS/qwyUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dropzone/dropzone.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/js/dropzone/dropzone.css">
<link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css" />		
<link href="${pageContext.request.contextPath}/Product/Admin/css/product_fabu_history.css" rel="stylesheet" type="text/css" />
<script src="${pageContext.request.contextPath}/Product/Admin/js/jquery-1.9.1.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/qwyUtilJS/qwyUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/Product/js/alert.js"></script>
<script src="${pageContext.request.contextPath}/js/artDialog4.1.7/jquery.artDialog.js?skin=blue"></script>
<style type="text/css">
	.rightText{
		text-align: right;
	}
	.leftText{
		text-align: left;
	}
	.divEdit {
	  width: 1200px;
	  overflow: auto;
	  min-height: 500px;
	  height: auto;
	  _height: 120px;
	  max-height: 160px;
	  cursor: text;
	  outline: none;
	  white-space: normal;
	  padding: 1px 2px 1px 2px;
	  font-family: SimSun,Verdana,sans-serif;
	  font-size: 12px;
	  line-height: 16px;
	  /*border: 1px solid black;*/
	}
	.a{
		color: blue;
		text-decoration: underline ;
	}
</style>
<script type="text/javascript">
        function ireportDo() {
            var list = '${table}';
            if (list != '1') {
                alert("无数据");
                return false;
            }
            var url = '${pageContext.request.contextPath}/Product/Admin/winner!iportLotteryRecordData.action?';
            var list = "${list}";
            var my = art.dialog({
                title: '提示',
                content: document.getElementById("psi_load"),
                height: 60,
                lock: true,
                cancel: false,
            });
            
            $.post(url, $("#form").serialize(), function (data) {
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

var prizeId = $("#prizeId option:selected").val();
var username = $("#username").text();
 $(function () {
    $("#prizeId option[value='${prizeId}']").attr("selected", true);
});  
 
 $(function () {
	    $("#type option[value='${type}']").attr("selected", true);
	});  

$(function(){
	$("#search").click(function(){
		//alert($("#form").serialize());
	$.ajax({
		type:"GET",
		url : "${pageContext.request.contextPath}/Product/Admin/winner!getLotterRecord.action",
		data:$("#form").serialize(),
		success : function(data) {
			if(data.status == 'ok'){
				alert(data.json);
				location.reload();
			}else{
				//alert("data.json");
				return false;
			}
		}
	});
	
});
	
	
});



</script>

<title>抽奖机会记录</title>
</head>
<body>
<div class="center">		
	<jsp:include page="/Product/Admin/common/head.jsp"/>	
	<div class="main" align="center">
		<h2>抽奖机会记录</h2>
		<form id="form" method="get">
		<span>用户名:</span> <input id="username" name="username" type="text" value="${username}" maxlength="11">
		<span>类型：
			<select name = "type" id = "type">
				<option value="">全部</option>
				<option value="0">获得机会</option>
				<option value="1">使用机会</option>
			</select>
		</span>
		<button id="search">查询</button>
		<input type="button" value="导出报表" onclick="ireportDo()">
		</form><br>
		<table border="1">
			<tr>
				<td width="200px" style="text-align: center;">序号</td>
				<td width="300px" style="text-align: center;">用户名</td>
				<td width="100px" style="text-align: center;">真实姓名</td>
				<td width="400px" style="text-align: center;">活动时间时间</td>
				<td width="300px" style="text-align: center;">备注</td>
			</tr>
		<c:forEach items="${pageUtil.list}"  var="item" varStatus="i">
			<tr>
				<td style="text-align: center;">${i.count + (pageUtil.currentPage-1)*pageUtil.pageSize}</td>
				<td><a class="a"  href="${pageContext.request.contextPath}/Product/Admin/userStat!loadUserInfo.action?username=${myel:jieMiUsername(item[0])}">${myel:jieMiUsername(item[0])}</a></td>
				<td style="text-align: center;">${item[1]}</td>
				<td style="text-align: center;">${item[2]}</td>
				<td style="text-align: center;">${item[3]}</td>
			</tr>
		</c:forEach>
		</table>
		
		<c:choose>
			<c:when test="${pageUtil.list ne '[]' &&  pageUtil.list ne '' && pageUtil.list ne null}"><jsp:include page="/Product/page.jsp" /></c:when>
			<c:otherwise>
				<div style="text-align: center;margin-top: 15px;"><!-- <img src="images/lh.jpg"> -->
			  		<img src="../images/no_record.png" />
			  	</div>
			</c:otherwise>
		</c:choose>
		
	</div>
</div>
</body>
</html>