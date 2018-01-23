<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/zlqEl.tld" prefix="myel" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>用户信息表</title>
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
            var url = "${pageContext.request.contextPath}/Product/Admin/userStat!exportAge.action?insertTime=" + insertTime;
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
        function queryProduct() {
            var insertTime = $("#insertTime").val();
            var url = "${pageContext.request.contextPath}/Product/Admin/userStat!loadAge.action?insertTime=" + insertTime;
            window.location.href = url;
        }
    </script>
    <script type="text/javascript">
        $(function () {
            var value = '${registPlatform}';
            if (value == '') {
                $("input[type=radio][name='registPlatform'][value=all]").attr("checked", 'checked');
            } else {
                $("input[type=radio][name='registPlatform'][value=" + value + "]").attr("checked", 'checked');
            }
        });
    </script>
</head>
<body>
<div class="center">
    <jsp:include page="/Product/Admin/common/head.jsp"/>
    <div class="main" align="center">
        <h3>用户年龄分布表</h3>
        <div id="div_condition">
            <span>注册平台</span>
            <label><input type="radio" id="" value="all" name="registPlatform" checked="checked">全部</label>&nbsp;&nbsp;
            <label><input type="radio" value="0" name="registPlatform">web端注册</label>&nbsp;&nbsp;
            <label><input type="radio" value="1" name="registPlatform">Android移动端</label>&nbsp;&nbsp;
            <label><input type="radio" value="2" name="registPlatform">IOS移动端</label>&nbsp;&nbsp;
            <label><input type="radio" value="3" name="registPlatform">微信注册</label>&nbsp;&nbsp;
        </div>
        <span>查询期间:</span>
        <input id="insertTime" name="insertTime" type="text" value="${insertTime}">
        <a class="sereach" href="javascript:queryProduct();" id="sereach">查询</a>
        <input type="button" value="导出报表" onclick="ireportDo()">
        <table border="1" cellspacing="0" cellpadding="0" style="text-align: center;">
            <tr>
                <td width="200px;">年龄段</td>
                <td width="200px;">人数</td>
                <td width="200px;">投资次数</td>
                <td width="200px;">投资总额</td>
            </tr>
            <c:forEach items="${list}" var="item">
                <tr>
                    <td>${item.ageCeng}</td>
                    <td>${item.rsCount}</td>
                    <td>${item.csCount}</td>
                    <td><fmt:formatNumber value="${item.jeCount*0.01}" pattern="#.##"/></td>
                </tr>
            </c:forEach>
        </table>
    </div>
</div>

<script type="text/javascript">
    $("#div_condition input[name='registPlatform']").click(function () {
        window.location.href = "${pageContext.request.contextPath}/Product/Admin/userStat!loadAge.action?registPlatform=" + $('input[name="registPlatform"]:checked').val();
    });

    var k4 = new Kalendae.Input("insertTime", {
        attachTo: document.body,
        months: 2,//多少个月显示出来,即看到多少个日历
        mode: 'range'

    });
</script>
</body>
</html>