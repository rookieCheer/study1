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
<%@ taglib uri="/WEB-INF/zlqEl.tld" prefix="myel" %>
<html>
<head>
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/Product/images/favicon.ico" type="image/x-icon">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css"/>
    <link href="${pageContext.request.contextPath}/Product/Admin/css/product_fabu.css" rel="stylesheet"
          type="text/css"/>
    <link href="${pageContext.request.contextPath}/Product/Admin/css/product_fabu_history.css" rel="stylesheet"
          type="text/css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/js/artDialog4.1.7/skins/default.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Product/css/credit.css" rel="stylesheet"
          type="text/css"/>
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

</head>
<body onload="init()">

<div class="model" id="deadline">
    <div class="m-top p30">
        <form id="frm" action="${pageContext.request.contextPath}/Product/Admin/creditProduct!borrowerProduct.action"
              method="GET">
            <select name="termLoan" id="termLoan" class="loan-deadline">
                <option value="" <c:if test="${termLoan eq '' }">selected</c:if>>请选择借款期限</option>
                <option value="15" <c:if test="${termLoan eq '15' }">selected</c:if>>15天</option>
                <option value="30" <c:if test="${termLoan eq '30' }">selected</c:if>> 30天</option>
                <option value="45" <c:if test="${termLoan eq '45' }">selected</c:if>>45天</option>
                <option value="90" <c:if test="${termLoan eq '90' }">selected</c:if>>3个月</option>
                <option value="180" <c:if test="${termLoan eq '180' }">selected</c:if>>6个月</option>
                <option value="270" <c:if test="${termLoan eq '270' }">selected</c:if>>9个月</option>
                <option value="360" <c:if test="${termLoan eq '360' }">selected</c:if>>12个月</option>
            </select>
            <input type="submit" value="确定" class="confirm">
        </form>
    </div>
    <form id="formid" name="myform">
        <input id="userIds" name="userIds" type="hidden" value=""/>
        <div class="m-content">
            <div class="loan-msg p30">
                <span>选中借款人数：</span><span id="bowwer1">0</span><span class="mr25">人</span>
                <span>借款金额：</span><span id="bowwer2">0</span><span class="mr25">元</span>
                <span>借款期限：</span><span id="bowwer3"><c:if test="${termLoan ne '' }">${termLoan}</c:if><c:if
                    test="${termLoan eq '' }">0</c:if></span><span class="day">天</span>
                </span>
                <div class="loan-list p30">
                    <table class="borrower-list ac">
                        <thead>
                        <tr class="m-title">
                            <td>借款人姓名</td>
                            <td>第三方借款流水号</td>
                            <td>借款金额</td>
                            <td>借款期限</td>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${list}" var="item" varStatus="i">
                            <tr>
                                <td id="name"><input id="userInfo${item[0]}" name="userInfo" value="${item[0]}"
                                                     type="checkbox"
                                                     onclick="getInfo(this, this.value)"/><label>${myel:jieMiUsername(item[1])}</label>
                                </td>
                                <td>${item[2]}</td>
                                <td id="money">${item[3]}</td>
                                <td id="count">${item[4]}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
                <div class="btns">
                    <input type="button" value="确定" class="btn2 publish" onclick="checkUser()">
                    <input type="button" value="取消" class="cancel publish">
                </div>
            </div>
        </div>
    </form>
</div>
</body>
<script>
    $('#termLoan').on('change', function () {
        $('#bowwer3').html("");
        $('.day').text($(this).find('option:selected').val() + "天");
    })
</script>
<script type="text/javascript">
    var productStatus = '${productStatus}';
    if (productStatus != '') {
        $("#productStatus").find("option[value='" + productStatus + "']").attr("selected", true);
    }

    function getInfo(obj, id) {
        //alert("aa=="+$(obj).parents("tr").children("td").eq(0).text());
        var td_content = $(obj).parents("tr").children("td");  //获取当前行中的所有td值
        if (document.getElementById("userInfo" + obj.value).checked) {
            //借款人人数处理
            //var content = td_content.eq(0).text(); //获取当前行第一个td的值
            var totalUsers = parseInt($('#bowwer1').text());
            totalUsers = parseInt(totalUsers) + 1;
            $('#bowwer1').html(totalUsers);

            // 借款金额处理
            var content2 = td_content.eq(2).text(); //获取当前行第二个td的值
            var moneys = parseInt($('#bowwer2').text());
            var moneystotal = parseInt(moneys) + parseInt(content2);
            $('#bowwer2').html(moneystotal);
        } else {
            //var content = td_content.eq(0).text(); //获取当前行第一个td的值
            var totalUsers = parseInt($('#bowwer1').text());
            if (totalUsers != 0) {
                totalUsers = parseInt(totalUsers) - 1;
            }
            $('#bowwer1').html(totalUsers);

            // 借款金额处理
            var content2 = td_content.eq(2).text(); //获取当前行第二个td的值
            var moneys = parseInt($('#bowwer2').text());
            var moneystotal = parseInt(moneys) - parseInt(content2);
            $('#bowwer2').html(moneystotal);
        }

        // 获取已选择用户ID
        var strgetSelectValue = "";
        var getSelectValueMenbers = $("input[name='userInfo']:checked").each(function (j) {
            if (j >= 0) {
                if (strgetSelectValue != "") {
                    strgetSelectValue += $(this).val() + ","
                } else {
                    strgetSelectValue = $(this).val() + ","
                }
            }
        });
        $('#userIds').val(strgetSelectValue);
    }

    function init() {
        if ($('#bowwer3').text() == "") {
            $('#bowwer3').html(0);
        }
    }

    function initDay() {
        $('#bowwer3').html(0);
    }

    function checkUser() {
        var userId = document.getElementById('userIds').value;
        window.opener.document.getElementById('userIds').value = userId;
        self.window.close();
    }

    $('.cancel').on('click', function () {
        $('input').prop('checked', false);
        $('#bowwer1').text(0);
        $('#bowwer2').text(0);
    })
</script>
</html>
