<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/Product/images/favicon.ico" type="image/x-icon">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/artDialog4.1.7/artDialog.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/My97DatePicker/WdatePicker.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/qwyUtilJS/qwyUtil.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dropzone/dropzone.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/js/dropzone/dropzone.css">
    <link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css"/>
    <link href="${pageContext.request.contextPath}/Product/Admin/css/product_fabu_history.css" rel="stylesheet"
          type="text/css"/>
    <script src="${pageContext.request.contextPath}/Product/Admin/js/jquery-1.9.1.min.js"></script>
    <title>新华金典-图标列表</title>
</head>
<body>
<form action="${pageContext.request.contextPath}/Icons/icons!loadIcons.action" method="get" enctype="multipart/form-data">
    <div class="center">
        <jsp:include page="/Product/Admin/common/head.jsp"/>
        <div class="main" align="center">
            <h3>图标</h3>
            <div>
                模块名： <s:select name="icons.module" list="#{'a':'全部','0':'首页','1':'发现' }"/>
                </select>
                <input value="查询" type="submit">
                &nbsp;&nbsp;<a href="${pageContext.request.contextPath}/icons/addIcons.jsp" style="color: #0000cc">添加</a>
                &nbsp;&nbsp;<a href="${pageContext.request.contextPath}/Icons/icons!flushRedis.action" style="color: #0000cc">刷新缓存</a>
            </div>
            <table border="1"
                   style="text-align: center;table-layout:fixed;width: 1500px;word-break:break-all;">
                <tr>
                    <td width="50" align="center">ID</td>
                    <td width="80" align="center">图标名</td>
                    <td width="50" align="center">顺序号</td>
                    <td width="200" align="center">图标URL</td>
                    <td width="60" align="center">图标模块</td>
                    <td width="60" align="center">是否可点击</td>
                    <td width="100" align="center">不可点击提示信息</td>
                    <td width="60" align="center">是否停用</td>
                    <td width="60" align="center">是否H5</td>
                    <td width="120" align="center">H5地址</td>
                    <td width="60" align="center">是否需要登录</td>
                    <td width="80" align="center">创建人</td>
                    <td width="70" align="center">添加时间</td>
                    <td width="80" align="center">修改人</td>
                    <td width="70" align="center">修改时间</td>
                    <td width="100" align="center">描述</td>
                    <td width="80" align="center">操作</td>
                </tr>
                <c:forEach items="${iconsList}" var="item" varStatus="i">
                    <tr>
                        <td>${item.id}</td>
                        <td>${item.name}</td>
                        <td>${item.seq}</td>
                        <td>${item.iconURL}</td>
                        <td>
                            <c:choose>
                                <c:when test="${item.module eq '0'.charAt(0)}">
                                    首页
                                </c:when>
                                <c:when test="${item.module eq '1'.charAt(0)}">
                                    发现
                                </c:when>
                            </c:choose>
                        </td>
                        <td>
                                ${item.isClick eq '0'.charAt(0) ? "不可以" : "可以"}
                        </td>
                        <td>${item.iconMsg}</td>
                        <td>
                                ${item.status eq '0'.charAt(0) ? "停用" : "启用"}
                        </td>
                        <td>
                                ${item.type eq '0'.charAt(0) ? "H5" : "原生"}
                        </td>
                        <td>${item.h5URL}</td>
                        <td>
                                ${item.isLogin eq '0'.charAt(0) ? "不需要" : "需要"}
                        </td>
                        <td>${item.creator}</td>
                        <td>${item.createTime}</td>
                        <td>${item.modifier}</td>
                        <td>${item.updateTime}</td>
                        <td>${item.note}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/Icons/icons!toEditIcons.action?icons.id=${item.id}"
                               class="a" style="color: #0000cc">修改</a>
                            <a href="${pageContext.request.contextPath}/Icons/icons!editIcons.action?icons.id=${item.id}&delFlag=delete"
                               class="a" style="color: #0000cc">删除</a>
                        </td>
                    </tr>
                </c:forEach>

            </table>
            <c:choose>
                <c:when test="${iconsList ne '[]' &&  iconsList ne '' && iconsList ne null}">
                    <jsp:include page="/Product/page.jsp"/>
                </c:when>
                <c:otherwise>
                    <div style="text-align: center;margin-top: 15px;">
                        <img src="/Product/images/no_record.png"/>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</form>
</body>
</html>