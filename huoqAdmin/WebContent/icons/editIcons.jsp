<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" deferredSyntaxAllowedAsLiteral="true" %>
<!-- <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd"> -->
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
    <!-- kindeditor上传 -->
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/Product/Admin/kindeditor/themes/default/default.css"/>
    <script charset="utf-8" type="text/javascript"
            src="${pageContext.request.contextPath}/Product/Admin/kindeditor/kindeditor.js"></script>
    <script charset="utf-8" type="text/javascript"
            src="${pageContext.request.contextPath}/Product/Admin/kindeditor/lang/zh_CN.js"></script>
    <link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css"/>
    <link href="${pageContext.request.contextPath}/Product/Admin/css/product_fabu.css" rel="stylesheet"
          type="text/css"/>
    <title>新华金典网-修改图标</title>
    <style type="text/css">
        .rightText {
            text-align: right;
        }

        .leftText {
            text-align: left;
        }

        .divEdit {
            width: 545px;
            overflow: auto;
            min-height: 120px;
            height: auto;
            _height: 120px;
            max-height: 160px;
            cursor: text;
            outline: none;
            white-space: normal;
            padding: 1px 2px 1px 2px;
            font-family: SimSun, Verdana, sans-serif;
            font-size: 12px;
            line-height: 16px;
            /*border: 1px solid black;*/
        }
    </style>
<script type="text/javascript">
    function isSubmit(){
        if($("#name").val()==""){
            alert("图标名称不能为空!");
            return false;
        }
//        if($("#modifier").val()==""){
//            alert("修改人不能为空!");
//            return false;
//        }

        var type=$("#type option:selected").val();
        var temp = "";
        if('0'==type){
            temp = "首页";
        }else if('1'==type){
            temp = "发现";
        }else{
            temp = "【状态为"+type+"】";
        }
        if(!confirm("确认修改【"+temp+"】图标")){
            return false;
        }
        $("#hidDescr").val($("#description").html());
        $("#btnSubmit").attr("disabled","disabled");
        return true;
    }

</script>
</head>
<body>
<div align="center">
    <jsp:include page="/Product/Admin/common/head.jsp"/>
    <div class="main">
        <h1 style="text-align: center;">修改图标</h1>
        <form action="${pageContext.request.contextPath}/Icons/icons!editIcons.action"
              method="post" enctype="multipart/form-data" onsubmit="return isSubmit()">
            <input id="id" type="hidden" name="icons.id" value="${icons.id}">
            <input id="createTime" type="hidden" name="icons.createTime" value="${icons.createTime}">
            <input id="isDelete" type="hidden" name="icons.isDelete" value="${icons.isDelete}">
            <input id="creator" type="hidden" name="icons.creator" value="${icons.creator}">
            <input id="iconURL" type="hidden" name="icons.iconURL" value="${icons.iconURL}">
            <table border="1" style="width: 850px;">
                <tr>
                    <td class="rightText" style="width:20%"><span style="color: red;">*</span>图标名称：</td>
                    <td class="leftText" style="width:80%">
                        <input id="name" name="icons.name" value="${icons.name}" style="width: 300px">
                    </td>
                </tr>
                <tr>
                    <td class="rightText" style="width:20%"><span style="color: red;">*</span>顺序号：</td>
                    <td class="leftText" style="width:80%">
                        <input id="seq" name="icons.seq" value="${icons.seq}" style="width: 300px">
                    </td>
                </tr>
                <tr>
                    <td>图标模块：</td>
                    <td>
                        <select id="type" name="icons.module" onchange="changgeURL()">
                            <c:if test="${icons.module == '0'.charAt(0)}">
                                <option value='0' selected="selected">首页</option>
                                <option value='1'>发现</option>
                            </c:if>
                            <c:if test="${icons.module == '1'.charAt(0)}">
                                <option value='0'>首页</option>
                                <option value='1' selected="selected">发现</option>
                            </c:if>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td class="rightText" style="text-align: right;vertical-align: top;">图标图片：</td>
                    <td class="leftText">
                        <input type="hidden" name="" value="" id="infoImg">
                        <div id="dropz" class="dropzone" name="file"></div>
                        <div id="dropz2" class="dropzone" name="file" style="display: none;"></div>
                    </td>
                </tr>
                <tr>
                    <td class="rightText" style="width:20%"><span style="color: red;">*</span>是否可以点击：</td>
                    <td class="leftText" id="radios">
                        <label><input id="noClick" type="radio" name="icons.isClick" value='0'>不可以</label>
                        <label><input id="yesClick" type="radio" name="icons.isClick" value='1'>可以</label>
                    </td>
                </tr>
                <tr id="shows">
                    <td class="rightText" style="text-align: right;vertical-align: top;">不可点击图标提示信息：</td>
                    <td class="leftText">
                        <s:textarea name="icons.iconMsg" cols="75" rows="3"/>
                    </td>
                </tr>
                <tr>
                    <td class="rightText" style="width:20%"><span style="color: red;">*</span>是否停用：</td>
                    <td class="leftText">
                        <label><input id="stopUse" type="radio" name="icons.status" value='0'>停用</label>
                        <label><input id="using" type="radio" name="icons.status" value='1'>启用</label>
                    </td>
                </tr>
                <tr>
                    <td class="rightText" style="width:20%"><span style="color: red;">*</span>是否是H5：</td>
                    <td class="leftText">
                        <label><input id="h5" type="radio" name="icons.type" value='0'>H5</label>
                        <label><input id="old" type="radio" name="icons.type" value='1'>原生</label>
                    </td>
                </tr>
                <tr>
                    <td class="rightText" style="width:20%"><span style="color: red;">*</span>H5地址：</td>
                    <td class="leftText" style="width:80%">
                        <input id="h5URL" name="icons.H5URL" value="${icons.h5URL}" style="width: 300px">
                    </td>
                </tr>
                <tr>
                    <td class="rightText" style="width:20%"><span style="color: red;">*</span>是否需要登录：</td>
                    <td class="leftText">
                        <label><input id="noLogin" type="radio" name="icons.isLogin" value='0'>不需要</label>
                        <label><input id="needLogin" type="radio" name="icons.isLogin" value='1'>需要</label>
                    </td>
                </tr>
                <tr>
                    <td class="rightText" style="width:20%"><span style="color: red;">*</span>修改人：</td>
                    <td class="leftText" style="width:80%">
                        <input id="modifier" name="icons.modifier" value="${icons.modifier}" style="width: 300px">
                    </td>
                </tr>
                <tr>
                    <td class="rightText" style="text-align: right;vertical-align: top;">描述：</td>
                    <td class="leftText">
                        <s:textarea name="icons.note" cols="75" rows="3"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2" align="center">
                        <input type="button" style="text-align: center;width:200px; height: 50px; font-size: 24px" onclick="javascript:history.go(-1)" value="返回">
                        <input type="submit" id="btn" style="text-align: center;width:200px; height: 50px; font-size: 24px" value="保存">
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
</body>

<script>
    $(function(){
        var val = eval("${icons.isClick}");
        if(val=="0"){
            $("#noClick").prop("checked",true);
        }else if(val=="1") {
            $("#yesClick").prop("checked", true);
        }
        var val = eval("${icons.status}");
        if(val=="0"){
            $("#stopUse").prop("checked",true);
        }else if(val=="1") {
            $("#using").prop("checked", true);
        }
        var val = eval("${icons.type}");
        if(val=="0"){
            $("#h5").prop("checked",true);
        }else if(val=="1") {
            $("#old").prop("checked", true);
        }
        var val = eval("${icons.isLogin}");
        if(val=="0"){
            $("#noLogin").prop("checked",true);
        }else if(val=="1") {
            $("#needLogin").prop("checked", true);
        }
    });
</script>
<script type="text/javascript">
//    if($('#radios input[name="icons.isClick"]:checked').val()=="noClick"){
//        $("#shows").show();
//    }else{
//        $("#shows").hide();
//    }
    if("${isOk}"=="ok"){
        alert("修改成功!");
        window.location.href="${pageContext.request.contextPath}/Icons/icons!loadIcons.action";
    }else if("${isOk}"=="no"){
        alert("修改失败!");
        window.location.href="${pageContext.request.contextPath}/Icons/icons!loadIcons.action";
    }
    var maxFiles=20;
    var fileNamesInfo = "";
    var fileNamesLaw = "";
    function changgeURL(){
        var type=$("#type option:selected").val();
        $("#dropz").html("<div class=\"dz-default dz-message\"  style=\"display: block;\"><span>请点击此添加图片或拖放图片进此</span></div>");
        $("#dropz2").html("<div class=\"dz-default dz-message\" style=\"display: block;\"><span>请点击此添加图片或拖放图片进此</span></div>");
        /* 	alert($("#infoImg").val());
         $("#infoImg").val("");
         alert($("#infoImg").val()); */
        fileNamesLaw = "";
        fileNamesInfo = "";
        $("#infoImg").val("");
        if(type==0){
            $("#dropz2").css("display","block");
            $("#dropz").css("display","none");
        }else{
            $("#dropz2").css("display","none");
            $("#dropz").css("display","block");
        }
    }
    //信息披露;
    $("#dropz").dropzone({
        url: "${pageContext.request.contextPath}/Icons/icons!uploadIconsImage.action",
        addRemoveLinks: true,
        dictRemoveLinks: "x",
        dictCancelUpload: "x",
        maxFiles: maxFiles,
        maxFilesize: 3,
        uploadMultiple:false,
        dictDefaultMessage: "请点击此添加图片或拖放图片进此",
        dictInvalidFileType: "图片格式错误,只支持‘png、jpg、jpeg’格式",
        dictFileTooBig: "图片大小超出上传范围",
        dictMaxFilesExceeded: "该图片上传失败,每次只能上传"+maxFiles+"张;建议刷新网页",
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
                var delUrl="${pageContext.request.contextPath}/Icons/icons!removeImage.action";
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


    //信息披露;
    $("#dropz2").dropzone({
        url: "${pageContext.request.contextPath}/Icons/icons!uploadIconsImage.action",
        addRemoveLinks: true,
        dictRemoveLinks: "x",
        dictCancelUpload: "x",
        maxFiles: maxFiles,
        maxFilesize: 3,
        uploadMultiple:false,
        dictDefaultMessage: "请点击此添加图片或拖放图片进此",
        dictInvalidFileType: "图片格式错误,只支持‘png、jpg、jpeg’格式",
        dictFileTooBig: "图片大小超出上传范围",
        dictMaxFilesExceeded: "该图片上传失败,每次只能上传"+maxFiles+"张;建议刷新网页",
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
                var delUrl="${pageContext.request.contextPath}/Icons/icons!removeImage.action";
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
</script>
</html>