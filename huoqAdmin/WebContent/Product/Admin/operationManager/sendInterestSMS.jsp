<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="font-family" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>发送红包短信</title>
    <link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css"/>
    <link href="${pageContext.request.contextPath}/Product/Admin/css/product.css" rel="stylesheet" type="text/css"/>
    <script src="${pageContext.request.contextPath}/Product/Admin/js/jquery-1.9.1.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/qwyUtilJS/qwyUtil.js"></script>
<script src="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.standalone.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.css" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/My97DatePicker/WdatePicker.js"></script>
    <script type="text/javascript">
        //禁用或启用
        function send() {
            var users_groups = $("#users_groups").val();
            if($("#returnTime").val()==""){
            	alert("还款日期不能为空");
            	return false;
            }
            if($("#money").val()==""){
            	alert("理财券金额不能为空!");
            	return false;
            }
            if($("#note").val()==""){
            	alert("备注不能为空!");
            	return false;
            }
            var smsContent = $("#smsContent").val();
            if (smsContent == null || "" == smsContent) {
                alert("填入短信内容！");
                return false;
            }
            smsContent = smsContent.replace(/%/g, "％");
            var url = "${pageContext.request.contextPath}/Product/Admin/querybalance!sendHongbaoSMS.action";
            alert($("#formData").serialize());
            $.post(url, $("#formData").serialize(), function (data) {
                if ("ok" == data.status) {
                    alert(data.json);
                    location.reload();
                } else{
                    alert(data.json);
                    return false;
                }
            });
        }
    </script>
</head>
<body>
<%-- <jsp:include page="top.jsp" /> --%>
<div class="center">
    <jsp:include page="/Product/Admin/common/head.jsp"/>
     <form id="formData">
     <center><h1>发送理财券短信</h1></center><br><br><br>
    <div style="text-align: center">
        <span style="cord:black;font-size: 15px;">短信发送注意事项：</span>
        <span style="color: red">不要对同一手机号频繁测试，同一个号码10分钟内只能接收3条&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
         <div>
         <span>还款日期(要发理财券的用户): </span><input id="returnTime" type="text" name="returnTime" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" readonly="readonly"/>
      	<span>除去还款日期: </span><input id="exReturnTime" name="exReturnTime" type="text" value="${insertTime}" ><br>
      	<span>发放理财券金额(元): </span><input id="money" type="text" name="coupon" ><br>
      	<span style="color: red;">(ps:如果理财券是需要多张分开发,请用英文状态下的";"来进行隔开,如"2000;500",代表发两张理财券,一张2000元,一张500元)</span><br>
      	<span>红包到期时间: </span><input type="text" name="overTime" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" readonly="readonly"/>
      	<span>备注: </span><input id="note" type="text" name="note" >
      	<span style="color:red;">(ps:备注,比如"圣诞福利、抽奖发放、元旦福利等")</span>
         </div>
    </div>
   
        <div id="tab" align="center">
            <p><label>短信内容
            <%--一条短信67个长度,最前面默认追加'【新华金典理财】'--%>
            <textarea id="smsContent" name="smsRecord.smsContent" maxlength="500" cols="40" rows="10" placeholder="直接编辑内容即可"></textarea>
            </label><span style="color:red;">(ps:注意,如果短信内容中有百分号,请复制此百分 "％")</span></p>
            <input type="button" onclick="send()" value="发送">
        </div>
    </form>
</div>
</body>
<script type="text/javascript">
    $(document).ready(function () {
        $("#users_groups").val($("input[name='users_groups']:checked").val());
        $("input[name='users_groups']").click(function () {
           // alert($(this).val());
            if ($(this).val() != "special") {
                //alert("readonly");
                $("#mobile").hide();
                $("#mobile").val("");
            }
            if ($(this).val() == "special") {
                $("#mobile").show();
            }
            $("#users_groups").val($(this).val());
        });

        $("#smsContent").change(function () {//发生改变时触发
            var smsContent = $("#smsContent").val();
            var contentLenght = smsContent.length + 5;
            $("#contentLenght").text(contentLenght);
        });
    });
    
    var k4 = new Kalendae.Input("exReturnTime", {
		attachTo:document.body,
		months:2,//多少个月显示出来,即看到多少个日历
		mode:'range'
		/* selected:[Kalendae.moment().subtract({d:7}), Kalendae.moment().add({d:0})] */
	});
</script>
</html>