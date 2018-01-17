<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/zlqEl.tld" prefix="myel" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>单个渠道详情统计表</title>
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
            width: auto;
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
            width: auto;
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
            var registChannel = document.getElementById("registChannel").value;
            var insertTime = document.getElementById("insertTime").value;
            var channelCode = document.getElementById("channelCode").value;
            var url = '${pageContext.request.contextPath}/Product/Admin/activity!iportQdtjDetailsTable.action?';
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
</head>
<body>
<div class="center">
    <jsp:include page="/Product/Admin/common/head.jsp"/>
    <div class="main" align="center">
        <h3 class="h3-title">单个渠道统计详情</h3>
        <form class="form" id="frm"
              action="${pageContext.request.contextPath}/Product/Admin/activity!loadQdtjDetails.action">
            <span>查询期间:</span> <input id="insertTime" name="insertTime" type="text" value="${insertTime}">
            <input id="registChannel" name="registChannel" type="hidden" value="${registChannel}">
            <input id="channelCode" name="channelCode" type="hidden" value="${channelCode}">
            <input class="input-button" type="submit" value="查询">
            <input class="input-button" type="button" value="导出报表" onclick="ireportDo()">
        </form>
        <div class="mes-div">
            <span class="span">渠道编号：${channel} </span>
            <span class="span">渠道名称：${channelName} </span>
            <span class="span">渠道编码：${channelCode} </span>
            <span class="span">投放日期：${insertTime}</span>
            <%--<span class="span">投放日期：<fmt:formatDate value="${insertTime}" pattern="yyyy-MM-dd"/>--%> <%--<fmt:formatDate value="${platChannel.date}" pattern="yyyy-MM-dd"/>--%> </span>
        </div>
        <div id="fixtop" class="fixtop"></div>
        <table id="tHead" class="ct-table" border="1" cellspacing="0" cellpadding="0" style="text-align: center;">
            <thead>
            <tr>
                <td width="30px;">序号</td>
                <td width="80px;">日期</td>
                <td width="30px;">点击量</td>
                <td width="30px;">下载量</td>
                <td width="30px;">激活量</td>
                <td width="50px;">渠道费用（元）</td>
                <td width="50px;">激活成本（元）</td>
                <td width="60px;">激活注册转化率(%)</td>
                <td width="auto;">当日注册人数</td>
                <td width="auto;">注册成本</td>
                <td width="auto;">认证人数</td>
                <td width="60px;">注册认证转化率(%)</td>
                <td width="60px;">认证首投转化率(%)</td>
                <td width="auto;">首投人数</td>
                <td width="auto;">首投成本</td>
                <td width="auto;">首投总金额(元)</td>
                <td width="60px;">人均首投总金额(元)</td>
                <td width="auto;">首投ROI</td>
                <td width="auto;">复投人数</td>
                <td width="auto;">复投成本(元)</td>
                <td width="auto;">复投金额</td>
                <td width="auto;">人均复投金额</td>
                <td width="auto;">复投ROI</td>
                <td width="auto;">新增复投用户数</td>
                <td width="auto;">新增复投用户投资总额(元)</td>
                <td width="auto;">新增复投率</td>
                <td width="auto;">投资人数</td>
                <td width="auto;">投资金额(元)</td>
                <td width="auto;">零钱罐投资金额(元)</td>
                <td width="60px;">人均投资金额(元)</td>
                <td width="auto;">投资ROI</td>
            </tr>
            </thead>

            <tbody>
            <tr>
                <td>合计</td>
                <td></td>
                <td></td>
                <td></td>
                <td><fmt:formatNumber value="${tj.activityCount}" pattern="#,##0.##"/></td>
                <td></td>
                <td></td>
                <td></td>
                <td><fmt:formatNumber value="${tj.regCount}" pattern="#,##0.##"/></td>
                <td></td>
                <td><fmt:formatNumber value="${tj.bindCount}" pattern="#,##0.##"/></td>
                <td></td>
                <td></td>
                <td><fmt:formatNumber value="${tj.strs}" pattern="#,##0.##"/></td>
                <td></td>
                <td><fmt:formatNumber value="${tj.stje}" pattern="#,##0.##"/></td>
                <c:choose>
	              	<c:when test="${tj.stje eq '0'}">
	              	<td>0</td>
	              	</c:when>
	              	<c:when test="${tj.strs eq '0'}">
	              	<td>0</td>
	              	</c:when>
	              	<c:otherwise>
	                <td><fmt:formatNumber value="${tj.stje/tj.strs}" pattern="#,##0.##"/></td>
	              	</c:otherwise>
                </c:choose>
                <td></td>
                <td><fmt:formatNumber value="${tj.ftrs}" pattern="#,##0.##"/></td>
                <td></td>
                <td><fmt:formatNumber value="${tj.ftje}" pattern="#,##0.##"/></td>
                <c:choose>
                    <c:when test="${tj.ftrs eq '0'}">
                        <td>0</td>
                    </c:when>
                    <c:when test="${tj.rjftje eq '0'}">
                        <td>0</td>
                    </c:when>
                    <c:otherwise>
                        <td><fmt:formatNumber value="${tj.ftje/tj.ftrs}" pattern="#,##0.##"/></td>
                    </c:otherwise>
                </c:choose>
                <td></td>
                <td><fmt:formatNumber value="${tj.xzftyh}" pattern="#,##0.##"/></td>
                <td><fmt:formatNumber value="${tj.xhftyhtzze}" pattern="#,##0.##"/></td>
                <td></td>
                <td><fmt:formatNumber value="${tj.tzrs}" pattern="#,##0.##"/></td>
                <td><fmt:formatNumber value="${tj.tzje}" pattern="#,##0.##"/></td>
                <td><fmt:formatNumber value="${tj.lqgje}" pattern="#,##0.##"/></td>
                <c:choose>
                	<c:when test="${tj.tzrs eq '0'}">
                	<td>0</td>
                	</c:when>
                	<c:when test="${tj.rjtzje eq '0'}">
                	<td>0</td>
                	</c:when>
                	<c:otherwise>
		                <td><fmt:formatNumber value="${tj.tzje/tj.tzrs}" pattern="#,##0.##"/></td>
                	</c:otherwise>
                </c:choose>
                <td></td>
            </tr>
            <c:forEach items="${list}" var="item" varStatus="i">
                <tr>
                    <td>${i.count + (pageUtil.currentPage-1)*pageUtil.pageSize}</td>
                    <td>${item.dateStr}</td>
                    <td></td>
                    <td></td>
                    <td><fmt:formatNumber value="${item.activityCount}" pattern="#,##0.##"/></td>
                    <td></td>
                    <td></td>
                    <td><fmt:formatNumber value="${item.qdzhl}" pattern="#,##0.##"/></td>
                    <td>${item.regCount}</td>
                    <td></td>
                    <td>${item.bindCount}</td>
                    <td><fmt:formatNumber value="${item.zcjhzhl}" pattern="#,##0.##"/></td>
                    <td><fmt:formatNumber value="${item.rzstzhl}" pattern="#,##0.##"/></td>
                    <td>${item.strs}</td>
                    <td></td>
                    <td><fmt:formatNumber value="${item.stje}" pattern="#,##0.##"/></td>
                    <td><fmt:formatNumber value="${item.rjstje}" pattern="#,##0.##"/></td>
                    <td></td>
                    <td>${item.ftrs}</td>
                    <td></td>
                    <td><fmt:formatNumber value="${item.ftje}" pattern="#,##0.##"/></td>
                    <td><fmt:formatNumber value="${item.rjftje}" pattern="#,##0.##"/></td>
                    <td></td>
                    <td><fmt:formatNumber value="${item.xzftyh}" pattern="#,##0.##"/></td>
                    <td><fmt:formatNumber value="${item.xhftyhtzze}" pattern="#,##0.##"/></td>
                    <td><fmt:formatNumber value="${item.xzftl}" pattern="#,##0.##"/></td>
                    <td>${item.tzrs}</td>
                    <td><fmt:formatNumber value="${item.tzje}" pattern="#,##0.##"/></td>
                    <td><fmt:formatNumber value="${item.lqgje}" pattern="#,##0.##"/></td>
                    <td><fmt:formatNumber value="${item.rjtzje}" pattern="#,##0.##"/></td>
                    <td></td>
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
</script>
</body>
</html>