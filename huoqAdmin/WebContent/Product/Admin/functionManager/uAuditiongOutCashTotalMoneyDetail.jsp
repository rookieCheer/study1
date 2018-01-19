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
    <title>未审核提现总额记录详情</title>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.7.2.min.js"></script>
    <script type="text/javascript">
        function queryTx(name,requestId){//易宝提现查询
            $.ajax({
                url: "${pageContext.request.contextPath}/Product/Admin/recharge!TxMoneyArrive.action?name="+name+"&requestId="+requestId,
                success: function(data){
                    alert(data.json);
                }});
        }
        function querylianlian(requestId,insertTime){//连连提现查询
            $.ajax({
                url: "${pageContext.request.contextPath}/Product/Admin/recharge!TxMoneyQuery.action?requestId="+requestId+"&insertTime="+insertTime,
                success: function(data){
                    alert(data.json);
                }});
        }
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
            var url='${pageContext.request.contextPath}/Product/Admin/checkTxsq!iportIndexTXTable.action?currentPage=${currentPage}&name='+name+'&insertTime='+insertTime+'&status='+status;
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

    <script type="text/javascript">
        //启动提现查询接口
        function txThread(){
            var url = "${pageContext.request.contextPath}/Product/Admin/startThread!startTxQueryThread.action";
            $.post(url,null,function(data){
                alert(data.json);//结果!
            });
        }
    </script>

</head>
<body>
<div class="center">
    <jsp:include page="/Product/Admin/common/head.jsp"/>
    <div class="main" align="center">
        <h1 style="text-align: center;">未审核提现总额记录详情</h1>
        <div id="div_condition" style="text-align: center;" >
            <label>用户名:<input type="text" name="name" id="name" value="${name}" maxlength="11">
                <span>提现时间:</span> <input id="insertTime" name="insertTime" type="text" value="${insertTime}">
                <input type="button" value="search" id="frm" onclick="search()"></label>&nbsp;&nbsp;
                 <input type="button" value="导出报表" onclick="exportExcel()">
            <c:if test="${usersAdmin.id!=11020}">
                <label><input type="button" value="启动【提现查询接口】线程" onclick="txThread()" id="startTxQueryThread"> </label>
            </c:if>
           
        </div>

        <table  style="width:100%;margin-top: 20px;text-align: center;" border="1">
            <tr>
                <td width="3%">序号</td>
                <td width="5%">用户名</td>
                <td width="3%">提现金额(元)</td>
                <td width="3%">姓名</td>
                <td width="3%">所属省份</td>
                <td width="3%">所属城市</td>
                <td width="3%">持卡人好友</td>
                <td width="5%">提现状态</td>
                <td width="10%">备注</td>
                <td width="3%">流水号</td>
                <td width="8%">申请提现时间</td>
                <td width="8%">审核提现时间</td>
                <td width="5%">提现类型</td>
                <td width="10%">平台订单号</td>
                <td width="8%">交易流水号</td>
                <td width="5%">提现方式</td>
                <c:if test="${usersAdmin.id!=11020}">
                    <td width="10%">操作</td>
                </c:if>
            </tr>
            <c:forEach items="${txRecordList}" var="list" varStatus="i">
                <tr>
                    <td width="3%">${i.count + (pageUtil.currentPage-1)*pageUtil.pageSize}</td>
                    <td width="5%">
                           
                    </td>
                    <td width="3%"><fmt:formatNumber value="${list.money * 0.01}" pattern="#,##0.##"/></td>
                    <td width="3%">
                            ${list.realName}
                    </td>
                    <td width="3%">${list.province}</td>
                    <td width="3%">${list.city}</td>
                    <td width="3%">${list.category}</td>
                    <td width="5%">${list.txzt}</td>
                    <td width="10%">${list.note}</td>
                    <td width="3%">${list.recordNumber}</td>
                    <td width="8%"><fmt:formatDate value="${list.insertTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                    <td width="8%"><fmt:formatDate value="${list.checkTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                    <td width="5%">
                        <c:choose>
                            <c:when test="${list.drawType eq '0'}">T+0</c:when>
                            <c:otherwise>T+1</c:otherwise>
                        </c:choose>
                    </td>
                    <td width="10%">${list.requestId}</td>
                    <td width="8%">${list.ybOrderId}</td>
                    <td width="5%">
                        <c:choose>
                            <c:when test="${list.type eq '0'}">易宝提现</c:when>
                            <c:when test="${list.type eq '1'}">支付宝提现</c:when>
                            <c:otherwise>连连提现</c:otherwise>
                        </c:choose>
                    </td>
                    <c:if test="${usersAdmin.id!=11020}">
                        <td width="10%">
                            <c:if test="${list.status eq '0'}">
                                <a class="a" href="javascript:checkUsersMoney('${list.usersId}','${list.id}');">审核资金</a>
                                <c:if test="${list.isCheck eq '1'}">
                                    <c:if test="${list.txStatus eq '1'}">
                                        <a class="a" href="javascript:allclearTx('${list.id}');">解除警报</a>
                                    </c:if>
                                    <br>
                                    <a class="a"
                                       href="javascript:shenhe('${list.id}','1','${list.txStatus}','${list.isCheck}');">审核通过</a>
                                    <a class="a"
                                       href="javascript:shenhe('${list.id}','2','${list.txStatus}','${list.isCheck}');">审核不通过</a>
                                </c:if>
                            </c:if>
                        </td>
                    </c:if>
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
   
    function search(){
        //window.location.href="${pageContext.request.contextPath}/Product/Admin/checkTxsq!loadIndexTxsq.action?name="+$("#name").val()+"&status="+$('input[name="status"]:checked').val()+"&insertTime="+$('#insertTime').val();
        window.location.href="${pageContext.request.contextPath}/Product/Admin/recharge!uAuditiongOutCashTotalMoneyDetail.action?name="+$("#name").val()+"&status="+$('input[name="status"]:checked').val()+"&insertTime="+$('#insertTime').val();
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


    //人工审核提现记录
    function shenhe(txRecordId, status,txStatus,isCheck) {
        if("1"!=isCheck){
            alert("请先操作【审核资金】");
            return false;
        }

        if(status == '1'){
            if("1"==txStatus){
                alert("如需【审核通过】请先【解除警报】");
                return false;
            }
            if(!confirm("确定审核通过？")){
                return false;
            }
        }else if(status == '2'){
            if(!confirm("确定审核不通过？")){
                return false;
            }
        }
        $.ajax({
            url: "${pageContext.request.contextPath}/Product/Admin/checkTxsq!shenheTx.action?txId=" + txRecordId + "&shStatus=" + status,
            success: function (data) {
                alert(data.json);
                location.reload();
            }
        });
    }
    //审核用户资金
    function checkUsersMoney(usersId, txRecordId) {
        $.ajax({
            url: "${pageContext.request.contextPath}/Product/Admin/checkTxsq!checkUsersMoney.action?txId=" + txRecordId + "&usersId=" + usersId,
            success: function (data) {
                alert(data.json);
                location.reload();
            },
            error:function(){
              alert("请求失败");
            }
        });
    }

    //解除警报--提现;
    function allclearTx(txRecordId) {
        $.ajax({
            url: "${pageContext.request.contextPath}/Product/Admin/checkTxsq!allclearTx.action?txId=" + txRecordId,
            success: function (data) {
                alert(data.json);
                location.reload();
            }
        });
    }
</script>
</body>
</html> 