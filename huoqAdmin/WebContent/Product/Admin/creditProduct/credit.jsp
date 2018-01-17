<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2017/12/27
  Time: 15:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" deferredSyntaxAllowedAsLiteral="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/Product/images/favicon.ico" type="image/x-icon">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css" />
    <link href="${pageContext.request.contextPath}/Product/Admin/css/product_fabu.css" rel="stylesheet" type="text/css" />
    <link href="${pageContext.request.contextPath}/Product/Admin/css/product_fabu_history.css" rel="stylesheet" type="text/css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/js/artDialog4.1.7/skins/default.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Product/css/credit.css" rel="stylesheet" type="text/css" />
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
    <title>新华金典理财-发布信用贷标</title>
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

        .tabs{
            float:left;
        }
        #frm p.tip{
            margin:0;
            padding:0;
            float:right;
            width:150px;
            font-weight:300;
        }

        .tab{
            width:110px;
            text-align:center;
            float:left;
            border-right:1px solid #ccc;
        }
        .active{
            background:#dedede;
        }
        /*彪*/
    </style>
</head>
<body>
<div class="center" style="min-height: 1000px;">
    <jsp:include page="/Product/Admin/common/head.jsp" />
    <div class="center" style="border:none;">
    <div class="main">
        <div style="float:left;margin-left: 20px;">
            <p>正在营销中的产品</p>
            <table style="width: 250px;table-layout: fixed;">
                <tr>
                    <td  style="width:60%">产品名称</td>
                    <td  style="width:40%">剩余金额(元)</td>
                </tr>
                <c:forEach items="${listZero}" var="list">
                    <tr>
                        <td style="width:60%">${list.title}</td>
                        <td style="width:40%">${list.leftCopies}</td>
                    </tr>
                </c:forEach>
            </table>
            <br/><button  id="autoReleaseProductOperate" style="margin-left: 30px;">启动发布预约产品线程</button>
        </div>

        <div style="float: right;margin-right: 20px;">
            <p> 正在预约的产品</p>
            <table style="width: 300px;table-layout: fixed;">
                <tr>
                    <td style="width:8%">序号</td>
                    <td  style="width:46%">产品名称</td>
                    <td  style="width:46%">预约时间</td>
                </tr>
                <c:forEach items="${listYuYue}" var="list" varStatus="index">
                    <tr>
                        <td>${index.count}</td>
                        <td >${list.title}</td>
                        <td ><fmt:formatDate value="${list.bookingTime}" pattern="yyyy-MM-dd HH:mm:ss" /> </td>
                    </tr>
                </c:forEach>
            </table>
        </div>
        <h1 style="text-align: center;">发布预约产品</h1>
        <h3 style="text-align: center;">正在使用的【预约关键字】：${yyKeyword}</h3>

        <form id="frm" action="${pageContext.request.contextPath}/Product/Admin/creditProduct!creditYYProduct.action?flag=yy"
              method="post" enctype="multipart/form-data" onsubmit="return isSubmit()">
            <input id="userIds" name="userIds" type="hidden" value=""/>
            <table border="1" style="width: 850px;" class="loan-table">
                <tr>
                    <td colspan="2" align="center">
                        以下带 <span style="color: red;">*</span> 为必填选项
                    </td>
                </tr>
                <tr>
                    <td><span class="red">*</span>投资类别</td>
                    <td>
                        <select name="product.investType" id="investType">
                            <option>请选择投资类型</option>
                            <option value="7">信用贷</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td><span class="red">*</span>是否新手产品</td>
                    <td>
                        <label class="mr25">
                            <input type="radio" name="product.productType" checked="checked" value="0">否
                        </label>
                        <label>
                            <input type="radio" name="product.productType" value="1">是
                        </label>
                    </td>
                </tr>
                <tr>
                    <td class="rightText" style="width:20%"><span class="red">*</span>产品名称：</td>
                    <td class="leftText" style="width:80%">
                        <input id="title" name="product.title" style="width: 200px" value="新华4号信用贷No.">
                        目前已发布 <span class="red">${productCount}</span> 个产品<b id="msg"  class="red"></b>
                    </td>
                </tr>
                <tr>
                    <td><span class="red">*</span>产品借款人</td>
                    <td><span>总借款人：</span><span>${count}人</span>
                        <span>总借款金额：</span><span>${amount}万</span>
                        <select name="borrower.dtCreate" id="modal">
                            <option value="">请选择借款日期</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>
                        <span class="red">*</span>年化收益率
                    </td>
                    <td>
                        <span>基础年化收益率 </span>
                        <input type="text" id="displayBaseEarnings" onchange="" name="product.displayBaseEarnings"  style="width:100px;" value="8" readonly>%
                        <span> + </span>
                        <span>贴加年化收益率</span>
                        <input type="text" id="displayExtraEarnings" name="product.displayExtraEarnings"  style="width:100px;" value="4.2" readonly>%
                        <span>=</span>
                        <input id="baseEarnings" name="product.baseEarnings"  style="width: 50px" value="12.2">%
                    </td>
                </tr>
                <tr>
                    <td><span class="red">*</span>起息日</td>
                    <td>
                        <select name="product.calcInterestWay" id="calcInterestWay" onchange="setLcqx(this.value)">
                            <option selected="selected" value="1">T + 1</option>
                        </select>
                        <input id="finishTime" name="finishTime" type="hidden" value="" />
                        <input id="endTime" type="hidden" name="endTime" value="" />
                    </td>
                </tr>
                <tr>
                    <td><span class="red">*</span>理财期限</td>
                    <td>
                        <input type="text" name="product.lcqx" id="lcqx" class="mr10">天
                    </td>
                </tr>
                <tr>
                    <td><span class="red">*</span>起投金额</td>
                    <td>
                        <input type="text" name="product.atleastMoney" id="atleastMoney" class="mr10" value="100">元
                    </td>
                </tr>
                <tr>
                    <td><span class="red">*</span>项目风险</td>
                    <td>
                        <label class="mr25">
                            <input type="radio" name="product.risk" value="A" >A
                        </label>
                        <label class="mr25">
                            <input type="radio" name="product.risk" value="B" >B
                        </label>
                        <label class="mr25">
                            <input type="radio" name="product.risk" value="C" >C
                        </label>
                        <label>
                            <input type="radio" name="product.risk" value="D">D
                        </label>
                    </td>
                </tr>
                <tr>
                    <td><span class="red">*</span>推荐产品</td>
                    <td>
                        <label style="margin-right: 41px;">
                            <input type="radio" name="product.isRecommend"  value="0">否
                        </label>
                        <label >
                            <input type="radio" name="product.isRecommend"  value="1" checked="checked">是
                        </label>
                    </td>
                </tr>

                <tr>
                    <td class="rightText"><span class="red">*</span>预约发布时间</td>
                    <td class="leftText">
                        <input id="bookingTime" name="product.bookingTime"
                               onClick="WdatePicker({minDate:'%y-%M-#{%d}',startDate:'%y-%M-%d',dateFmt:'yyyy-MM-dd HH:mm'})"
                               onchange="setNoBuyDate(this)" readonly="readonly" style="width:180px;padding-left: 10px;" placeholder="选择日期时间">
                    </td>
                </tr>
            </table>
            <div id="appointment-pb">
                <input type="submit" value="预约发布" class="publish">
            </div>
        </form>
    </div>
</div>
</div>
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
</script>
<script type="text/javascript">
    $("#autoReleaseProductOperate").click(function(){
        var url="${pageContext.request.contextPath}/Product/Admin/startThread!autoReleaseProductOperate.action";
        $.post(url,null,function(data){
            alert(data.json);//结果!
            window.location.reload();
        });
    });
    var isShow = false;
    function isSubmit() {
        var investType = $("#investType option:selected").val();
        var productTypeNew = document.getElementsByName("product.productType");
        for(var i=0;i<productTypeNew.length;i++){
            if(productTypeNew[i].checked){
                if(i==0 && title.indexOf("新手")!=-1){
                    if(!confirm("检测到你发布的产品可能是【新手产品】,但是并没有选中【新手】选项,是否继续发布该产品?")){
                        return false;
                    }
                } if(i==1 && title.indexOf("新手")==-1){
                    if(!confirm("检测到你发布的产品可能是【常规产品】,但是并没有选中【常规】选项,是否继续发布该产品?")){
                        return false;
                    }
                }
            }
        }
        var title = $("#title").val();
        var atleastMoney = $("#atleastMoney").val();
        var gradeReg=/^[0-9]*$/;
        var proNo=$('#title').val().slice(10);
        var baseEarnings = $("#baseEarnings").val();//年利率
        var displayBaseEarnings = $("#displayBaseEarnings").val();//基础利率
        var displayExtraEarnings = $("#displayExtraEarnings").val();
        var earning = parseFloat(displayBaseEarnings) + parseFloat(displayExtraEarnings);

        console.log(displayBaseEarnings);
        console.log(baseEarnings);
        console.log(displayExtraEarnings);
        console.log(earning);

        if(proNo==''){
            alert("请输入产品标号");
            return false;
        }else if(!gradeReg.test(proNo)){
            alert("产品标号必须为数字");
            return false;
        }else if (!isOnlyNumber(atleastMoney)) {
            alert("起投金额格式不正确!");
            return false;
        } else if (parseInt(atleastMoney) % 50 != 0) {
            alert("起投金额必须为【50】的整数倍!");
            return false;
        }


        if(!confirm("确定要发布预约产品?")){
            return false;
        }
        isShow = true;
        return true;
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

    if ("${isOk}" == "ok") {
        alert("发布成功!");
        window.location.href = "${pageContext.request.contextPath}/Product/Admin/creditProduct!creditYYProduct.action?flag=yy";
    } else if ("${isOk}" == "no") {
        alert("发布失败!");

    }
</script>
<script>
    $('#modal').on('click',function(){
        window.open("${pageContext.request.contextPath}/Product/Admin/creditProduct/bowwerInfo.jsp","_blank","height=500,width=700,left=500,top=300");

    })

</script>
</html>
