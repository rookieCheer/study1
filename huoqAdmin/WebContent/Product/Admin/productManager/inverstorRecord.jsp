<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/zlqEl.tld" prefix="myel" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link
            href="${pageContext.request.contextPath}/Product/Admin/css/public.css"
            rel="stylesheet" type="text/css"/>
    <link
            href="${pageContext.request.contextPath}/Product/Admin/css/product.css"
            rel="stylesheet" type="text/css"/>
    <link
            href="${pageContext.request.contextPath}/Product/Admin/css/product_fabu_history.css"
            rel="stylesheet" type="text/css"/>
    <script
            src="${pageContext.request.contextPath}/Product/Admin/js/jquery-1.9.1.min.js"></script>
    <script
            src="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.standalone.js"></script>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.css"
          type="text/css">
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/Product/js/alert.js"></script>
    <script
            src="${pageContext.request.contextPath}/js/artDialog4.1.7/jquery.artDialog.js?skin=blue"></script>

    <style type="text/css">
        .a {
            color: blue;
            text-decoration: underline;
        }
    </style>

    <title>投资记录</title>
</head>
<body>
<div class="center">
    <jsp:include page="/Product/Admin/common/head.jsp"/>
    <div class="main" align="center">
        <h1 style="text-align: center;">投资记录</h1>
        <div id="div_condition" style="text-align: center;">
            <label>用户名:<input type="text" name="name" id="name" value="${name}" maxlength="11"></label>
            <label>产品名称:<input type="text" name="productTitle" id="productTitle" value="${productTitle}"></label>
            <label>
                <span>投资时间:</span>
                <input id="insertTime" name="insertTime" type="text" value="${insertTime}"></label>
            <label><input type="button" value="search" id="frm" onclick="Byname()"></label>
            <label><input type="button" value="导出报表" onclick="ireportDo()">
                <label><input type="radio" value="all" name="status" checked="checked">全部</label>
                <label><input type="radio" value="0" name="status">待付款</label>
                <label><input type="radio" value="1" name="status">已付款</label>
                <label><input type="radio" value="2" name="status">结算中</label>
                <label><input type="radio" value="3" name="status">已结算</label>
                <input type="hidden" name="productId" id="productId" value="${productId}">
        </div>

        <table style="width:1200px;margin-top: 20px;width: 100%;">
            <tr>
                <td>序号</td>
                <td>用户名</td>
                <td>姓名</td>
                <td>渠道</td>
                <td>是否首投</td>
                <td>产品名称</td>
                <td>投资<br>天数
                </td>
                <td>理财<br>期限
                </td>
                <td>剩余<br>天数
                </td>
                <td>购买状态</td>
                <td>购买份数</td>
                <td>投入本金</td>
                <td>本金<br>收益
                </td>
                <td>投资券</td>
                <td>投资券来源</td>
                <td>投资券<br>收益
                </td>
                <td>红包<br>金额
                </td>
                <td>红包<br>来源
                </td>
                <td>最终年<br>化收益
                </td>
                <td>到期总<br>收益
                </td>
                <td>投资时间</td>
                <td>起息时间</td>
                <td>结算时间</td>
                <td>项目到期时间</td>
            </tr>
            <c:forEach items="${pageUtil.list}" var="item" varStatus="i">
                <tr>
                    <td>${i.count + (pageUtil.currentPage-1)*pageUtil.pageSize}</td>
                    <td>${item.username}</td>
                    <td>${item.realname}</td>
                    <td>${item.registChannel}</td>
                    <td>${item.isFirstInvt}</td>
                    <td>${item.title}</td>
                    <td>${item.tits}</td>
                    <td>${item.lcqx}</td>
                    <td>${item.tzqx}</td>
                    <td>${item.tzzt}</td>
                    <td><fmt:formatNumber value="${item.copies}" pattern="#,##0.##"/></td>
                    <td><fmt:formatNumber value="${item.inMoney}" pattern="#,##0.##"/></td>
                    <td><fmt:formatNumber value="${item.expectEarnings}" pattern="#,##0.##"/></td>
                    <td><fmt:formatNumber value="${item.coupon }" pattern="#,##0.##"/></td>
                    <td>${item.investSource}</td>
                    <td><fmt:formatNumber value="${item.couponShouyi }" pattern="#,##0.##"/></td>
                    <td><fmt:formatNumber value="${item.hongbao }" pattern="#,##0.##"/></td>
                    <td>${item.redPackageSource}</td>
                    <td>${item.annualEarnings}</td>
                    <td><fmt:formatNumber value="${item.finalEarnings}" pattern="#,##0.##"/></td>
                    <td><fmt:formatDate value="${item.payTime}" pattern="yyyy-MM-dd HH:mm:ss"/> </td>
                    <td><fmt:formatDate value="${item.startTime}" pattern="yyyy-MM-dd HH:mm:ss"/> </td>
                    <td><fmt:formatDate value="${item.clearTime}" pattern="yyyy-MM-dd HH:mm:ss"/> </td>
                    <td><fmt:formatDate value="${item.finishTime}" pattern="yyyy-MM-dd HH:mm:ss"/> </td>
                </tr>
            </c:forEach>
        </table>
        <c:choose>
            <c:when
                    test="${pageUtil.list ne '[]' &&  pageUtil.list ne '' && pageUtil.list ne null}">
                <jsp:include
                        page="/Product/page.jsp"/>
            </c:when>
            <c:otherwise>
                <div style="text-align: center;margin-top: 15px;">
                    <!-- <img src="images/lh.jpg"> -->
                    <img src="../images/no_record.png"/>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>
<script type="text/javascript">
    function ireportDo() {
        var url = '${pageContext.request.contextPath}/Product/Admin/investors!iportTable.action?name=' + $("#name").val() + "&status=" + $('input[name="status"]:checked').val() + "&insertTime=" + $('#insertTime').val();
        var productTitle = $("#productTitle").val();
        if (productTitle != '' && productTitle != null) {
            url += "&productTitle=" + productTitle;
        }
        var my = art.dialog({
            title: '提示',
            content: document.getElementById("psi_load"),
            height: 60,
            lock: true,
            cancel: false
        });
        $.post(url, null, function (data) {
            my.close();
            data = '${pageContext.request.contextPath}' + data;
            var ssss = "导出成功&nbsp;&nbsp;&nbsp;&nbsp;<a href='" + data + "' style='color:red;'>点击下载</a>";
            art.dialog({
                title: '提示',
                content: ssss,
                height: 60,
                lock: true,
                ok: function () {
                    //mysss.close();
                }
            });
        });

    }

    $("#div_condition input[name='status']").click(function () {
        window.location.href = "${pageContext.request.contextPath}/Product/Admin/investors!findInvertors.action?name=" + $("#name").val() + "&status=" + $('input[name="status"]:checked').val() + "&insertTime=" + $('#insertTime').val();
    });

    $("#div_condition input[name='status']").each(function () {
        if ($(this).attr("value") == "${status}" || "${status}" == "") {
            $(this).attr("checked", true);
        }
    });

    function Byname() {
        var url = "${pageContext.request.contextPath}/Product/Admin/investors!findInvertors.action?name=" + $("#name").val() + "&status=" + $('input[name="status"]:checked').val() + "&insertTime=" + $('#insertTime').val();
        var productTitle = $("#productTitle").val();
        if (productTitle != '' && productTitle != null) {
            url += "&productTitle=" + productTitle;
        }
        window.location.href = url;
    }

    var k4 = new Kalendae.Input("insertTime", {
        attachTo: document.body,
        months: 2,//多少个月显示出来,即看到多少个日历
        mode: 'range'
        /* selected:[Kalendae.moment().subtract({d:7}), Kalendae.moment().add({d:0})] */
    });
</script>
</body>
</html>
