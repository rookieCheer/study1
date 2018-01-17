<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
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
<title>后台管理 - 补发兑换机会</title>
</head>
<body>
<div class="center">		
	<jsp:include page="/Product/Admin/common/head.jsp"/>	
	<div class="main" align="center">
		<h1 style="text-align: center;">补发兑换机会</h1>
			<form id="sendHongBaoForm" method="post">
				<table>
					<tr>
						<td>用户名:</td>
						<td><input id="username" type="text" name="username" maxlength="11"/></td>
					</tr>
					<tr>
						<td colspan="2" align="center"><span id="realName"></span></td>
					</tr>
					<tr>
						<td>补发次数:</td>
						<td><input id="freeNum" type="text" name="lotteryTimes.freeNum" maxlength="4"></td>
					</tr>
					
					<tr>
						<td>活动类型:</td>
						<td><input id="type" type="text" name="lotteryTimes.type"><font color="red">（1：微信抽奖 2端午活动 3六月热活动）</font></td>
					</tr>
					<tr>
						<td>活动备注:</td>
						<td><input id="note" type="text" name="note" ><font color="red">（如：获得粽子5个）</font></td>
					</tr>
					
					<tr style="text-align: center;">
						<td colspan="2"><input type="button" id="sendTimes" value="补发机会" > </td>
					</tr>
				</table>
			</form>
	</div>
</div>
	<script type="text/javascript">
	function checkHongbao(){
		
		var username=$("#username").val();
		var freeNum=$("#freeNum").val();
		var type=$("#type").val();
		var note=$("#note").val();
		//alert(username);
		if(isEmpty(username)){
			/* alert("请填写用户名"); */
			confirm("用户名不能为空");
			return false;
		}else if(isEmpty(freeNum)){
			alert("次数不能为空");
			return false;
		}else if(isEmpty(type)){
			alert("活动类型不能为空");
			return false;
		}else if(isEmpty(note)){
			alert("活动备注不能为空");
			return false;
		}
		if(!confirm("确定补发机会?")){
			return false;
		}
		return true;
	}
	
	$("#username").blur(function(){
		var username=$("#username").val();
		if(""==username){
			$("#username").select();
			alert("用户名不能为空");
			return false;
		}
		var url="${pageContext.request.contextPath}/Product/Admin/sendCoupon!getRealNameByUsername.action";
		$.ajax({
			type:"post",
			url:url,
			data:"username="+username,
			success:function(data){
				//alert(data.json);
				if(data.status=="ok"){
					$("#realName").text("真实姓名: "+data.json);
				}else{
					$("#realName").text("提示: "+data.json);
				}
			}
		});
	});
	
	
	$(document).ready(function(){
		$("#sendTimes").click(function(){
			if(!checkHongbao()){
				return false;
			}
			/* alert($("#sendHongBaoForm").serialize()); */
			var url="${pageContext.request.contextPath}/Product/Admin/bannerActivity!sendLotteryTimes.action";
			$.ajax({
				type:"post",
				url:url,
				data:$("#sendHongBaoForm").serialize(),
				success:function(data){
					alert(data.json);
					if(data.status=="ok"){
						window.location.reload();
					}
				}
			});
			
		});
	});

	
	</script>
</body>
</html>