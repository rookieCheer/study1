<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/zlqEl.tld" prefix="myel" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>银行卡数据统计</title>
    <link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css"/>
    <link href="${pageContext.request.contextPath}/Product/Admin/css/product_fabu_history.css" rel="stylesheet"
          type="text/css"/>
    <script src="${pageContext.request.contextPath}/Product/Admin/js/jquery-1.9.1.min.js"></script>
    <!-- 引入时间组件 -->
    <script src="${pageContext.request.contextPath}/Product/Admin/js/jQuery-calendar/js/laydate.js"></script>
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
</head>
<body>
<div class="center">
    <jsp:include page="/Product/Admin/common/head.jsp"/>
    <div class="main" align="center">
        <h3>银行卡数据统计</h3>
        <form id="frm" action="${pageContext.request.contextPath}/Product/Admin/userStat!loadbankStatistics.action"
              onsubmit="return checkTime()">
            <span>查询时间:</span>
            <input id="insertTime" name="insertTime" type="text" value="${insertTime}">
            <input type="submit" value="查询">
            <input type="button" value="导出报表" onclick="exportExcel()">
        </form>
        <table border="1" cellspacing="0" cellpadding="0" style="text-align: center;">
            <tr>
                <td width="200px;">序号</td>
                <td width="200px;">银行</td>
                <td width="200px;">绑定人数</td>
                <td width="200px;">充值总额(元)</td>
                <td width="200px;">投资成功次数</td>
                <td width="200px;">投资失败次数</td>
            </tr>
            <c:forEach items="${list}" var="item" varStatus="i">
                <tr>
                    <td>${i.index+1}</td>
                    <td>${item.bankName}</td>
                    <td>${item.rsCount}</td>
                    <td><fmt:formatNumber value="${item.jeCount}" pattern="###.##"/></td>
                    <td>${item.cgCount}</td>
                    <td>${item.sbCount}</td>
                </tr>
            </c:forEach>
        </table>
    </div>
</div>
<div id="psi_load" style="display:none;">
    <img width="18" src="${pageContext.request.contextPath}/Product/images/progress.gif"><span
        id="psi_tip">正在导出,请稍等...</span>
</div>
</body>
<script type="text/javascript">
    function exportExcel() {
        var insertTime = $("#insertTime").val();
        var form = $("<form>");
        form.attr('target', 'iframe');
        form.attr('method', 'post');
        form.attr('action', 'userStat!exportExcelList.action');
        var input = $('<input>');
        input.attr('type', 'hidden');
        input.attr('name', 'insertTime');
        input.attr('value', insertTime);
        var iframe = $("<iframe>")
        iframe.attr('id', 'iframe');
        iframe.attr('name', 'iframe');
        iframe.attr('src', 'about:blank');
        iframe.attr('style', 'display:none;');
        $('body').append(iframe);
        $('body').append(form);
        form.append(input);
        form.submit();
    }


    var start = {
        elem: '#begin',
        format: 'YYYY-MM-DD hh:mm:ss',
        istime: true,
        istoday: false,
        choose: function (datas) {
            end.min = datas;
            end.start = datas
        }
    };

    var end = {
        elem: '#end',
        format: 'YYYY-MM-DD hh:mm:ss',
        istime: true,
        istoday: false,
        choose: function (datas) {
            start.max = datas;
        }
    };
    laydate(start);
    laydate(end);
</script>
    <script type="text/javascript">
        var k4 = new Kalendae.Input("insertTime", {
            attachTo: document.body,
            months: 2,//多少个月显示出来,即看到多少个日历
            mode: 'range'
        });

    </script>
</html>