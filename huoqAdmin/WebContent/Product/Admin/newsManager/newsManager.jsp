<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"  deferredSyntaxAllowedAsLiteral="true" %>
    <!-- <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd"> -->
<html>
<head><link rel="shortcut icon" href="${pageContext.request.contextPath}/Product/images/favicon.ico" type="image/x-icon">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="${pageContext.request.contextPath}/js/dropzone/dropzone.css" type="text/css"/>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/artDialog4.1.7/artDialog.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/qwyUtilJS/qwyUtil.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dropzone/dropzone.js"></script>

<link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css" />
<link href="${pageContext.request.contextPath}/Product/Admin/css/product.css" rel="stylesheet" type="text/css" />
<!-- kindeditor上传 -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/Product/Admin/kindeditor/themes/default/default.css" />
<script charset="utf-8"  type="text/javascript" src="${pageContext.request.contextPath}/Product/Admin/kindeditor/kindeditor.js"></script>
<script charset="utf-8"  type="text/javascript" src="${pageContext.request.contextPath}/Product/Admin/kindeditor/lang/zh_CN.js"></script>

<title>新华金典理财-发布新闻</title>
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
<script type="text/javascript">
function isSubmit(){
	//$("#hidNote").val(editor.html());
	
	if($("#title").val().trim()==""){
		alert("新闻标题不能为空!");
		return false;
	}
	return true;
}

//预览
function preview(){
	 var url = "${pageContext.request.contextPath}/Product/Admin/newsManager!previewNews.action";
	 console.log(url);
	 $("#frm").attr("target","_blank");
	 $('#frm').attr("action", url).submit();
}

//发布
function sub(){
	 var url = "${pageContext.request.contextPath}/Product/Admin/newsManager!releaseNews.action?";
     console.log(url);
	 $('#frm').attr("action", url).submit();
}
</script>
</head>
<body>
<div class="center">			
 	 <jsp:include page="/Product/Admin/common/head.jsp"/>	
	<div class="main" align="center" >
		<h1 style="text-align: center;">发布新闻</h1>
		<form id="frm" action="${pageContext.request.contextPath}/Product/Admin/newsManager!sendNews.action" method="post" enctype="multipart/form-data" onsubmit="return isSubmit()">
				<table border="1" style="width: 100%；">
					<tr>
						<td colspan="2" align="center">
						以下带 <span style="color: red;">*</span> 为必填选项
						</td>
					</tr>
					<tr>
						<td class="rightText" style="text-align: right;vertical-align: top;">新闻图片：</td>
						<td class="leftText">
						<input type="hidden" name="news.imgUrl" value="" id="infoImg">
						<div id="dropzaa" class="dropzone" name="file"></div>
						</td>
					</tr>
					<tr>
						<td class="rightText" style="width:100px;"><span style="color: red;">*</span>新闻标题：</td>
						<td class="leftText" style="width:auto">
							<input  id="title" name="news.newsTitle" style="width: 100%;"><br>
						</td>
					</tr>
					<%--<tr>
						<td>新闻ID</td>
						<td><input type="text" name="news.id"></td>
					</tr>--%>
					<tr>
						<td class="rightText" >新闻链接：</td>
						<td><input type="text" name="news.httpUrl" style="width: 100%;"></td>
					</tr>

					<tr>
						<td class="rightText" style="text-align: right;vertical-align: top;">新闻备注：</td>
						<td class="leftText">
						<%--<input type="hidden" id="hidNote" name="news.note">--%>
							<textarea id="editor_id" style="width:800px;height:100px;" name="news.note"></textarea>
						</td>
					</tr>
					<tr>
						<td colspan="2" align="center">
							<input type="button" onclick="preview()" id="ylSubmit" style="text-align: center;width:200px; height: 50px; font-size: 24px" value="预览" >
							<input type="button" onclick="sub()" id="btnSubmit" style="text-align: center;width:200px; height: 50px; font-size: 24px" value="发布" >
						</td>
					</tr>
				</table>
				</form>
			</div>
		</div>
</body>
<script type="text/javascript">

 if("${isOk}"=="ok"){
 	alert("发布成功!");
 	window.location.href="${pageContext.request.contextPath}/Product/Admin/newsManager!listNews.action?";
 }else if("${isOk}"=="nocontent"){
	 	alert("发布失败,内容为空!");
	 	window.location.href="${pageContext.request.contextPath}/Product/Admin/newsManager/newsManager.jsp";
 }else if("${isOk}"=="no"){
 	alert("发布失败!");
 	window.location.href="${pageContext.request.contextPath}/Product/Admin/newsManager/newsManager.jsp";
 }
 
 var maxFiles=20;
 var fileNamesInfo = "";
 var fileNamesLaw = "";
 //标题缩略图;
 $(function(){
	 $("#dropzaa").dropzone({
         url: "${pageContext.request.contextPath}/Product/Admin/newsManager!uploadSLT.action",
         addRemoveLinks: true,
         dictRemoveLinks: "x",
         dictCancelUpload: "x",
         maxFiles: maxFiles,
         maxFilesize: 1,
         uploadMultiple:false,
         dictDefaultMessage: "请点击此添加图片或拖放图片进此",
         dictInvalidFileType: "图片格式错误,只支持‘png、jpg、jpeg’格式",
         dictFileTooBig: "图片大小超出上传范围",
         dictMaxFilesExceeded: "该图片上传失败,每次只能上传"+maxFiles+"张",
         acceptedFiles: ".png,.jpg,.jpeg",
         dictRemoveFile:"删除图片",
         init: function() {
             this.on("success", function(file,response) {
             	var json = eval('(' + response + ')');
             	if("ok"==json.status){
 	            	fileNamesInfo+=json.json;
 	            	file.serverId = json.json;
 	            	//alert(fileNamesInfo);
 	                console.log("File " + file.name + "uploaded");
 	                $("#infoImg").val(fileNamesInfo);
             	}else{
             		alert("上传图片失败");
             	}
             });
             this.on("removedfile", function(file) {
            	 	var delUrl="${pageContext.request.contextPath}/Product/Admin/newsManager!removeSLT.action";
             	var removeId = file.serverId;
             	fileNamesInfo = fileNamesInfo.replace(removeId, "");
             	//alert("undefined"==removeId+"");
             	if("undefined"==removeId+""){
             		return false;
             	}
             	 $("#infoImg").val(fileNamesInfo);
             		//alert(removeId);
             	$.post(delUrl,"removeId="+removeId,function(data){
             		if("ok"!=data.status)
     					alert(data.json);//失败的结果
     			}); 
                 console.log("File " + file.name + "removed");
                 
             });
         }
 });
 });
 
</script>
</html>