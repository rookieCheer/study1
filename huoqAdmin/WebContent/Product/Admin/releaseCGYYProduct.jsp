<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" deferredSyntaxAllowedAsLiteral="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
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
    <!--   <link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css" />
	<link href="${pageContext.request.contextPath}/Product/Admin/css/wecome.css" rel="stylesheet" type="text/css" />-->
    <script charset="utf-8" type="text/javascript"
            src="${pageContext.request.contextPath}/Product/Admin/kindeditor/kindeditor.js"></script>
    <script charset="utf-8" type="text/javascript"
            src="${pageContext.request.contextPath}/Product/Admin/kindeditor/lang/zh_CN.js"></script>
    <title>新华金典理财-发布预约产品</title>
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

        /*彪*/
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

        .tabs {
            float: left;
        }

        #frm p.tip {
            margin: 0;
            padding: 0;
            float: right;
            width: 150px;
            font-weight: 300;
        }

        .tab {
            width: 130px;
            text-align: center;
            float: left;
            border-right: 1px solid #ccc;
        }

        .active {
            background: #dedede;
        }

        /*彪*/
    </style>
</head>
<!--彪 -->

<body>
<%-- <jsp:include page="top.jsp" /> --%>
<div style="float: left;margin-left: 20px;">
    <p>正在营销中的产品</p>
    <table style="width: 250px;table-layout: fixed;">
        <tr>
            <td style="width:60%">产品名称</td>
            <td style="width:40%">剩余金额(元)</td>
        </tr>
        <c:forEach items="${listZero}" var="list">
            <tr>
                <td style="width:60%">${list.title}</td>
                <td style="width:40%">${list.leftCopies}</td>
            </tr>
        </c:forEach>
    </table>
    <br/>
    <button id="autoReleaseProductOperate" style="margin-left: 30px;">启动发布预约产品线程</button>
</div>

<div style="float: right;margin-right: 20px;">
    <p> 正在预约的产品</p>
    <table style="width: 300px;table-layout: fixed;">
        <tr>
            <td style="width:8%">序号</td>
            <td style="width:46%">产品名称</td>
            <td style="width:46%">插入时间</td>
        </tr>
        <c:forEach items="${listYuYue}" var="list" varStatus="index">
            <tr>
                <td>${index.count}</td>
                <td>${list.title}</td>
                <td><fmt:formatDate value="${list.bookingTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            </tr>
        </c:forEach>
    </table>
</div>
<div align="center">
    <div class="main">
        <h1 style="text-align: center;">发布预约产品</h1>
        <h3 style="text-align: center;">正在使用的【预约关键字】：${yyKeyword}</h3>

        <form id="frm"
              action="${pageContext.request.contextPath}/Product/Admin/releaseProduct!releaseYYProduct.action?flag=yy"
              method="post" enctype="multipart/form-data" onsubmit="return isSubmit()">
            <table border="1" style="width: 850px;">
                <tr>
                    <td colspan="2" align="center">
                        <ul class="tabs clearfix" style="width:690px;">
                            <li class="active tab" id="tab1"><a href="javascript:;">常规标1(月标)</a></li>
                            <li class="tab" id="tab2"><a href="javascript:;">常规标2(季标)</a></li>
                            <li class="tab" id="tab3"><a href="javascript:;">常规标3(2号双月标)</a></li>
                            <li class="tab" id="tab4"><a href="javascript:;">常规标4(3号双月标)</a></li>
                        </ul>
                        <p class="tip"> 以下带 <span style="color: red;">*</span> 为必填选项</p>
                    </td>
                </tr>
                <tr>
                    <td class="rightText" id="pro" style="width:20%"><span style="color: red;">*</span>产品名称：</td>
                    <td class="leftText" style="width:80%">
                        <input id="title" name="product.title" style="width: 500px" value="新华2号月标No."><br>
                        目前已发布 <font color="red">${productCount}</font> 个产品(含新手专享产品和预约中的产品)<b id="msg"
                                                                                             style="color: red"></b>
                        <font color="red" size="1px"><br/>新手标标名规则：新手专享1/2 号No.001</font>
                    </td>
                </tr>
                <tr>
                    <td class="rightText"><span style="color: red;">*</span>是否新手产品：</td>
                    <td class="leftText">
                        <label><input type="radio" name="product.productType" checked="checked" value="0">否</label>
                        <label><input type="radio" name="product.productType" value="1">是</label>
                    </td>
                </tr>
                <tr>
                    <td class="rightText" style="width:20%"><span style="color: red;">*</span>发起企业：</td>
                    <td class="leftText" style="width:80%">
                        <input id="realName" name="product.realName" style="width: 200px" value="${pro.realName}">
                    </td>
                </tr>
                <tr>
                    <td class="rightText" style="width:20%"><span style="color: red;">*</span>证件号：</td>
                    <td class="leftText" style="width:80%">
                        <input id="idcard" name="product.idcard" style="width: 200px" value="${pro.idcard}">
                    </td>
                </tr>
                <tr>
                    <td class="rightText" style="width:20%"><span style="color: red;">*</span>发起人联系电话：</td>
                    <td class="leftText" style="width:80%">
                        <input id="phone" name="product.phone" style="width: 200px" value="${pro.phone}">
                    </td>
                </tr>
                <tr>
                    <td class="rightText" style="width:20%"><span style="color: red;">*</span>发起人联系地址：</td>
                    <td class="leftText" style="width:80%">
                        <input id="address" name="product.address" style="width: 500px" value="${pro.address}">
                    </td>
                </tr>
                <tr>
                    <td class="rightText"><span style="color: red;">*</span>投资类别：</td>
                    <td class="leftText">
                        <select id="investType" name="product.investType">
                            <option value="0">车无忧</option>
                            <option value="5" selected="selected">货押宝</option>
                        </select>
                        <span style="color: red;">*新手标是货押宝投资类别</span>
                    </td>
                </tr>
                <tr>
                    <td class="rightText"><span style="color: red;">*</span>常规产品时间类型：</td>
                    <td class="leftText" onchange="updatehdby()">
                        <label><input type="radio" name="product.qxType" id="type1" value="0">周标</label>
                        <label><input type="radio" name="product.qxType" id="type2" checked="checked" value="1">月标</label>
                        <label><input type="radio" name="product.qxType" id="type3" value="2">单季标</label>
                        <label><input type="radio" name="product.qxType" id="type4" value="3">双季标</label>
                        <label><input type="radio" name="product.qxType" id="type5" value="4">新手标</label>
                        <label><input type="radio" name="product.qxType" id="type6" value="5">半季标</label>
                        <label><input type="radio" name="product.qxType" id="type7" value="6">双月标</label>
                        <span style="color: red;">*时间类型要与理财期限相对应</span>
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
                    <td class="leftText"><input id="financingAmount" name="product.financingAmount" value="200000">(元)
                    </td>
                </tr>
                <tr>
                    <td class="rightText"><span style="color: red;">*</span>起投金额：</td>
                    <td class="leftText"><input id="atleastMoney" name="product.atleastMoney" value="1000">(元)</td>
                </tr>
                <tr>
                    <td class="rightText"><span style="color: red;">*</span>年化收益：</td>
                    <td class="leftText"><input id="baseEarnings" name="product.baseEarnings" value="8"
                                                style="width: 50px">%
                    </td>
                </tr>
                <tr>
                    <td class="rightText"><span style="color: red;">*</span>基础年化收益：</td>
                    <td class="leftText"><input id="displayBaseEarnings" onchange="" name="product.displayBaseEarnings"
                                                value="5" style="width: 50px" aria-colspan="3">%
                    </td>
                </tr>
                <tr>
                    <td class="rightText"><span style="color: red;">*</span>额外年化收益：</td>
                    <td class="leftText"><input id="displayExtraEarnings" name="product.displayExtraEarnings" value="3"
                                                readonly="readonly" style="width: 50px">%
                    </td>
                </tr>
                <tr>
                    <td class="rightText"><span style="color: red;">*</span>奖励收益：</td>
                    <td class="leftText">
                        <input id="jiangLiEarnings" readonly="readonly" name="product.jiangLiEarnings"
                               style="width: 50px">%
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        <input type="hidden" id="annualEarnings" name="product.annualEarnings">
                        总收益：<span id="span_earnings">0</span>%
                    </td>
                </tr>
                <tr>
                    <td class="rightText"><span style="color: red;">*</span>还款方式：</td>
                    <td class="leftText">
                        <select name="product.payInterestWay">
                            <!-- <option  value="0">按月付息到期还本</option> -->
                            <option selected="selected" value="1">到期还本付息</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td class="rightText"><span style="color: red;">*</span>起息日：</td>
                    <td class="leftText">
                        <select name="product.calcInterestWay" id="calcInterestWay" onchange="setLcqx(this.value)">
                            <!--  <option value="0">T + 0</option> -->
                            <option selected="selected" value="1">T + 1</option>
                        </select>
                        <input id="finishTime" name="finishTime" type="hidden" value=""/>
                        <input id="endTime" type="hidden" name="endTime" value=""/>
                    </td>
                </tr>
                <tr>
                    <td class="rightText"><span style="color: red;">*</span>理财期限：</td>
                    <td class="leftText"><input id="lcqx" style="width: 50px" value="28" name="product.lcqx">天<b
                            id="lcqxmsg"
                            style="color: red"></b>
                        <span style="color: red">&nbsp;&nbsp;*截至3天前不能购买</span>
                    </td>
                </tr>
                <tr>
                    <td class="rightText" style="width:20%">活动标语：</td>
                    <td class="leftText" style="width:80%">
                        <input id="hdby" name="product.hdby" style="width: 500px" value="投资该产品100元，可获得1积分"><br>
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
                            <input type="text" class="taginput" maxlength="4" onblur="addThis(this)"/><br><font
                                color="red">*每个标签最多填不超过四个字;</font>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td class="rightText" style="width:20%"><span style="color: red;">*</span>借款用途：</td>
                    <td class="leftText" style="width:80%">
                        <input id="jkyt" name="product.jkyt" style="width: 500px" value=""><br>
                    </td>
                </tr>
                <tr>
                    <td class="rightText" style="width:20%">标的活动名称：<br/>如：端午节活动</td>
                    <td class="leftText" style="width:80%">
                        <input id="activity" name="product.activity" style="width: 500px"><br>
                    </td>
                </tr>
                <tr>
                    <td class="rightText" style="width:20%">标的活动名称颜色;<br/>#号+6位颜色码;例如#24b599或咨询UI：</td>
                    <td class="leftText" style="width:80%">
                        <input id="activityColor" name="product.activityColor" style="width: 500px"><br>
                    </td>
                </tr>
                <tr id="jk_age">
                    <td class="rightText" style="width:20%"><span style="color: red;">*</span>年龄：</td>
                    <td class="leftText" style="width:80%">
                        <input id="age" name="product.age" style="width: 500px"><br>
                    </td>
                </tr>
                <tr id="jk_edu">
                    <td class="rightText" style="width:20%"><span style="color: red;">*</span>学历：</td>
                    <td class="leftText" style="width:80%">
                        <input id="educational" name="product.education" style="width: 500px"><br>
                    </td>
                </tr>
                <tr id="jk_merr">
                    <td class="rightText" style="width:20%"><span style="color: red;">*</span>婚姻：</td>
                    <td class="leftText" style="width:80%">
                        <input id="marriage" name="product.marriage" style="width: 500px"><br>
                    </td>
                </tr>
                <tr id="jk_hjaddr">
                    <td class="rightText" style="width:20%"><span style="color: red;">*</span>户籍地址：</td>
                    <td class="leftText" style="width:80%">
                        <input id="hjAddress" name="product.hjAddress" style="width: 500px"><br>
                    </td>
                </tr>
                <tr id="car_dingf">
                    <td class="rightText" style="width:20%"><span style="color: red;">*</span>车贷审查服务方：</td>
                    <td class="leftText" style="width:80%">
                        <input id="carDingf" name="product.carDingf" style="width: 500px"><br>
                    </td>
                </tr>
                <tr id="car_dingf_addr">
                    <td class="rightText" style="width:20%"><span style="color: red;">*</span>车贷审查服务方地址：</td>
                    <td class="leftText" style="width:80%">
                        <input id="carDingfAddress" name="product.carDingfAddress" style="width: 200px"><br>
                    </td>
                </tr>
                <tr id="jk">
                    <td class="rightText" style="text-align: right;vertical-align: top;">借款人信息：</td>
                    <td class="leftText">
                        <input type="hidden" id="hidJkrxx" name="product.jkrxx">
                        <div id="Jkrxx" class="divEdit" contenteditable="true" style="width:650px;height:200px;"></div>
                    </td>
                </tr>
                <tr id="hk">
                    <td class="rightText" style="text-align: right;vertical-align: top;">还款来源：</td>
                    <td class="leftText">
                        <input type="hidden" id="hidHkly" name="product.hkly">
                        <div id="hkly" class="divEdit" contenteditable="true" style="width:650px;height:200px;"></div>
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
                <tr id="xmsx">
                    <td class="rightText" style="text-align: right;vertical-align: top;">项目图片：(相关资料)</td>
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
                <tr id="fk">
                    <td class="rightText" style="text-align: right;vertical-align: top;">风控措施：<br/>(车贷宝填写)</td>
                    <td class="leftText">
                        <input type="hidden" id="hidRiskSteps" name="product.riskSteps">
                        <div id="riskSteps" class="divEdit" contenteditable="true"
                             style="width:650px;height:250px;"></div>
                    </td>
                </tr>
                <tr id="fx">
                    <td class="rightText" style="text-align: right;vertical-align: top;">风险提示：<br/>(车贷宝填写)</td>
                    <td class="leftText">
                        <input type="hidden" id="hidRiskHints" name="product.riskHints">
                        <div id="riskHints" class="divEdit" contenteditable="true"
                             style="width:650px;height:200px;"></div>
                    </td>
                </tr>
                <tr>
                    <td colspan="2" align="center">
                        <input type="submit" id="btnSubmit"
                               style="text-align: center;width:200px; height: 50px; font-size: 24px"
                               value="预约发布">
                        <input type="button" id="btn"
                               style="display:none;text-align: center;width:200px; height: 50px; font-size: 24px"
                               value="预览"
                               onclick="preview()">
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
    <input type="submit" style="text-align: center;width:200px; height: 50px; font-size: 24px" value="发布"
           onClick="javascript:Submit();">
</form>
</body>
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
        editor.html('新华2号是新华金典平台推出的一款为核心企业量身打造的供应链金融理财产品，具有低风险、高收益、高流动性的特点。借款人以小微企业为主，借款资金主要用于帮助企业解决融资，提高资金利用率，扩大企业经营资产规模等需求。新华金典通过与大数据机构、核心企业的合作，严格筛选资金需求合理，经营利润率较高的借款企业，同时严格遵循小额分散的原则，对借款企业的资产规模和负债结构进行严格审核，最大程度降低项目风险。<br />')
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
        editor5 = K.create('#productStyle', options);
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
        editor6 = K.create('#riskSteps', options);
        editor6.html('1、平台严格审核借款人资质，并聘请专业人员评估企业运行情况，明确资金用途，确保借款人还款来源稳定。<br />' +
            '2、核心企业承诺回购债权。<br />' +
            '3、投资人资金与平台隔离，保证投资人资金安全。<br />' +
            '4、借款人资料真实有效，借款申请经过平台风控团队的严格审核。<br />')
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
    var editor8;
    KindEditor.ready(function (K) {
        var options = {
            filterMode: true,
            themeType: 'default',
            uploadJson: '${pageContext.request.contextPath}/Product/kindEditor!keuploadproduct.action',
            fileManagerJson: '<%=request.getContextPath()%>/keupload/manager',
            allowFileManager: false,
            resizeType: 1
        };
        editor8 = K.create('#riskHints', options);
    });
    var isShow = false;
/*    function bendihuancun(){
        $('#frm input').each(function(){

            var strValue=$(this).val();
            if (window.localStorage) {
                window.localStorage.setItem(strKey, strValue);
            } else {
                Cookie.write(strKey, strValue);
            }

        })

    }*/


    function isSubmit() {

        var title = $("#title").val();
        /*var investType = $("#investType").find("option:selected").text();
         if (title.indexOf(investType) < 0) {
            if (!confirm("项目名称与所选类型不一致,是否继续发布")) {
                return false;
            }
        } */
        var productTypeNew = document.getElementsByName("product.productType");
        for (var i = 0; i < productTypeNew.length; i++) {
            if (productTypeNew[i].checked) {
                if (i == 0 && title.indexOf("新手") != -1) {
                    if (!confirm("检测到你发布的产品可能是【新手产品】,但是并没有选中【新手】选项,是否继续发布该产品?")) {
                        return false;
                    }
                }
                if (i == 1 && title.indexOf("新手") == -1) {
                    if (!confirm("检测到你发布的产品可能是【常规产品】,但是并没有选中【常规】选项,是否继续发布该产品?")) {
                        return false;
                    }
                }
            }
        }
        $("#hidDescr1").val(editor.html());
        $("#hidHkly").val(editor2.html());
        $("#hidZjbz").val(editor3.html());
        $("#hidCplxjs").val(editor4.html());
        $("#hidProductStyle").val(editor5.html());
        $("#hidRiskSteps").val(editor6.html());
        $("#hidJkrxx").val(editor7.html());
        $("#hidRiskHints").val(editor8.html());
        //alert("div: "+$("#description").html());
        //alert("editor: "+editor.html());
        var atleastMoney = $("#atleastMoney").val();
        var financingAmount = $("#financingAmount").val();
        var isJiangLi = $('input[name="product.isJiangLi"]:checked').val();
        var reg = /^[\u4E00-\u9FA5]+$/;
        var idcardReg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
        var gradeReg = /^[0-9]*$/;
        var proNo = $('#title').val().slice(10);
        var baseEarnings = $("#baseEarnings").val();//年利率
        var displayBaseEarnings = $("#displayBaseEarnings").val();//基础利率
        var displayExtraEarnings = $("#displayExtraEarnings").val();
        var earning = parseFloat(displayBaseEarnings) + parseFloat(displayExtraEarnings);
        console.log(displayBaseEarnings);
        console.log(baseEarnings);
        console.log(displayExtraEarnings);
        console.log(earning);
        // var gradeReg=/(^\d{3}$)/;
        if (proNo == '') {
            alert("请输入产品标号");
            return false;
        } else if (!gradeReg.test(proNo)) {
            alert("产品标号必须为数字");
            return false;
        } else if ($("#phone").val() == "") {
            alert("联系电话不正确!");
            return false;
        } else if ($("#idcard").val() == "") {
            alert("证件号不正确!");
            return false;
        } else if ($("#address").val() == null) {
            alert("联系地址不正确!");
            return false;
        } else if (!isOnlyNumber(financingAmount)) {
            alert("项目总额格式不正确!");
            return false;
        } else if (parseInt(financingAmount) % 100 != 0) {
            alert("项目总额必须为100的整数倍!");
            return false;
        } else if (!isOnlyNumber(atleastMoney)) {
            alert("起投金额格式不正确!");
            return false;
        } else if (parseInt(atleastMoney) % 50 != 0) {
            alert("起投金额必须为【50】的整数倍!");
            return false;
        } else if (parseInt(baseEarnings) != earning) {
            alert("总年化收益不正确");
            return false;
        } else if (!isPrice($("#baseEarnings").val())) {
            alert("基础年化收益格式不正确!");
            return false;
        } else if (isJiangLi == 1 && $("#jiangLiEarnings").val() == 0) {
            alert("奖励收益可填时，不能为空!");
            return false;
        } else if (parseInt(atleastMoney) > parseInt(financingAmount)) {
            alert("起投金额 【不能大于】项目总额!");
            return false;
        } else if ($("#jkyt").val() == "") {
            alert("【借款用途】不能为空");
            return false;
        }


        var investType = $("#investType option:selected").val();
        if (investType == 6) {
            if ($("#age").val() == "") {
                alert("借款人年龄不能为空!");
                return false;
            } else if ($("#educational").val() == "") {
                alert("借款人学历不能为空!");
                return false;
            } else if ($("#marriage").val() == "") {
                alert("借款人婚姻状况不能为空!");
                return false;
            } else if ($("#hjAddress").val() == "") {
                alert("借款人户籍地址不能为空!");
                return false;
            } else if ($("#carDingf").val() == "") {
                alert("车贷服务方不能为空!");
                return false;
            } else if ($("#carDingfAddress").val() == "") {
                alert("车贷服务方地址不能为空!");
                return false;
            }
        }
        if (!confirm("确定要发布预约产品?")) {
            return false;
        }
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
        var num = $("#dateNum").val() - 1;
        today.setDate(today.getDate() - num);
        //不准购买日期;
        var noBuyDate = dateFormat(today);
        //alert(noBuyDate);
        $("#noBuyTime").text(noBuyDate);
        $("#endTime").val(noBuyDate);
        var payWay = $("#calcInterestWay").val();
        var diffDayNum = DateDiff(dateFormat(new Date()), obj.value)
        if (payWay == "0") {
            //包含开始和结束时间;
            diffDayNum++;
        } else if (payWay == "1") {
            //不包含开始时间;
        }
        //alert(diffDayNum);
        $("#lcqx").val(diffDayNum);
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
        iDays = parseInt(Math.abs(oDate1 - oDate2) / 1000 / 60 / 60 / 24);    //把相差的毫秒数转换为天数
        return iDays;
    }

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
        $('#tab1').on('click', function () {
            $(this).addClass('active').siblings('.tab').removeClass('active');
            $('#title').val('新华2号月标No.');
            $('#realName').val('');
            $('#idcard').val('');
            $('#address').val('');
            $('#investType').val(5);
            $('#type2').attr('checked', true);
            $('#financingAmount').val('200000');
            $('#baseEarnings').val('8');
            $('#displayBaseEarnings').val('5');
            $('#lcqx').val('28');
            editor2.html('1、第一还款来源：借款企业经营收入。<br />' +
                '2、第二还款来源：核心企业回购资金。<br />')
            editor8.html('投资有风险 理财需谨慎' +
                '本产品不等同于银行存款，投资人应根据平台提供的项目信息及资料审慎思考、独立判断，并自行承担可能面临的投资风险，包括但不限于政策风险、延时风险、流动性风险、兑付风险和不可抗力风险等。<br />')
        })
        $('#tab2').on('click', function () {
            $(this).addClass('active').siblings('.tab').removeClass('active');
            $('#title').val("新华2号季标No.");
            $('#realName').val('');
            $('#idcard').val('');
            $('#address').val('');
            $('#investType').val(5);
            $('#type3').attr('checked', true);
            $('#financingAmount').val('200000');
            $('#baseEarnings').val('10');
            $('#displayBaseEarnings').val('7');
            $('#lcqx').val('90');
            editor2.html('1、第一还款来源：借款企业经营收入。<br />' +
                '2、第二还款来源：核心企业回购资金。<br />')
            editor8.html('投资有风险 理财需谨慎' +
                '本产品不等同于银行存款，投资人应根据平台提供的项目信息及资料审慎思考、独立判断，并自行承担可能面临的投资风险，包括但不限于政策风险、延时风险、流动性风险、兑付风险和不可抗力风险等。<br />')
        })
        $('#tab3').on('click', function () {
            $(this).addClass('active').siblings('.tab').removeClass('active');
            $('#title').val('新华2号双月标No.');
            $('#realName').val('');
            $('#idcard').val('');
            $('#address').val('');
            $('#investType').val(5);
            $('#type7').attr('checked', true);
            $('#financingAmount').val('200000');
            $('#baseEarnings').val('9');
            $('#displayBaseEarnings').val('6');
            $('#lcqx').val('58');
            editor2.html('1、第一还款来源：借款企业经营收入。<br />' +
                '2、第二还款来源：核心企业回购资金。<br />')
            editor8.html('投资有风险 理财需谨慎' +
                '本产品不等同于银行存款，投资人应根据平台提供的项目信息及资料审慎思考、独立判断，并自行承担可能面临的投资风险，包括但不限于政策风险、延时风险、流动性风险、兑付风险和不可抗力风险等。<br />')
        })
        $('#tab4').on('click', function () {
            $(this).addClass('active').siblings('.tab').removeClass('active');
            $('#title').val('新华3号双月标No.');
            $('#realName').val('中楚共融襄阳企业管理有限公司');
            $('#phone').val('13588242687');
            $('#idcard').val('914206003523396000');
            $('#address').val('襄阳市樊城区内环西路卧龙大道环球金融城2栋1单元18楼');
            $('#investType').val(0);
            $('#jkyt').val("实标");
            $('#type7').attr('checked', true);
            $('#financingAmount').val('137000');
            $('#baseEarnings').val('9');
            $('#displayBaseEarnings').val('6');
            $('#lcqx').val('58');
            editor2.html('第一还款来源为合作方代购车人向银行办理抵押贷款的放款。放款后，将借款本息偿还给投资者。 第二还款来源：合作方向投资者承诺回购债权，保障投资者本金和收益的安全性。<br />')
            editor.html('购车人支付首付款后，合作方受购车人委托在按揭贷款额度内向新华金典平台投资者借款，以支付购车尾款。同时合作方以车辆向银行抵押的贷款为还款来源，平台及合作方完整把控流程，资金流动闭环，风险可控。<br />')
            editor3.html('1、合作方与银行保持长期合作关系，抵押贷款业务成熟可靠。<br />' +
                '2、合作方严格审核购车人授信资质，挑选优质购车人进入银行抵押贷款申请流程。 <br />' +
                '3、购车人签署划款授权委托书，保证银行贷款直接用于偿付投资者本息。  <br />' +
                '4、合作方提供债权回购承诺函，保障投资者本息安全。 <br />' +
                '其他安全保障措施： <br />' +
                '1、新车风险低：贷款标的为新车，贬值风险小。 <br />' +
                '2、折扣率低：借款金额不超过新车车辆价格的70%。 <br />' +
                '3、GPS跟踪：抵押车辆均安装GPS，能够准确定位跟踪。 <br />' +
                '4、专业贷后服务：合作方提供违约后垫款、处置等一系列贷后服务承诺。 <br />');
            editor4.html('新华3号是新华金典平台推出的车贷理财项目，购车人与新华金典核准的合作方（汽车按揭服务公司）签订购车分期付款服务协议，委托其向银行办理分期付款购车。<br />');
        })



            $("#investType").prop("value", investType);
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

    //aaaaa
    if ("${isOk}" == "ok") {
        alert("发布成功!");
        window.location.href = "${pageContext.request.contextPath}/Product/Admin/releaseProduct!sendProduct.action?flag=yy";
    } else if ("${isOk}" == "no") {
        alert("发布失败!");
        window.location.href = "${pageContext.request.contextPath}/Product/Admin/releaseProduct!sendProduct.action?flag=yy";
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
        maxFilesize: 3,
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
    //项目流程图
    $("#dropz2").dropzone({
        url: "${pageContext.request.contextPath}/Product/Admin/releaseProduct!uploadProImage.action",
        addRemoveLinks: true,
        dictRemoveLinks: "x",
        dictCancelUpload: "x",
        maxFiles: maxFiles,
        maxFilesize: 3,
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
                var delUrl = "${pageContext.request.contextPath}/Product/Admin/releaseProduct!removeInfoImage.action";
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
    $("#autoReleaseProductOperate").click(function () {
        var url = "${pageContext.request.contextPath}/Product/Admin/startThread!autoReleaseProductOperate.action";
        $.post(url, null, function (data) {
            alert(data.json);//结果!
            window.location.reload();
        });
    });
</script>
<script type="text/javascript">
    function updatehdby() {
        var qxType = $('input:radio[name="product.qxType"]:checked').val();
        var hdby = "";
        if (4 == qxType) {
            hdby = "每人限购1次，多多购买吧！"
        } else if (0 == qxType) {
            hdby = "投资该产品100元，可获得1积分"
        } else if (1 == qxType) {
            hdby = "投资该产品100元，可获得4积分"
        } else if (2 == qxType) {
            hdby = "投资该产品100元，可获得16积分"
        } else if (3 == qxType) {
            hdby = "投资该产品100元，可获得35积分"
        } else if (5 == qxType) {
            hdby = "投资该产品100元，可获得7积分"
        } else if (6 == qxType) {
            hdby = "投资该产品100元，可获得10积分"
        }
        $("#hdby").val(hdby);

    }
</script>

</html>