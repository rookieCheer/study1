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
<style type="text/css">
.a {
  color: blue;
  text-decoration: underline;
}
</style>

<title>情人节用户地址表</title>

<script type="text/javascript">
function Byname(){
	var username = $("#username").val();
	//var  realnames = encodeURI(encodeURI(realname,"utf-8"),"utf-8");
		var url="${pageContext.request.contextPath}/Product/Admin/bannerActivity!loadAddress.action?username="+username;
		
		window.location.href=url;
	}
</script>
<script type="text/javascript">

	function toModifyBanner(id){
		var status=$("#modify_"+id).text();
		if("修改"==status){
			$("#modify_"+id).text("保存");
			$(".dis_"+id).removeAttr("disabled");
			$(".dis_"+id).removeAttr("readonly");
		}else{			
			var address = $("#address_"+id).val();
			
			var url = "${pageContext.request.contextPath}/Product/Admin/bannerActivity!modifyAddress.action";
 			var formData="muserAddress.address="+address;
			    formData+="&muserAddress.id="+id;
			    /* formData+="&prize.winningRate="+winningRate; */
			    
		if(confirm("是否确定保存修改？")){
			 $.post(url,formData,function(data){
				 /* alert(formData); */
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

</head>
<body>
<div class="center">			
	<jsp:include page="/Product/Admin/common/head.jsp"/>
	<div class="main" align="center">
		<h3 style="text-align: center;">情人节用户地址表</h3><br/>
		<div id="div_condition" style="text-align: center;" >
		</div>
		<span>联系电话:</span> <input id="username" name="username" type="text" value="${username}"/>
		<a class="sereach" href="javascript:Byname();" id="sereach">查询</a> 
		<table style="margin-top: 20px;width: 80%;" border="1" >
		
		<tr>
		<td>序号</td>
		<td>用户ID</td>
		<td>联系人</td>
		<td>联系电话</td>
		<td>联系地址</td>
		<td>详细地址</td>
		<td>操作</td>
		</tr>
		
		<c:forEach items="${list}" var="item" varStatus="i">
		
		<tr>
		<td>${i.count + (pageUtil.currentPage-1)*pageUtil.pageSize}</td>
		<td>${item.usersId }</td>
		<td>${item.contractName }</td>
		<td>${item.phone }</td>
		<td><input class="dis_${item.id}" style="width:100%;" id="address_${item.id}" readonly="readonly" disabled="disabled" value="${item.address}" /></td>
		<td>${item.addressDetail }</td>
		<td><a id="modify_${item.id}"  href="javascript:toModifyBanner(${item.id})" class="a">修改</a></td>
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
		$("#username").keydown(function(event){
			if(event.keyCode==13){
				queryProduct(); 
			}
		});
		
	});
	
</script>
</body>
</html>	