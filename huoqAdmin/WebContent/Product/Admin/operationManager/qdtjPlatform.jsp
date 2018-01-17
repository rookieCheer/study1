<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/zlqEl.tld" prefix="myel" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>各平台渠道统计</title>
    <link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css"/>
    <link href="${pageContext.request.contextPath}/Product/Admin/css/product_fabu_history.css" rel="stylesheet"
          type="text/css"/>
    <script src="${pageContext.request.contextPath}/Product/Admin/js/jquery-1.9.1.min.js"></script>
    <script src="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.standalone.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.css"
          type="text/css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/Product/js/alert.js"></script>
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

        .main {
            position: relative;
        }

        .fixtop {
            position: fixed;
            width: 1500px;
            line-height: 30px;
            height: 30px;
            z-index: 99;
            background: #c1c1c1;
            display: none;
            left: 50%;
            margin-left: -750px;
            top: 0px;
            overflow: hidden;
        }

        .ct-table {
            border: 1px solid #ccc;
            border-collapse: collapse;
            border-spacing: 0;
        }

        .ct-table thead {
            background: #c1c1c1;
        }

        .ct-table thead.fix {
            position: fixed;
            left: 50%;
            top: 0px;
            margin-left: -750px;
            width: 1500px;
        }

        .ct-table tbody {
            margin-top: 30px
        }

        .ct-table tbody.mt {
            margin-top: 30px;
        }

        .ct-table tr td {
            line-height: 30px;
        }

        .ct-table tbody > tr > td:nth-child(even) {
            background: #e2e2e2;
        }

        .ct-table tbody > tr > td:nth-child(odd) {
            background: #f9f9f9;
        }

        .h3-title {
        }

        .form {
            line-height: 30px;
            font-size: 14px;
        }

        .input-text {
            width: 200px;
            height: 24px;
            line-height: normal;
        }

        .input-button {
            height: 25px;
            line-height: 25px;
            text-align: center;
            padding: 0px 15px;
        }

        .mes-div {
            line-height: 30px;
            padding: 0px 20px;
            margin-top: 10px;
            text-align: left;
        }

        .mes-div .span {
            margin: 0px 50px 0px 0px;
            font-size: 14px;
        }
    </style>
    <script type="text/javascript">
        function ireportDo() {
            var list = '${table}';
            if (list != '1') {
                alert("无数据");
                return false;
            }
            var url = '${pageContext.request.contextPath}/Product/Admin/qdtjPlatform!iportQdtjPlatform.action?';
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
        $(function () {
            var hTop = $("#tHead").offset().top - 30;
            $("#fixtop").append($("#tHead").clone());
            $(window).scroll(function () {
                var scrollTop = $(window).scrollTop();
                if (scrollTop > hTop) {
                    $("#fixtop").show();
                } else {
                    $("#fixtop").hide();
                }
            })
        })
    </script>
    
<script type="text/javascript">

	/* $(function () {
	   $("#platform option[value='${platform}']").attr("selected", true);
	}); 

	function Byname(){
		
		var platform = $("#platform option:selected").val();
		var insertTime = $("#insertTime").val();
		window.location.href="${pageContext.request.contextPath}/Product/Admin/qdtjPlatform!loadQdtjPlatform.action?insertTime="+insertTime+"&platform="+platform;
	} */
	var platform = $("#platform option:selected").val();
	var insertTime = $("#insertTime").text();
	 $(function () {
    	$("#platform option[value='${platform}']").attr("selected", true);
	});  

	$(function(){
		$("#search").click(function(){
		window.location.href="${pageContext.request.contextPath}/Product/Admin/qdtjPlatform!loadQdtjPlatform.action?insertTime="+insertTime+"&platform="+platform;
	}); 
	
});

</script>
</head>
<body>
<div class="center">
    <jsp:include page="/Product/Admin/common/head.jsp"/>
    <div class="main" align="center">
        <h3 class="h3-title">各平台渠道统计</h3>
        <form class="form" id="frm" method="post">
            <span>查询日期:</span> <input id="insertTime" name="insertTime" type="text" value="${insertTime}">
          <span>平台  <select  id="platform" name="platform">
            	<option value = "">全部</option>
            	<c:forEach items="${platformList}"  var="item" varStatus="i">
					<option value="${item}">
					<c:choose>
						<c:when test="${item eq '0'}">web</c:when>
						<c:when test="${item eq '1'}">android</c:when>
						<c:when test="${item eq '2'}">IOS</c:when>
						<c:when test="${item eq '3'}">微信</c:when>
						<c:otherwise>${item}</c:otherwise>
					</c:choose>
					
					</option>
				</c:forEach>
            </select></span> 
            <input class="input-button" type="submit" value="查询" id="search" >
			<!--<input class="input-button" type="button" value="查询" id="search" onclick="Byname()" > -->
            <input class="input-button" type="button" value="导出报表" onclick="ireportDo()">
            <input type="button" value="更新当天数据" id="updateQdtjPlatformToday">
        </form>
        <div id="fixtop" class="fixtop"></div>
        <h2>合计: 是对当前的查询条件进行统计合计，而不是当前页。</h2>
        <h2>激活次数: 只对移动设备app进行统计，如：Android APP、IOS APP</h2>
        <table id="tHead" class="ct-table" border="1" cellspacing="0" cellpadding="0" style="text-align: center;">
            <thead>
            <tr>
                <td width="200px;">序号</td>
                <td width="200px;">日期</td>
                <td width="200px;">平台</td>
                <td width="200px;">激活次数</td>
                <td width="200px;">注册人数</td>
                <td width="200px;">注册/激活<br>(人数)%</td>
                <td width="200px;">绑定人数</td>
                <td width="200px;">绑定/注册<br>(人数)%</td>
                <td width="200px;">首投人数</td>
                <td width="200px;">首投/绑定<br>(人数)%</td>
                <td width="200px;">首投本金<br>(元)</td>
                <td width="200px;">人均首投<br>本金(元)</td>
                <td width="200px;">投资人数<br>(人)</td>
                <td width="200px;">投资本金<br>(元)</td>
                <td width="200px;">人均投资<br>本金(元)</td>
                <td width="200px;">充值金额<br>(元)</td>
                <td width="200px;">提现金额<br>(元)</td>
                <td width="200px;">存量(元)</td>
                
            </tr>
            </thead>

            <tbody>
            	<tr>
            		<td>合计</td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td><fmt:formatNumber value="${hj[0][0]}" pattern="#,##0.##"/></td>
                    <td></td>
                    <td><fmt:formatNumber value="${hj[0][1]}" pattern="#,##0.##"/></td>
                    <td><fmt:formatNumber value="${hj[0][2]*100}" pattern="#,##0.##"/></td>
                    <td><fmt:formatNumber value="${hj[0][3]}" pattern="#,##0.##"/></td>
                    <td><fmt:formatNumber value="${hj[0][4]*100}" pattern="#,##0.##"/></td>
                    <td><fmt:formatNumber value="${hj[0][5]*0.01}" pattern="#,##0.##"/></td>
                    <td><fmt:formatNumber value="${hj[0][6]*0.01}" pattern="#,##0.##"/></td>
                    <td><fmt:formatNumber value="${hj[0][7]}" pattern="#,##0.##"/></td>
                    <td><fmt:formatNumber value="${hj[0][8]*0.01}" pattern="#,##0.##"/></td>
                    <td><fmt:formatNumber value="${hj[0][9]*0.01}" pattern="#,##0.##"/></td>
                    <td><fmt:formatNumber value="${hj[0][10]*0.01}" pattern="#,##0.##"/></td>
                    <td><fmt:formatNumber value="${hj[0][11]*0.01}" pattern="#,##0.##"/></td>
                    <td><fmt:formatNumber value="${(hj[0][10]-hj[0][11])*0.01}" pattern="#,##0.##"/></td>
                   
               </tr>
            
            <c:forEach items="${list}" var="item" varStatus="i">
                <tr>
                	<td>${i.count + (pageUtil.currentPage-1)*pageUtil.pageSize}</td>
                    <td><fmt:formatDate value="${item.queryDate}" pattern="yyyy-MM-dd"/></td>
                    <td><c:choose>
						<c:when test="${item.platform eq '0'}">web</c:when>
						<c:when test="${item.platform eq '1'}">android</c:when>
						<c:when test="${item.platform eq '2'}">IOS</c:when>
						<c:when test="${item.platform eq '3'}">微信</c:when>
						<c:otherwise>${item.platform}</c:otherwise>
					</c:choose></td>
                    <td><fmt:formatNumber value="${item.jhcs}" pattern="#,##0.##"/></td>
                    <td><fmt:formatNumber value="${item.zcrs}" pattern="#,##0.##"/></td>
                    <td><fmt:formatNumber value="${item.zczhl}" pattern="#,##0.##"/></td>
                    <td><fmt:formatNumber value="${item.bkrs}" pattern="#,##0.##"/></td>
                    <td><fmt:formatNumber value="${item.bkzhl}" pattern="#,##0.##"/></td>
                    <td><fmt:formatNumber value="${item.strs}" pattern="#,##0.##"/></td>
                    <td><fmt:formatNumber value="${item.stzhl}" pattern="#,##0.##"/></td>
                    <td><fmt:formatNumber value="${item.stje*0.01}" pattern="#,##0.##"/></td>
                    <td><fmt:formatNumber value="${item.rjstje*0.01}" pattern="#,##0.##"/></td>
                    <td><fmt:formatNumber value="${item.tzrs}" pattern="#,##0.##"/></td>
                    <td><fmt:formatNumber value="${item.tzje*0.01}" pattern="#,##0.##"/></td>
                    <td><fmt:formatNumber value="${item.rjtzje*0.01}" pattern="#,##0.##"/></td>
                    <td><fmt:formatNumber value="${item.czje*0.01}" pattern="#,##0.##"/></td>
                    <td><fmt:formatNumber value="${item.txje*0.01}" pattern="#,##0.##"/></td>
                    <td><fmt:formatNumber value="${(item.czje-item.txje)*0.01}" pattern="#,##0.##"/></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <c:choose>
            <c:when test="${list ne '[]' &&  list ne '' && list ne null}">
                <jsp:include page="/Product/page.jsp"/>
            </c:when>
            <c:otherwise>
                <div style="text-align: center;margin-top: 15px;"><!-- <img src="images/lh.jpg"> -->
                    <img src="../images/no_record.png"/>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>
<div id="psi_load" style="display:none;">
    <img width="18" src="${pageContext.request.contextPath}/Product/images/progress.gif"><span
        id="psi_tip">正在导出,请稍等...</span>
</div>
<script type="text/javascript">
    var k4 = new Kalendae.Input("insertTime", {
        attachTo: document.body,
        months: 2,//多少个月显示出来,即看到多少个日历
        mode: 'range'
        /* selected:[Kalendae.moment().subtract({d:7}), Kalendae.moment().add({d:0})] */
    });
    $("#updateQdtjPlatformToday").click(function () {
        var url = "${pageContext.request.contextPath}/Product/Admin/qdtjPlatform!updateQdtjPlatformToday.action";
        $.post(url, null, function (data) {
            alert(data.json);//结果!
            window.location.reload();
        });
    });
</script>
</body>
</html>