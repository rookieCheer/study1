<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/zlqEl.tld" prefix="myel" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css"/>
    <link href="${pageContext.request.contextPath}/Product/Admin/css/product.css" rel="stylesheet" type="text/css"/>
    <script src="${pageContext.request.contextPath}/Product/Admin/js/jquery-1.9.1.min.js"></script>
    <script src="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.standalone.js"></script>
    <script src="${pageContext.request.contextPath}/js/artDialog4.1.7/jquery.artDialog.js?skin=blue"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.css"
          type="text/css">
    <title>投资券记录</title>
    <style type="text/css">
        .a {
            color: blue;
            text-decoration: underline;
        }
    </style>
    <script type="text/javascript">
        function ireportDo() {
            var insertTime = $("#insertTime").val();
            var useTime = $("#useTime").val();
            if (((insertTime == null || insertTime == '' || insertTime.length == 0) &&
                    (useTime == null || useTime == '' || useTime.length == 0))
            ) {
                alert("请选择要导出报表日期！");
                return false;
            }
            if (insertTime.indexOf("-") != -1) {
                var startDate = insertTime.split("-")[0];
                var endDate = insertTime.split("-")[1];
                var startTime = new Date(Date.parse(startDate.replace(/-/g, "/"))).getTime();
                var endTime = new Date(Date.parse(endDate.replace(/-/g, "/"))).getTime();
                var dates = Math.abs((startTime - endTime)) / (1000 * 60 * 60 * 24);
                if (31 - dates <= 0) {
                    alert("【发送投资券时间】请选择日期间隔为31天的数据导出！")
                    return false;
                }
            }
            if (useTime.indexOf("-") != -1) {
                var startDate = useTime.split("-")[0];
                var endDate = useTime.split("-")[1];
                var startTime = new Date(Date.parse(startDate.replace(/-/g, "/"))).getTime();
                var endTime = new Date(Date.parse(endDate.replace(/-/g, "/"))).getTime();
                var dates = Math.abs((startTime - endTime)) / (1000 * 60 * 60 * 24);
                if (31 - dates <= 0) {
                    alert("【使用投资券时间】请选择日期间隔为31天的数据导出！")
                   return false;
                }
            }
            var username = document.getElementById("username").value;
            var note = document.getElementById("note").value;
            var status = $("#status option:selected").val();
            var url = '${pageContext.request.contextPath}/Product/Admin/sendCoupon!exportExcelData.action?currentPage=${currentPage}&username=' + username + '&insertTime=' + insertTime + '&status=' + status + '&note=' + note + '&useTime=' + useTime;
            var list = "${txRecordList}";
            if (list != null && list != "[]") {
                var my = art.dialog({
                    title: '提示',
                    content: document.getElementById("psi_load"),
                    height: 60,
                    lock: true,
                    cancel: false
                });
                $.post(url, $("#frm").serialize(), function (data) {
                    my.close();
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
        }
    </script>

</head>
<body>
<div class="center">
    <jsp:include page="/Product/Admin/common/head.jsp"/>

    <div class="main" align="center">
        <h1 style="text-align: center;">投资券记录</h1>
        <form action="${pageContext.request.contextPath}/Product/Admin/sendCoupon!couponRecord.action" method='post'>
            <label>用户名:<input type="text" name="username" id="username" value="${username}" maxlength="11">
                <span>发送投资券时间:</span> <input id="insertTime" name="insertTime" type="text" value="${insertTime}">
                <span>使用投资券时间:</span> <input id="useTime" name="useTime" type="text" value="${useTime}">
                <span>关键字查询:</span> <input id="note" name="note" type="text" value="${note}">
                <br>
                状态：<select id="status" name="status">
                    <option value="">所有</option>
                    <option value="0">未使用</option>
                    <option value="2">已用完</option>
                    <option value="3">已过期</option>
                </select>
                <input type="submit" value="search"></label>&nbsp;&nbsp;
            <input type="button" value="导出报表" onclick="ireportDo()">
        </form>
        <table style="width:100%;margin-top: 20px;text-align: center;">
            <tr>
                <td>序号</td>
                <td>用户名</td>
                <td>发送金额</td>
                <td>状态</td>
                <td>类型</td>
                <td>发送时间</td>
                <td>到期时间</td>
                <td>使用时间</td>
                <td>注册平台</td>
                <td>注册渠道</td>
                <td>备注</td>
            </tr>
            <c:forEach items="${pageUtil.list}" var="item" varStatus="i">
                <tr>
                    <td>${i.count + (pageUtil.currentPage-1)*pageUtil.pageSize}</td>
                    <td><a class="a"
                           href="${pageContext.request.contextPath}/Product/Admin/userStat!loadUserInfo.action?username=${myel:jieMiUsername(item.users.username)}">${myel:jieMiUsername(item.users.username)}</a>
                    </td>
                    <td><fmt:formatNumber value="${item.initMoney * 0.01}" pattern="#,##0.##"/></td>
                    <td>${item.statusChina}</td>
                    <td>
                    	<c:if test="${item.type eq 0}">理财券</c:if>
                    	<c:if test="${item.type eq 1}">新手理财券</c:if>
                    	<c:if test="${item.type eq 2}">红包</c:if>
                    	<c:if test="${item.type eq 3}">新手红包</c:if>
                    </td>
                    <td><fmt:formatDate value="${item.insertTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                    <td><fmt:formatDate value="${item.overTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                    <td><fmt:formatDate value="${item.useTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>

                    <td>
                        <c:choose>
                            <c:when test="${item.users.registPlatform == 0}">web端注册</c:when>
                            <c:when test="${item.users.registPlatform == 1}">Android移动端</c:when>
                            <c:when test="${item.users.registPlatform == 2}">IOS移动端</c:when>
                            <c:when test="${item.users.registPlatform == 3}">微信注册</c:when>
                            <c:otherwise>${item.users.registPlatform}</c:otherwise>
                        </c:choose>

                    </td>
                    <td>${item.users.registChannel}</td>
                    <td>${item.note}</td>
                </tr>
            </c:forEach>
        </table>

        <c:choose>
            <c:when test="${pageUtil.list ne '[]' &&  pageUtil.list ne '' && pageUtil.list ne null}">
                <jsp:include page="/Product/page.jsp"/>
            </c:when>
            <c:otherwise>
                <div style="text-align: center;margin-top: 15px;"><!-- <img src="images/lh.jpg"> -->
                    <img src="../images/no_record.png"/>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
<script type="text/javascript">
    var k4 = new Kalendae.Input("insertTime", {
        attachTo: document.body,
        months: 2,//多少个月显示出来,即看到多少个日历
        mode: 'range'
        /* selected:[Kalendae.moment().subtract({d:7}), Kalendae.moment().add({d:0})] */
    });
    var k5 = new Kalendae.Input("useTime", {
        attachTo: document.body,
        months: 2,//多少个月显示出来,即看到多少个日历
        mode: 'range'
        /* selected:[Kalendae.moment().subtract({d:7}), Kalendae.moment().add({d:0})] */
    });
    $(function () {
        $("#status option[value='${status}']").attr("selected", true);
    });
</script>
</html>
