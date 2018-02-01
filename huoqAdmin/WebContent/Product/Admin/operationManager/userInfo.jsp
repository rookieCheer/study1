<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/zlqEl.tld" prefix="myel" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>用户信息表</title>
    <link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css"/>
    <link href="${pageContext.request.contextPath}/Product/Admin/css/product_fabu_history.css" rel="stylesheet"
          type="text/css"/>
    <script src="${pageContext.request.contextPath}/Product/Admin/js/jquery-1.9.1.min.js"></script>
    <script src="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.standalone.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.css"
          type="text/css">
    <script src="${pageContext.request.contextPath}/js/artDialog4.1.7/jquery.artDialog.js?skin=blue"></script>
    <style type="text/css">
        .sereach {
            width: 50px;
            height: 20px;
            line-height: 20px;
            text-align: center;
            border: 1px solid #009DDA;
            border-radius: 5px;
            display: inline-block;
        }

        .select1 {
            border-radius: 5px;
            border-color: #009DDA;
            margin-right: 5px;
        }

        .a {
            color: blue;
            font-size: 12px;
            text-decoration: underline;
        }
    </style>

    <script type="text/javascript">
        function ireportDo() {
            var insertTime = $("#insertTime").val();
            var acinsertTime = $("#acinsertTime").val();
            var name = $("#username").val();
            var isbindbank = $("#isbindbank option:selected").val();
            var islqg = $("#islqg option:selected").val();
            var level = $("#level option:selected").val();
            var inMoney1 = $("#inMoney1").val();
            var inMoney2 = $("#inMoney2").val();
            var channel = $("#channel").val();
            // if (interval == null || interval == '' || interval.length == 0) {
            //     if (acinterval == null || acinterval == '' || acinterval.length == 0) {
            //         alert("请选择要导出报表日期！");
            //         return false;
            //     }
            //
            // }
            //
            // if (interval.indexOf("-") != -1 && acinterval.indexOf("-") != -1) {
            //     var startDate = interval.split("-")[0];
            //     var endDate = interval.split("-")[1];
            //     var startTime = new Date(Date.parse(startDate.replace(/-/g, "/"))).getTime();
            //     var endTime = new Date(Date.parse(endDate.replace(/-/g, "/"))).getTime();
            //     var dates = Math.abs((startTime - endTime)) / (1000 * 60 * 60 * 24);
            //     if (31 - dates <= 0) {
            //         alert("请选择日期间隔为31天的数据导出！")
            //         return false;
            //     }
            //     //alert(dates);
            // }
            var name = document.getElementById("username").value;
            var channel = document.getElementById("channel").value;
            var insertTime = document.getElementById("insertTime").value;
            var acinsertTime = document.getElementById("acinsertTime").value;
            var isbindbank = $("#isbindbank option:selected").val();
            var islqg = $("#islqg option:selected").val();
            var level = $("#level option:selected").val();
            var inMoney1 = document.getElementById("inMoney1").value;
            var inMoney2 = document.getElementById("inMoney2").value;
            var url = '${pageContext.request.contextPath}/Product/Admin/userStat!iportUserInfo.action?currentPage=${currentPage}&username=' + name + '&insertTime=' + insertTime + '&acinsertTime=' + acinsertTime + '&channel=' + channel + '&isbindbank=' + isbindbank + '&level=' + level + '&inMoney1=' + inMoney1 + '&inMoney2=' + inMoney2 +"&islqg="+islqg;
            var list = "${list}";
            if (list != null && list != "[]") {
                var my = art.dialog({
                    title: '提示',
                    content: document.getElementById("psi_load"),
                    height: 60,
                    lock: true,
                    cancel: false
                });
                $.post(url, $("#sereach").serialize(), function (data) {
                    my.close();
                    data = '${pageContext.request.contextPath}' + data;
                    var ssss = "导出成功&nbsp;&nbsp;&nbsp;&nbsp;<a href='" + data + "' style='color:red;'>点击下载</a>";
                    art.dialog({
                        title: '提示',
                        content: ssss,
                        height: 60,
                        lock: true,
                        ok: function () {
                            //mysss.close();
                        }
                    });
                });
            }
        }
    </script>


    <script type="text/javascript">

        function queryIns(name, isbindbank) {
            if (isbindbank == 1) {
                window.open("${pageContext.request.contextPath}/Product/Admin/investors!findInvertors.action?status=all&name=" + name);
            } else {
                alert("没有绑定银行卡");
            }
        }

        function queryProduct() {
            var insertTime = $("#insertTime").val();
            var acinsertTime = $("#acinsertTime").val();
            var username = $("#username").val();
            var isbindbank = $("#isbindbank option:selected").val();
            var islqg = $("#islqg option:selected").val();
            var level = $("#level option:selected").val();
            var inMoney1 = $("#inMoney1").val();
            var inMoney2 = $("#inMoney2").val();
            var channel = $("#channel").val();

            var url = "${pageContext.request.contextPath}/Product/Admin/userStat!loadUserInfo.action?insertTime=" + insertTime + "&username=" + username + "&isbindbank=" + isbindbank + "&channel=" + channel + "&level=" + level + "&inMoney1=" + inMoney1 + "&inMoney2=" + inMoney2 +
                "&acinsertTime=" + acinsertTime+"&islqg="+islqg;
            window.location.href = url;
        }
        
        
        
        /**
        重置输入框
        */
        function toReset(){
           
            $("#username").val("");//用户名空
            $("#insertTime").val("");
            $("#acinsertTime").val("");
            $("#channel").val("");
            
            //效果没有出来
            //$("#isbindbank option[value='']").attr("selected","selected");
            var select=$("#isbindbank");
            resetSelect(select);
             var selectOne=$("#islqg");//活期
            resetSelect(selectOne);
             var selectTwo=$("#level");//等级
            resetSelect(selectTwo);  // inMoney1
            
            $("#inMoney1").val("");
            $("#inMoney2").val("");
            //$("#isbindbank").prop("selectedIndex",0);
            /**
            效果没出来
            */
            //$("#isbindbank option[0]").attr("selected","selected");
        }
        
        function resetSelect(select){
           var options =select["0"];
            var length = options.length;
            for(var i=0;i<length;i++){
                if(i==0){
                  options[0].selected=true;
                }else{
                 options[i].selected=false;
                }
            }
        }

        function goqueryProduct() {
            var goPage = $("#goPage").val();
            var pageSize = $("#pageSize").val();
            var insertTime = $("#insertTime").val();
            var acinsertTime = $("#acinsertTime").val();
            var username = $("#username").val();
            var isbindbank = $("#isbindbank option:selected").val();
            var islqg = $("#islqg option:selected").val();
            var level = $("#level option:selected").val();
            var inMoney1 = $("#inMoney1").val();
            var inMoney2 = $("#inMoney2").val();
            var channel = $("#channel").val();
            var url = "${pageContext.request.contextPath}/Product/Admin/userStat!loadUserInfo.action?&currentPage=" + goPage + "&pageSize=" + pageSize + "&goPage=" + goPage;
            var url = "${pageContext.request.contextPath}/Product/Admin/userStat!loadUserInfo.action?insertTime=" + insertTime + "&username=" + username + "&isbindbank=" + isbindbank + "&channel=" + channel + "&level=" + level + "&inMoney1=" + inMoney1 + "&inMoney2=" + inMoney2 +
                "&acinsertTime=" + acinsertTime+"&islqg="+islqg+"&currentPage=" + goPage + "&pageSize=" + pageSize + "&goPage=" + goPage;
            window.location.href = url;
        }

        function updatetoutiao() {
            var url = "${pageContext.request.contextPath}/Product/Admin/updateDateInfo!updateUserDateInfo.action";
            window.location.href = url;
        }

        function ins(username) {
            var url = "${pageContext.request.contextPath}/Product/Admin/account!showMyAccount.action?username=" + username;
            window.open(url);
        }

        function queryCzRecord(name) {
            var url = "${pageContext.request.contextPath}/Product/Admin/recharge!rechargeMoneyRecord.action?status=all&name=" + name + "";
            window.open(url);
        }

        function queryTxRecord(name) {
            var url = "${pageContext.request.contextPath}/Product/Admin/checkTxsq!loadTxsq.action?status=all&name=" + name + "";
            window.open(url);
        }

        function queryCoupon(name) {
            var url = "${pageContext.request.contextPath}/Product/Admin/sendCoupon!couponRecord.action?username=" + name + "&useTime=" + "" + "";
            window.open(url);
        }

        function queryZiJin(name) {
            var url = "${pageContext.request.contextPath}/Product/Admin/fundRecord!loadFund.action?name=" + name + "";
            window.open(url);
        }

        function queryInsterest(name) {
            var url = "${pageContext.request.contextPath}/Product/Admin/interestDetails!findInvertorsByProduct.action?username=" + name + "";
            window.open(url);
        }

        function profitDiagram(id) {
            //var url = "${pageContext.request.contextPath}/Product/Admin/operationManager/profitDiagrams.jsp?usersId="+id+"";
            var url = "${pageContext.request.contextPath}/Product/statistics!queryAllUserProfit.action?usersId=" + id + "";
            window.open(url);
        }

        function queryShiftTo(name) {
            var url = "${pageContext.request.contextPath}/Product/Admin/usersPurse!loadUsersShiftTo.action?username=" + name + "";
            window.open(url);
        }

        function queryCoinPurse(name) {
            var url = "${pageContext.request.contextPath}/Product/Admin/usersPurse!loadUsersCoinPurse.action?username=" + name + "";
            window.open(url);
        }

        function queryRollOut(name) {
            var url = "${pageContext.request.contextPath}/Product/Admin/usersPurse!loadUsersRollOut.action?username=" + name + "";
            window.open(url);
        }

        function queryMcoin(name) {
            var url = "${pageContext.request.contextPath}/Product/Admin/meowIncome!findUsersMcoin.action?username=" + name + "";
            window.open(url);
        }

        function queryTeam(name) {
            var url = "${pageContext.request.contextPath}/Product/Admin/userTeam!teamList.action?username=" + name + "";
            window.open(url);
        }
    </script>
    <script type="text/javascript">
        $(function () {
            $("#registPlatform option[value='${registPlatform}']").attr("selected", true);
            $("#username,#channel,#insertTime,#isbindbank").keydown(function (event) {
                if (event.keyCode == 13) {
                    queryProduct();
                }
            });

            $("#inMoney1").blur(function () {
                var inMoney1 = $("#inMoney1").val();
                var inMoney2 = $("#inMoney2").val();
                if (inMoney1 != "" && inMoney2 != "" && inMoney1 > inMoney2) {
                    $("#inMoney1").val(inMoney2);
                    $("#inMoney2").val(inMoney1);
                }
            });

            $("#inMoney2").blur(function () {

                var inMoney1 = $("#inMoney1").val();
                var inMoney2 = $("#inMoney2").val();

                if (inMoney1 != "" && inMoney2 != "" && inMoney1 > inMoney2) {
                    $("#inMoney1").val(inMoney2);
                    $("#inMoney2").val(inMoney1);
                }
            });


        });

    </script>
</head>
<body>
<div class="center">
    <jsp:include page="/Product/Admin/common/head.jsp"/>
    <div class="main" align="center">
        <h3>用户信息表</h3>  <input type="button" onclick="updatetoutiao()" value="更新头条渠道数据">
        <span>关键字:</span> <input id="username" name="username" type="text" value="${username}" maxlength="11"
                                 placeholder="用户ID/用户名/姓名" style="width:120px">
        <span>渠道:</span> <input id="channel" name="channel" type="text" value="${channel}" style="width:80px">
        <span>注册时间:</span> <input id="insertTime" name="insertTime" type="text" value="${insertTime}">
        <span>绑卡时间:</span> <input id="acinsertTime" name="acinsertTime" type="text" value="${acinsertTime}">

        <span>是否绑卡</span>
        <select id="isbindbank">
            <option value="">所有</option>
            <option value="1">是</option>
            <option value="0">否</option>
        </select>
        <span>只投资活期/定期</span>
        <select id="islqg">
            <option value="">所有</option>
            <option value="1">活期</option>
            <option value="0">定期</option>
        </select>
        <span>等级</span>
        <select id="level">
            <option value="">所有</option>
            <option value="0">0</option>
            <option value="1">1</option>
            <option value="2">2</option>
            <option value="3">3</option>
            <option value="4">4</option>
            <option value="5">5</option>
            <option value="6">6</option>
        </select>
        <span>投资总额范围:</span> <input id="inMoney1" name="inMoney1" type="text" value="${inMoney1}" style="width:50px">
        -<input id="inMoney2" name="inMoney2" type="text" value="${inMoney2}" style="width:50px"> （万）
        <a class="sereach" href="javascript:queryProduct();" id="sereach">查询</a>
        <a class="sereach" href="javascript:toReset();" id="reset">重置</a>
        <input type="button" value="导出报表" onclick="ireportDo()">
        <input type="hidden" id="pageSize" value="${pageSize}">
        <table border="0.5" cellspacing="0" cellpadding="0" align="center" style="margin-top:10px;">
            <tr align="center">
                <td width="50px;">编号</td>
                <td width="50px;">用户ID</td>
                <td width="50px;">用户名</td>
                <td width="50px;">姓名</td>
                <td width="50px;">性别</td>
                <td width="50px;">年龄</td>
                <td width="50px;">生日</td>
                <td width="70px;">所属省份</td>
                <td width="70px;">所属城市</td>
                <td width="70px;">电话类型</td>
                <td width="80px;">注册时间</td>
                <td width="80px;">绑卡时间</td>
                <td width="50px;">注册平台</td>
                <td width="100px;">是否绑定银行卡</td>
                <td width="50px;">首投日期</td>
                <td width="50px;">首投产品</td>
                <td width="50px;">首投金额（元）</td>
                <td width="65px;">投资总金额</td>
                <td width="65px;">零钱罐金额</td>
                <td width="50px;">VIP等级</td>
                <td width="50px;">渠道号</td>
                <td width="400px;">操作</td>
            </tr>
            <c:forEach items="${pageUtil.list}" var="item" varStatus="i">
                <tr>
                    <td align="center">${i.count + (pageUtil.currentPage-1)*pageUtil.pageSize}</td>
                    <td align="center">${item.id}</td>
                    <td align="center"><a class="a"
                                          href="${pageContext.request.contextPath}/Product/Admin/userStat!loadUserInfo.action?username=${myel:jieMiUsername(item.username)}">${myel:jieMiUsername(item.username)}</a>
                    </td>
                    <td align="center">${item.realName}</td>
                    <td align="center">${item.sex}</td>
                    <td align="center">${item.age}</td>
                    <td align="center">${item.birthday}</td>

                    <td align="center">${item.province}</td>
                    <td align="center">${item.city}</td>
                    <td align="center">${item.cardType}</td>
                    <td align="center"><fmt:formatDate value="${item.insertTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                    <td align="center"><fmt:formatDate value="${item.acinsertTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                    <td align="center">${item.registPlatform}</td>
                    <td align="center">
                        <c:choose>
                            <c:when test="${item.isBindBank == '1'}">是</c:when>
                            <c:otherwise>否</c:otherwise>
                        </c:choose>
                    </td>
                    <td align="center"><fmt:formatDate value="${item.payTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td></td>
                    <td align="center">${item.title}</td>
                    <td align="center"><fmt:formatNumber value="${item.inMoney1}" pattern="#,##0.##"/></td>
                    <td align="center"><fmt:formatNumber value="${item.inMoney}" pattern="#,##0.##"/></td>
                    <td align="center">${item.coinPurseFundsRecordMoney}</td>
                    <td align="center">${item.level}</td>
                    <td align="center">${item.registChannel}</td>
                    <td align="center" style="margin: 10px; padding:0 10px;">
                        <a class="a"
                           href="javascript:queryIns('${myel:jieMiUsername(item.username)}','${item.isBindBank}')">投资记录</a>
                        <a class="a" href="javascript:queryCzRecord('${myel:jieMiUsername(item.username)}')">充值记录</a>
                        <a class="a" href="javascript:queryTxRecord('${myel:jieMiUsername(item.username)}')">提现记录</a>
                        <a class="a" href="javascript:queryCoupon('${myel:jieMiUsername(item.username)}')">投资券</a>
                        <a class="a" href="javascript:queryZiJin('${myel:jieMiUsername(item.username)}')">用户资金流水</a>
                        <a class="a" target="_blank"
                           href="javascript:queryInsterest('${myel:jieMiUsername(item.username)}')">付息明细</a><br>
                        <a class="a" target="_blank"
                           href="${pageContext.request.contextPath}/Product/Admin/account!showMyPurse.action?username=${myel:jieMiUsername(item.username)}">账户详情</a>
                        <c:if test="${item.isBindBank == '1'}">
                            <a class="a" target="_blank"
                               href="${pageContext.request.contextPath}/Product/Admin/bindCardRecord!findAccount.action?name=${myel:jieMiUsername(item.username)}&isBindBank=${item.isBindBank}">绑卡人信息</a>
                        </c:if> <a class="a" target="_blank" href="javascript:profitDiagram('${item.id}')">收益曲线图</a>
                        <a class="a" href="javascript:queryMcoin('${myel:jieMiUsername(item.username)}')">喵币</a><br>
                        <a class="a" href="javascript:queryShiftTo('${myel:jieMiUsername(item.username)}')">零钱包转入记录</a>
                        <a class="a" href="javascript:queryRollOut('${myel:jieMiUsername(item.username)}')">零钱包转出记录</a>
                        <a class="a" href="javascript:queryCoinPurse('${myel:jieMiUsername(item.username)}')">零钱包余额</a>
                        <a class="a" href="javascript:queryTeam('${myel:jieMiUsername(item.username)}')">团队收益</a>
                        <p>&nbsp;</p>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <c:choose>
            <c:when test="${pageUtil.list ne '[]' &&  pageUtil.list ne '' && pageUtil.list ne null}">
                <jsp:include page="/Product/page.jsp"/>
                跳转页面:<input id="goPage" name="goPage" type="text" value="${currentPage}" style="width:50px">
                <a class="sereach" href="javascript:goqueryProduct();">跳转</a>
            </c:when>
            <c:otherwise>
                <div style="text-align: center;margin-top: 15px;"><!-- <img src="images/lh.jpg"> -->
                    <img src="../images/no_record.png"/>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>
<script type="text/javascript">
    var k4 = new Kalendae.Input("insertTime", {
        attachTo: document.body,
        months: 2,//多少个月显示出来,即看到多少个日历
        mode: 'range'
        /* selected:[Kalendae.moment().subtract({d:7}), Kalendae.moment().add({d:0})] */
    });
    var k4 = new Kalendae.Input("acinsertTime", {
        attachTo: document.body,
        months: 2,//多少个月显示出来,即看到多少个日历
        mode: 'range'
        /* selected:[Kalendae.moment().subtract({d:7}), Kalendae.moment().add({d:0})] */
    });
    $(function () {
        $("#isbindbank option[value='${isbindbank}']").attr("selected", true);
        $("#level option[value='${level}']").attr("selected", true);
        $("#islqg option[value='${islqg}']").attr("selected", true);
    });
</script>
</body>
</html>