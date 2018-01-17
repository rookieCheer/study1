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
    <title>企业用户名单</title>
    <style type="text/css">
        .a {
            color: blue;
            text-decoration: underline;
        }
    </style>
</head>
<body>
<%-- <jsp:include page="top.jsp" /> --%>
<div class="center">
    <jsp:include page="/Product/Admin/common/head.jsp"/>
    <div class="main" align="center">
        <h1 align="center">添加企业用户</h1>
        <table>
            <tr>
                <td>企业用户:</td>
                <td><input type="text" id="username_add" name="usersCompany.username" maxlength="11"
                           placeholder="请输入11位正确的手机号码" style="width: 200px"/></td>
            </tr>
            <tr>
                <td>备注内容</td>
                <td><textarea id="note_add" name="usersCompany.note" maxlength="500" cols="35" rows="6"
                              placeholder=""></textarea>
                    *目前长度：<span id="contentLenght">0</span>
                </td>
            </tr>
            <tr style="text-align: center;">
                <td colspan="2"><input type="button" onclick="submit()" value="提交"></td>
            </tr>
        </table>
        <h1 style="text-align: center;">企业用户名单</h1>
        <div id="div_condition">
            <label>企业用户:<input type="text" name="username" id="username" value="${username}" maxlength="11">
                <span>时间:</span> <input id="insertTime" name="insertTime" type="text" value="${insertTime}">
                <input type="button" value="search" id="frm" onclick="Byname()"></label>&nbsp;&nbsp;
        </div>

        <table style="width:100%;margin-top: 20px;text-align: center;" border="1px;">
            <tr>
                <td>序号</td>
                <td>企业用户</td>
                <td>用户类型</td>
                <td>用户状态</td>
                <td>手机号</td>
                <td>创建时间</td>
                <td>上次登录时间</td>
                <%--<td>是否在线</td>--%>
                <td>备注</td>
            </tr>
            <c:forEach items="${list}" var="list" varStatus="i">
                <tr>
                    <td>${i.count + (pageUtil.currentPage-1)*pageUtil.pageSize}</td>
                    <td>
                            ${myel:jieMiUsername(list.username)}
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${list.userType eq 0}">普通用户</c:when>
                            <c:when test="${list.userType eq -1}">超级管理员</c:when>
                        </c:choose>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${list.userType eq 0}">正常</c:when>
                            <c:otherwise>不正常</c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                            ${myel:jieMiUsername(list.phone)}
                    </td>
                    <td><fmt:formatDate value="${list.insertTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                    <td><fmt:formatDate value="${list.lastTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                   <%-- <td>
                            ${list.isOnline}
                    </td>--%>
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
    function Byname() {
        window.location.href = "${pageContext.request.contextPath}/Product/Admin/usersCompany!listUsersCompany.action?username=" + $("#username").val() + "&insertTime=" + $('#insertTime').val();
    }
</script>
<script type="text/javascript">
    var k4 = new Kalendae.Input("insertTime", {
        attachTo: document.body,
        months: 2,//多少个月显示出来,即看到多少个日历
        mode: 'range'
        /* selected:[Kalendae.moment().subtract({d:7}), Kalendae.moment().add({d:0})] */
    });

    //回车绑定
    $("#name,#insertTime").keydown(function (event) {
        if (event.keyCode == 13) {
            Byname();
        }
    });
</script>
<script>
    //提交添加
    function submit() {
        var username = $("#username_add").val();
        var note = $("#note_add").val();
        if (username == null || "" == username) {
            alert("手机号不能为空");
        }
        if (note == null || "" == note) {
            alert("填入备注内容！");
            return false;
        }
        var formData = "usersCompany.username=" + username;
        formData += "&usersCompany.note=" + note;
        var url = "${pageContext.request.contextPath}/Product/Admin/usersCompany!saveUsersCompany.action";
        $.post(url, formData, function (data) {
            if ("ok" == data.status) {
                alert(data.json);
                location.reload();
            } else if ("error" == data.status) {
                alert(data.json);
                return false;
            }
        });
    }
</script>
<script type="text/javascript">
    $(document).ready(function () {
        $("#note_add").change(function () {//发生改变时触发
            var note = $("#note_add").val();
            var contentLenght = note.length;
            $("#contentLenght").text(contentLenght);
        });
    });
</script>
</body>
</html>