<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>添加企业用户</title>
    <link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css"/>
    <link href="${pageContext.request.contextPath}/Product/Admin/css/product.css" rel="stylesheet" type="text/css"/>
    <script src="${pageContext.request.contextPath}/Product/Admin/js/jquery-1.9.1.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/qwyUtilJS/qwyUtil.js"></script>
    <script>
        function submit() {
            var username = $("#username").val();
            var note = $("#note").val();
            if (username == null || "" == username) {
                alert("手机号不能为空");
            }
            if (note == null || "" == note) {
                alert("填入备注内容！");
                return false;
            }
            var formData = "blackList.username=" + username;
            formData += "&blackList.note=" + note;
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
</head>
<body>
<div class="center">
    <jsp:include page="/Product/Admin/common/head.jsp"/>
    <h1 align="center">添加企业用户</h1>
    <div class="main" align="center">
        <form action="" onsubmit="return isSubmit();">
            <table>
                <tr>
                    <td>企业用户:</td>
                    <td><input type="text" id="username" name="blackList.username" maxlength="11"
                               placeholder="请输入11位正确的手机号码" style="width: 200px"/></td>
                </tr>
                <tr>
                    <%--<td>短信内容</td>--%>
                    <td>备注内容</td>
                    <td><textarea id="note" name="usersCompany.note" maxlength="500" cols="40" rows="8"
                                  placeholder=""></textarea>
                        *目前长度：<span id="contentLenght">0</span>
                    </td>
                </tr>
                <tr style="text-align: center;">
                    <td colspan="2"><input type="button" onclick="submit()" value="提交"></td>
                </tr>
            </table>
        </form>
    </div>
</div>
</body>
<script type="text/javascript">
    $(document).ready(function () {
        $("#note").change(function () {//发生改变时触发
            var note = $("#note").val();
            var contentLenght = note.length;
            $("#contentLenght").text(contentLenght);
        });
    });
</script>
</html>