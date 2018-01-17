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
<title>充值记录</title>
<script type="text/javascript">
	function queryCz(requestId,type,insertTime){
		var url="";
		if(type=='3'||type=='4'){//连连支付
			url="${pageContext.request.contextPath}/Product/Admin/recharge!CzMoneyQuery.action?requestId="+requestId+"&type="+type+"&insertTime="+insertTime;
		}else{
			url="${pageContext.request.contextPath}/Product/Admin/recharge!CzMoneyArrive.action?requestId="+requestId+"&type="+type;
		}
		$.ajax({ 
			url: url,
			success: function(data){
	       		alert(data.json);
	      }});
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
	var interval = $("#insertTime").val();
	if(interval == null || interval == '' || interval.length == 0){
		alert("请选择要导出报表日期！");
		return false;
	}
	if (interval.indexOf("-") != -1){
		var startDate = interval.split("-")[0];
		var endDate = interval.split("-")[1];
		var startTime = new Date(Date.parse(startDate.replace(/-/g,   "/"))).getTime();
		var endTime = new Date(Date.parse(endDate.replace(/-/g,   "/"))).getTime();
		var dates = Math.abs((startTime - endTime))/(1000*60*60*24);
		if(31 - dates <= 0){
			alert("请选择日期间隔为31天的数据导出！")
			return false;
		}
		//alert(dates);
	}
	    var name=document.getElementById("name").value;
	    var insertTime=document.getElementById("insertTime").value;
	    var status =  $('input[name=status]:checked').val();
 		var url='${pageContext.request.contextPath}/Product/Admin/recharge!iportCzTable.action?currentPage=${currentPage}&name='+name+'&insertTime='+insertTime+'&status='+status;
 		var list="${czRecordList}";
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
    		data = '${pageContext.request.contextPath}'+data;
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
<%-- <jsp:include page="top.jsp" /> --%>
<div class="center">			
	<jsp:include page="/Product/Admin/common/head.jsp"/>
	<div class="main" align="center">
		<h1 style="text-align: center;">满标企业详情</h1>
		<div id="div_condition">
		<label>满标时间:<input type="text" name="insertTime" id="insertTime" value="" maxlength="11">
		<span>公司名称:</span> <input id="companyName" name="companyName" type="text" value="">
		<input type="button" value="search" id="frm" onclick=""></label>&nbsp;&nbsp;
		<!--label><input type="radio" id="" value="all" name="status" checked="checked">全部</label>&nbsp;&nbsp;
		<label><input type="radio" value="0" name="status">待充值</label>&nbsp;&nbsp;
		<label><input type="radio" value="1" name="status">充值成功</label>&nbsp;&nbsp;
		<label><input type="radio" value="2" name="status">充值失败</label>&nbsp;&nbsp;
		<label><input type="radio" value="3" name="status">宝付充值成功,数据库插入失败</label>&nbsp;&nbsp;
		<input type="button" value="导出报表" onclick="ireportDo()">
<!-- 		<select name="type">
			<option value="">全部</option>
			<option value="0">易宝</option>
			<option value="1">连连</option>
		</select> -->
		</div>

		<table style="width:100%;margin-top: 20px;text-align: center;" border="1px;">
			<tr>
				<td>序号</td>
				<td>借款公司</td>
				<td>法人</td>
				<td>借款额度(元)</td>
				<td>标的类型</td>
				<td>子标数目</td>
				<td>标的编号</td>
				<td>满标时间</td>
				<td>到期时间</td>
				<td>企业到期时间</td>
				<td>企业回款时间</td>
				<td>虚拟投资金额(元)</td>
				<td>实际投资金额(元)</td>
				
			</tr>
			<c:if test="${not empty czRecordList}">
			<c:forEach items="${czRecordList}" var="list" varStatus="i">
				<tr>
					<td width="5%" rowspan="4">${i.count + (pageUtil.currentPage-1)*pageUtil.pageSize}</td>
					<td width="6%" rowspan="4">
					${list.companyName}
					</td>
					<td width="3%" rowspan="4">${list.legalPerson}</td>
					<td width="3%" rowspan="4">${list.browLimit}</td>
					<td width="3%" rowspan="4">${list.type}</td>
					<td width="3%" rowspan="4">${list.childBidNumber}</td>
					
					
					   <!-- 四行一列  tr 行 td 列-->
					  <c:forEach items="${list.innerMessage}" var="innerList" varStatus="j">
					  
					     
					        <td>${innerList.number}</td>
					   
					    
					     
					    
					      <td>
					         <fmt:formatDate value="${innerList.expiringDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
					      </td>
					    
					      
					      
					      
					       <td>
					          <fmt:formatDate value="${innerList.fullTagDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
					       </td>
					     
					       
					       
					       
					       
					  </c:forEach>
				
				
					<td width="10%" rowspan="4"><fmt:formatDate value="${list.companyDueTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<td width="10%" rowspan="4"><fmt:formatDate value="${list.backMoneyTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				
				
                      <c:forEach items="${list.innerMessage}" var="innerList" varStatus="p">
                       
                          <td>
                             ${innerList.virtualInvest}
                          </td>
                         
                      </c:forEach>
                      
                   
                    <td width="10%" rowspan="4">${list.realInvest}</td>
				</tr>
			</c:forEach>
			</c:if>
		</table>
		
	</div>
</div> 
<div id="psi_load" style="display:none;">
<img width="18" src="${pageContext.request.contextPath}/Product/images/progress.gif"><span  id="psi_tip">正在导出,请稍等...</span>
</div>	
		<script type="text/javascript">
		$("#div_condition input[name='status']").click(function(){
			window.location.href="${pageContext.request.contextPath}/Product/Admin/recharge!rechargeMoneyRecord.action?name="+$("#name").val()+"&status="+$('input[name="status"]:checked').val()+"&insertTime="+$('#insertTime').val();
		});
			$("#div_condition input[name='status']").each(function(){
				if($(this).attr("value")=="${status}" || "${status}"==""){
					$(this).attr("checked",true);
				}
			});
			function Byname(){
				window.location.href="${pageContext.request.contextPath}/Product/Admin/recharge!rechargeMoneyRecord.action?name="+$("#name").val()+"&status="+$('input[name="status"]:checked').val()+"&insertTime="+$('#insertTime').val();
			}
		</script>
		<script type="text/javascript">
	var k4 = new Kalendae.Input("insertTime", {
		attachTo:document.body,
		months:2,//多少个月显示出来,即看到多少个日历
		mode:'range'
		/* selected:[Kalendae.moment().subtract({d:7}), Kalendae.moment().add({d:0})] */
	});
	//为input框绑定回车事件，当用户在文本框中输入搜索关键字时，按回车键，即可触发search():

	//回车绑定
	/* $("#name,#insertTime").keydown(function(event){
				if(event.keyCode==13){
					Byname();
				}
			}); */
</script>
</body>
</html>