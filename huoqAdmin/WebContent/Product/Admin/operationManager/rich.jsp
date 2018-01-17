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
<style type="text/css">
.a {
  color: blue;
  text-decoration: underline;
}
table{border-collapse:collapse;empty-cells:show;}
table tr td{border:1px solid #555;}
</style>

<title>土豪星球被邀请者投资情况</title>

	<script type="text/javascript">
        function ireportDo() {
            var list = '${table}';
            if (list != '1') {
                alert("无数据");
                return false;
            }
            var url = '${pageContext.request.contextPath}/Product/Admin/bannerActivity!iportRichData.action?';
            var list = "${list}";
            var my = art.dialog({
                title: '提示',
                content: document.getElementById("psi_load"),
                height: 60,
                lock: true,
                cancel: false,
            });
            
            $.post(url, $("#frm").serialize(), function (data) {
                my.close();
                data = '${pageContext.request.contextPath}'+data;
                var ssss = "导出成功&nbsp;&nbsp;&nbsp;&nbsp;<a href='" + data + "' style='color:red;'>点击下载</a>";
                art.dialog({
                    title: '提示',
                    content: ssss,
                    height: 60,
                    lock: true,
                    ok: function () {
                        //mysss.close();
                    },
                });
            });

        }
    </script>

<script type="text/javascript">
function Byname(){
	var insertTime = $("#insertTime").val();
	
	var url="${pageContext.request.contextPath}/Product/Admin/bannerActivity!getRichData.action?insertTime="+insertTime;
		
	window.location.href=url;
	}
</script>


</head>
<body>
<div class="center">			
	<jsp:include page="/Product/Admin/common/head.jsp"/>
	<div class="main" align="center">
		<h3 style="text-align: center;">土豪星球被邀请者投资情况</h3><br/>
		<div id="div_condition" style="text-align: center;" >
		</div>
		 <form class="form" id="frm" method="post">
		<span>注册时间:</span> <input id="insertTime" name="insertTime" type="text" value="${insertTime}"/>
		
		<a class="sereach" href="javascript:Byname();" id="sereach">查询</a>
		<input class="input-button" type="button" value="导出报表" onclick="ireportDo()"><br/><br/>
		</form>
		<table  cellpadding="0" cellspacing="0" >
		
		<tr>
		<td width="100px" style="text-align: center;">序号</td>
		<td width="100px" style="text-align: center;">被邀请者</td>
		<td width="100px" style="text-align: center;">产品名称</td>
		<td width="100px" style="text-align: center;">投入金额</td>
		<td width="100px" style="text-align: center;">投资券</td>
		<td width="100px" style="text-align: center;">红包</td>
		<td width="100px" style="text-align: center;">订单生成时间</td>
		<td width="100px" style="text-align: center;">注册时间</td>
		<td width="100px" style="text-align: center;">邀请者</td>
		</tr>
		
		<c:forEach items="${list}" var="item" varStatus="i">
		
		<tr>
		<td width="100px" style="text-align: center;">${i.count + (pageUtil.currentPage-1)*pageUtil.pageSize}</td>
		<td width="300px" style="text-align: center;"><a class="a"  href="${pageContext.request.contextPath}/Product/Admin/userStat!loadUserInfo.action?username=${myel:jieMiUsername(item[0])}">${myel:jieMiUsername(item[0])}</a></td>
		<td width="200px" style="text-align: center;">
			<c:if test="${item[1] eq null}">--</c:if>
			<c:if test="${item[1] ne null}">${item[1] }</c:if>
		</td>
		<td width="200px" style="text-align: center;">
			<c:if test="${item[3] eq null}">--</c:if>
			<c:if test="${item[3] ne null}"><fmt:formatNumber value="${item[3]}" pattern="#,##0"/></c:if>
		</td>
		<td width="200px" style="text-align: center;">
			<c:if test="${item[4] eq null}">--</c:if>
			<c:if test="${item[4] ne null}"><fmt:formatNumber value="${item[4]}" pattern="#,##0"/></c:if>
		</td>
		<td width="200px" style="text-align: center;">
			<c:if test="${item[5] eq null}">--</c:if>
			<c:if test="${item[5] ne null}"><fmt:formatNumber value="${item[5]}" pattern="#,##0"/></c:if>
		</td>
		<td width="200px" style="text-align: center;">
			<c:if test="${item[6] eq null}">--</c:if>
			<c:if test="${item[6] ne null}">${item[6]}</c:if>
		</td>
		<td width="200px" style="text-align: center;">
			<c:if test="${item[7] eq null}">--</c:if>
			<c:if test="${item[7] ne null}">${item[7]}</c:if>
		</td>
		<td width="300px" style="text-align: center;"><a class="a"  href="${pageContext.request.contextPath}/Product/Admin/userStat!loadUserInfo.action?username=${myel:jieMiUsername(item[2])}">${myel:jieMiUsername(item[2])}</a></td>
		</tr>
		</c:forEach>
		</table>
		<c:choose>
			<c:when test="${list ne '[]' && list ne '' && list ne null}"><jsp:include page="/Product/page.jsp" /></c:when>
			<c:otherwise>
				<div style="text-align: center;margin-top: 15px;"><!-- <img src="images/lh.jpg"> -->
			  		<img src="../images/no_record.png" />
			  	</div>
			</c:otherwise>
		</c:choose>
	</div>
</div> 

<script type="text/javascript">
	var k4 = new Kalendae.Input("insertTime", {
		attachTo:document.body,
		months:1,//多少个月显示出来,即看到多少个日历
		mode:'range'
		/* selected:[Kalendae.moment().subtract({d:7}), Kalendae.moment().add({d:0})] */
	});
	
	$(function(){
		$("#insertTime").keydown(function(event){
			if(event.keyCode==13){
				Byname(); 
			}
		});
		
	});
	
</script>
</body>
</html>	