<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
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
    <title>后台管理 - 投资券规则</title>
</head>
<body>
<div class="center">
    <div class="main" align="center">
        <h1 style="text-align: center;">新增投资券规则</h1>
        <form id="addRultForm" method="post">
            <table>
                <tr>
                    <td>类型:</td>
                    <td>
                        <select id="ruleType" name="ruleType">
                            <s:iterator value="#request.ruleTypeList" id="mycontent" status="status">
                                <option value="<s:property value="#mycontent.value"/>" ><s:property value="#mycontent.label"/></option>
                            </s:iterator>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>面值:</td>
                    <td><input id="value" type="text" name="value" maxlength="11"/></td>
                </tr>
                <tr>
                    <td colspan="2" align="center"><span id="realName"></span></td>
                </tr>
                <tr>
                    <td>面值类型</td>
                    <td>
                        <select id="valueType" name="valueType">
                            <option value="1" selected="selected">固定数值</option>
                            <option value="2" >根据产品额度计算</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>使用条件:</td>
                    <td><input type="text" id="conditionValue" name="conditionValue"  /></td>
                </tr>
                <tr>
                    <td>使用条件类型</td>
                    <td>
                        <select id="conditionType" name="conditionType">
                            <option value="1" selected="selected">固定数值</option>
                            <option value="2" >根据产品额度计算</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>产品的必须满足周期:</td>
                    <td><input type="text" id="requiredPeriod" name="requiredPeriod"  /></td>
                </tr>
                <tr>
                    <td>卷的过期天数:</td>
                    <td><input type="text" id="expireDay" name="expireDay"  /></td>
                </tr>
                <tr style="text-align: center;">
                    <td colspan="2"><input type="button" id="btnAddRule" value="提交" > </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<script type="text/javascript">
    $(document).ready(function(){

        $("#btnAddRule").click(function(){
            var ruleType = $("#ruleType").val();//类型
            var value = $("#value").val();//面值
            var valueType = $("#valueType").val();//类型
            var conditionValue = $("#conditionValue").val();//使用条件
            var conditionType = $("#conditionType").val();//使用条件
            var requiredPeriod = $("#requiredPeriod").val();
            var expireDay = $("#expireDay").val();

            var re = /^[0-9]+.?[0-9]*$/;
            if (!re.test(value)){
                alert("面值请输入数值。");
                return false;
            }
            if (!re.test(conditionValue)){
                alert("使用条件请输入数值。");
                return false;
            }
            if (!re.test(requiredPeriod)){
                alert("产品的必须满足周期请输入数值。");
                return false;
            }
            if (!re.test(expireDay)){
                alert("卷的过期天数请入数值。");
                return false;
            }


            //创建tr元素
            var trElemnet = document.createElement("tr");
            //创建td元素
            var td1Element = document.createElement("td");
            var td2Element = document.createElement("td");
            var td3Element = document.createElement("td");
            var td4Element = document.createElement("td");
            var td5Element = document.createElement("td");
            var td6Element = document.createElement("td");
            var td7Element = document.createElement("td");
            var td8Element = document.createElement("td");
            //将用户名和邮箱添加到td元素
            td1Element.innerHTML = $("#ruleType").find("option:selected").text()+"<input id='ruleType' type='hidden' value='"+ruleType+"'/>";
            td2Element.innerHTML = value;
            td3Element.innerHTML = $("#valueType").find("option:selected").text()+"<input id='valueType' type='hidden' value='"+valueType+"'/>";
            td4Element.innerHTML = conditionValue;
            td5Element.innerHTML = $("#conditionType").find("option:selected").text()+"<input id='conditionType' type='hidden' value='"+conditionType+"'/>";
            td6Element.innerHTML = requiredPeriod;
            td7Element.innerHTML = expireDay;

            //创建按钮
            var delElement = document.createElement("input");
            delElement.type="button";
            delElement.value="删除";
            //为按钮添加单击事件
            delElement.onclick=function(){
                //删除按钮所在的tr对象
                trElemnet.parentNode.removeChild(trElemnet);
            }
            td8Element.appendChild(delElement);
            //将td元素添加到tr元素中
            trElemnet.appendChild(td1Element);
            trElemnet.appendChild(td2Element);
            trElemnet.appendChild(td3Element);
            trElemnet.appendChild(td4Element);
            trElemnet.appendChild(td5Element);
            trElemnet.appendChild(td6Element);
            trElemnet.appendChild(td7Element);
            trElemnet.appendChild(td8Element);
            //将tr元素添加到tbody元素中
            window.opener.document.getElementById("tbodyID").appendChild(trElemnet);
            window.close();
        });
    });


</script>
</body>
</html>
