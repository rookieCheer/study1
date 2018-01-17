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
                var startTime = new Date(Date.parse(startDate.replace(/-/g, "/"))).getTime();
                var endTime = new Date(Date.parse(endDate.replace(/-/g, "/"))).getTime();
                var dates = Math.abs((startTime - endTime)) / (1000 * 60 * 60 * 24);
                if (31 - dates <= 0) {
                    alert("请选择日期间隔为31天的数据导出！")
                    return false;
                }
                //alert(dates);
            }
            var name = document.getElementById("username").value;
            var channel = document.getElementById("channel").value;
            var insertTime = document.getElementById("insertTime").value;
            var isbindbank = $("#isbindbank option:selected").val();
            var level = $("#level option:selected").val();
            var inMoney1 = document.getElementById("inMoney1").value;
            var inMoney2 = document.getElementById("inMoney2").value;
            var url = '${pageContext.request.contextPath}/Product/Admin/userStat!iportUserInfo.action?currentPage=${currentPage}&name=' + name + '&insertTime=' + insertTime + '&channel=' + channel + '&isbindbank=' + isbindbank + '&level=' + level + '&inMoney1=' + inMoney1 + '&inMoney2=' + inMoney2;
            var list = "${list}";
            if (list != null && list != "[]") {
                var my = art.dialog({
                    title: '提示',
                    content: document.getElementById("psi_load"),
                    height: 60,
                    lock: true,
                    cancel: false
                });
                $.post(url, $("#sereach").serialize(), function (data) {
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
        }
    </script>


    <script type="text/javascript">


    </script>
    <script type="text/javascript">
        $(function () {
            $("#registPlatform option[value='${registPlatform}']").attr("selected", true);
            $("#username,#channel,#insertTime,#isbindbank").keydown(function (event) {
                if (event.keyCode == 13) {
                    queryProduct();
                }
            });

            $("#inMoney1").blur(function () {
                var inMoney1 = $("#inMoney1").val();
                var inMoney2 = $("#inMoney2").val();
                if (inMoney1 != "" && inMoney2 != "" && inMoney1 > inMoney2) {
                    $("#inMoney1").val(inMoney2);
                    $("#inMoney2").val(inMoney1);
                }
            });

            $("#inMoney2").blur(function () {

                var inMoney1 = $("#inMoney1").val();
                var inMoney2 = $("#inMoney2").val();

                if (inMoney1 != "" && inMoney2 != "" && inMoney1 > inMoney2) {
                    $("#inMoney1").val(inMoney2);
                    $("#inMoney2").val(inMoney1);
                }
            });
            
            function showFriendRecords(userId) {
                var url = "${pageContext.request.contextPath}/Product/Admin/userTeam!friendRecords.action?userId="+userId+"";
                window.open();
            }

        });

    </script>
    <script type="text/javascript">
        function loadUserInfo(toUserId) {
            var url = "${pageContext.request.contextPath}/Product/Admin/userStat!loadUserInfo.action?username="+toUserId;
            window.open(url);
        }
    </script>
</head>
<body>
<div class="center">
    <jsp:include page="/Product/Admin/common/head.jsp"/>
    <div class="main" align="center">
        <h3>好友收益记录</h3>
        <table border="1" width="80%">
            <tr>
                <td width="33%">用户名</td>
                <td width="33%">注册时间</td>
                <td width="34%">总金额</td>
            </tr>
            <tr>
                <td>
                    <a class="a"  href="javascript:loadUserInfo(${data.userName})">${data.userName}</a>
                </td>
                <td>
                    ${data.insertTime}
                </td>
                <td>
                    ${data.sumMoney}
                </td>
            </tr>
        </table>
        <table border="1" width="80%" style="margin-top: 20px;">
            <tr>
                <td width="25%">日期</td>
                <td width="25%">产品</td>
                <td width="25%">类型</td>
                <td width="25%">金额</td>
            </tr>
            <c:forEach items="${data.list}" var="item">
                <tr>
                    <td>${item.createTime}</td>
                    <td>${item.productName}</td>
                    <c:if test="${item.type == 1}">
                    	<td>理财券</td>
                    </c:if>
                    <c:if test="${item.type == 2}">
                    	<td>理财券</td>
                    </c:if>
                    <c:if test="${item.type == 3}">
                    	<td>红包</td>
                    </c:if>
                    <c:if test="${item.type == 4}">
                    	<td>红包</td>
                    </c:if>
                    <c:if test="${item.type eq '5'}">
                    	<td>现金</td>
                    </c:if>
                    <td>${item.inItMoney}</td>
                </tr>
            </c:forEach>

        </table>
    </div>
</div>
<script type="text/javascript">
    var k4 = new Kalendae.Input("insertTime", {
        attachTo: document.body,
        months: 2,//多少个月显示出来,即看到多少个日历
        mode: 'range'
        /* selected:[Kalendae.moment().subtract({d:7}), Kalendae.moment().add({d:0})] */
    });
    $(function () {
        $("#isbindbank option[value='${isbindbank}']").attr("selected", true);
        $("#level option[value='${level}']").attr("selected", true);
    });
</script>
</body>
</html>