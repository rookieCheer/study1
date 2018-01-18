<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/zlqEl.tld" prefix="myel" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>渠道费用表</title>
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
            width: auto;
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

        .main {
            position: relative;
        }

        .fixtop {
            position: fixed;
            width: 1500px;
            line-height: 30px;
            height: 30px;
            z-index: 99;
            background: #c1c1c1;
            display: none;
            left: 50%;
            margin-left: -750px;
            top: 0px;
            overflow: hidden;
        }

        .ct-table {
            width: 80%;
            margin-top: 20px;
            border: 1px solid #ccc;
            border-collapse: collapse;
            border-spacing: 0;
        }

        .ct-table thead {
            background: #c1c1c1;
        }

        .ct-table thead.fix {
            position: fixed;
            left: 50%;
            top: 0px;
            margin-left: -750px;
            width: 1500px;
        }

        .ct-table tbody {
            margin-top: 30px
        }

        .ct-table tbody.mt {
            margin-top: 30px;
        }

        .ct-table tr td {
            line-height: 30px;
        }

        .ct-table tbody > tr > td:nth-child(even) {
            background: #e2e2e2;
        }

        .ct-table tbody > tr > td:nth-child(odd) {
            background: #f9f9f9;
        }

        .link {
            color: blue;
        }
    </style>


</head>
<body>
<div class="center">
    <jsp:include page="/Product/Admin/common/head.jsp"/>
    <div class="main" align="center">
        <h3>渠道费用表</h3>
        <form id="frm" action="${pageContext.request.contextPath}/Product/Admin/activity!addAndroidChannelData.action">
            <span>渠道名称:</span> <input id="channelName" name="channelName" type="text" value="${channelName}">
            <span>查询期间:</span> <input id="insertTime" name="insertTime" type="text" value="${insertTime}">
            <select id="channelType" name="channelType">
                <c:choose>
                    <c:when test="${channelType == 1}">
                        <option value="1" selected="selected">平台渠道</option>
                        <option value="2">广告渠道</option>
                    </c:when>
                    <c:when test="${channelType == 2}">
                        <option value="1">平台渠道</option>
                        <option value="2" selected="selected">广告渠道</option>
                    </c:when>
                    <c:otherwise>
                        <option value="1">平台渠道</option>
                        <option value="2">广告渠道</option>
                    </c:otherwise>
                </c:choose>
            </select>
            <input type="submit" value="查询">
        </form>
        <div id="fixtop" class="fixtop"></div>
        <table id="tHead" class="ct-table" border="1" cellspacing="0" cellpadding="0" style="text-align: center;">
            <thead>
            <tr>
                <td>序号</td>
                <td>渠道名称</td>
                <td>渠道费用</td>
                <td>时间</td>
                <td>操作</td>
            </tr>
            </thead>
            <form id="frm1" action="${pageContext.request.contextPath}/Product/Admin/activity!updateQdtjData.action"
            onsubmit="submit()">
                <c:forEach items="${list}" var="item" varStatus="i">
                    <tr>
                        <td>${i.index+1}</td>
                        <td>${item[1]}</td>
                            <input type="hidden" name="qdtjlist[${i.index}].channelName" value="${item[1]}" >
                        <td>
                            <span class="channelVal"></span>
                            <input type="text" value="${item[2]}" name="qdtjlist[${i.index}].channelCost" id="ChannelCost">
                        </td>
                        <c:choose>
                            <c:when test="${item[3] == '0'}">
                                <td></td>
                            </c:when>
                            <c:otherwise>
                                <td><fmt:formatDate value="${item[3]}" pattern="yyyy-MM-dd" /></td>
                            </c:otherwise>
                        </c:choose>
                        <td>
                            <a href="javascript:update('${item[4]}');" class="link">修改费用</a>
                        </td>
                        <td>
                            <input type="hidden" id="qdtjTime"  name="qdtjlist[${i.index}].insertTime">
                        </td>
                    </tr>
                </c:forEach>
                <input type="submit" value="提交">
            </form>
        </table>
        <c:choose>
            <c:when test="${list ne '[]' &&  list ne '' && list ne null}">
                <jsp:include page="/Product/page.jsp"/>
            </c:when>
            <c:otherwise>
                <div style="text-align: center;margin-top: 15px;">
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
    });
</script>
<script type="application/javascript">

    //修改费用
    function update(qdtjId) {
        var id = $("#ChannelCost");
        window.location.href = "${pageContext.request.contextPath}/Product/Admin/activity!updateChannelData.action?qdtjId=" + qdtjId;
    };

    function submit(){
        var insetTime = $("#insertTime").val();
        $("#qdtjTime").val(insetTime);
        if(insetTime = null || insetTime==''){
            alert("请选择时间");
            return false;
        }
        return true;
    }
</script>
</body>
</html>