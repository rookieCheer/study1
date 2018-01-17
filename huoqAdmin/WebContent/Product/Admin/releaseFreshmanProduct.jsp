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
    <title>新华网-发布新手产品</title>
    <style type="text/css">
        .rightText {
            text-align: right;
        }

        .leftText {
            text-align: left;
        }

        .divEdit {
            width: 100%;
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
        #tag {
            padding: 3px 5px;
            border: 1px solid #ccc;
        }

        #box span {
            float: left;
            padding: 0px 4px 0px 2px;
            margin-right: 5px;
            color: #333;
            font-size: 12px;
            cursor: pointer;
            border: 1px solid #ccc;
        }

        #box span::after {
            content: '×';
            display: inline-block;
            font-size: 16px;
        }

        .taginput {
            width: 100px;
            margin: 0px;
            padding: 0px;
        }

        }
    </style>
</head>
<script type="text/javascript">
    Array.prototype.indexOf = function (val) {
        for (var i = 0; i < this.length; i++) {
            if (this[i] == val) return i;
        }
        return -1;
    };
    Array.prototype.remove = function (val) {
        var index = this.indexOf(val);
        if (index > -1) {
            this.splice(index, 1);
        }
    };
    var Str = new Array();
    function addThis(id) {
        var str = $(id).val();
        if (str != '') {
            $("#box").append('<span onclick="remo(this)">' + str + '</span>');
            Str.push(str);
        }
        $(id).val('');
        $("#subform").val(Str);
    }
    function remo(ids) {
        var removestrtt = $(ids).html();
        Str.remove(removestrtt);
        $(ids).remove();
        $("#subform").val(Str);
    }
</script>
<script type="text/javascript">

    var editor;//项目介绍
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
        editor.html('新华新手是新华金典平台推出的一款为核心企业量身打造的供应链金融理财产品，具有低风险、高收益、高流动性的特点。借款人以小微企业为主，借款资金主要用于帮助企业解决融资，提高资金利用率，扩大企业经营资产规模等需求。新华金典通过与大数据机构、核心企业的合作，严格筛选资金需求合理，经营利润率较高的借款企业，同时严格遵循小额分散的原则，对借款企业的资产规模和负债结构进行严格审核，最大程度降低项目风险。<br />');
    });
    var editor2;//还款来源
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
        editor2.html('1、第一还款来源：借款企业经营收入。<br />' +
            '2、第二还款来源：核心企业回购资金。<br />');

    });

    var editor3;//资金保障
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

    var editor4;//产品类型介绍
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
    var editor5;//项目特点
    KindEditor.ready(function (K) {
        var options = {

            filterMode: true,
            themeType: 'default',
            uploadJson: '${pageContext.request.contextPath}/Product/kindEditor!keuploadproduct.action',
            fileManagerJson: '<%=request.getContextPath()%>/keupload/manager',
            allowFileManager: false,
            resizeType: 1
        };
        editor5 = K.create('#productStyle', options);
    });
    
    var editor6;//风控措施
    KindEditor.ready(function (K) {
        var options = {

            filterMode: true,
            themeType: 'default',
            uploadJson: '${pageContext.request.contextPath}/Product/kindEditor!keuploadproduct.action',
            fileManagerJson: '<%=request.getContextPath()%>/keupload/manager',
            allowFileManager: false,
            resizeType: 1
        };
        editor6 = K.create('#riskSteps', options);
        editor6.html('1、平台严格审核借款人资质，并聘请专业人员评估企业运行情况，明确资金用途，确保借款人还款来源稳定。<br />' +
            '2、核心企业承诺回购债权。<br />' +
            '3、投资人资金与平台隔离，保证投资人资金安全。<br />' +
            '4、借款人资料真实有效，借款申请经过平台风控团队的严格审核。<br />');
    });
    var editor7;//风险提示
    KindEditor.ready(function (K) {
        var options = {

            filterMode: true,
            themeType: 'default',
            uploadJson: '${pageContext.request.contextPath}/Product/kindEditor!keuploadproduct.action',
            fileManagerJson: '<%=request.getContextPath()%>/keupload/manager',
            allowFileManager: false,
            resizeType: 1
        };
        editor7 = K.create('#riskHints', options);
        editor7.html('投资有风险 理财需谨慎本产品不等同于银行存款，投资人应根据平台提供的项目信息及资料审慎思考、独立判断，并自行承担可能面临的投资风险，包括但不限于政策风险、延时风险、流动性风险、兑付风险和不可抗力风险等。<br />');
    });
    var editor8;//借款人信息
    KindEditor.ready(function (K) {
        var options = {

            filterMode: true,
            themeType: 'default',
            uploadJson: '${pageContext.request.contextPath}/Product/kindEditor!keuploadproduct.action',
            fileManagerJson: '<%=request.getContextPath()%>/keupload/manager',
            allowFileManager: false,
            resizeType: 1
        };
        editor8 = K.create('#Jkrxx', options);
    });
    var isShow = false;
    function isSubmit() {
        var title = $("#title").val();
        var investType = $("#investType").find("option:selected").text();
        /*if (title.indexOf(investType) < 0) {
            if (!confirm("项目名称与所选类型不一致,是否继续发布")) {
                return false;
            }
        }*/
        $("#hidDescr1").val(editor.html());
        $("#hidHkly").val(editor2.html());
        /* $("#hidZjbz").val(editor3.html()); */
        $("#hidCplxjs").val(editor4.html());
        $("#hidProductStyle").val(editor5.html());
      	$("#hidRiskSteps").val(editor6.html());
      	$("#hidRiskHints").val(editor7.html());
      	$("#hidJkrxx").val(editor8.html());
        var atleastMoney = $("#atleastMoney").val();
        var financingAmount = $("#financingAmount").val();
        var reg = /^[\u4E00-\u9FA5]+$/;
        var idcardReg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
        var gradeReg=/^[0-9]*$/;
        var proNo=$('#title').val().slice(7);
        var annualEarnings = $("#annualEarnings").val();//年利率
        var displayBaseEarnings = $("#displayBaseEarnings").val();//基础利率
        var displayExtraEarnings = $("#displayExtraEarnings").val();
        var earning = parseInt(displayBaseEarnings) + parseInt(displayExtraEarnings);
        if(!gradeReg.test(proNo)){
        	alert("产品标号必须为数字!");
        	return false;
        }else if(proNo==''){
        	alert("请输入产品标号");
        	return false;
        } else if (!isPhoneNumber($("#phone").val())) {
            alert("联系电话不正确!");
            return false;
        } else if ($("#idcard").val() == 0 || $("#idcard").val() == "" ) {
            alert("证件号不正确!");
            return false;
        } else if ($("#address").val() == null) {
            alert("联系地址不正确!");
            return false;
        } else if (!isOnlyNumber(financingAmount)) {
            alert("项目总额格式不正确!");
            return false;
        } else if (!isOnlyNumber(atleastMoney)) {
            alert("起投金额格式不正确!");
            return false;
        } else if (!isPrice($("#annualEarnings").val())) {
            alert("年化收益格式不正确!");
            return false;
        } else if ($("#finishTime").val() == "") {
            alert("项目结束时间不能为空!");
            return false;
        } else if (parseInt(atleastMoney) > parseInt(financingAmount)) {
            alert("起投金额 【不能大于】项目总额!");
            return false;
        } else if (parseInt(financingAmount) % 1000 != 0) {
            alert("项目总额必须为【1000】的整数倍!");
            return false;
        }else if ($("#jkyt").val() == "") {
            alert("借款用途不能为空");
            return false;
        }

        var annualEarnings = $("#annualEarnings").val();//年利率
        var displayBaseEarnings = $("#displayBaseEarnings").val();//基础利率
        var displayExtraEarnings = $("#displayExtraEarnings").val();
        var earning = parseFloat(displayBaseEarnings) + parseFloat(displayExtraEarnings);

        if (!isPrice($("#displayBaseEarnings").val())) {
            alert("基础年化收益格式不正确!");
            return false;
        }
        if (earning != parseFloat(annualEarnings)){
            alert("年化收益不正确!");
            return false;
        }

        /* $("#btnSubmit").attr("disabled","disabled"); */
        isShow = true;
        return true;
    }

    /**
     * 设置不能购买时间;
     */
    function setNoBuyDate(obj) {
        var newObj = obj.value.replace(/-/g, "/");
        //alert(newObj);
        var today = new Date(newObj);
        //var num = $("#dateNum").val()-1;
        //today.setDate(today.getDate() - num);
        //不准购买日期;
        var noBuyDate = dateFormat(today);
        //alert(noBuyDate);
        //$("#noBuyTime").text(noBuyDate);
        $("#endTime").val(noBuyDate);
        /* var payWay = $("#calcInterestWay").val();
         var diffDayNum = DateDiff(dateFormat(new Date()),obj.value)
         if(payWay=="0"){
         //包含开始和结束时间;
         diffDayNum++;
         }else if(payWay=="1"){
         //不包含开始时间;
         }
         //alert(diffDayNum);
         $("#lcqx").val(diffDayNum); */
    }

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

    //预览发布产品效果
    function preview() {
        //alert("0000");
        var url = "${pageContext.request.contextPath}/Product/Admin/releaseProduct!showProductDetailsFreshman.action";
        $("#frm").attr("target", "_blank");
        $('#frm').attr("action", url).submit();
        if (isShow) {
            $("#btnSubmit").css('display', 'inline');
        }
    }

    //发布产品
    function sub() {
        //alert("0000");
        var url = "${pageContext.request.contextPath}/Product/Admin/releaseFreshmanProduct!releaseProduct.action";
        $("#frm").attr("target", "_self");
        $('#frm').attr("action", url).submit();
    }

    /**
     * 获取日期的yyyy-MM-dd格式
     */
    function dateFormat(today) {
        return today.getFullYear() + "-" + ((today.getMonth() + 101) + "").substring(1) + "-" + ((today.getDate() + 100) + "").substring(1);
    }

    /**比较两个时间的相差天数;不包含开始时间, 例如 18号到20号,相差2天;
     * @param sDate1 开始时间;
     * @param sDate2 结束时间;
     */
    function DateDiff(sDate1, sDate2) {    //sDate1和sDate2是2006-12-18格式
        var aDate, oDate1, oDate2, iDays;
        aDate = sDate1.split("-");
        oDate1 = new Date(aDate[1] + '-' + aDate[2] + '-' + aDate[0]);    //转换为12-18-2006格式
        aDate = sDate2.split("-");
        oDate2 = new Date(aDate[1] + '-' + aDate[2] + '-' + aDate[0]);
        //iDays = parseInt(Math.abs(oDate1 - oDate2) / 1000 / 60 / 60 / 24);    //把相差的毫秒数转换为天数
        return iDays;
    }
  
    $("#annualEarnings").blur(function () {//失去焦点是触发
                var annualEarnings = $("#annualEarnings").val();
                if (annualEarnings == null || "" == annualEarnings) {
                    annualEarnings = 0;
                }
                if (!isPrice(annualEarnings)) {
                    alert("基础年化收益格式不正确!");
                    return false;
                }
                $('#baseEarnings').val(annualEarnings);
            }
    );
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
                    var baseEarnings = $("#baseEarnings").val();
                    var jiangLiEarnings = $("#jiangLiEarnings").val();
                    if (jiangLiEarnings == null || "" == jiangLiEarnings) {
                        jiangLiEarnings = 0;
                    }
                    if (!isPrice(baseEarnings)) {
                        alert("基础年化收益格式不正确!");
                        return false;
                    }
                    var annualEarnings = parseFloat(baseEarnings) + parseFloat(jiangLiEarnings);
                    $("#span_earnings").text(annualEarnings.toFixed(2));
                    $('#annualEarnings').val(annualEarnings.toFixed(2));
                }
            );

            $("#jiangLiEarnings").change(function () {//发生改变时触发
                    var baseEarnings = $("#baseEarnings").val();
                    var jiangLiEarnings = $("#jiangLiEarnings").val();
                    if (jiangLiEarnings == null || "" == jiangLiEarnings) {
                        jiangLiEarnings = 0;
                    }
                    if (!isPrice(baseEarnings)) {
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
                    var baseEarnings = $("#baseEarnings").val();
                    var displayBaseEarnings = $("#displayBaseEarnings").val();
                    if (displayBaseEarnings == null || "" == displayBaseEarnings) {
                        displayBaseEarnings = 0;
                    }
                    if (!isPrice(baseEarnings)) {
                        alert("年化收益格式不正确!");
                        return false;
                    } else if (!isPrice(displayBaseEarnings)) {
                        alert("基础年化收益格式不正确!");
                        return false;
                    }
                    var displayExtraEarnings = parseFloat(baseEarnings) - parseFloat(displayBaseEarnings);
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
            $("#investType").prop("value", ${product.investType});
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
</script>
<body>
<%-- <jsp:include page="top.jsp" /> --%>
<div align="center">
    <h1 style="text-align: center;">发布新手产品</h1>
    <img alt="新手专享" src="${pageContext.request.contextPath}/Product/images/gold.png">
    <font color="red" size="3px"><br/>新手标标名规则：新手专享1/2 号No.001</font>
    <form action="${pageContext.request.contextPath}/Product/Admin/releaseFreshmanProduct!releaseProduct.action"
          id="frm" method="post" enctype="multipart/form-data" onsubmit="return isSubmit()" target="_blank">
        <table border="1" style="width: 850px;">
            <tr>
                <td colspan="2" align="center">
                    以下带 <span style="color: red;">*</span> 为必填选项
                </td>
            </tr>
            <tr>
                <td class="rightText" style="width:20%"><span style="color: red;">*</span>产品名称：</td>
                <td class="leftText" style="width:80%">
                    <input id="title" name="product.title" style="width: 500px" value="新华新手No."><br>
                    目前已发布 <font color="red">${productCount}</font> 个新手专享产品（含预约中的产品）<b id="msg" style="color: red"></b>
                </td>
            </tr>
            <tr>
                <td class="rightText"><span style="color: red;">*</span>是否预约排队</td>
                <td class="leftText" id="isYY">
                    <label><input type="radio" name="product.productStatus"  value="0">否</label>
                    <label><input type="radio" name="product.productStatus" value="-3" checked="checked">是</label>
                </td>
            </tr>
            <tr>
                <td class="rightText" style="width:20%"><span style="color: red;">*</span>借款企业：</td>
                <td class="leftText" style="width:80%">
                    <input id="realName" name="product.realName" style="width: 200px" value="">
                </td>
            </tr>
            <tr>
                <td class="rightText" style="width:20%"><span style="color: red;">*</span>证件号：</td>
                <td class="leftText" style="width:80%">
                    <input id="idcard" name="product.idcard" style="width: 200px" value="">
                </td>
            </tr>
            <tr>
                <td class="rightText" style="width:20%"><span style="color: red;">*</span>发起人联系电话：</td>
                <td class="leftText" style="width:80%">
                    <input id="phone" name="product.phone" style="width: 200px" value="13588242687">
                </td>
            </tr>
            <tr>
                <td class="rightText" style="width:20%"><span style="color: red;">*</span>发起人联系地址：</td>
                <td class="leftText" style="width:80%">
                    <input id="address" name="product.address" style="width: 500px" value="">
                </td>
            </tr>
            <tr>
                <td class="rightText"><span style="color: red;">*</span>投资类别：</td>
                <td class="leftText"><input type="hidden" name="product.isRecommend" value="0">
                    <select id="investType" name="product.investType">
                        <option value="0"  >车无忧</option>
                         <option value="5" selected="selected">货押宝</option>
                    </select>
                    <span style="color: red;">*新手标投资类别为：货押宝</span>
                </td>
            </tr>
            <tr>
                <td class="rightText"><span style="color: red;">*</span>项目总额：</td>
                <td class="leftText"><input id="financingAmount" name="product.financingAmount" value="100000">(元)</td>
            </tr>
            <tr>
                <td class="rightText"><span style="color: red;">*</span>起投金额：</td>
                <td class="leftText"><input id="atleastMoney"  value="1000" readonly="readonly"
                                            name="product.atleastMoney">(元)
                </td>
            </tr>
            <tr>
                <td class="rightText"><span style="color: red;">*</span>总年化收益：</td>
                <td class="leftText">
                    <input type="hidden" id="baseEarnings" name="product.baseEarnings">
                    <input type="hidden" id="jiangLiEarnings" name="product.jiangLiEarnings" value="0">
                    <input id="annualEarnings" value="15" name="product.annualEarnings" style="width: 50px">%
                </td>
            </tr>
            <tr>
                <td class="rightText"><span style="color: red;">*</span>基础年化收益：</td>
                <td class="leftText"><input id="displayBaseEarnings" value="10" onchange="" name="product.displayBaseEarnings" style="width: 50px" aria-colspan="3">%</td>
            </tr>
            <tr>
                <td class="rightText"><span style="color: red;">*</span>额外年化收益：</td>
                <td class="leftText"><input id="displayExtraEarnings" value="5" name="product.displayExtraEarnings" style="width: 50px">%</td>
            </tr>
            <tr>
                <td class="rightText"><span style="color: red;">*</span>还款方式：</td>
                <td class="leftText">
                    <select name="product.payInterestWay">
                        <% //<option selected="selected" value="0">按月付息到期还本</option> %>
                        <option value="1" selected="selected">到期还本付息</option>
                        <option value="3">按年付息到期还本</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="rightText"><span style="color: red;">*</span>起息日：</td>
                <td class="leftText">
                    <select name="product.calcInterestWay" id="calcInterestWay" onchange="setLcqx(this.value)">
                        <% //<option  value="0">T + 0</option> %>
                        <option selected="selected" value="1">T + 1</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="rightText"><span style="color: red;">*</span>自动进入<br>【已售罄】<br>的时间：</td>
                <td class="leftText">
                    <input id="finishTime" name="finishTime"
                           onClick="WdatePicker({minDate:'%y-%M-#{%d+1}',startDate:'%y-%M-%d',dateFmt:'yyyy-MM-dd'})"
                           onchange="setNoBuyDate(this)" readonly="readonly" style="width:180px;text-align: center;">
                    <input id="endTime" type="hidden" name="endTime" value=""><br>当前时间如果达到此时间,则该新手产品自动进入【已售罄】状态;
                </td>
            </tr>
            <!-- <tr >
                <td style="text-align: center;color: red;" class="rightText" colspan="2">截至<input value="3" id="dateNum" readonly="readonly" style="width:30px;text-align: center;"/>天前不能购买;即【<span id="noBuyTime">2014-01-05</span>】当天,用户不能购买;</td>
            </tr> -->
            <tr>
                <td class="rightText"><span style="color: red;">*</span>理财期限：</td>
                <td class="leftText"><input id="lcqx" style="width: 50px" value="15" name="product.lcqx">天</td>
            </tr>
            <tr>
                <td class="rightText" style="width:20%">活动标语：</td>
                <td class="leftText" style="width:80%">
                    <input id="hdby" name="product.hdby" style="width: 500px"><br>
                </td>
            </tr>
            <tr>
                <td class="rightText" style="width:20%">手机活动链接：</td>
                <td class="leftText" style="width:80%">
                    <input id="hdlj" name="product.hdlj" style="width: 500px"><br>
                </td>
            </tr>
            <tr>
                <td class="rightText" style="width:20%">网站活动链接：</td>
                <td class="leftText" style="width:80%">
                    <input id="hdljWeb" name="product.hdljWeb" style="width: 500px"><br>
                </td>
            </tr>
            <tr>
                <td class="rightText" style="width:20%">产品标签：</td>
                <td class="leftText" style="width:80%">
                    <div id="tag">
                        <input type="hidden" id="subform" name="product.label" value=""/>
                        <div id="box">
                        </div>
                        <input type="text" class="taginput" maxlength="4" onblur="addThis(this)"/><br><font color="red">*每个标签最多填不超过四个字;</font>
                    </div>
                </td>
            </tr>
            <tr>
	            <td class="rightText" style="width:20%"><span style="color: red;">*</span>借款用途：</td>
	            <td class="leftText" style="width:80%">
	            	<input id="jkyt" name="product.jkyt" style="width: 500px" value=""><br>
	            </td>
            </tr>
            <tr id = "jk">
                <td class="rightText" style="text-align: right;vertical-align: top;">借款人信息：</td>
                <td class="leftText">
                    <input type="hidden" id="hidJkrxx" name="product.jkrxx">
                    <div id="Jkrxx" class="divEdit" contenteditable="true" style="height: 200px"></div>
                </td>
            </tr>
            <tr id = "hk" style="text-align: right;vertical-align: top;">
                <td class="rightText" style="text-align: right;vertical-align: top;">还款来源：</td>
                <td class="leftText">
                    <input type="hidden" id="hidHkly" name="product.hkly">
                    <div id="hkly" class="divEdit" contenteditable="true" style="height: 200px"></div>
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
            <tr id="xmImg">
                <td class="rightText" style="text-align: right;vertical-align: top;">项目流程图：<br/>(货押宝填写)</td>
                <td class="leftText">
                    <input type="hidden" name="product.proImg" value="" id="proImg">
                    <div id="dropz2" class="dropzone" name="file"></div>
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
            <tr id = "xmsx">
                <td class="rightText" style="text-align: right;vertical-align: top;">项目图片：</td>
                <td class="leftText">
                    <input type="hidden" name="product.infoImg" value="" id="infoImg">
                    <div id="dropz" class="dropzone" name="file"></div>
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
            <tr id = "fk" >
	            <td class="rightText" style="text-align: right;vertical-align: top;">风控措施：</td>
	            <td class="leftText">
		            <input type="hidden" id="hidRiskSteps" name="product.riskSteps">
		            <div id="riskSteps" class="divEdit" contenteditable="true" style="height:200px;"></div>
	            </td>
            </tr>
            <tr id = "fx">
                <td class="rightText" style="text-align: right;vertical-align: top;">风险提示：</td>
                <td class="leftText">
                    <input type="hidden" id="hidRiskHints" name="product.riskHints">
                    <div id="riskHints" class="divEdit" contenteditable="true" style="height:200px;"></div>
                </td>
            </tr>
            <tr>
                <td colspan="2" align="center">
                    <input type="submit" id="btnSubmit"
                           style="display:none;text-align: center;width:200px; height: 50px; font-size: 24px" value="发布"
                           onclick="sub()">
                    <input type="button" id="btn" style="text-align: center;width:200px; height: 50px; font-size: 24px"
                           value="预览" onclick="preview()">
                </td>
            </tr>
        </table>
    </form>
</div>
<form action="${pageContext.request.contextPath}/Product/Admin/releaseFreshmanProduct!test.action"
      style="display:none;">
    <input id="endTime2" name="st" onClick="WdatePicker()" readonly="readonly" style="width:180px;text-align: center;">
    <div id="description2" class="divEdit" contenteditable="true"></div>
    <input type="hidden" id="hidDescr" name="product.description">
    <input type="submit" style="text-align: center;width:200px; height: 50px; font-size: 24px" value="发布"
           onClick="javascript:Submit();">
</form>
</body>
<script type="text/javascript">
    if ("${isOk}" == "ok") {
        alert("发布【新手产品】成功!");
        window.location.href = "${pageContext.request.contextPath}/Product/Admin/releaseFreshmanProduct!sendProduct.action";
    } else if ("${isOk}" == "no") {
        alert("发布【新手产品】失败!");
        window.location.href = "${pageContext.request.contextPath}/Product/Admin/releaseFreshmanProduct!sendProduct.action";
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
        url: "${pageContext.request.contextPath}/Product/Admin/releaseProduct!uploadProImage.action",
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
                    $("#proImg").val(fileNamesLaw);
                } else {
                    alert("上传图片失败");
                }
            });
            this.on("removedfile", function (file) {
                var delUrl = "${pageContext.request.contextPath}/Product/Admin/releaseProduct!removeLawImage.action";
                var removeId = file.serverId;
                fileNamesLaw = fileNamesLaw.replace(removeId, "");
                $("#proImg").val(fileNamesLaw);
                $.post(delUrl, "removeId=" + removeId, function (data) {
                    if ("ok" != data.status)
                        alert(data.json);//失败的结果
                });
                console.log("File " + file.name + "removed");

            });
        }
    });
</script>

</html>