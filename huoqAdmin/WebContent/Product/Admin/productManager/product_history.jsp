<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/zlqEl.tld" prefix="myel" %>
<!doctype html>
<html>
<meta charset="utf-8"/>
<title>产品历史记录</title>
<link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css"/>
<link href="${pageContext.request.contextPath}/Product/Admin/css/product_fabu_history.css" rel="stylesheet"
      type="text/css"/>
<script src="${pageContext.request.contextPath}/Product/Admin/js/jquery-1.9.1.min.js"></script>
<script src="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.standalone.js"></script>
<script src="${pageContext.request.contextPath}/js/artDialog4.1.7/jquery.artDialog.js?skin=blue"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.css"
      type="text/css">
<style>
    .a {
        color: blue;
        text-decoration: underline;
    }
</style>
<script type="text/javascript">
    function ireportDo() {
        var list = '${table}';
        if (list != '1') {
            alert("无数据");
            return false;
        }
        var url = '${pageContext.request.contextPath}/Product/Admin/releaseProduct!iportProductData.action?';
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
            data = '${pageContext.request.contextPath}' + data;
            var ssss = "导出成功&nbsp;&nbsp;&nbsp;&nbsp;<a href='" + data + "' style='color:red;'>点击下载</a>";
            art.dialog({
                title: '提示',
                content: ssss,
                height: 60,
                lock: true,
                ok: function () {

                },
            });
        });

    }
</script>
<script type="text/javascript">
    function checkNum() {
        var reg = new RegExp("^[+-]?[1-9]?[0-9]*\.[0-9]*$");
        var reg2 = new RegExp("^[0-9]*$");
        var annualEarnings = $("#annualEarnings").val();
        //alert(str);
        if (annualEarnings != '') {
            if (!reg.test(annualEarnings) || !reg2.test(annualEarnings)) {
                alert("年化收益输入错误");
                return false;
            }
        }
        var financingAmount = $("#financingAmount").val();
        if (financingAmount != '') {
            if (!reg.test(financingAmount) || !reg2.test(financingAmount)) {
                alert("总金额输入错误");
                return false;
            }
        }
        return true;
    }


</script>
<script type="text/javascript">
    function showBorrowerInfo(id) {
        window.location.href = "${pageContext.request.contextPath}/Product/Admin/creditProduct!toShowBorrowerInfo.action?productId="+id;
    }

    function modifyProduct(id) {
        window.location.href = "${pageContext.request.contextPath}/Product/Admin/releaseProduct!toModifyProduct.action?productId=" + id;
    }

    function modifyFundProduct(id) {
        window.location.href = "${pageContext.request.contextPath}/Product/Admin/releaseFundProduct!toModifyFundProduct.action?productId=" + id;
    }

    //修改预约产品
    function modifyYYProduct(id) {
        window.location.href = "${pageContext.request.contextPath}/Product/Admin/releaseProduct!toModifyProduct.action?flag=yy&productId=" + id;
    }

    //修改预约新手产品
    function modifyYYFundProduct(id) {
        window.location.href = "${pageContext.request.contextPath}/Product/Admin/releaseFundProduct!toModifyFundProduct.action?flag=yy&productId=" + id;
    }

    //删除预约产品
    function deleteYYProduct(productTitle, productId) {
        alert("确定要删除" + productTitle + "?");
        var url = "${pageContext.request.contextPath}/Product/Admin/releaseProduct!deleteYYproduct.action?productId=" + productId;
        $.ajax({
            type: "POST",
            url: url,
            data: productId,
            success: function (data, textStatus) {
                if (textStatus == 'success') {
                    alert(data.status);
                    if ("success" == data.status) {
                        alert("删除成功！");
                        window.location.href = "${pageContext.request.contextPath}/Product/Admin/releaseProduct!.action";
                    }
                }
            }
        });
    }
    //删除信用贷预约产品
    function deleteCreditProduct(productTitle, productId) {
        alert("确定要删除信用贷预约产品" + productTitle + "?");
        var url = "${pageContext.request.contextPath}/Product/Admin/creditProduct!deleteCreditProduct.action?productId=" + productId;
        $.ajax({
            type: "POST",
            url: url,
            data: productId,
            success: function (data, textStatus) {
                if (textStatus == 'success') {
                    alert(data.status);
                    if ("success" == data.status) {
                        alert("删除成功！");
                        window.location.href = "${pageContext.request.contextPath}/Product/Admin/releaseProduct!.action";
                    }
                }
            }
        });
    }
</script>
</head>
<body>
<div class="center">
    <jsp:include page="/Product/Admin/common/head.jsp"/>
    <div class="main" align="center">
        <h3 align="center">产品发布历史记录</h3>
        <form id="frm" action="${pageContext.request.contextPath}/Product/Admin/releaseProduct!productRecord.action"
              method="GET" onsubmit="return checkNum()">
            产品名称:<input type="text" id="title" name="title" value="${title}"/>
            年化收益：<input type="text" id="annualEarnings" name="annualEarnings" value="${annualEarnings}"/>
            项目总额：<input type="text" id="financingAmount" name="financingAmount" value="${financingAmount}"/>
            借款企业: <input type="text" id="borrowingEnterprise" name="realName" value="${realName}"/><br>
            发布时间：<input type="text" id="insertTime" name="insertTime" value="${insertTime}"/>
            到期时间:<input type="text" id="finishTime" name="finishTime" value="${finishTime}"/>
            产品状态:<select id="productStatus" name="productStatus">
            <option value="" selected="selected">全部</option>
            <option value="-3">预约中</option>
            <option value="-2">审核不通过</option>
            <option value="-1">未审核</option>
            <option value="0">营销中</option>
            <option value="1">已售罄</option>
            <option value="2">结算中</option>
            <option value="3">已结算</option>
        </select>
            <input type="submit" value="查询">
            <input type="button" value="导出报表" onclick="ireportDo()">
        </form>
        <p>共有<span>${pageUtil.count}</span>个项目</p>
        <div class="table">
            <table cellspacing="0" cellpadding="0" style="  width: 100%;">
                <tbody>
                <tr>
                    <td>序号</td>
                    <td>发布状态</td>
                    <td>产品名称</td>
                    <td>借款企业名称</td>
                    <td>产品类型</td>
                    <td>年化总收益</td>
                    <td>奖励（浮动）收益</td>
                    <td>项目总额</td>
                    <td>虚拟投资总额</td>
                    <td>真实投资总额</td>
                    <td>到期时间</td>
                    <td>产品状态</td>
                    <td>发布时间</td>
                    <td>操作</td>
                </tr>
                <c:forEach items="${list}" var="item" varStatus="i">
                    <tr>
                        <td>${i.count + (pageUtil.currentPage-1)*pageUtil.pageSize}</td>
                        <td>提交成功</td>
                        <td>${item[1]}</td>
                        <td> ${item[13]} </td>
                        <td>
                            <c:if test="${item[2] eq 0}">车无忧</c:if>
                            <c:if test="${item[2] eq 1}">贸易通</c:if>
                            <c:if test="${item[2] eq 5}">货押宝</c:if>
                            <c:if test="${item[2] eq 6}">车贷宝</c:if>
                            <c:if test="${item[2] eq 2}">牛市通</c:if>
                            <c:if test="${item[2] eq 3}">房盈宝</c:if>
                            <c:if test="${item[2] eq 7}">信用贷</c:if>
                        </td>
                        <td><fmt:formatNumber value="${item[3]}" pattern="#.##"/>%</td>
                        <td>
                            <c:if test="${item[4] eq null}">0</c:if>
                            <c:if test="${item[4] ne null}">
                                <fmt:formatNumber value="${item[4]}" pattern="#.##"/>
                            </c:if>%
                        </td>
                        <td><fmt:formatNumber value="${item[5]*0.01}" pattern="#,##0.##"/></td>
                        <td>
                            <c:if test="${item[8] eq null}">0</c:if>
                            <c:if test="${item[8] ne null}">
                                <fmt:formatNumber value="${item[8]}" pattern="#,##0.##"/>
                            </c:if>
                        </td>
                        <td>
                            <c:if test="${item[9] eq null}">0</c:if>
                            <c:if test="${item[9] ne null}">
                                <fmt:formatNumber value="${item[9]}" pattern="#,##0.##"/>
                            </c:if>
                        </td>
                        <td>${item[6]}</td>
                        <td>
                            <c:if test="${item[0] eq -2}">审核不通过</c:if>
                            <c:if test="${item[0] eq -1}">未审核</c:if>
                            <c:if test="${item[0] eq 0}">营销中</c:if>
                            <c:if test="${item[0] eq 1}">已售罄</c:if>
                            <c:if test="${item[0] eq 2}">结算中</c:if>
                            <c:if test="${item[0] eq 3}">已还款</c:if>
                            <c:if test="${item[0] eq -3}">预约中</c:if>
                        </td>
                        <td>${item[7]}</td>
                        <td>
                            <c:if test="${item[2] eq 7}">
                                <a class="a" href="javascript:showBorrowerInfo('${item[11]}');">借款人信息</a>
                                <c:if test="${item[12] eq 9}">
                                    已打款
                                </c:if>
                                <c:if test="${item[12] eq 0}">
                                    <a class="a"  id="modify_${item[11]}" href="javascript:playMoney('${item[11]}');" data-id='1'>打款</a>
                                </c:if>
                            </c:if>
                            <c:if test="${item[0] eq 0||item[0] eq 1}">
                                <c:choose>
                                    <c:when test="${item[10] eq 2}">
                                        <a class="a" id="modify_${item[11]}"
                                           href="javascript:modifyFundProduct('${item[11]}');">查看</a>
                                    </c:when>
                                    <c:otherwise>
                                        <a class="a" href="javascript:modifyProduct('${item[11]}');">查看</a>
                                    </c:otherwise>
                                </c:choose>
                            </c:if>
                            <c:if test="${item[0] eq -3}">
                                <c:if test="${item[2] eq 7}">
                                    <a class="a" target="_blank" href="javascript:void(0)"
                                       onclick="deleteCreditProduct('${item[1]}','${item[11]}');">删除信用贷预约产品</a>
                                </c:if>
                               <c:if test="${item[2] ne 7}">
                                   <a class="a" target="_blank" href="javascript:void(0)"
                                      onclick="deleteYYProduct('${item[1]}','${item[11]}');">删除预约产品</a>
                               </c:if>
                                <c:choose>
                                    <c:when test="${item[10] eq 2}">
                                        <a class="a" id="modify_${item[11]}"
                                           href="javascript:modifyYYFundProduct('${item[11]}');">修改</a>
                                    </c:when>
                                    <c:otherwise>
                                        <a class="a" href="javascript:modifyYYProduct('${item[11]}');">修改</a>
                                    </c:otherwise>
                                </c:choose>

                            </c:if>

                        </td>
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
</div>
</body>
<script type="text/javascript">
    var productStatus = '${productStatus}';
    if (productStatus != '') {
        $("#productStatus").find("option[value='" + productStatus + "']").attr("selected", true);
    }
  /* var clickNum = [];
    $('a').click(function() {
        var id = $(this).data('id');
        clickNum[id] = clickNum[id] > 0 ? ++clickNum[id] : 1;
       alert('超链接《' + id + '》被点击 ' + clickNum[id] + ' 次！');
    });
*/
</script>
<script type="text/javascript">
    var k4 = new Kalendae.Input("insertTime", {
        attachTo: document.body,
        months: 2,//多少个月显示出来,即看到多少个日历
        mode: 'range'
        /* selected:[Kalendae.moment().subtract({d:7}), Kalendae.moment().add({d:0})] */
    });
</script>
<script type="text/javascript">
    var k4 = new Kalendae.Input("finishTime", {
        attachTo: document.body,
        months: 2,//多少个月显示出来,即看到多少个日历
        mode: 'range'
        /* selected:[Kalendae.moment().subtract({d:7}), Kalendae.moment().add({d:0})] */
    });

    function playMoney(productId) {
        $.ajax({
            type: "POST",
            url: "${pageContext.request.contextPath}/Product/Admin/assetSide!playMoney.action",
            data: {"productId": productId},
            datatype: "json",
            success: function (data) {
                if (data.status == "success") {
                    alert("打款成功");
                    window.location.reload();//刷新当前页面
                } else {
                    alert(data.json);
                    window.location.reload();//刷新当前页面
                }
            }
        });

    }
</script>
</html>