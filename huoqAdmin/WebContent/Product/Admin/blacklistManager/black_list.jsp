<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/zlqEl.tld" prefix="myel" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css"/>
    <link href="${pageContext.request.contextPath}/Product/Admin/css/product.css" rel="stylesheet" type="text/css"/>
    <script src="${pageContext.request.contextPath}/Product/Admin/js/jquery-1.9.1.min.js"></script>
    <script src="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.standalone.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Product/Admin/plugins\kalendae\build\kalendae.css"
          type="text/css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/Product/js/alert.js"></script>
    <script src="${pageContext.request.contextPath}/js/artDialog4.1.7/jquery.artDialog.js?skin=blue"></script>
    <title>黑名单记录</title>
    <style type="text/css">
        .a {
            color: blue;
            text-decoration: underline;
        }
    </style>
    <script type="text/javascript">
        function ireportDo() {
            var username = document.getElementById("username").value;
            var insertTime = document.getElementById("insertTime").value;
            var status = $('input[name=status]:checked').val();
            var url = '${pageContext.request.contextPath}/Product/Admin/blackList!iportTable.action?currentPage=${currentPage}&username=' + username + '&insertTime=' + insertTime + '&status=' + status;
            var list = "${list}";
            if (list != null && list != "[]") {
                var my = art.dialog({
                    title: '提示',
                    content: document.getElementById("psi_load"),
                    height: 60,
                    lock: true,
                    cancel: false
                });
                $.post(url, $("#frm").serialize(), function (data) {
                    my.close();
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
</head>
<body>
<%-- <jsp:include page="top.jsp" /> --%>
<div class="center">
    <jsp:include page="/Product/Admin/common/head.jsp"/>
    <div class="main" align="center">
        <h1 style="text-align: center;">黑名单记录</h1>
        <div id="div_condition">
            <label>黑名单:<input type="text" name="username" id="username" value="${username}" maxlength="11">
                <span>时间:</span> <input id="insertTime" name="insertTime" type="text" value="${insertTime}">
                <input type="button" value="search" id="frm" onclick="Byname()"></label>&nbsp;&nbsp;
            <label><input type="radio" id="" value="all" name="status" checked="checked">全部</label>&nbsp;&nbsp;
            <label><input type="radio" value="0" name="status">使用中</label>&nbsp;&nbsp;
            <label><input type="radio" value="1" name="status">已解除</label>&nbsp;&nbsp;
            <input type="button" value="导出报表" onclick="ireportDo()">
        </div>

        <table style="width:100%;margin-top: 20px;text-align: center;" border="1px;">
            <tr>
                <td width="2%">序号</td>
                <td width="6%">黑名单</td>
                <td width="9%">创建时间</td>
                <td width="9%">更新时间</td>
                <td width="3%">状态</td>
                <td width="55%">备注</td>
                <td width="12%">描述</td>
                <td width="4%">操作</td>
            </tr>
            <c:forEach items="${list}" var="list" varStatus="i">
                <tr>
                    <td>${i.count + (pageUtil.currentPage-1)*pageUtil.pageSize}</td>
                    <td>
                            <%-- ${myel:jieMiUsername(list.username)}--%>
                            <%-- ${list.username} --%>
                            <a target="_blank" class="a" href="${pageContext.request.contextPath}/Product/Admin/userStat!loadUserInfo.action?username=${list.username}">${list.username}</a>
                    </td>
                    <td>
                           <fmt:formatDate value="${list.insertTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                    </td>
                    <td><fmt:formatDate value="${list.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                    <td>
                        <c:choose>
                            <c:when test="${list.status eq 0}">使用中</c:when>
                            <c:when test="${list.status eq 1}">已解除</c:when>
                        </c:choose>
                    </td>
                    <td>${list.note}</td>
                    <td>${list.description}</td>
                    <td>
                    <a class="a" id="a_${list.id}" href="javascript:descriptionAdd('${list.username}','${list.id}');">添加描述</a><br/>
                    <c:if test="${list.status eq 0}">
                    		<a class="a" id="a_${list.id}" href="javascript:updateStatus('${list.id}');">解除黑名单</a>
                    </c:if>
                    </td>
                   <%-- <td>
                        <c:choose>
                            <c:when test="${list.status eq 0}"><a class="a" href="javascript:shenhe('${list.id}','1');">解除</a></c:when>
                            <c:when test="${list.status eq 1}"><a class="a" href="javascript:shenhe('${list.id}','0');">拉黑</a></c:when>
                        </c:choose>
                    </td>--%>
                </tr>
            </c:forEach>
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
    
    
    <div  id="div_desc" style=" width: 300px; height: 200px; display: none;"  >
    	<input id="blackid" class="blackid" type="hidden"  />
    	<p style="color: red;">注意：添加描述会直接覆盖之前的内容，如想保留，则需要把之前的内容与新添加的内容一起添加！</p>
    	<table style="width:100%;margin-top: 20px;text-align: center;" >
    		<tr>
    			<td style="width: 80px; ">用户：</td>
    			<td><span id="blackusername" class="blackusername"></span> </td>
    		</tr>
    		<tr>
    			<td style="width: 80px; ">描述：</td>
    			<td><textarea id="tet_description"  class="description" cols="30" rows="5"></textarea> </td>
    		</tr>
    		<tr>
    			<td colspan="2">
    			<input type="button" value="提交" onclick="updateDescription()">
    			</td>
    			
    		</tr>
    	</table>
    </div>
</div>
<div id="psi_load" style="display:none;">
    <img width="18" src="${pageContext.request.contextPath}/Product/images/progress.gif"><span
        id="psi_tip">正在导出,请稍等...</span>
</div>
<script type="text/javascript">
    $("#div_condition input[name='status']").click(function () {
        window.location.href = "${pageContext.request.contextPath}/Product/Admin/blackList!listBlack.action?username=" + $("#username").val() + "&status=" + $('input[name="status"]:checked').val() + "&insertTime=" + $('#insertTime').val();
    });
    $("#div_condition input[name='status']").each(function () {
        if ($(this).attr("value") == "${status}" || "${status}" == "") {
            $(this).attr("checked", true);
        }
    });
    function Byname() {
        window.location.href = "${pageContext.request.contextPath}/Product/Admin/blackList!listBlack.action?username=" + $("#username").val() + "&status=" + $('input[name="status"]:checked').val() + "&insertTime=" + $('#insertTime').val();
    }

    //人工审核黑名单记录
    function shenhe(blackListId, status) {
        if (status == '0') {
            if (!confirm("确定拉黑？")) {
                return false;
            }
        } else if (status == '1') {
            if (!confirm("确定解除？")) {
                return false;
            }
        }
        $.ajax({
            url: "${pageContext.request.contextPath}/Product/Admin/blackList!changeBlackListStatus.action?blackListId=" + blackListId + "&status=" + status,
            success: function (data) {
                alert(data.json);
                location.reload();
            }
        });
    }
    function descriptionAdd(username,blackListId) {
    	var dialog = art.dialog({
    	    title: '添加描述',
    	    content: document.getElementById("div_desc")
    	});
    	$("#blackid").val(blackListId);
    	$("#blackusername").html(username);
    }
    function updateDescription() {
    	var id=$("#blackid").val();
    	var description=$("#tet_description").val();
    	 $.ajax({
             url: "${pageContext.request.contextPath}/Product/Admin/blackList!descriptionAdd.action",
           	type:"post",
           	data:"blackListId=" + id + "&description=" + description,
             success: function (data) {
                 alert(data.json);
                 location.reload();
             }
         });
    	//window.location.href = "${pageContext.request.contextPath}/Product/Admin/blackList!descriptionAdd.action?blackListId=" + id + "&description=" + description
    }
    
    
  //更改黑名单状态
    function updateStatus(id){
    	
    	if(!confirm("确定修改状态？")){
    		return false;
    	};
		$.ajax({
			type : "get",
			url : "${pageContext.request.contextPath}/Product/Admin/blackList!updateStatus.action?id="+id,
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
    
</script>
<script type="text/javascript">
    var k4 = new Kalendae.Input("insertTime", {
        attachTo: document.body,
        months: 2,//多少个月显示出来,即看到多少个日历
        mode: 'range'
        /* selected:[Kalendae.moment().subtract({d:7}), Kalendae.moment().add({d:0})] */
    });
    //为input框绑定回车事件，当用户在文本框中输入搜索关键字时，按回车键，即可触发search():

    //回车绑定
    $("#username,#insertTime").keydown(function (event) {
        if (event.keyCode == 13) {
            Byname();
        }
    });
</script>
</body>
</html>