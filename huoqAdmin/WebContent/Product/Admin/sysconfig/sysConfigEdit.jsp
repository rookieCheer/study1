<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>添加模块功能</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.7.2.min.js"></script>
<link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css" />
<link href="${pageContext.request.contextPath}/Product/Admin/css/product.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/Product/js/alert.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/Product/css/alert.css">
<script src="${pageContext.request.contextPath}/js/artDialog4.1.7/jquery.artDialog.js?skin=default"></script>
</head>
<!--  
<script type="text/javascript">
function isSubmit(){
	var name = $("#name").val();
	var type = $("#type").val();
	var state = $("#type").val();
	var note = $("#note").val();
	if(name == ''){
		alert("名字不能为空！");
		return false;
	}
	if(note == ''){
		alert("描述不能为空！");
		return false;
	}
	if(state == ''){
		alert("请选择状态！");
		return false;
	}
	if(type == ''){
		alert("请选择注册端！");
		return false;
	}

}
</script>-->
<body>

<div class="center">	
	<jsp:include page="/Product/Admin/common/head.jsp"/>		
	<div class="main" align="center" style="text-align: center;">
		<h1 style="text-align: center;">编辑配置管理</h1>
		<div style="margin-top: 10px;">
		<form action="${pageContext.request.contextPath}/Product/Admin/sysConfig!editDo.action" id="subForm"><!-- onsubmit="return isSubmit(); -->
		<input type="hidden" id="id" name="id" value="${id}">
		<input type="hidden" id="typeValue"  value="${type}">
		<input type="hidden" id="stateValue"  value="${state}">
			<table  style="margin:auto;">
			
				<tr>
					<td>编&nbsp;码:</td>
					
					<td><input type="text" id="code" name="code" value="${code}" readonly="readonly"><span><font color="red">*</font></span></td>
				</tr>
				<tr>
					<td>名&nbsp;称:</td>
					
					<td><input type="text" id="name" name="name" value="${name}"><span><font color="red">*</font></span></td>
				</tr>
				
				<tr>
					<td>类型:</td>
					<td>
						<select id="type" onchange="query()" style="width:153px; height: 20px;" name="type" >
							<option value="1">Android移动端</option>
							<option value="2">IOS移动端</option>
							<option value="3">微信注册</option>
							<option value="0">web端注册</option>
						</select><span><font color="red">*</font></span>
					</td>
				</tr>
				<tr>
					<td>状&nbsp;态:</td>
					<td>
					<select id="state" onchange="query()" style="width:153px; height: 20px;" name="state" >
							<option value="0">停用</option>
							<option value="1">启用</option>
						</select><span><font color="red">*</font></span>
					</td>
				</tr>
				<tr>
					<td>描&nbsp;述:</td>
					<td>
					<textarea rows="2" cols="20" style="height: 40px;width: 153px;margin-top: 2px;" name="note" id="note" >${note}</textarea><span><font color="red">*</font></span>
					</td>
				</tr>
				
				<tr>
					<td colspan="2"><input type="button" value="提交" id="subBtn" style="width: 50px;height: 25px;margin-left: 20px;margin-top: 5px; cursor: pointer;"></td>
				</tr>
			</table>
		</form>
		</div>
	</div>
	</div>
</body>
<script type="text/javascript">
	$(function(){
		var typeValue = $("#typeValue").val();
		var stateValue = $("#stateValue").val();
		$("#type").val(typeValue);
		$("#state").val(stateValue);
		var isOk = "${isOk}";
		$("#subBtn").click(function(){
			isOk = '';
			var name = $("#name").val();
			var type = $("#type").val();
			var state = $("#type").val();
			var note = $("#note").val();
			if(name == ''){
				alert("名字不能为空！");
				return false;
			}
			if(note == ''){
				alert("描述不能为空！");
				return false;
			}
			if(state == ''){
				alert("请选择状态！");
				return false;
			}
			if(type == ''){
				alert("请选择类型！");
				return false;
			}
			$("#subForm").submit();
			
			
		})
		
		
		if(isOk != ''){
			if(isOk == 'ok'){
				alert("修改成功");
			}else{
				alert("修改失败");
			}
			window.location.href="${pageContext.request.contextPath}/Product/Admin/sysConfig!getSysConfig.action";
		}
		
		
	});
</script>
</html>