<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/zlqEl.tld" prefix="myel"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>购买情况统计表</title>
<link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css" />
<link href="${pageContext.request.contextPath}/Product/Admin/css/product_fabu_history.css" rel="stylesheet" type="text/css" />
<script src="${pageContext.request.contextPath}/Product/Admin/js/jquery-1.9.1.min.js"></script>
<script src="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.standalone.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.css" type="text/css">
<script src="${pageContext.request.contextPath}/js/artDialog4.1.7/jquery.artDialog.js?skin=blue"></script>
<style type="text/css">
.sereach {
	width: 200px;
	height: 32px;
	line-height: 32px;
	text-align: center;
	border: 1px solid #009DDA;
	border-radius: 5px;
}

.select1 {
	border-radius: 5px;
	border-color: #009DDA;
	margin-right: 5px;
}

.a {
	color: blue;
	text-decoration: underline;
}
</style>
<script type="text/javascript">
	function exportExcel() {
		
           var insertTime=$("#insertTime").val();
           var phone=$("#phone").val();
           var realname=$("#realname").val();
           var isnew=$("#isnew").val();
           var form = $("<form>");
        form.attr('target', 'iframe');
        form.attr('method', 'post');
        form.attr('action', 'userBuy!exportExcelBuyProductInfoList.action');
        var input1 = $('<input>');
        input1.attr('type', 'hidden');
        input1.attr('name', 'insertTime');
        input1.attr('value', insertTime);

        var input2 = $('<input>');
        input2.attr('type', 'hidden');
        input2.attr('name', 'phone');
        input2.attr('value', phone);
        var input3 = $('<input>');
        input3.attr('type', 'hidden');
        input3.attr('name', 'realname');
        input3.attr('value', realname);
        var input4 = $('<input>');
        input4.attr('type', 'hidden');
        input4.attr('name', 'isnew');
        input4.attr('value', isnew);

        var iframe = $("<iframe>")
        iframe.attr('id', 'iframe');
        iframe.attr('name', 'iframe');
        iframe.attr('src', 'about:blank');
        iframe.attr('style', 'display:none;');
        $('body').append(iframe);
        $('body').append(form);
        form.append(input1);
        form.append(input2);
        form.append(input3);
        form.append(input4);
        form.submit();
    }
	
	function queryProduct(){
		var insertTime=$("#insertTime").val();
		var phone=$("#phone").val();
		var isnew=$("#isnew").val();
		if (isnew != null) {
		var url = "${pageContext.request.contextPath}/Product/buyInfo/userBuy!productInfo.action?insertTime="+insertTime+"&phone="+phone+"&isnew="+isnew;
		}else{
		var url = "${pageContext.request.contextPath}/Product/buyInfo/userBuy!productInfo.action?insertTime="+insertTime+"&phone="+phone;
		}
		window.location.href=url;
	}
	function isInteger(obj) {
        if(!/^\d+$/.test(obj)){  
               return false; 
            }else{
                return true;
        }
    }
</script>
</head>
<body>
	<div class="center">
		<jsp:include page="/Product/Admin/common/head.jsp" />
		<div class="main" align="center">
			<h3>购买情况统计表</h3>
			<span>购买时间:</span> 
			<input id="insertTime" name="insertTime" type="text" value="${insertTime}"> 
			<span>手机号:</span>
			<input id="phone" name="phone" type="text" value="${phone}" > 
			<input type="hidden" value="${isnew}" id="isnew" name="isnew">
			<a class="sereach"href="javascript:queryProduct();" id="sereach">查询</a> 
			<input type="button" value="导出报表" onclick="exportExcel()">
			<table border="1" width="80%">
				<tr>
					<td>序号</td>
					<td>购买产品</td>
					<td>兑付日期倒计时</td>
					<td>购买金额(元)</td>
					<td>客户姓名</td>
					<td>好友</td>
					<td>所属省份</td>
					<td>所属城市</td>
					<td>手机</td>
					<td>购买日期</td>
					<td>兑付日期</td>
					<td>性别</td>
				</tr>
				<c:forEach items="${list}" var="mylist" varStatus="status">
					<tr>
						<td>${status.index + 1}</td>
						<td>${mylist.productName}</td>
						<td>${mylist.endTime}</td>
						<td>${mylist.inMoney/100}</td>
						<td>${mylist.realName}</td>
						<td>${mylist.friend}</td>
						<td>${mylist.province}</td>
                        <td>${mylist.city}</td>
						<td>${myel:jieMiUsername(mylist.phone)}</td>
						<td>${mylist.insterTime}</td>
                        <td>${mylist.finishTime}</td>
						<td>${mylist.gender}</td>
					</tr>
				</c:forEach>
			</table>
			<c:choose>
				<c:when
					test="${pageUtil.list ne '[]' &&  pageUtil.list ne '' && pageUtil.list ne null}"><jsp:include
						page="/Product/page.jsp" />
			跳转页面:<input id="goPage" name="goPage" type="text" value="${goPage}"
						style="width: 50px">
					<a class="sereach" href="javascript:goqueryProduct();">跳转</a>
				</c:when>
				<c:otherwise>
					<div style="text-align: center; margin-top: 15px;">
						<img src="../images/no_record.png" />
					</div>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
	<script type="text/javascript">
		var k4 = new Kalendae.Input("insertTime", {
			attachTo : document.body,
			months : 2,//多少个月显示出来,即看到多少个日历
			mode : 'range'
		/* selected:[Kalendae.moment().subtract({d:7}), Kalendae.moment().add({d:0})] */
		});
		$(function() {
			$("#isbindbank option[value='${isbindbank}']").attr("selected",
					true);
			$("#level option[value='${level}']").attr("selected", true);
		});
		
		function goqueryProduct(){
            var pageNum =$("#goPage").val();
            if(isInteger(pageNum)){
                pageNum =parseInt(pageNum);
                var allNum =$("#allNum").html();
                allNum = allNum.substr(0,allNum.length-1);//去除最后一个
                allNum = allNum.substr(1,allNum.length);//去除第一个字符
                
               
                allNum=parseInt(allNum);
               
                if(pageNum<0  ){
                    alert("请输入1-"+allNum+"之间的整数");
                    return false;
                }else if(pageNum>allNum){
                    alert("请输入1-"+allNum+"之间的整数");
                    return false;
                }
                var insertTime=$("#insertTime").val();
                var phone=$("#phone").val();
                var isnew=$("#isnew").val();
                if (isnew != null) {
                var url = "${pageContext.request.contextPath}/Product/buyInfo/userBuy!productInfo.action?insertTime="+insertTime+"&phone="+phone+"&isnew="+isnew+"&currentPage="+pageNum;
                }else{
                var url = "${pageContext.request.contextPath}/Product/buyInfo/userBuy!productInfo.action?insertTime="+insertTime+"&phone="+phone+"&currentPage="+pageNum;
                }
                window.location.href=url;
            }else{
                alert("页码不是整数,pageNum:"+pageNum);
                return false;
            }
            
        }
	</script>
</body>
</html>