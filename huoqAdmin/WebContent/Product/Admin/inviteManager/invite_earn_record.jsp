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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.css"
          type="text/css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/Product/js/alert.js"></script>
    <script src="${pageContext.request.contextPath}/js/artDialog4.1.7/jquery.artDialog.js?skin=blue"></script>
    <title>邀请投资奖励记录</title>
    <style type="text/css">
        .a {
            color: blue;
            text-decoration: underline;
        }
    </style>
    <script type="text/javascript">
        function ireportDo() {
            var inviter = document.getElementById("inviter").value;
            var insertTime = document.getElementById("insertTime").value;
            var status = $('input[name=status]:checked').val();
            var inviteId = $("#inviteId").val();
            //var url = '${pageContext.request.contextPath}/Product/Admin/inviteEarn!iportInviteEarnTable.action?currentPage=${currentPage}&inviter=' + inviter + '&insertTime=' + insertTime + '&status=' + status + "&inviteId=" + inviteId;
            var url = '${pageContext.request.contextPath}/Product/Admin/inviteEarn!iportTable.action?currentPage=${currentPage}&inviter=' + inviter + '&insertTime=' + insertTime + '&status=' + status + "&inviteId=" + inviteId;
            var list = "${list}";
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
<%-- <jsp:include page="top.jsp" /> --%>
<div class="center">
    <jsp:include page="/Product/Admin/common/head.jsp"/>
    <div class="main" align="center">
        <h1 style="text-align: center;">邀请投资奖励记录</h1>
        <div id="div_condition">
            <label>邀请人:<input type="text" name="inviter" id="inviter" value="${inviter}" maxlength="11">
                    <label>邀请人id:<input type="text" name="inviteId" id="inviteId" value="${inviteId}" maxlength="11">
                            <span>时间:</span> <input id="insertTime" name="insertTime" type="text" value="${insertTime}">
                            <input type="button" value="search" id="frm" onclick="Byname()"></label>&nbsp;&nbsp;
                        <label><input type="radio" id="" value="all" name="status" checked="checked">全部</label>&nbsp;&nbsp;
                        <label><input type="radio" value="0" name="status">未发放</label>&nbsp;&nbsp;
                        <label><input type="radio" value="1" name="status">已发放</label>&nbsp;&nbsp;
                        <input type="button" value="导出报表" onclick="ireportDo()">
        </div>

        <table style="width:100%;margin-top: 20px;text-align: center;" border="1px;">
            <tr>
                <td>序号</td>
                <td>邀请人id</td>
                <td>邀请人</td>
                <td>被邀请人id</td>
                <td>被邀请人手机号</td>
                <td>被邀请人投资份数</td>
                <td>奖励金额</td>
                <td>创建时间</td>
                <td>发放奖励时间</td>
                <td>状态</td>
                <td>备注</td>
            </tr>
            <c:forEach items="${list}" var="list" varStatus="i">
                <tr>
                    <td>${i.count + (pageUtil.currentPage-1)*pageUtil.pageSize}</td>
                    <td>${list.inviteId}</td>
                    <td>
                        ${myel:jieMiUsername(list.users.username)}
                    </td>
                    <td>
                        ${list.beInvitedId}
                    </td>
                    <td>
                        ${myel:jieMiUsername(list.beInvitePhone)}
                    </td>
                    <td>${list.copies}</td>
                    <td><fmt:formatNumber value="${list.earnMoney * 0.01}" pattern="#,##0.##"/></td>
                    <td><fmt:formatDate value="${list.insertTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                    <td><fmt:formatDate value="${list.returnTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                    <td>
                        <c:choose>
                            <c:when test="${list.status eq 0}">未发放</c:when>
                            <c:when test="${list.type eq 1}">已发放</c:when>
                        </c:choose>
                    </td>
                    <td>${list.note}</td>
                </tr>
            </c:forEach>
        </table>
        <c:choose>
            <c:when test="${list ne '[]' &&  list ne '' && list ne null}">
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
<div id="psi_load" style="display:none;">
    <img width="18" src="${pageContext.request.contextPath}/Product/images/progress.gif"><span
        id="psi_tip">正在导出,请稍等...</span>
</div>
<script type="text/javascript">
    $("#div_condition input[name='status']").click(function () {
        window.location.href = "${pageContext.request.contextPath}/Product/Admin/inviteEarn!inviteEarnRecords.action?inviter=" + $("#inviter").val()+"&inviteId="+$("#inviteId").val()+"&status=" + $('input[name="status"]:checked').val() + "&insertTime=" + $('#insertTime').val();
    });
    $("#div_condition input[name='status']").each(function () {
        if ($(this).attr("value") == "${status}" || "${status}" == "") {
            $(this).attr("checked", true);
        }
    });
    function Byname() {
        window.location.href = "${pageContext.request.contextPath}/Product/Admin/inviteEarn!inviteEarnRecords.action?inviter=" + $("#inviter").val()+"&inviteId="+$("#inviteId").val()+"&status=" + $('input[name="status"]:checked').val() + "&insertTime=" + $('#insertTime').val();
    }
</script>
<script type="text/javascript">
    var k4 = new Kalendae.Input("insertTime", {
        attachTo: document.body,
        months: 2,//多少个月显示出来,即看到多少个日历
        mode: 'range'
        /* selected:[Kalendae.moment().subtract({d:7}), Kalendae.moment().add({d:0})] */
    });
    //为input框绑定回车事件，当用户在文本框中输入搜索关键字时，按回车键，即可触发search():

    //回车绑定
    $("#name,#insertTime").keydown(function (event) {
        if (event.keyCode == 13) {
            Byname();
        }
    });
</script>
</body>
</html>