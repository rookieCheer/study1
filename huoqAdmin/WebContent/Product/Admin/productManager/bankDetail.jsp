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
<link href="${pageContext.request.contextPath}/Product/Admin/css/product_fabu_history.css" rel="stylesheet" type="text/css" />
<script src="${pageContext.request.contextPath}/Product/Admin/js/jquery-1.9.1.min.js"></script>
<script src="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.standalone.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.css" type="text/css">
<script type="text/javascript" src="${pageContext.request.contextPath}/Product/js/alert.js"></script>
<script src="${pageContext.request.contextPath}/js/artDialog4.1.7/jquery.artDialog.js?skin=blue"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/qwyUtilJS/qwyUtil.js"></script>

<style type="text/css">
.a {
  color: blue;
  text-decoration: underline;
}
table{border-collapse:collapse;empty-cells:show;}
table tr td{border:1px solid #555;}
</style>

<title>银行卡限额明细</title>
</head>
<body>
<div class="center">			
	<jsp:include page="/Product/Admin/common/head.jsp"/>
	<div class="main" align="center">
		<h1 style="text-align: center;">银行卡限额明细</h1>
		<br/>
		<c:choose>
			<c:when test="${list ne '[]' &&  list ne '' && list ne null}">
		<table cellpadding="0" cellspacing="0" style="text-align: center;" >
		<tr>
		<td width="100px" style="text-align: center;">序号</td>
		<td width="200px" style="text-align: center;">银行名称</td>
		<td width="200px" style="text-align: center;">银行编码</td>
		<td width="100px" style="text-align: center;">银行图标</td>
		<td width="100px" style="text-align: center;">是否支持此银行</td>
		<td width="100px" style="text-align: center;">提现单笔限额</td>
		<td width="100px" style="text-align: center;">充值单笔限额</td>
		<td width="300px" style="text-align: center;">备注</td>
		<td width="100px" style="text-align: center;">操作</td>
		
		</tr>
		<c:forEach items="${list}" var="item" varStatus="i">
		<tr>
		<td>${i.index+1}</td>
		<td>${item.bankName}</td> 
		<td>${item.bankCode}</td> 
		<td>${item.bankUri}</td>
		<td>
			<c:if test="${item.status eq 0}"><a class="a" href="javascript:update('${item.id}')">支持</a></c:if>
			<c:if test="${item.status eq 1}"><a class="a" href="javascript:update('${item.id}')">不支持</a></c:if>
		</td>
		<td><input class="dis_${item.id}" style="width:100%;" id="tx_${item.id}" readonly="readonly" disabled="disabled" value="${item.txQuota}" /></td>
		<td><input class="dis_${item.id}" style="width:100%;" id="cz_${item.id}" readonly="readonly" disabled="disabled" value="${item.czQuota}" /></td>
		<td><input class="dis_${item.id}" style="width:100%;" id="note_${item.id}" readonly="readonly" disabled="disabled" value="${item.bankNote}" /></td>
		<td><a id="modify_${item.id}"  href="javascript:toModifyBank(${item.id})" class="a">修改</a></td>
		
		</tr>
		</c:forEach>
		</table>
		</c:when>
			<c:otherwise>
				<div style="text-align: center;margin-top: 15px;"><!-- <img src="images/lh.jpg"> -->
			  		<img src="../images/no_record.png" />
			  	</div>
			</c:otherwise>
		</c:choose>
	</div>
</div> 
<script type="text/javascript">

$(function () {
    $("#status option[value='${status}']").attr("selected", true);
});  

	function Byname(){
		var stau = $("#status option:selected").val();
		var url="${pageContext.request.contextPath}/Product/Admin/moneyProblem!loadMoneyProblemUsers.action?status="+stau+"&name="+$('#name').val()+"&insertTime="+$('#insertTime').val();
		
		window.location.href=url;
	}
	
	/* 修改状态 */
	function update(id){
		if(!confirm("确定修改状态？")){
			return false;
		}
		$.ajax({
			type : "get",
			url : "${pageContext.request.contextPath}/Product/Admin/bank!updateStatus.action?id="+id,
			success : function(data) {
				if(data.status == 'ok'){
					alert(title+"成功");
					location.reload();
				}else{
					alert(title+"失败");
					return false;
				}
			}
		});
		
		location.reload();
		
	}
	
	
	//修改	
	function toModifyBank(id){
		var status=$("#modify_"+id).text();
		if("修改"==status){
			$("#modify_"+id).text("保存");
			$(".dis_"+id).removeAttr("disabled");
			$(".dis_"+id).removeAttr("readonly");
		}else{			
			var tx = $("#tx_"+id).val();
			var cz = $("#cz_"+id).val();
			var note = $("#note_"+id).val();
			if(!isPrice(tx)){
				alert("请输入正确的提现限额格式");
				return false;
			}
			
			if(!isPrice(cz)){
				alert("请输入正确的充值限额格式");
				return false;
			}
		
			
			var url = "${pageContext.request.contextPath}/Product/Admin/bank!modifyBank.action?";
 			var formData="bank.id="+id;
			    formData+="&bank.txQuota="+tx;
			    formData+="&bank.czQuota="+cz;
			    formData+="&bank.bankNote="+note;
			    
		if(confirm("是否确定保存修改？")){
			 $.post(url,formData,function(data){
				 alert(formData);
				 if(data.status=="ok"){
						//验证码发送成功，跳转到验证页面
						alert(data.json);
						$("#modify_"+id).text("修改");
						$(".dis_"+id).attr("disabled","disabled");
						$(".dis_"+id).attr("readonly","readonly");
						location.reload();
				 }else{
					 alert(data.json);
					 return false;
				 }
			 });
		 }	    

		}
	}

	var k4 = new Kalendae.Input("insertTime", {
		attachTo:document.body,
		months:2,//多少个月显示出来,即看到多少个日历
		mode:'range'
		/* selected:[Kalendae.moment().subtract({d:7}), Kalendae.moment().add({d:0})] */
	});
	
	//回车绑定
    $("#name,#insertTime").keydown(function (event) {
        if (event.keyCode == 13) {
            Byname();
        }
    });
</script>
</body>
</html>	