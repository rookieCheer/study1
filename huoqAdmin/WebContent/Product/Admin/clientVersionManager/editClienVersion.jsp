<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/Product/images/favicon.ico" type="image/x-icon">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/artDialog4.1.7/artDialog.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/qwyUtilJS/qwyUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dropzone/dropzone.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/js/dropzone/dropzone.css">
<link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css" />
<link href="${pageContext.request.contextPath}/Product/Admin/css/product.css" rel="stylesheet" type="text/css" />
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
</style>
<title>发布版本</title>
</head>
<body>
<div class="center">		
	<jsp:include page="/Product/Admin/common/head.jsp"/>	
	<div class="main" align="center">
		<h3>发布版本</h3>
		<form action="${pageContext.request.contextPath}/Product/Admin/clientVersion!updateVersions.action" method="post"" id="submitBtn" enctype="multipart/form-data">
			<input type="hidden" name="versions.id" value="${id }">
			<input type="hidden" name="versions.type" value="${type }">
			 <table border="1">
				<tr>
					<td>版本</td>
					<td><input type="text" name="versions.versions" value=${versions }></td>
				</tr>
				<tr>
					<td class="rightText" style="text-align: right;vertical-align: top;">更新内容：</td>
					<td class="leftText">
						<input type="hidden" id="content" name="versions.content">
						<div id="description" class="divEdit"  contenteditable="true" >${content } </div>
					</td>
				</tr>
				<tr>
					<td>客户端</td>
					<td>
						<select id="type" disabled="disabled">
							<option value="1">Android</option>
							<option value="0">IOS</option>
						</select>
						
					</td>
					
				</tr>
				
			  	<tr>
					<td>是否需要更新</td>
					<td>
						<select name="versions.isUpdate" id="isUpdate">
							<option value="0">不需要</option>
							<option value="1">需要</option>
						</select>
						
					</td>
					
				</tr>
				<tr id="pathTr">
					<td>更新地址</td>
					<td>
						<input type="hidden" name="versions.path" value="${path}">
						<input type="file" name="updateFile" id="updateFile"><font color="red">可不改</font>
					</td>
				</tr>
				<tr>
					<td colspan="2" align="center">
						<input type="button" id="subBtn" style="text-align: center;width:200px; height: 50px; font-size: 24px" value="修改" >
					</td>
				</tr>
			</table> 
			
		</form>
	</div>
</div>
</body>
<script type="text/javascript">

$(function(){
	var type="${type}";
	var isUpdate="${isUpdate}";
	if(type == '0'){
		$("#pathTr").hide();
	}
	
	$("#isUpdate").val(isUpdate);
	var isOk = "${isOk}";
	$("#subBtn").click(function(){
		$("#content").val($("#description").html());
		isOk = '';
		$("#submitBtn").submit();
		
		
	})
	if(isOk != ''){
		if(isOk == 'ok'){
			alert("修改成功");
		}else{
			alert("修改失败");
		}
		window.location.href="${pageContext.request.contextPath}/Product/Admin/clientVersion!loadClientVersion.action";
	}
	
	
});
</script>
</html>