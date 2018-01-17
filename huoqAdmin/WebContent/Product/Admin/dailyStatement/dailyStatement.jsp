<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2018/1/16
  Time: 17:29
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/zlqEl.tld" prefix="myel" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>运营日报表</title>
    <link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css"/>
    <link href="${pageContext.request.contextPath}/Product/Admin/css/product_fabu_history.css" rel="stylesheet"
          type="text/css"/>
    <script src="${pageContext.request.contextPath}/Product/Admin/js/jquery-1.9.1.min.js"></script>
    <script src="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.standalone.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.css"
          type="text/css">
    <script src="${pageContext.request.contextPath}/js/artDialog4.1.7/jquery.artDialog.js?skin=blue"></script>
    <style type="text/css">
        .sereach {
            width: 200px;
            height: 32px;
            line-height: 32px;
            text-align: center;
            border: 1px solid #009DDA;
            border-radius: 5px;
        }

        .select1 {
            border-radius: 5px;
            border-color: #009DDA;
            margin-right: 5px;
        }

        .a {
            color: blue;
            text-decoration: underline;
        }
    </style>
    <script type="text/javascript">
        function queryProduct() {
            var insertTime = $("#insertTime").val();
            var url = "${pageContext.request.contextPath}/Product/Admin/dailyStatement!findDailyStatement.action?insertTime=" + insertTime;
            window.location.href = url;
        }

        function goqueryProduct(){
            var goPage = $("#goPage").val();
            var pageSize = ${pageSize};
            var url = "${pageContext.request.contextPath}/Product/Admin/dailyStatement!findDailyStatement.action?&currentPage="+goPage+"&pageSize="+pageSize;
            window.location.href = url;
        }

        function ireportDo() {
            var interval = $("#insertTime").val();
            if (interval == null || interval == '' || interval.length == 0) {
                alert("请选择要导出报表日期！");
                return false;
            }
            if (interval.indexOf("-") != -1) {
                var startDate = interval.split("-")[0];
                var endDate = interval.split("-")[1];
                var startTime = new Date(Date.parse(startDate.replace(/-/g, "/")))
                    .getTime();
                var endTime = new Date(Date.parse(endDate.replace(/-/g, "/")))
                    .getTime();
                var dates = Math.abs((startTime - endTime)) / (1000 * 60 * 60 * 24);
                if (31 - dates <= 0) {
                    alert("请选择日期间隔为31天的数据导出！")
                    return false;
                }
            }
            var insertTime = $("#insertTime").val();
            var url = "${pageContext.request.contextPath}/Product/Admin/dailyStatement!exportDailyStatement.action?insertTime=" + insertTime;
            var list = "${list}";
            if (list != null && list != "[]") {
                var my = art.dialog({
                    title: '提示',
                    content: document.getElementById("psi_load"),
                    height: 60,
                    lock: true,
                    cancel: false
                });
                $.post(
                    url,
                    $("#sereach").serialize(),
                    function (data) {
                        my.close();
                        data = '${pageContext.request.contextPath}'
                            + data;
                        var ssss = "导出成功&nbsp;&nbsp;&nbsp;&nbsp;<a href='" + data + "' style='color:red;'>点击下载</a>";
                        art.dialog({
                            title: '提示',
                            content: ssss,
                            height: 60,
                            lock: true,
                            ok: function () {

                            }
                        });
                    });
            }
        }

    </script>
</head>
<body>
<div class="center">
    <jsp:include page="/Product/Admin/common/head.jsp"/>
    <div class="main" align="center">
        <h3>运营日报表</h3>
        <span>查询时间:</span>
        <input id="insertTime" name="insertTime" type="text" value="${insertTime}"/>
        <a class="sereach" href="javascript:queryProduct();" id="sereach">查询</a>
        <input type="button" value="导出报表" onclick="ireportDo()">
        <table border="1" width="80%">
            <tr>
                <td>序号</td>
                <td width="70px;">查询日期</td>
                <td>交易额</td>
                <td>在贷金额（含零钱罐）</td>
                <td>在贷金额（不含零钱罐）</td>
                <td>回款金额（不含零钱罐）</td>
                <td>回款金额（含零钱罐及余额）</td>
                <td>支付利息</td>
                <td>提现金额</td>
                <td>回款用户投资率</td>
                <td>资金流入额</td>
                <td>净流入金额</td>
                <td>资金存量</td>
                <td>激活用户数</td>
                <td>投资用户数</td>
                <td>注册用户数</td>
                <td>认证用户数</td>
                <td>首投用户数</td>
                <td>首投用户转化率（%）</td>
                <td>首投总金额（元）</td>
                <td>首投客单金额（元）</td>
                <td>复投金额（元）</td>
                <td>零钱罐新增金额（元）</td>
                <td>复投用户数</td>
                <td>新增复投用户数</td>
                <td>新增复投用户投资总额（元）</td>
                <td>复投次数</td>
                <td>新增复投率（%）</td>
                <td>复投用户占比（%）</td>
                <td>复投金额占比（%）</td>
                <td>复投客单金额（元）</td>
                <td>人均投资金额（元）</td>
            </tr>
            <c:forEach items="${list}" var="mylist" varStatus="status">
                <tr>
                    <td>${status.index + 1}</td>
                    <td><fmt:formatDate value="${mylist.insertTime}" pattern="yyyy-MM-dd"/></td>
                    <td><fmt:formatNumber value="${mylist.tradingVolume}" pattern="#.##"/></td>
                    <td><fmt:formatNumber value="${mylist.loanAmountAll}" pattern="#.##"/></td>
                    <td><fmt:formatNumber value="${mylist.loanAmount}" pattern="#.##"/></td>
                    <td><fmt:formatNumber value="${mylist.reimbursementAmount}" pattern="#.##"/></td>
                    <td><fmt:formatNumber value="${mylist.reimbursementAmountAll}" pattern="#.##"/></td>
                    <td><fmt:formatNumber value="${mylist.interestpayment}" pattern="#.##"/></td>
                    <td><fmt:formatNumber value="${mylist.todayOutCashMoney}" pattern="#.##"/></td>
                    <td><fmt:formatNumber value="${mylist.returnInvestmentRate}" pattern="#.##"/></td>
                    <td><fmt:formatNumber value="${mylist.capitalInflow}" pattern="#.##"/></td>
                    <td><fmt:formatNumber value="${mylist.netInflow}" pattern="#.##"/></td>
                    <td><fmt:formatNumber value="${mylist.capitalStock}" pattern="#.##"/></td>
                    <td><fmt:formatNumber value="${mylist.activationCount}" pattern="#.##"/></td>
                    <td><fmt:formatNumber value="${mylist.investCount}" pattern="#.##"/></td>
                    <td><fmt:formatNumber value="${mylist.todayregisterCount}" pattern="#.##"/></td>
                    <td><fmt:formatNumber value="${mylist.todaycertificationCount}" pattern="#.##"/></td>
                    <td><fmt:formatNumber value="${mylist.todayNewBuyNumber}" pattern="#.##"/></td>
                    <td><fmt:formatNumber value="${mylist.firstPercentConversion}" pattern="#.##"/></td>
                    <td><fmt:formatNumber value="${mylist.firstInvestmentTotalMoney}" pattern="#.##"/></td>
                    <td><fmt:formatNumber value="${mylist.firstInvestmentMoney}" pattern="#.##"/></td>
                    <td><fmt:formatNumber value="${mylist.reInvestmentMoney}" pattern="#.##"/></td>
                    <td><fmt:formatNumber value="${mylist.amountNewMoney}" pattern="#.##"/></td>
                    <td><fmt:formatNumber value="${mylist.reInvestmentCount}" pattern="#.##"/></td>
                    <td><fmt:formatNumber value="${mylist.addReInvestmentCount}" pattern="#.##"/></td>
                    <td><fmt:formatNumber value="${mylist.addReInvestmentMoney}" pattern="#.##"/></td>
                    <td><fmt:formatNumber value="${mylist.reInvestmentAmount}" pattern="#.##"/></td>
                    <td><fmt:formatNumber value="${mylist.multipleRate}" pattern="#.##"/></td>
                    <td><fmt:formatNumber value="${mylist.occupationRatio}" pattern="#.##"/></td>
                    <td><fmt:formatNumber value="${mylist.reInvestmentRate}" pattern="#.##"/></td>
                    <td><fmt:formatNumber value="${mylist.sumMoney}" pattern="#.##"/></td>
                    <td><fmt:formatNumber value="${mylist.capitaInvestmentMoney}" pattern="#.##"/></td>
                </tr>
            </c:forEach>
        </table>
        <c:choose>
            <c:when
                    test="${pageUtil.list ne '[]' &&  pageUtil.list ne '' && pageUtil.list ne null}">
                <jsp:include page="/Product/page.jsp"/>
                跳转页面:<input id="goPage" name="goPage" type="text" value="${currentPage}"
                style="width: 50px">
                <a class="sereach" href="javascript:goqueryProduct();">跳转</a>
            </c:when>
            <c:otherwise>
                <div style="text-align: center; margin-top: 15px;">
                    <img src="../images/no_record.png"/>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>
<script type="text/javascript">
    var k4 = new Kalendae.Input("insertTime", {
        attachTo: document.body,
        months: 2,//多少个月显示出来,即看到多少个日历
        mode: 'range'

    });
    $(function () {
        $("#isbindbank option[value='${isbindbank}']").attr("selected", true);
        $("#level option[value='${level}']").attr("selected", true);
    });
</script>
</body>
</html>
