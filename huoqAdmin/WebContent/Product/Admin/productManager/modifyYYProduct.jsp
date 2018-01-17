<%@ taglib prefix="c" uri="/struts-tags" %>
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
    <title>新华网-修改产品</title>
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
</head>
<script type="text/javascript">

    var editor;
    KindEditor.ready(function (K) {
        var options = {

            filterMode: true,
            themeType: 'default',
            uploadJson: '${pageContext.request.contextPath}/Product/kindEditor!keuploadproduct.action',
            fileManagerJson: '<%=request.getContextPath()%>/keupload/manager',
            allowFileManager: false,
            resizeType: 1
        };
        editor = K.create('#description', options);
    });

    var editor2;
    KindEditor.ready(function (K) {
        var options = {

            filterMode: true,
            themeType: 'default',
            uploadJson: '${pageContext.request.contextPath}/Product/kindEditor!keuploadproduct.action',
            fileManagerJson: '<%=request.getContextPath()%>/keupload/manager',
            allowFileManager: false,
            resizeType: 1
        };
        editor2 = K.create('#hkly', options);
    });

    var editor3;
    KindEditor.ready(function (K) {
        var options = {

            filterMode: true,
            themeType: 'default',
            uploadJson: '${pageContext.request.contextPath}/Product/kindEditor!keuploadproduct.action',
            fileManagerJson: '<%=request.getContextPath()%>/keupload/manager',
            allowFileManager: false,
            resizeType: 1
        };
        editor3 = K.create('#zjbz', options);
    });

    var editor4;
    KindEditor.ready(function (K) {
        var options = {

            filterMode: true,
            themeType: 'default',
            uploadJson: '${pageContext.request.contextPath}/Product/kindEditor!keuploadproduct.action',
            fileManagerJson: '<%=request.getContextPath()%>/keupload/manager',
            allowFileManager: false,
            resizeType: 1
        };
        editor4 = K.create('#cplxjs', options);
    });

    var editor5;
    KindEditor.ready(function (K) {
        var options = {

            filterMode: true,
            themeType: 'default',
            uploadJson: '${pageContext.request.contextPath}/Product/kindEditor!keuploadproduct.action',
            fileManagerJson: '<%=request.getContextPath()%>/keupload/manager',
            allowFileManager: false,
            resizeType: 1
        };
        editor5 = K.create('#riskSteps', options);
    });

    var editor6;
    KindEditor.ready(function (K) {
        var options = {

            filterMode: true,
            themeType: 'default',
            uploadJson: '${pageContext.request.contextPath}/Product/kindEditor!keuploadproduct.action',
            fileManagerJson: '<%=request.getContextPath()%>/keupload/manager',
            allowFileManager: false,
            resizeType: 1
        };
        editor6 = K.create('#riskHints', options);
    });

    var editor7;
    KindEditor.ready(function (K) {
        var options = {

            filterMode: true,
            themeType: 'default',
            uploadJson: '${pageContext.request.contextPath}/Product/kindEditor!keuploadproduct.action',
            fileManagerJson: '<%=request.getContextPath()%>/keupload/manager',
            allowFileManager: false,
            resizeType: 1
        };
        editor7 = K.create('#Jkrxx', options);
    });
    var isShow = false;

    function isSubmit() {
        var title = $("#title").val();
        $("#hidDescr1").val(editor.html());
        $("#hidHkly").val(editor2.html());
        $("#hidZjbz").val(editor3.html());
        $("#hidCplxjs").val(editor4.html());
        $("#hidRiskSteps").val(editor5.html());
        $("#hidRiskHints").val(editor6.html());
        $("#hidJkrxx").val(editor7.html());

        if ($("#title").val() == "") {
            alert("产品名称不能为空!");
            return false;
        }
        if (!confirm("确定要修改预约产品?")) {
            return false;
        }
        isShow = true;
        return true;

    }

    /*function preview() {
        var url = "${pageContext.request.contextPath}/Product/Admin/releaseProduct!showModifyProduct.action?";
        $("#frm").attr("target", "_blank");
        $('#frm').attr("action", url).submit();
        if (isShow) {
            $("#btnSubmit").css('display', 'inline');
        }
    }

    //发布产品
    function sub() {
            var url = "${pageContext.request.contextPath}/Product/Admin/releaseProduct!releaseYYProduct.action";
            $("#frm").attr("target", "_self");
            $('#frm').attr("action", url).submit();
    }*/

    function setLcqx(value) {
        var lcqx = $("#lcqx").val();
        if (!isEmpty(lcqx)) {
            if (value == "0") {
                //包含开始和结束时间;
                lcqx++;
            } else if (value == "1") {
                //不包含开始时间;
                lcqx--;
            }
            $("#lcqx").val(lcqx);
        }
    }
</script>
<body>
<div align="center">
    <jsp:include page="/Product/Admin/common/head.jsp"/>
    <div class="main">
        <h1 style="text-align: center;">修改预约产品</h1>
        <form action="${pageContext.request.contextPath}/Product/Admin/releaseProduct!releaseYYProduct.action?" id="frm"
              method="post" enctype="multipart/form-data" onsubmit="return isSubmit()">
            <table border="1" style="width: 850px;">

                <tr>
                    <td class="rightText" style="width:20%"><span style="color: red;">*</span>产品名称：</td>
                    <td class="leftText" style="width:80%">
                        <input id="id" type="hidden" name="product.id" value="${product.id}">
                        <input id="title" name="product.title" value="${product.title}"
                               style="width: 500px">
                    </td>
                </tr>
                <tr>
                    <td class="rightText" style="width:20%"><span style="color: red;">*</span>发起人姓名：</td>
                    <td class="leftText" style="width:80%">
                        <input id="realName" name="product.realName" value="${product.realName}" style="width: 200px">
                    </td>
                </tr>
                <tr>
                    <td class="rightText" style="width:20%"><span style="color: red;">*</span>发起人身份证号：</td>
                    <td class="leftText" style="width:80%">
                        <input id="idcard" name="product.idcard" value="${product.idcard}" style="width: 200px">
                    </td>
                </tr>
                <tr>
                    <td class="rightText" style="width:20%"><span style="color: red;">*</span>发起人联系电话：</td>
                    <td class="leftText" style="width:80%">
                        <input id="phone" name="product.phone" value="${product.phone}" style="width: 200px">
                    </td>
                </tr>
                <tr>
                    <td class="rightText" style="width:20%"><span style="color: red;">*</span>发起人联系地址：</td>
                    <td class="leftText" style="width:80%">
                        <input id="address" name="product.address" value="${product.address}" style="width: 500px">
                    </td>
                </tr>
                <tr>
                    <td class="rightText"><span style="color: red;">*</span>投资类别：</td>
                    <td class="leftText">
                        <select id="investType" name="product.investType">
                            <option value="5">货押宝</option>
                            <option value="0">车无忧</option>
                            <option value="7">信用贷</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td class="rightText"><span style="color: red;">*</span>推荐产品：</td>
                    <td class="leftText">
                        <label><input type="radio" name="product.isRecommend" checked="checked" value="0">否</label>
                        <label><input type="radio" name="product.isRecommend" value="1">是</label>
                    </td>
                </tr>
                <tr>
                    <td class="rightText"><span style="color: red;">*</span>常规产品类型：</td>
                    <td class="leftText">
                        <label><input type="radio" name="product.cgcpType" checked="checked" value="0">普通</label>
                        <label><input type="radio" name="product.cgcpType" value="1">爆款</label>
                        <label><input type="radio" name="product.cgcpType" value="2">活动</label>
                    </td>
                </tr>
                <tr>
                    <td class="rightText"><span style="color: red;">*</span>奖励产品：</td>
                    <td class="leftText">
                        <label><input type="radio" class="radioItem" name="product.isJiangLi" checked="checked"
                                      value="0">否</label>
                        <label><input type="radio" class="radioItem" name="product.isJiangLi" value="1">是</label>
                    </td>
                </tr>
                <tr>
                    <td class="rightText"><span style="color: red;">*</span>放置模块：</td>
                    <td class="leftText">
                        <label><input type="radio" name="product.module" checked="checked" value="0">优选理财</label>
                        <label><input type="radio" name="product.module" value="1">特色理财</label>
                    </td>
                </tr>
                <tr>
                    <td class="rightText"><span style="color: red;">*</span>项目总额：</td>
                    <td class="leftText"><input id="financingAmount" name="product.financingAmount"
                                                value="${product.financingAmount}">(元)
                    </td>
                </tr>
                <tr>
                    <td class="rightText"><span style="color: red;">*</span>起投金额：</td>
                    <td class="leftText">
                        <input id="atleastMoney1" name="product.atleastMoney" value="${product.atleastMoney}">(元)
                    </td>
                </tr>
                <tr>
                    <td class="rightText"><span style="color: red;">*</span>总年化收益：</td>
                    <td class="leftText">
                        <input type="hidden" id="baseEarnings" name="product.baseEarnings">
                        <input type="hidden" id="jiangLiEarnings" name="product.jiangLiEarnings" value="0">
                        <input id="annualEarnings" value="${product.annualEarnings}" name="product.annualEarnings"
                               style="width: 50px">%
                    </td>
                </tr>
                <tr>
                    <td class="rightText"><span style="color: red;">*</span>基础年化收益：</td>
                    <td class="leftText"><input id="displayBaseEarnings" value="${product.displayBaseEarnings}"
                                                onchange="" name="product.displayBaseEarnings" style="width: 50px"
                                                aria-colspan="3">%
                    </td>
                </tr>
                <tr>
                    <td class="rightText"><span style="color: red;">*</span>额外年化收益：</td>
                    <td class="leftText"><input id="displayExtraEarnings" value="${product.displayExtraEarnings}"
                                                name="product.displayExtraEarnings" style="width: 50px">%
                    </td>
                </tr>
                <tr>
                    <td class="rightText"><span style="color: red;">*</span>还款方式：</td>
                    <td class="leftText">
                        <select name="product.payInterestWay" id="payInterestWay">
                            <!-- <option  value="0">按月付息到期还本</option> -->
                            <option selected="selected" value="1">到期还本付息</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td class="rightText"><span style="color: red;">*</span>起息日：</td>
                    <td class="leftText">
                        <select name="product.calcInterestWay" id="calcInterestWay" onchange="setLcqx(this.value)">
                            <option value="0">T + 0</option>
                            <option selected="selected" value="1">T + 1</option>
                        </select>
                    </td>
                </tr>
                <input id="finishTime" name="finishTime" type="hidden" value="">
                <input id="endTime" type="hidden" name="endTime" value="">
                <tr>
                    <td class="rightText"><span style="color: red;">*</span>理财期限：</td>
                    <td class="leftText"><input id="lcqx" style="width: 50px" name="product.lcqx"
                                                value="${product.lcqx}">天<b id="lcqxmsg"
                                                                            style="color: red"></b>
                        <span style="color: red">&nbsp;&nbsp;*截至3天前不能购买</span>
                    </td>
                </tr>
                <tr>
                    <td class="rightText" style="width:20%">活动标语：</td>
                    <td class="leftText" style="width:80%">
                        <input id="hdby" name="product.hdby" value="${product.hdby}" style="width: 500px"><br>
                    </td>
                </tr>
                <tr>
                    <td class="rightText" style="width:20%"><span style="color: red;">*</span>借款用途：</td>
                    <td class="leftText" style="width:80%">
                        <input id="jkyt" name="product.jkyt" style="width: 500px" value="${product.jkyt}"><br>
                    </td>
                </tr>
                <tr id="jk">
                    <td class="rightText" style="text-align: right;vertical-align: top;">借款人信息：</td>
                    <td class="leftText">
                        <input type="hidden" id="hidJkrxx" name="product.jkrxx">
                        <div id="Jkrxx" class="divEdit" contenteditable="true" readonly="readonly"
                             style="height: 250px;width: 650px;">${product.jkrxx}</div>
                    </td>
                </tr>
                <tr id="hk">
                    <td class="rightText" style="text-align: right;vertical-align: top;">还款来源：</td>
                    <td class="leftText">
                        <input type="hidden" id="hidHkly" name="product.hkly">
                        <div id="hkly" class="divEdit" contenteditable="true" readonly="readonly"
                             style="height:250px">${product.hkly}</div>
                    </td>
                </tr>
                <tr id="zj">
                    <td class="rightText" style="text-align: right;vertical-align: top;">资金保障：</td>
                    <td class="leftText">
                        <input type="hidden" id="hidZjbz" name="product.zjbz">
                        <div id="zjbz" class="divEdit" contenteditable="true" readonly="readonly"
                             style="width:650px;height:250px;">${product.zjbz}</div>
                    </td>
                </tr>
                <tr id="xmsm">
                    <td class="rightText" style="text-align: right;vertical-align: top;">产品类型介绍：</td>
                    <td class="leftText">
                        <input type="hidden" id="hidCplxjs" name="product.cplxjs">
                        <div id="cplxjs" class="divEdit" contenteditable="true" readonly="readonly"
                             style="width:650px;height:250px;">${product.cplxjs}</div>
                    </td>
                </tr>
                <tr id="desc">
                    <td class="rightText" style="text-align: right;vertical-align: top;">项目介绍：</td>
                    <td class="leftText">
                        <input type="hidden" id="hidDescr1" name="product.description">
                        <div id="description" class="divEdit" contenteditable="true" readonly="readonly"
                             style="width: 650px;height: 250px;">${product.description}</div>
                    </td>
                </tr>
                <tr id="xmtd">
                    <td class="rightText" style="text-align: right;vertical-align: top;">项目特点：</td>
                    <td class="leftText">
                        <input type="hidden" id="hidProductStyle" name="product.productStyle">
                        <div id="productStyle" class="divEdit" contenteditable="true"
                             style="height:250px;">${product.productStyle}</div>
                    </td>
                </tr>
                <tr id="fk">
                    <td class="rightText" style="text-align: right;vertical-align: top;">风控措施：</td>
                    <td class="leftText">
                        <input type="hidden" id="hidRiskSteps" name="product.riskSteps">
                        <div id="riskSteps" class="divEdit" contenteditable="true"
                             style="width:650px;height:250px;">${product.riskSteps}</div>
                    </td>
                </tr>
                <tr id="fx">
                    <td class="rightText" style="text-align: right;vertical-align: top;">风险提示：</td>
                    <td class="leftText">
                        <input type="hidden" id="hidRiskHints" name="product.riskHints">
                        <div id="riskHints" class="divEdit" contenteditable="true"
                             style="width:650px;height:250px;">${product.riskHints}</div>
                    </td>
                </tr>
                <!--<tr>
                    <td class="rightText" style="text-align: right;vertical-align: top;">项目图片：</td>
                    <td class="leftText">
                    <input type="hidden" name="product.infoImg" value="" id="infoImg">
                    <div id="dropz" class="dropzone" name="file"></div>
                    </td>
                </tr>  -->

                <tr>
                    <td colspan="2" align="center">
                        <input type="submit" id="btnSubmit"
                               style="display:none;text-align: center;width:200px; height: 50px; font-size: 24px"
                               value="修改预约产品">
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<form action="${pageContext.request.contextPath}/Product/Admin/releaseProduct!test.action" style="display:none;">
    <input id="endTime2" name="st" onClick="WdatePicker()" readonly="readonly" style="width:180px;text-align: center;">
    <div id="description2" class="divEdit" contenteditable="true"></div>
    <input type="hidden" id="hidDescr" name="product.description">
    <input type="submit" style="text-align: center;width:200px; height: 50px; font-size: 24px" value="预约发布"
           onClick="javascript:Submit();">
</form>
</body>
<script type="text/javascript">
    $(document).ready(function () {

            $(".radioItem").change(function () {
                    var baseEarnings = $("#baseEarnings").val();
                    var jiangLiEarnings = $("#jiangLiEarnings").val();
                    if (baseEarnings == null || "" == baseEarnings) {
                        baseEarnings = 0;
                    }
                    if (jiangLiEarnings == null || "" == jiangLiEarnings) {
                        jiangLiEarnings = 0;
                    }
                    var isJiangLi = $('input[name="product.isJiangLi"]:checked').val();
                    if ("0" == isJiangLi) {
                        $("#jiangLiEarnings").attr("readonly", true);//将奖励收益元素设置为readonly
                        $("#jiangLiEarnings").val('');
                        $("#annualEarnings").val(baseEarnings);
                        $("#span_earnings").text(baseEarnings);
                    }
                    if ("1" == isJiangLi) {
                        $("#jiangLiEarnings").attr("readonly", false);
                        var annualEarnings = parseFloat(baseEarnings) + parseFloat(jiangLiEarnings);
                        $("#annualEarnings").val(annualEarnings.toFixed(2));
                        $("#span_earnings").text(annualEarnings.toFixed(2));
                    }

                }
            );

            $("#baseEarnings").blur(function () {//失去焦点是触发
                    var displayBaseEarnings = $("#displayBaseEarnings").val();
                    var jiangLiEarnings = $("#jiangLiEarnings").val();
                    if (jiangLiEarnings == null || "" == jiangLiEarnings) {
                        jiangLiEarnings = 0;
                    }
                    if (!isPrice(displayBaseEarnings)) {
                        alert("基础年化收益格式不正确!");
                        return false;
                    }
                    var annualEarnings = parseFloat(baseEarnings) + parseFloat(jiangLiEarnings);
                    $("#span_earnings").text(annualEarnings.toFixed(2));
                    $('#annualEarnings').val(annualEarnings.toFixed(2));
                }
            );

            $("#jiangLiEarnings").change(function () {//发生改变时触发
                    var annualEarnings = $("#annualEarnings").val();
                    var jiangLiEarnings = $("#jiangLiEarnings").val();
                    if (jiangLiEarnings == null || "" == jiangLiEarnings) {
                        jiangLiEarnings = 0;
                    }
                    if (!isPrice(annualEarnings)) {
                        alert("基础年化收益格式不正确!");
                        return false;
                    } else if (!isPrice(jiangLiEarnings)) {
                        alert("奖励收益格式不正确!");
                        return false;
                    }
                    var annualEarnings = parseFloat(annualEarnings) + parseFloat(jiangLiEarnings);
                    $("#span_earnings").text(annualEarnings.toFixed(2));
                    $('#annualEarnings').val(annualEarnings.toFixed(2));
                }
            );
            //验证发布产品名称的合法性
            //规则为：1,同类型的产品只能出现一个为“营销中”且新增的产品默认为“排队中”
            //        2,新增同类型产品编号要大于当前编号
            $("#title").blur(function () {//失去焦点时触发
                    var title = $("#title").val();
                    if (title != null && title != '') {
                        validateProducdtionTitle(title);
                    }
                }
            );
            $("#lcqx").blur(function () {//失去焦点时触发
                    $("#lcqxmsg").html("");
                    var lcqx = $("#lcqx").val();
                    if (lcqx < 4) {
                        $("#lcqxmsg").html("理财期限必须大于4天！");
                        $("#lcqx").focus();
                    }
                }
            );
        }
    );
    if ("${isOk}" == "ok") {
        alert("修改成功!");
        window.location.href = "${pageContext.request.contextPath}/Product/Admin/releaseProduct!productRecord.action";
    } else if ("${isOk}" == "no") {
        alert("修改失败!");
    }

    var maxFiles = 20;
    var fileNamesInfo = "";
    var fileNamesLaw = "";
    //信息披露;
    $("#dropz").dropzone({
        url: "${pageContext.request.contextPath}/Product/Admin/releaseProduct!uploadInfoImage.action",
        addRemoveLinks: true,
        dictRemoveLinks: "x",
        dictCancelUpload: "x",
        maxFiles: maxFiles,
        maxFilesize: 1,
        uploadMultiple: false,
        dictDefaultMessage: "请点击此添加图片或拖放图片进此",
        dictInvalidFileType: "图片格式错误,只支持‘png、jpg、jpeg’格式",
        dictFileTooBig: "图片大小超出上传范围",
        dictMaxFilesExceeded: "该图片上传失败,每次只能上传" + maxFiles + "张",
        acceptedFiles: ".png,.jpg,.jpeg",
        dictRemoveFile: "删除图片",
        init: function () {
            this.on("success", function (file, response) {
                var json = eval('(' + response + ')');
                if ("ok" == json.status) {
                    fileNamesInfo += json.json;
                    file.serverId = json.json;
                    //alert(fileNamesInfo);
                    console.log("File " + file.name + "uploaded");
                    $("#infoImg").val(fileNamesInfo);
                } else {
                    alert("上传图片失败");
                }
            });
            this.on("removedfile", function (file) {
                var delUrl = "${pageContext.request.contextPath}/Product/Admin/releaseProduct!removeInfoImage.action";
                var removeId = file.serverId;
                fileNamesInfo = fileNamesInfo.replace(removeId, "");
                //alert("undefined"==removeId+"");
                if ("undefined" == removeId + "") {
                    return false;
                }
                $("#infoImg").val(fileNamesInfo);
                //alert(removeId);
                $.post(delUrl, "removeId=" + removeId, function (data) {
                    if ("ok" != data.status)
                        alert(data.json);//失败的结果
                });
                console.log("File " + file.name + "removed");

            });
        }
    });
    //法律意见书
    $("#dropz2").dropzone({
        url: "${pageContext.request.contextPath}/Product/Admin/releaseProduct!uploadLawImage.action",
        addRemoveLinks: true,
        dictRemoveLinks: "x",
        dictCancelUpload: "x",
        maxFiles: maxFiles,
        maxFilesize: 1,
        uploadMultiple: false,
        dictDefaultMessage: "请点击此添加图片或拖放图片进此",
        dictInvalidFileType: "图片格式错误,只支持‘png、jpg、jpeg’格式",
        dictFileTooBig: "图片大小超出上传范围",
        dictMaxFilesExceeded: "该图片上传失败,每次只能上传" + maxFiles + "张",
        acceptedFiles: ".png,.jpg,.jpeg",
        dictRemoveFile: "删除图片",
        init: function () {
            this.on("success", function (file, response) {
                var json = eval('(' + response + ')');
                if ("ok" == json.status) {
                    fileNamesLaw += json.json;
                    file.serverId = json.json;
                    //alert(fileNamesInfo);
                    console.log("File " + file.name + "uploaded");
                    $("#lawImg").val(fileNamesLaw);
                } else {
                    alert("上传图片失败");
                }
            });
            this.on("removedfile", function (file) {
                var delUrl = "${pageContext.request.contextPath}/Product/Admin/releaseProduct!removeLawImage.action";
                var removeId = file.serverId;
                fileNamesLaw = fileNamesLaw.replace(removeId, "");
                $("#lawImg").val(fileNamesLaw);
                $.post(delUrl, "removeId=" + removeId, function (data) {
                    if ("ok" != data.status)
                        alert(data.json);//失败的结果
                });
                console.log("File " + file.name + "removed");

            });
        }
    });
    $(document).ready(function () {

            $(".radioItem").change(function () {
                    var baseEarnings = $("#baseEarnings").val();
                    var jiangLiEarnings = $("#jiangLiEarnings").val();
                    if (baseEarnings == null || "" == baseEarnings) {
                        baseEarnings = 0;
                    }
                    if (jiangLiEarnings == null || "" == jiangLiEarnings) {
                        jiangLiEarnings = 0;
                    }
                    var isJiangLi = $('input[name="product.isJiangLi"]:checked').val();
                    if ("0" == isJiangLi) {
                        $("#jiangLiEarnings").attr("readonly", true);//将奖励收益元素设置为readonly
                        $("#jiangLiEarnings").val('');
                        $("#annualEarnings").val(baseEarnings);
                        $("#span_earnings").text(baseEarnings);
                    }
                    if ("1" == isJiangLi) {
                        $("#jiangLiEarnings").attr("readonly", false);
                        var annualEarnings = parseFloat(baseEarnings) + parseFloat(jiangLiEarnings);
                        $("#annualEarnings").val(annualEarnings.toFixed(2));
                        $("#span_earnings").text(annualEarnings.toFixed(2));
                    }

                }
            );

            $("#baseEarnings").blur(function () {//失去焦点是触发
                    var displayBaseEarnings = $("#displayBaseEarnings").val();
                    var jiangLiEarnings = $("#jiangLiEarnings").val();
                    if (jiangLiEarnings == null || "" == jiangLiEarnings) {
                        jiangLiEarnings = 0;
                    }
                    if (!isPrice(displayBaseEarnings)) {
                        alert("基础年化收益格式不正确!");
                        return false;
                    }
                    var annualEarnings = parseFloat(baseEarnings) + parseFloat(jiangLiEarnings);
                    $("#span_earnings").text(annualEarnings.toFixed(2));
                    $('#annualEarnings').val(annualEarnings.toFixed(2));
                }
            );

            $("#jiangLiEarnings").change(function () {//发生改变时触发
                    var displayBaseEarnings = $("#displayBaseEarnings").val();
                    var jiangLiEarnings = $("#jiangLiEarnings").val();
                    if (jiangLiEarnings == null || "" == jiangLiEarnings) {
                        jiangLiEarnings = 0;
                    }
                    if (!isPrice(displayBaseEarnings)) {
                        alert("基础年化收益格式不正确!");
                        return false;
                    } else if (!isPrice(jiangLiEarnings)) {
                        alert("奖励收益格式不正确!");
                        return false;
                    }
                    var annualEarnings = parseFloat(baseEarnings) + parseFloat(jiangLiEarnings);
                    $("#span_earnings").text(annualEarnings.toFixed(2));
                    $('#annualEarnings').val(annualEarnings.toFixed(2));
                }
            );
            $("#displayBaseEarnings").change(function () {//发生改变时触发
                    var annualEarnings = $("#annualEarnings").val();
                    var displayBaseEarnings = $("#displayBaseEarnings").val();
                    if (displayBaseEarnings == null || "" == displayBaseEarnings) {
                        displayBaseEarnings = 0;
                    }
                    if (!isPrice(annualEarnings)) {
                        alert("年化收益格式不正确!");
                        return false;
                    } else if (!isPrice(displayBaseEarnings)) {
                        alert("基础年化收益格式不正确!");
                        return false;
                    }
                    var displayExtraEarnings = parseFloat(annualEarnings) - parseFloat(displayBaseEarnings);
                    $('#displayExtraEarnings').val(displayExtraEarnings.toFixed(2));
                }
            );
            $("#lcqx").blur(function () {//失去焦点时触发
                    $("#lcqxmsg").html("");
                    var lcqx = $("#lcqx").val();
                    if (lcqx < 4) {
                        $("#lcqxmsg").html("理财期限必须大于4天！");
                        $("#lcqx").focus();
                    }
                }
            );

        $("#investType").prop("value",${product.investType});
        var investType = $("#investType option:selected").val();
        if (investType == 5) { //货押宝类型时
            $("#zj").hide();
            $("#car_dingf_addr").hide();
            $("#car_dingf").hide();
            $("#jk_hjaddr").hide();
            $("#jk_age").hide();
            $("#jk_edu").hide();
            $("#jk_merr").hide();
            $("#jk").show();
            $("#hk").show();
            $("#fk").show();
            $("#fx").show();
            $("#xmsm").hide();
            $("#xmImg").show();
            $("#xmtd").hide();
        } else if (investType == 6) {  //车贷宝类型时
            $("#xmsm").hide();
            $("#xmImg").hide();
            $("#xmtd").hide();
            $("#hk").show();
            $("#fk").show();
            $("#car_dingf_addr").show();
            $("#car_dingf").show();
            $("#jk_hjaddr").show();
            $("#jk_age").show();
            $("#jk_edu").show();
            $("#jk_merr").show();
        } else if (investType == 0) {
            $("#fx").hide();
            $("#jk").hide();
            $("#xmsm").show();
            $("#xmtd").hide();
            $("#fk").hide();
            $("#car_dingf_addr").hide();
            $("#car_dingf").hide();
            $("#jk_hjaddr").hide();
            $("#jk_age").hide();
            $("#jk_edu").hide();
            $("#jk_merr").hide();
            $("#hk").show();
            $("#zj").show();
        } else {
            $("#zj").hide();
            $("#hk").show();
        }

            $("#investType").change(function () {//发生改变时触发
                    var investType = $("#investType option:selected").val();
                    if (investType == 5) { //货押宝类型时
                        $("#zj").hide();
                        $("#car_dingf_addr").hide();
                        $("#car_dingf").hide();
                        $("#jk_hjaddr").hide();
                        $("#jk_age").hide();
                        $("#jk_edu").hide();
                        $("#jk_merr").hide();
                        $("#jk").show();
                        $("#hk").show();
                        $("#fk").show();
                        $("#fx").show();
                        $("#xmsm").hide();
                        $("#xmImg").show();
                        $("#xmtd").hide();
                    } else if (investType == 6) {  //车贷宝类型时
                        $("#xmsm").hide();
                        $("#xmImg").hide();
                        $("#xmtd").hide();
                        $("#hk").show();
                        $("#fk").show();
                        $("#car_dingf_addr").show();
                        $("#car_dingf").show();
                        $("#jk_hjaddr").show();
                        $("#jk_age").show();
                        $("#jk_edu").show();
                        $("#jk_merr").show();
                    } else if (investType == 0) {
                        $("#fx").hide();
                        $("#jk").hide();
                        $("#xmsm").show();
                        $("#xmtd").hide();
                        $("#fk").hide();
                        $("#car_dingf_addr").hide();
                        $("#car_dingf").hide();
                        $("#jk_hjaddr").hide();
                        $("#jk_age").hide();
                        $("#jk_edu").hide();
                        $("#jk_merr").hide();
                        $("#hk").show();
                        $("#zj").show();
                    } else {
                        $("#zj").hide();
                        $("#hk").show();
                    }
                }
            );

        }
    );
    $("#btnSubmit").css('display', 'inline');
</script>
<script type="text/javascript">
    var type = '${product.investType}';
    $("#investType").find("option[value='" + type + "']").attr("selected", true);
</script>
<script type="text/javascript">
    var type = '${product.payInterestWay}';
    $("#payInterestWay").find("option[value='" + type + "']").attr("selected", true);
</script>
<script type="text/javascript">
    var type = '${product.calcInterestWay}';
    $("#calcInterestWay").find("option[value='" + type + "']").attr("selected", true);
</script>
<script type="text/javascript">
    var type = '${product.isRecommend}';
    $("input[name='product.isRecommend'][value='" + type + "'").attr("checked", true);
</script>
<script type="text/javascript">
    var type = '${product.cgcpType}';
    $("input[name='product.cgcpType'][value='" + type + "'").attr("checked", true);
</script>
<script type="text/javascript">
    var type = '${product.isJiangLi}';
    $("input[name='product.isJiangLi'][value='" + type + "'").attr("checked", true);
</script>
<script type="text/javascript">
    var type = '${product.module}';
    $("input[name='product.module'][value='" + type + "'").attr("checked", true);
</script>
</html>