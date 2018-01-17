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
<script src="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.standalone.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.css" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/js/dropzone/dropzone.css">
<link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css" />		
<link href="${pageContext.request.contextPath}/Product/Admin/css/product_fabu_history.css" rel="stylesheet" type="text/css" />
<script src="${pageContext.request.contextPath}/Product/Admin/js/jquery-1.9.1.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/qwyUtilJS/qwyUtil.js"></script>
<script src="${pageContext.request.contextPath}/js/artDialog4.1.7/jquery.artDialog.js?skin=blue"></script>
<style type="text/css">
table{border-collapse:collapse;empty-cells:show;}
table tr td{border:1px solid #555;}
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
            var url = '${pageContext.request.contextPath}/Product/Admin/winner!iportWinnerData.action?';
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
	function clickMe(){
		$.ajax({
			type:"GET",
			url : "${pageContext.request.contextPath}/Product/Admin/winner!getWinnerList.action",
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
	
	}
	$(function () {
	    $("#prizeId option[value='${prizeId}']").attr("selected", true);
	});
	$(function () {
	    $("#type option[value='${type}']").attr("selected", true);
	});
</script>
<script type="text/javascript">
	$(function(){
		$("#username,#prizeId").keydown(function(event){
			if(event.keyCode == 13){
				clickMe();
			}
		});
	})
</script>
<title>中奖信息记录表</title>
</head>
<body>
<div class="center">		
	<jsp:include page="/Product/Admin/common/head.jsp"/>	
	<div class="main" align="center">
	
		<h2>中奖信息记录表</h2>
		<form id="form" method="get">
		<span>用户名:</span> <input id="username" name="username" type="text" value="${username}" maxlength="11">
		<span>奖品</span>
			<select id="prizeId" name="prizeId" >
				<option value="">所有</option>
				<c:forEach items="${prizeIdAndName}"  var="item" varStatus="i">
					<option value="${item[0]}">${item[1]}</option>
				</c:forEach>
			</select>
		<!-- <span>活动类型：
			<select id="type" name="type">
				<option value=""></option>
				<option value="1">挖宝奖品</option>
				<option value="2">六月热奖品</option>
			</select></span> -->
		<span>时间：<input type="text" name="insertTime" id="insertTime" value="${insertTime}"></span>
		<button id="search" onclick = "clickMe()">查询</button>
		<input type="button" value="导出报表" onclick="ireportDo()">
		</form><br>
		<table border="1">
			<tr>
				<td width="50px" style="text-align: center;">序号</td>
				<td width="100px" style="text-align: center;">用户名</td>
				<td width="100px" style="text-align: center;">真实姓名</td>
				<!-- <td width="100px" style="text-align: center;">累计投资额</td>
				<td width="100px" style="text-align: center;">剩余抽奖次数</td>
				<td width="100px" style="text-align: center;">已用抽奖次数</td> -->
				<td width="200px" style="text-align: center;">奖品</td>
				<td width="200px" style="text-align: center;">抽奖时间</td>
				<td width="100px" style="text-align: center;">收货人姓名</td>
				<td width="100px" style="text-align: center;">收货人电话</td>
				<td width="200px" style="text-align: center;">收货人地址</td>
				<td width="100px" style="text-align: center;">收货人邮编</td>
			</tr>
		<c:forEach items="${pageUtil.list}"  var="item" varStatus="i">
			<tr></a>
				<td style="text-align: center;">${i.count + (pageUtil.currentPage-1)*pageUtil.pageSize}</td>
				<td style="text-align: center;"><a class="a"  href="${pageContext.request.contextPath}/Product/Admin/userStat!loadUserInfo.action?username=${myel:jieMiUsername(item[0])}">${myel:jieMiUsername(item[0])}</a></td>
				<td style="text-align: center;">${item[1]}</td>
				<td style="text-align: center;">${item[2]}</td>
				<td style="text-align: center;">${item[9]}</td>
				<td style="text-align: center;">${item[3]}</td>
				<td style="text-align: center;">${item[4]}</td>
				<td style="text-align: center;">${item[5]}</td>
				<td style="text-align: center;">${item[6]}</td>
				<%-- <td style="text-align: center;">${item[7]}</td>
				<td style="text-align: center;">${item[8]}</td>
				<td style="text-align: center;">${item[9]}</td> --%>
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

<script type="text/javascript">
	var k4 = new Kalendae.Input("insertTime", {
		attachTo:document.body,
		months:2,//多少个月显示出来,即看到多少个日历
		mode:'range'
		/* selected:[Kalendae.moment().subtract({d:7}), Kalendae.moment().add({d:0})] */
	});	
</script>
</body>
</html>