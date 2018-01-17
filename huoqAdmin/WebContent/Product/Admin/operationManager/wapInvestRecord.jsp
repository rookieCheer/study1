<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/zlqEl.tld" prefix="myel" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>wap用户投资统计报表</title>
    <link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css"/>
    <link href="${pageContext.request.contextPath}/Product/Admin/css/product_fabu_history.css" rel="stylesheet"
          type="text/css"/>
    <script src="${pageContext.request.contextPath}/Product/Admin/js/jquery-1.9.1.min.js"></script>
    <script src="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.standalone.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.css"
          type="text/css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/Product/js/alert.js"></script>
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
            var registPlatform = $("#registPlatform option:selected").val();
            var url = "${pageContext.request.contextPath}/Product/Admin/wapInvestorRecord!loadWapInvestorRecords.action?insertTime=" + insertTime + "&registPlatform=" + registPlatform;
            //alert(insertTime);
            window.location.href = url;
        }
    </script>
    <script type="text/javascript">
        function ireportDo() {
            var interval = $("#insertTime").val();
            if(interval == null || interval == '' || interval.length == 0){
                alert("请选择要导出报表日期！");
                return false;
            }
            if (interval.indexOf("-") != -1){
                var startDate = interval.split("-")[0];
                var endDate = interval.split("-")[1];
                var startTime = new Date(Date.parse(startDate.replace(/-/g,   "/"))).getTime();
                var endTime = new Date(Date.parse(endDate.replace(/-/g,   "/"))).getTime();
                var dates = Math.abs((startTime - endTime))/(1000*60*60*24);
                if(31 - dates <= 0){
                    alert("请选择日期间隔为31天的数据导出！")
                    return false;
                }
                //alert(dates);
            }
            var list = '${table}';
            if (list != 'true') {
                alert("无数据");
                return false;
            }
            var insertTime = $("#insertTime").val();
            var registPlatform = $("#registPlatform option:selected").val();
            var url = '${pageContext.request.contextPath}/Product/Admin/wapInvestorRecord!exportTable.action?';
            var my = art.dialog({
                title: '提示',
                content: document.getElementById("psi_load"),
                height: 60,
                lock: true,
                cancel: false
            });
            $.post(url, $("#frm").serialize(), function (data) {
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
        $(function () {
            $("#registPlatform option[value='${registPlatform}']").attr("selected", true);
            $("#insertTime,#registPlatform").keydown(function (event) {
                if (event.keyCode == 13) {
                    $("#frm").submit();
                }
            });
            $("#startCurrEveryDayStatsOperate").click(function () {
                var url = "${pageContext.request.contextPath}/Product/Admin/startThread!startCurrEveryDayStatsOperate.action";
                $.post(url, null, function (data) {
                    alert(data.json);//结果!
                    window.location.reload()
                });
            });
        });
    </script>
</head>
<body>
<div class="center">
    <jsp:include page="/Product/Admin/common/head.jsp"/>
    <div class="main" align="center">
        <h3>wap用户投资统计</h3>
        <form id="frm"
              action="${pageContext.request.contextPath}/Product/Admin/wapInvestorRecord!loadWapInvestorRecords.action">
            <span>查询期间:</span> <input id="insertTime" name="insertTime" type="text" value="${insertTime}">
           <%-- <select id="registPlatform" name="registPlatform">
                <option value="wx">微信</option>
                <option value="wap">WAP</option>
            </select>--%>
            <input type="button" value="查询" onclick="queryProduct()">
            <input type="button" value="导出报表" onclick="ireportDo()">
            <%--<input type="button" value="更新当天数据" id="startCurrEveryDayStatsOperate">--%>
        </form>
        <table border="1" cellspacing="0" cellpadding="0" style="text-align: center;">
            <tr>
                <td width="60px;">序号</td>
                <td width="200px;">统计日期</td>
                <td width="200px;">投资平台</td>
                <td width="200px;">注册人数</td>
                <td width="100px;">绑卡人数</td>
                <td width="200px;">投资人数</td>
                <td width="200px;">投资金额(元)</td>
                <td width="200px;">复投人数</td>
                <td width="200px;">复投率(%)</td>
            </tr>
            <%--<tr>
                    <td>合计</td>
                    <td>${tj.reuserscount}</td>
                    <td>${tj.bindcount}</td>
                    <td>${tj.insusercount}</td>
                    <td><fmt:formatNumber value="${tj.allcopies/100}" pattern="0.##"/></td>
                    <td> <fmt:formatNumber value="${tj.rjtz/100}" pattern="0.##"/></td>
                    <td>${tj.inscount}</td>
                    <td>${tj.ftinscs}</td>
                    <td>${tj.reginscount}</td>
                    <td><fmt:formatNumber value="${tj.regcopies/100}" pattern="0.##"/></td>
                    <td><fmt:formatNumber value="${tj.zhl}" pattern="0.##"/></td>
                    <td>${tj.stinsrs}</td>
                    <td>${tj.ftinsrs}</td>
                    <td><fmt:formatNumber value="${tj.ftl}" pattern="0.##"/></td>
            </tr>--%>
            <c:forEach items="${pageUtil.list}" var="item" varStatus="i">
                <tr>
                    <td>${i.count + (pageUtil.currentPage-1)*pageUtil.pageSize}</td>
                    <td>${item.date}</td>
                    <td>${item.platform}</td>
                    <td>${item.registCount}</td>
                    <td>${item.bindCount}</td>
                    <td>${item.investCount}</td>
                    <td>${item.totalInvestMoney}</td>
                    <td>${item.reinvestCount}</td>
                    <td><fmt:formatNumber value="${item.reinvestRate}" pattern="0.##"/></td>
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
        <span style="font-style: italic;font-size: medium;color: red">*复投人数:投资两次以上的用户（含两次）<br/>复投率:复投人数/投资人数<br/>wap：非手机客户端访问
		</span>
    </div>
</div>
<div id="psi_load" style="display:none;">
    <img width="18" src="${pageContext.request.contextPath}/Product/images/progress.gif"><span
        id="psi_tip">正在导出,请稍等...</span>
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
    });
</script>
</body>
</html>