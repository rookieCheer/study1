<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/zlqEl.tld" prefix="myel"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css" />
    <link href="${pageContext.request.contextPath}/Product/Admin/css/product.css" rel="stylesheet" type="text/css" />
    <script src="${pageContext.request.contextPath}/Product/Admin/js/jquery-1.9.1.min.js"></script>
    <script src="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.standalone.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.css" type="text/css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/Product/js/alert.js"></script>
    <script src="${pageContext.request.contextPath}/js/artDialog4.1.7/jquery.artDialog.js?skin=blue"></script>
    <title>提现记录</title>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.7.2.min.js"></script>
    <script type="text/javascript">
        function delRule(ruleId) {
            if(!confirm("确定要删除该规则吗?")){
                return false;
            }
            $.ajax({
                url: "${pageContext.request.contextPath}/Product/CouponReleaseRule/couponReleaseRule!delCouponRule.action?ruleId=" + ruleId,
                success: function (data) {
                    alert(data.json);
                    location.reload();
                }
            });
        }
        function modityRule(ruleId) {
            var url = '${pageContext.request.contextPath}/Product/CouponReleaseRule/couponReleaseRule!queryCouponRuleById.action?ruleId='+ruleId;
            var iWidth = '800';
            var iHeight = '600';
            var iTop = (window.screen.availHeight-30-iHeight)/2;       //获得窗口的垂直位置;
            var iLeft = (window.screen.availWidth-10-iWidth)/2;           //获得窗口的水平位置;
            myWindow  = window.open( url, 'child', 'height=' + iHeight + ',innerHeight=' + iHeight + ',width=' + iWidth + ',innerWidth=' + iWidth + ',top=' + iTop + ',left=' + iLeft + ', toolbar=no, menubar=no, scrollbars=no, resizable=no, location=no, status=no, titlebar=yes, alwaysRaised=yes');
        }
    </script>
</head>
<body>
<div class="center">
    <jsp:include page="/Product/Admin/common/head.jsp"/>
    <div class="main" align="center">
        <h1 style="text-align: center;">投资券规则列表</h1>
        <div id="div_condition" style="text-align: center;" >
            <%--<label>用户名:<input type="text" name="name" id="name" value="${name}" maxlength="11">--%>
                <%--<input type="button" value="search" id="delRule" onclick="Byname()"></label>--%>
        </div>

        <table  style="width:100%;margin-top: 20px;text-align: center;" border="1">
            <tr>
                <td width="5%">序号</td>
                <td width="7%">名称</td>
                <td width="5%">是否应用所用产品</td>
                <td width="5%">是否启用</td>
                <c:if test="${usersAdmin.id!=11020}">
                    <td width="5%">操作</td>
                </c:if>
            </tr>
            <c:forEach items="${pageUtil.list}" var="list" varStatus="i">
                <tr>
                    <td width="5%">${i.count + (pageUtil.currentPage-1)*pageUtil.pageSize}</td>
                    <td width="5%">${list.name}</td>
                    <td width="10%">${list.toAllProduct}</td>
                    <td width="5%">${list.isEnable}</td>
                    <td width="5%">
                        <a class="a" href="javascript:modityRule('${list.id}');">修改</a>
                        <c:if test="${list.canDelete}">
                             <a class="a" href="javascript:delRule('${list.id}');">删除</a>
                        </c:if>
                    </td>

                </tr>
            </c:forEach>
        </table>
    </div>
</div>
</body>
</html>