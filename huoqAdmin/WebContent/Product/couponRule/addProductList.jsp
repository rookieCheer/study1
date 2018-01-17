<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!doctype html>
<html>
<meta charset="utf-8" />
<title>产品记录</title>
<link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.css" type="text/css">
<link href="${pageContext.request.contextPath}/Product/Admin/css/product_fabu_history.css" rel="stylesheet" type="text/css" />
<script src="${pageContext.request.contextPath}/Product/Admin/js/jquery-1.9.1.min.js"></script>
<script src="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.standalone.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/Product/js/alert.js"></script>
<script src="${pageContext.request.contextPath}/js/artDialog4.1.7/jquery.artDialog.js?skin=blue"></script>
<style type="text/css">
    .a{
        color: blue;
        text-decoration: underline ;
    }
</style>
<script type="text/javascript">

</script>
</head>
<body>
<div class="center">
    <div class="main" align="center">
        <h3 align="center">新增产品</h3>
        <form action="${pageContext.request.contextPath}/Product/CouponReleaseRule/couponReleaseRule!redirectAddProduct.action">
            产品名称:<input type="text" name="product.title" id="title" value="${product.title}">
            到期时间:<input  type="text"  id="finishTime" name="finishTime" value="${finishTime}">
            <input type="submit" value="查询">
        </form>
        <div class="table">
            <button id="a1">add</button>
            <table >
                <tbody>
                <tr>
                    <td style="width: 30px;">序号</td>
                    <td style="width: 366px;">产品名称</td>
                    <td style="width: 58px;">产品类型</td>
                    <td style="width: 58px;">产品状态</td>
                    <td style="width: 58px;">理财期限</td>
                    <td style="width: 143px;">发布时间</td>
                </tr>
                <c:forEach items="${list}"  var="item" varStatus="i">
                    <tr <c:if test="${item.productStatus gt 0 && item.leftCopies ne 0}" >style="background: #ffff00;"</c:if>>
                        <td><input name="prodcbx" type="checkbox" value="${item.id},${item.title}"></td>
                        <td>${item.title}</td>
                        <td>${item.cplx}</td>
                        <td>
                                ${item.cpzt}
                        </td>
                        <td>
                                ${item.lcqx}
                        </td>
                        <td><fmt:formatDate value="${item.finishTime}" pattern="yyyy-MM-dd"/></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <c:choose>
                <c:when test="${list ne '[]' &&  list ne '' && list ne null}">
                    <jsp:include page="/Product/page.jsp" /></c:when>
                <c:otherwise>
                    <div style="text-align: center;margin-top: 15px;"><!-- <img src="images/lh.jpg"> -->
                        <img src="../images/no_record.png" />
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>
</body>
<script type="text/javascript">
    var k4 = new Kalendae.Input("finishTime", {
        attachTo:document.body,
        months:2,//多少个月显示出来,即看到多少个日历
        mode:'range'
    });
    $("#a1").click(function(){
        var obj = document.getElementsByName("prodcbx");
        for(k in obj){
            if(obj[k].checked){
                var val = obj[k].value;
                var prod = val.split(",");

                //创建tr元素
                var trElemnet = document.createElement("tr");
                //创建td元素
                var td1Element = document.createElement("td");
                var td2Element = document.createElement("td");
                td1Element.innerHTML = prod[1]+"<input id='ruleType' type='hidden' value='"+prod[0]+"'/>";
                //创建按钮
                var delElement = document.createElement("input");
                delElement.type="button";
                delElement.value="删除";
                //为按钮添加单击事件
                delElement.onclick=function(){
                    //删除按钮所在的tr对象
                    trElemnet.parentNode.removeChild(trElemnet);
                }
                td2Element.appendChild(delElement);
                //将td元素添加到tr元素中
                trElemnet.appendChild(td1Element);
                trElemnet.appendChild(td2Element);
                //将tr元素添加到tbody元素中
                window.opener.document.getElementById("tbodyProd").appendChild(trElemnet);
            }
        }
        window.close();
    })
</script>
</html>