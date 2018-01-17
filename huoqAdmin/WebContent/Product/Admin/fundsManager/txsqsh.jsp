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
<title>提现记录审核</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.7.2.min.js"></script>
<script type="text/javascript">
	function requestTx(txId,txStatus){
		if(txStatus=='0'){
			$.ajax({ 
				url: "${pageContext.request.contextPath}/Product/Admin/checkTxsq!requestTx.action?txId="+txId,
				success: function(data){
		       		alert(data.json);
		       		location.reload();
		      }});
		}else{
			alert("该记录已被操作");
			return false;
		}
	}
	//人工审核提现记录
	function shenhe(txRecordId, status) {
		if(status == '1'){
			if(!confirm("确定审核通过？")){
				return false;
			}
		}else if(status == '2'){
			if(!confirm("确定审核不通过？")){
				return false;
			}
		}
		$.ajax({
			url: "${pageContext.request.contextPath}/Product/Admin/checkTxsq!TxMoneySH.action?txRecordId=" + txRecordId + "&status=" + status,
			success: function (data) {
				alert(data.json);
				location.reload();
			}
		});
	}
</script>
<style type="text/css">
.a{
		color: blue;
		text-decoration: underline ;
	}
</style>
<script type="text/javascript">
function ireportDo(){
    var name=document.getElementById("name").value;
    var insertTime=document.getElementById("insertTime").value;
    var status =  $('input[name=status]:checked').val();
 		var url='${pageContext.request.contextPath}/Product/Admin/recharge!iportTxTable.action?currentPage=${currentPage}&name='+name+'&insertTime='+insertTime+'&status='+status;
 		var list="${txRecordList}";
 		if(list!=null&&list!="[]"){
    	var my= art.dialog({	
    	    title: '提示',
    	    content:document.getElementById("psi_load"),
    	    height: 60,
    	    lock: true,
    	    cancel: false
    	});  
    	$.post(url,$("#frm").serialize(),function(data){			
    		my.close();
    			var ssss="导出成功&nbsp;&nbsp;&nbsp;&nbsp;<a href='"+data+"' style='color:red;'>点击下载</a>";
           		art.dialog({
       		    title: '提示',
       		    content:ssss,
       		    height: 60,
       		    lock: true,
       		    ok:function(){
       		    	//mysss.close();
       		    }
       		});   
    	});
  }
}
</script> 
</head>
<body>
<div class="center">			
	<jsp:include page="/Product/Admin/common/head.jsp"/>
	<div class="main" align="center">
		<h1 style="text-align: center;">用户提现记录审核</h1>
		<div id="div_condition" style="text-align: center;" >
		<label>用户名:<input type="text" name="name" id="name" value="${name}" maxlength="11">
		<span>提现时间:</span> <input id="insertTime" name="insertTime" type="text" value="${insertTime}">
		<input type="button" value="search" id="frm" onclick="Byname()"></label>&nbsp;&nbsp;
		<label><input type="radio" value="all" name="status" checked="checked">全部</label>&nbsp;&nbsp;
		<label><input type="radio" value="0" name="status">待审核</label>&nbsp;&nbsp;
		<label><input type="radio" value="3" name="status">审核中</label>&nbsp;&nbsp;
		<label><input type="radio" value="1" name="status">审核通过</label>&nbsp;&nbsp;
		<label><input type="radio" value="2" name="status">审核失败</label>&nbsp;&nbsp;
		<input type="button" value="导出报表" onclick="ireportDo()">
		</div>
		
		<table  style="width:100%;margin-top: 20px;text-align: center;">
		<tr>
		<td>序号</td>
		<td>流水号</td>
		<td>用户名</td>
		<td>提现金额(元)</td>
		<td>姓名</td>
		<td>申请提现时间</td>
		<td>审核提现时间</td>
		<td>提现类型</td>
		<td>平台订单号</td>
		<td>交易流水号</td>
		<td>提现状态</td>
		<td>备注</td>
		<td>提现方式<td>
		<td>操作</td>
		</tr>
		<c:forEach items="${txRecordList}" var="list" varStatus="i">
		<tr>
		<td>${i.count + (pageUtil.currentPage-1)*pageUtil.pageSize}</td>
		<td>${list.recordNumber}</td>
		<td><a class="a"  href="${pageContext.request.contextPath}/Product/Admin/userStat!loadUserInfo.action?username=${myel:jieMiUsername(list.users.username)}">${myel:jieMiUsername(list.users.username)}</a></td> 
		<td><fmt:formatNumber value="${list.money * 0.01}" pattern="#,##0.##"/></td>
		<td>
		${list.users.usersInfo.realName}
		</td>
		<td><fmt:formatDate value="${list.insertTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
		<td><fmt:formatDate value="${list.checkTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
		<td>
		<c:if test="${list.drawType eq 0}">T+0</c:if>
		<c:if test="${list.drawType eq 1}">T+1</c:if>
		</td>
		<td>${list.requestId}</td>
		<td>${list.ybOrderId}</td>
		<td>${list.txzt}</td>
		<td>${list.note}</td>
	    <td>
		    <c:choose>
		    	<c:when test="${list.type eq '2'}">连连提现</c:when>
		    	<c:otherwise>易宝提现</c:otherwise>
		    </c:choose>
		</td>
		<td>
			<c:if test="${list.status eq '0'}">
				<a class="a" href="javascript:shenhe('${list.id}','1');">审核通过</a>
				<a class="a" href="javascript:shenhe('${list.id}','2');">审核不通过</a>
			</c:if>
		</td>
		</tr>
		</c:forEach>
		</table>
		<c:choose>
			<c:when test="${txRecordList ne '[]' &&  txRecordList ne '' && txRecordList ne null}"><jsp:include page="/Product/page.jsp" /></c:when>
			<c:otherwise>
				<div style="text-align: center;margin-top: 15px;"><!-- <img src="images/lh.jpg"> -->
			  		<img src="../images/no_record.png" />
			  	</div>
			</c:otherwise>
		</c:choose>
	</div>
</div> 
<script type="text/javascript">
$("#div_condition input[name='status']").click(function(){
	window.location.href="${pageContext.request.contextPath}/Product/Admin/checkTxsq!loadTxsqToCheck.action?name="+$("#name").val()+"&status="+$('input[name="status"]:checked').val()+"&insertTime="+$('#insertTime').val();
});

	$("#div_condition input[name='status']").each(function(){
		if($(this).attr("value")=="${status}" || "${status}"==""){
			$(this).attr("checked",true);
		}
	});
	function Byname(){
		window.location.href="${pageContext.request.contextPath}/Product/Admin/checkTxsq!loadTxsqToCheck.action?name="+$("#name").val()+"&status="+$('input[name="status"]:checked').val()+"&insertTime="+$('#insertTime').val();
	}
</script>
<script type="text/javascript">
	var k4 = new Kalendae.Input("insertTime", {
		attachTo:document.body,
		months:2,//多少个月显示出来,即看到多少个日历
		mode:'range'
		/* selected:[Kalendae.moment().subtract({d:7}), Kalendae.moment().add({d:0})] */
	});
	//回车绑定
	$("#name,#insertTime").keydown(function(event){
				if(event.keyCode==13){
					Byname();
				}
			});
</script>
</body>
</html>	