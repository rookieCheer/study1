<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2018/1/5
  Time: 20:24
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
<body>

<div class="center">
    <div class="content">
    </div>
    <h2 class="title">借款人信息</h2>
    <table class="borrower">
        <thead>
        <tr>
            <th>借款人姓名</th>
            <th>第三方流水账号</th>
            <th>借款金额(元)</th>
            <th>借款期限(天)</th>
            <th>借款状态</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${list}" var="item" varStatus="i">
            <tr align="center">
                <td>${myel:jieMiUsername(item[2])}</td>
                <td>${item[3]}</td>
                <td>${item[4]}</td>
                <td>${item[5]}</td>
                <td>
                    <c:forEach items="${blist}" var="items" varStatus="y">
                        <c:if test="${i.index eq y.index}" >
                            <c:if test="${items[2] eq 0}">打款成功</c:if>
                            <c:if test="${items[2] eq 1}">打款失败</c:if>
                            <c:if test="${items[2] eq 2}">打款中</c:if>
                        </c:if>
                    </c:forEach>
                </td>
                <td>
                    <c:forEach items="${blist}" var="items" varStatus="y">
                        <c:if test="${i.index eq y.index}" >

                            <c:if test="${items[2] eq 1}"><a class="a" href="javascript: playMoneyAlone('${item[0]}');">打款</a></c:if>

                        </c:if>
                    </c:forEach>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
</body>
<script type="text/javascript">
    function playMoneyAlone(playMoneyId) {
        $.ajax({
            type: "POST",
            url: "${pageContext.request.contextPath}/Product/Admin/assetSide!playMoneyAlone.action",
            data: {"playMoneyId": playMoneyId},
            datatype: "json",
            success: function (data) {
                if (data.status == "success") {
                    alert("打款成功");
                    window.location.reload();//刷新当前页面
                } else {
                    alert(data.json);
                    window.location.reload();//刷新当前页面
                }
            }
        });
    }
</script>
</html>
