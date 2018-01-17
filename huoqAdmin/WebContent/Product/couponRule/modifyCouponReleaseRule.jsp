<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/zlqEl.tld" prefix="myel"%>
<!-- <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd"> -->
<html>
<head><link rel="shortcut icon" href="${pageContext.request.contextPath}/Product/images/favicon.ico" type="image/x-icon">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.7.2.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/My97DatePicker/WdatePicker.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/qwyUtilJS/qwyUtil.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/Product/js/alert.js"></script>
    <link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css" />
    <link href="${pageContext.request.contextPath}/Product/Admin/css/product.css" rel="stylesheet" type="text/css" />
    <title>后台管理 - 修改投资券规则</title>
</head>
<body>
<div class="center">
    <jsp:include page="/Product/Admin/common/head.jsp"/>
    <div class="main" align="center">
        <h1 style="text-align: center;">修改投资券规则</h1>
        <form id="addRultForm" method="post">
            <table>
                <tr>
                    <td>规则名称:</td>
                    <td><input id="value" type="text" name="paramMap.name" value="${couponReleaseRule.name}" maxlength="11"/></td>
                    <input type="hidden" name="paramMap.id" value="${couponReleaseRule.id}"/>
                </tr>
                <tr>
                    <td>是否应用所有产品</td>
                    <td>
                        <s:if test="${couponReleaseRule.toAllProduct eq 'true'}">
                            <input type="radio" name="paramMap.toAllProduct" value="true" checked="checked" />是
                            <input type="radio" name="paramMap.toAllProduct" value="false" />否
                        </s:if>
                        <s:if test="${couponReleaseRule.toAllProduct eq 'false'}">
                            <input type="radio" name="paramMap.toAllProduct" value="true"  />是
                            <input type="radio" name="paramMap.toAllProduct" value="false" checked="checked"/>否
                        </s:if>
                    </td>
                </tr>
                <tr>
                    <td>是否启用</td>
                    <td>
                        <s:if test="${couponReleaseRule.isEnable eq 'true'}">
                            <input type="radio" name="paramMap.isEnable" value="true" checked="checked" />是
                            <input type="radio" name="paramMap.isEnable" value="false" />否
                        </s:if>
                        <s:if test="${couponReleaseRule.isEnable eq 'false'}">
                            <input type="radio" name="paramMap.isEnable" value="true"  />是
                            <input type="radio" name="paramMap.isEnable" value="false" checked="checked"/>否
                        </s:if>
                    </td>

                </tr>
                <tr>
                    <td>规则项:</td>
                    <td>
                        <input type="button" value="添加规则项" onclick="open_win('children','800','600')"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2"><%--<td colspan="2" align="center"><span id="realName"></span></td>--%>
                        <table id="tableID" border="1" >
                            <tr>
                                <th>类型</th>
                                <th>面值</th>
                                <th>面值类型</th>
                                <th>使用条件</th>
                                <th>使用条件类型</th>
                                <th>满足周期</th>
                                <th>过期天数</th>
                                <th>操作</th>
                            </tr>
                            <s:if test="${couponReleaseRule.ruleItems != null && couponReleaseRule.ruleItems.size() > 0 }">

                                <s:forEach items="${couponReleaseRule.ruleItems}" var="item">
                                    <tr>
                                        <td>${item.itemType}</td>
                                        <td>${item.value}</td>
                                        <td>
                                            <s:if test="${item.valueType eq '1'}">
                                                固定
                                            </s:if>
                                            <s:if test="${item.valueType eq '2'}">
                                                动态百分比计算
                                            </s:if>
                                        </td>
                                        <td><s:if test="${item.itemTypeValue != '5'}">${item.conditionValue}</s:if> </td>
                                        <td>
                                            <s:if test="${item.itemTypeValue != '5' && item.conditionType eq '1'}">
                                                固定
                                            </s:if>
                                            <s:if test="${item.itemTypeValue != '5' && item.conditionType eq '2'}">
                                                动态百分比计算
                                            </s:if>
                                        </td>
                                        <td><s:if test="${item.itemTypeValue != '5'}">${item.requiredPeriod}</s:if></td>
                                        <td><s:if test="${item.itemTypeValue != '5'}">${item.expireDay}</s:if></td>

                                        <td><a class="a" href="javascript:delRuleItem('${item.id}');">删除</a></td>
                                    </tr>
                                </s:forEach>
                            </s:if>
                            <tbody id="tbodyID">
                            </tbody>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td></td>
                    <td>
                        <div id="ruleItems"></div>
                    </td>
                </tr>
                <tr>
                    <td>产品选择:</td>
                    <td>
                        <input type="button" value="添加产品" onclick="prod_win('children','800','600')"/>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <table id="tableProd" border="1" >
                            <tr>
                                <th>产品名称</th>
                                <th>操作</th>
                            </tr>
                            <s:if test="${prodList.products != null && prodList.products.size() > 0 }">

                                <s:forEach items="${prodList.products}" var="item">
                                    <tr>
                                        <td>${item.title}</td>
                                        <td><a class="a" href="javascript:delRuleProd('${item.id}','${prodList.id}');">删除</a></td>
                                    </tr>
                                </s:forEach>

                            </s:if>
                            <tbody id="tbodyProd">
                            </tbody>
                        </table>
                    </td>
                </tr>
                <tr style="text-align: center;">
                    <td colspan="2"><input type="button" id="btnAddRule" value="提交" > </td>
                </tr>
            </table>
            <input type="hidden" name="paramMap.ruleItem" id="ruleItem"/>
            <input type="hidden" name="paramMap.productIds" id="productIds"/>
        </form>
    </div>
</div>
<script type="text/javascript">
    $(document).ready(function(){
        $("#btnAddRule").click(function(){

            var trList =document.getElementById("tbodyID").rows;
            var arr = [];


            console.log(trList);
            for (var i=0;i<trList.length;i++) {
                var obj = {
                    ruleType:'',
                    rule:{
                    }
                };
                var ruleType = trList[i].cells[0].childNodes[1].value;//类别
                var value = trList[i].cells[1].innerHTML;//金额
                var valueType = trList[i].cells[2].childNodes[1].value;//  面值类型
                var conditionValue = trList[i].cells[3].innerHTML;//  条件
                var conditionType = trList[i].cells[4].childNodes[1].value;//  条件类型
                var requiredPeriod = trList[i].cells[5].innerHTML;//  周期
                var expireDay = trList[i].cells[6].innerHTML;//  过期天数
                obj.ruleType = ruleType;
                obj.rule.value = value;
                obj.rule.valueType = valueType;
                obj.rule.conditionValue = conditionValue;
                obj.rule.conditionType = conditionType;
                obj.rule.requiredPeriod = requiredPeriod;
                obj.rule.expireDay = expireDay;
                arr.push(obj);
            }
            $("#ruleItem").val(JSON.stringify(arr));
            //产品
            var trProdList =document.getElementById("tbodyProd").rows;
            console.log(trProdList);
            var prodarr = [];
            for (var i=0;i<trProdList.length;i++) {
                var prodId =  trProdList[i].cells[0].childNodes[1].value;//金额
                prodarr.push(prodId);
            }
            $("#productIds").val(prodarr);

            var url="${pageContext.request.contextPath}/Product/CouponReleaseRule/couponReleaseRule!saveCouponRuleItem.action";
            $.ajax({
                type:"post",
                url:url,
                data:$("#addRultForm").serialize(),
//                data: value,
                success:function(data){
                    alert("成功");
                    if(data.status=="ok"){
                        window.location.reload();
                    }
                }
            });

        });
    });

    function delRuleItem(ruleId) {
        if(!confirm("确定要删除该规则吗?")){
            return false;
        }
        $.ajax({
            url: "${pageContext.request.contextPath}/Product/CouponReleaseRule/couponReleaseRule!delRuleItem.action?ruleId="+ruleId,
            success: function (data) {
                alert(data.json);
                location.reload();
            }
        });
    }
    function delRuleProd(prodId,ruleId) {
        if(!confirm("确定要删除该规则吗?")){
            return false;
        }
        $.ajax({
            url: "${pageContext.request.contextPath}/Product/CouponReleaseRule/couponReleaseRule!delRuleProd.action?prodId="+prodId+"&ruleId="+ruleId,
            success: function (data) {
                alert(data.json);
                location.reload();
            }
        });
    }

    function open_win(title, w, h) {
        var url = '${pageContext.request.contextPath}/Product/CouponReleaseRule/couponReleaseRule!redirectAddItem.action';
        var iWidth = w;
        var iHeight = h;
        var iTop = (window.screen.availHeight-30-iHeight)/2;       //获得窗口的垂直位置;
        var iLeft = (window.screen.availWidth-10-iWidth)/2;           //获得窗口的水平位置;
        myWindow  = window.open( url, title, 'height=' + iHeight + ',innerHeight=' + iHeight + ',width=' + iWidth + ',innerWidth=' + iWidth + ',top=' + iTop + ',left=' + iLeft + ', toolbar=no, menubar=no, scrollbars=no, resizable=no, location=no, status=no, titlebar=yes, alwaysRaised=yes');
    }

    function prod_win(title, w, h) {
        var url = '${pageContext.request.contextPath}/Product/CouponReleaseRule/couponReleaseRule!redirectAddProduct.action';
        var iWidth = w;
        var iHeight = h;
        var iTop = (window.screen.availHeight-30-iHeight)/2;       //获得窗口的垂直位置;
        var iLeft = (window.screen.availWidth-10-iWidth)/2;           //获得窗口的水平位置;
        myWindow  = window.open( url, title, 'height=' + iHeight + ',innerHeight=' + iHeight + ',width=' + iWidth + ',innerWidth=' + iWidth + ',top=' + iTop + ',left=' + iLeft + ', toolbar=no, menubar=no, scrollbars=no, resizable=no, location=no, status=no, titlebar=yes, alwaysRaised=yes');
    }


</script>
</body>
</html>
