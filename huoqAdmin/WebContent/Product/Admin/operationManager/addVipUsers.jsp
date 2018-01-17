<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/zlqEl.tld" prefix="myel"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>添加内测VIP用户</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.7.2.min.js"></script>
<link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css" />
<link href="${pageContext.request.contextPath}/Product/Admin/css/product.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/Product/js/alert.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/Product/css/alert.css">
<script src="${pageContext.request.contextPath}/js/artDialog4.1.7/jquery.artDialog.js?skin=default"></script>
<script src="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.standalone.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.css" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/qwyUtilJS/qwyUtil.js"></script>
<style type="text/css">
.a{
		color: blue;
		text-decoration: underline ;
	}
</style>
<script type="text/javascript">

	/* 添加关键字时非空检查 */
	function isSubmit(){
		var username = $("#username").val();
		if(keyword==''){
			alert("请输入用户名");
			return false;
		}
	}
	/* 添加关键字时非空检查   end */

	/* 根据ID修改状态 */
function updateStatus(value,title){
	$.ajax({
		type : "get",
		url : "${pageContext.request.contextPath}/Product/Admin/usersVIPBeat!updateStatuById.action?usersVIPBeat.id="+value,
		success : function(data) {
			if(data.status == 'ok'){
				alert(title+"成功");
				location.reload();
			}else{
				alert(title+"失败");
				return false;
			}
		}
	});
}
	/* 根据ID修改状态     end  */

</script>
<script type="text/javascript">
function update(id){
	var status=$("#modify_"+id).text();
	if("修改"==status){
		$("#modify_"+id).text("保存");
		$(".dis_"+id).removeAttr("disabled");
		$(".dis_"+id).removeAttr("readonly");
	}else{			
		var addInterestRates = $("#addInterestRates_"+id).val();
		var note = $("#note_"+id).val();
		var vip = $("#vip_"+id).val();
		
		if(!isPrice(addInterestRates)){
			alert("请输入正确百分比");
			return false;
		}else{
			if(addInterestRates>100){
				alert("请输入正确百分比");
				return false;
			}
		}
		
		var url = "${pageContext.request.contextPath}/Product/Admin/usersVIPBeat!modifyVip.action";
			var formData="usersVIPBeat.id="+id;
		    formData+="&usersVIPBeat.vip="+vip;
		    formData+="&usersVIPBeat.note="+note;
		    formData+="&usersVIPBeat.addInterestRates="+addInterestRates;
		    
	if(confirm("是否确定保存修改？")){
		 $.post(url,formData,function(data){
			 alert(formData); 
			 if(data.status=="ok"){
					//验证码发送成功，跳转到验证页面
					alert(data.json);
					$("#modify_"+id).text("修改");
					$(".dis_"+id).attr("disabled","disabled");
					$(".dis_"+id).attr("readonly","readonly");
					location.reload();
			 }else{
				 alert(data.json);
				 return false;
			 }
		 });
	 }	    

	}
}
</script>
<script type="text/javascript">

function clickMe(){
	$.ajax({
	type : "get",
	url : "${pageContext.request.contextPath}/Product/Admin/usersVIPBeat!loadUsersVipList.action",
	data:$("#form").serialize(),
	success : function(data) {
		if(data.status == 'ok'){
			alert(title+"成功");
			location.reload();
		}else{
			alert(title+"失败");
			return false;
		}
	}
}); 

window.location.href=url;
}

</script>
</head>
<body>

<div class="center">	
	<jsp:include page="/Product/Admin/common/head.jsp"/>		
	<div class="main" align="center" style="text-align: center;">
	  <h3 >添加内测VIP用户</h3>
		<form action="${pageContext.request.contextPath}/Product/Admin/usersVIPBeat!addVIPUsers.action" method="post" onsubmit="return isSubmit();">
			<ul id="tab">
				<li>
					<label>添加VIP内测用户:</label>
					<input type="text" id="username" name="usersVIPBeat.username" maxlength="11">
					<label><input type="submit" value="提交" ></label><br/>
				</li>
			</ul>
		</form>
		
		<div><form id = "form">
			查询： 用户名：<input id="username" name="username" type="text" value="${username}"/>
			<span>时间:</span> <input id="insertTime" name="insertTime" type="text" value="${insertTime}">
			<span>状态:<select id="status" name = "status">
						<option value="">全部</option>
						<option value="0">可用</option>
						<option value="1">不可用</option>
					 </select>
			</span>
			<%-- <span>加息额度:<input id="rate" name="rate" type="text" value="${rate}"></span> --%>
			<button id="search" onclick = "clickMe()">查询</button>
			</form>
		</div>
		
		<br/> <br/>
		<div align="center" class="main">
		<table border="1">
			<tr>
				<td width="50px" style="text-align: center;">序号</td>
				<td width="100px" style="text-align: center;">用户名</td>
				<td width="100px" style="text-align: center;">用户ID</td>
				<td width="100px" style="text-align: center;">当前状态</td>
				<!-- <td width="100px" style="text-align: center;">VIP等级</td> -->
				<td width="300px" style="text-align: center;">备注</td>
				<!-- <td width="100px" style="text-align: center;">加息额度</td> -->
				<td width="200px" style="text-align: center;">插入时间</td>
				<td width="200px" style="text-align: center;">修改时间</td>
				<td width="100px" style="text-align: center;">操作</td>
			</tr>
		<c:forEach items="${list}"  var="item" varStatus="i">
			<tr>
				<td style="text-align: center;">${i.index + 1}</td>
				<td style="text-align: center;">
					<a target="black" class="a" href="${pageContext.request.contextPath}/Product/Admin/userStat!loadUserInfo.action?username=${myel:jieMiUsername(item.username)}">${myel:jieMiUsername(item.username)}</a>
				</td>
				<!-- 用户ID -->
				<td style="text-align: center;">${item.usersId}</td>
				<!-- 用户状态 -->
				<td style="text-align: center;">
					<c:if test="${item.status eq 0}">可用</c:if>
					<c:if test="${item.status eq 1}">不可用</c:if>
				</td>
				
				<!-- vip等级 -->
				<%-- <td style="text-align: center;">
					<input class="dis_${item.id}" style="width:100%;text-align: center" id="vip_${item.id}" readonly="readonly" disabled="disabled" value="${item.vip}" />
				</td> --%>
				
				<!-- 用户备注 -->
				<td style="text-align: center;">${item.note}
					<%-- <input class="dis_${item.id}" style="width:100%;text-align: center" id="note_${item.id}" readonly="readonly" disabled="disabled" value="${item.note}" /> --%>
				</td>
				
				<!-- 加息额度 -->
				<%-- <td style="text-align: center;">
					<input class="dis_${item.id}" style="width:100%; text-align: center" id="addInterestRates_${item.id}" readonly="readonly" disabled="disabled" value="${item.addInterestRates}" />
				</td> --%>
				<!-- 插入时间 -->
				<td style="text-align: center;">${item.insertTime}</td>
				<!-- 修改时间 -->
				<td style="text-align: center;">${item.updateTime}</td>
				<!-- 操作 -->
				<td style="text-align: center;">
					<c:choose>
						<c:when test="${item.status != null && item.status eq 0}"><a href="javascript:updateStatus('${item.id}','停用')" class="a">停用</a></c:when>
						<c:otherwise><a href="javascript:updateStatus('${item.id}','启用')" class="a">启用</a></c:otherwise>
					</c:choose>
					<%-- <a id="modify_${item.id}" href="javascript:update('${item.id}')" class="a">修改</a> --%>
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
	
	</div>
</body>
<script type="text/javascript">
	$(function(){
		var message='${message}';
		if(message!=''){
			alert(message);
            window.location.href="${pageContext.request.contextPath}/Product/Admin/usersVIPBeat!loadUsersVipList.action";

		}
	});
</script>
<script type="text/javascript">
	var k4 = new Kalendae.Input("insertTime", {
		attachTo:document.body,
		months:1,//多少个月显示出来,即看到多少个日历
		mode:'range'
		/* selected:[Kalendae.moment().subtract({d:7}), Kalendae.moment().add({d:0})] */
	});
	$(function(){
		$("#status option[value='${status}']").attr("selected",true);
	});
	$(function(){
		$("#insertTime,#username").keydown(function(event){
			if(event.keyCode==13){
				Byname(); 
			}
		});
		
	});
	
</script>
</html>