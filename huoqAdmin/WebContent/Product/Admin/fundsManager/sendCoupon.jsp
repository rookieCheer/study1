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
<script src="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.standalone.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.css" type="text/css">
<link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css" />
<link href="${pageContext.request.contextPath}/Product/Admin/css/product.css" rel="stylesheet" type="text/css" />
<title>后台管理 - 活动发送奖励券</title>
</head>
<body>
<div class="center">		
	<jsp:include page="/Product/Admin/common/head.jsp"/>	
	<div class="main" align="center">
		<h1 style="text-align: center;">活动发送奖励券</h1>
			<form id="sendHongBaoForm" method="post">
				<table>
					<tr>
						<td>类型:</td>
						<td>
							<select id="type" name="type">
								<option value="1" selected="selected">投资</option>
								<option value="2">充值</option>
							</select>
						</td>
					</tr>
					<tr>
						<td>活动时间:</td>
						<td><input type="text" name="activityTime" id="activityTime" />(日期)</td>
					</tr>
					<tr>
						<td>满足条件:</td>
						<td><input type="text" name="atLeast" id="atLeast" />(元)</td>
					</tr>
					<tr id = "isIncludes">
						<td>是否包含理财券:</td>
						<td>
							<select id="isInclude" name="isInclude">
								<option value="1" selected="selected">包含</option>
								<option value="2">纯本金</option>
							</select>
						</td>
					</tr>
					<tr>
						<td>是否累计:</td>
						<td>
							<select id="isSum" name="isSum">
								<option value="1" selected="selected">累计总额</option>
								<option value="2">单笔本金</option>
							</select>
						</td>
					</tr>
					<tr id="isIncludeProduct">
						<td>是否不包含某类产品:</td>
						<td><input type="text" name="isIncludePro" id="isIncludePro" /></td>
					</tr>
					<tr>
						<td>发放金额:</td>
						<td><input id="money" type="text" name="con.money" maxlength="20">(元)</td>
					</tr>
					
					<tr>
						<td>红包有效期：</td>
						<td><input type="text" name="overTime" id = "overTime"/>(天)</td>
					</tr>
					<tr>
						<td>红包类型</td>
						<td id = "couponType">
						<select name="con.type">
						<!-- <option value="1" >新手投资券</option> -->
						<option value="0" selected="selected">理财券</option>
						<option value="1" >新手理财券</option>
						<option value="2" >红包</option>
						<option value="3" >新手红包</option>
						</select>
						</td>
					</tr>
					<tr>
						<td>备注</td>
						<td><input id="note" type="text" name="con.note"> </td>
					</tr>
					<tr>
						<td>使用范围</td>
						<td><input id="useRange" type="text" name="con.useRange">
						</td>
					</tr>
					<tr><span style="color: red">* 使用范围为空则默认为：用于全部产品（新手标、零钱罐除外）</span></tr>
					<tr style="text-align: center;">
						<td colspan="2"><input type="button" id="btnSendHongBao" value="发放红包" > </td>
					</tr>
				</table>
			</form>
	</div>
</div>
	<script type="text/javascript">
	function checkHongbao(){
		if(!confirm("确定发送投资券?")){
			return false;
		}
		
		var type = $("#type option:selected").val();
		var isInclude = $("#isInclude option:selected").val();
		var couponType = $("#couponType option:selected").val();
		var money=$("#money").val();
		var note=$("#note").val();
		var activityTime = $("#activityTime").val();
		var atLeast = $("#atLeast").val();
		var isSum = $("#isSum").val();
		var overTime = $("#overTime").val();
		
		/* 活动类型为投资时  是否包含理财券不能为空 */
		if(1 == type){
			if(isEmpty(isInclude)){
				alert("请选择是否包含理财券");
				return false;
			}			
		}
		/* 活动类型为投资时  是否包含理财券不能为空 */
		
		
		if(isEmpty(money)){
			alert("金额格式错误");
			return false;
		}else if(isEmpty(note)){
			alert("备注不能为空");
			return false;
		}else if(isNaN(money)){
			alert("金额格式错误");
			return false;
		}else if(!(/(^[1-9]\d*$)/.test(money))){
			alert("金额格式错误");
			return false;
		}else if(isEmpty(type)){
			alert("活动类型不能为空");
			return false;
		}else if(isEmpty(activityTime)){
			alert("活动时间不能为空");
			return false;
		}else if(isEmpty(atLeast)){
			alert("活动目标金额不能为空");
			return false;
		}else if(isEmpty(isSum)){
			alert("请选择是否累计");
			return false;
		}else if(isEmpty(overTime)){
			alert("券的有效期不能为空");
			return false;
		}else if(isEmpty(couponType)){
			alert("券的类型不能为空");
			return false;
		}else{
			return true;
		}
		
	}
	
	
	$(document).ready(function(){
		$("#btnSendHongBao").click(function(){
			if(!checkHongbao()){
				return false;
			}
			/* alert($("#sendHongBaoForm").serialize()); */
			var url="${pageContext.request.contextPath}/Product/Admin/sendActivityReward!sendReward.action";
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
	
	<!-- 活动类型的判断显示 充值时 不显示 是否包含理财券和产品类型的选择项     start    -->
	<script type="text/javascript">
	 $(document).ready(function () {
		 $("#type").change(function (){
			 var type = $("#type option:selected").val();
			 if(type == 2){
				 $("#isIncludes").hide();
				 $("#isIncludeProduct").hide();
			 }else if(type == 1){
				 $("#isIncludes").show();
				 $("#isIncludeProduct").show();
			 }
		 })
	 });
	</script>
	<!-- 活动类型的判断显示 充值时 不显示 是否包含理财券和产品类型的选择项     end    -->
	
	
<script type="text/javascript">
	var k4 = new Kalendae.Input("activityTime", {
		attachTo:document.body,
		months:2,//多少个月显示出来,即看到多少个日历
		mode:'range'
		/* selected:[Kalendae.moment().subtract({d:7}), Kalendae.moment().add({d:0})] */
	});	
</script>
</body>
</html>
