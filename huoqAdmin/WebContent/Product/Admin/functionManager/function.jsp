<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>功能操作</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.7.2.min.js"></script>
<link href="${pageContext.request.contextPath}/Product/Admin/css/public.css" rel="stylesheet" type="text/css" />
<link href="${pageContext.request.contextPath}/Product/Admin/css/product.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/Product/js/alert.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/Product/css/alert.css">
<script src="${pageContext.request.contextPath}/js/artDialog4.1.7/jquery.artDialog.js?skin=default"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/qwyUtilJS/qwyUtil.js"></script>
</head>
<body>

<div class="center">	
	<jsp:include page="/Product/Admin/common/head.jsp"/>		
	<div class="main" style="text-align: center;">
		<h1 style="text-align: center;">功能操作</h1>
		<!-- <label><input type="button" value="启动所有线程" id="startAllThread"> </label> -->
		<label><input type="button" value="启【更新结算理财产品】线程" id="startUpdateProductThread"> </label>
		<label><input type="button" value="启动【发放收益】线程" id="startSendProfitThread"> </label>
		<label><input type="button" value="启动【扫描过期投资券】线程" id="startFinishCouponThread"> </label>
		<!-- <label><input type="button" value="启动【扫描过期加息券】线程" id="startFinishInterestCouponThread"> </label> -->
		<label><input type="button" value="设置用户归属地" id="setUsersMobileLocation"> </label>
		<label><input type="button" value="启动【更新用户信息】线程" id="startUpdateUsersInfoThread"> </label>
		<label><input type="button" value="启动【自动计算收益】线程" id="autoSendRatesThread"> </label>
		<label><input type="button" value="启动【自动发放收益】线程" id="coinPurseRatesThread"> </label><br/>
		<!-- <label><input type="button" value="启动【自动转入】线程" id="autoShiftTohread"> </label> -->
		<!-- <label><input type="button" value="启动【发送新手投资到期短信】线程" id="sendFreshmanMessegeThread"> </label> -->
		<!-- <label><input type="button" value="启动【发放喵币】线程" id="sendMcoinThread"> </label> -->
		<label><input type="button" value="启动【发放邀请投资奖励】线程" id="sendVIPGradeThread"> </label>
		<!-- <label><input type="button" value="启动喵币每天明细表线程" id="meowCurrencyReport"> </label> -->
		<!-- <label><input type="button" value="关闭一年清空喵币" id="cleanMCoin"> </label> -->	
		<label><input type="button" value="启动【每日各平台统计】" id="startEveryDayStatsOperate"> </label>	
		<label><input type="button" value="启动【更新昨日渠道统计（安卓）】" id="startEveryDayChannelOperate"> </label>
		<label><input type="button" value="启动【更新昨日各个渠道统计】" id="updateQdtjPlatform"> </label>
		<!-- <label><input type="button" value="启动【查询待处理的连连充值记录】线程" id="lianLianCzThread"> </label> -->
		<label><input type="button" value="启动【每日各渠道统计（IOS）】" id="startEveryDayIphoneOperate"> </label>
		<label><input type="button" value="启动【自动发布预约产品功能】" id="autoReleaseProductOperate"> </label>
		<label><input type="button" value="启动【提现查询接口】线程" id="startTxQueryThread"> </label> 
		<label><input type="button" value="启动【固定线程入口】线程" id="FixedOrderThread"> </label>
		<label><input type="button" value="启动【扫描异常充值用户】线程" id="ScanExcpCzUsersThread"> </label>
		<label><input type="button" value="启动【发短信】线程" id="SendsmsThread"> </label>
		<label><input type="button" value="启动【新华金典日收益加成奖励】线程" id="SendGdpThread"> </label>
		<!-- <label><input type="button" value="启动【土豪星球奖励共享】线程" id="SendRichThread"> </label> -->
		<label><input type="button" value="启动【修复异常资金账户】线程" id="RepairUsersMoney"> </label>
		<label><input type="button" value="启动【每日最高新手奖励发放】线程" id="TopDayInveste"> </label>
		<label><input type="button" value="启动【端午节充值达标发送理财券】线程" id="sendCzCoupon"> </label>
		<!-- <label><input type="button" value="启动【自动发放返款奖励】线程" id="AutoSendCoupon"> </label> --> 
		<label><input type="button" value="启动【发放奖励】线程" id="sendRewardMoney"> </label>
	<!-- 	<label><input type="button" value="启动【新华金典网-1月28日早上8点】线程" id="MessageSendThreadEight"> </label>
		<label><input type="button" value="启动【新华金典网-2月3日晚上21点】线程" id="MessageSendThreadTen"> </label> -->
		<div id="psi_load" style="display:none;">
		<img width="18" src="${pageContext.request.contextPath}/Product/Admin/img/progress.gif"><span  id="psi_tip">正在执行,请稍等...</span>
		</div>
		<p>&nbsp;</p>
		<label>充值的平台订单号:<input type="text" value="" placeholder="输入充值的平台订单号" id="czOrderId" style="  width: 350px;"> <input type="button" value="宝付充值接口查询" id="queryBFCzRecord"></label><br>
		<!-- <label>提现的平台订单号:<input type="text" value="" placeholder="输入提现的平台订单号" id="txOrderId" style="  width: 350px;"> <input type="button" value="易宝提现接口查询" id="queryYbTxRecord"></label><br>
		<label>用户ID:<input type="text" value="" placeholder="输入用户ID" id="usersId" style="  width: 350px;"> <input type="button" value="用户绑卡接口查询" id="queryBindList"></label><br> -->
		<div id="ybResult">
		</div>
	</div>
</div>

<script type="text/javascript">
var text = "<img src='${pageContext.request.contextPath}/Product/images/progress.gif' width='24' height='24'> <span style='font-family:楷体;font-size:22px;'>正在设置,请稍等...</span>";
//启动更新结算理财产品线程;
$("#setUsersMobileLocation").click(function(){
	var url = "${pageContext.request.contextPath}/Product/Admin/userStat!setMobileLocation.action";
	var id = chuangkou(text,"温馨提示","",0,true);
	$.post(url,null,function(data){
		closeAlert(id);
		alert(data.json);//结果!
	}); 
});
//启动所有线程;
$("#startAllThread").click(function(){
	/* var url = "${pageContext.request.contextPath}/Product/Admin/startThread!startAllThread.action";
	$.post(url,null,function(data){
		alert(data.json);//结果!
	});  */
});
//启动更新结算理财产品线程;
$("#startUpdateProductThread").click(function(){
	var url = "${pageContext.request.contextPath}/Product/Admin/startThread!startUpdateProductThread.action";
	$.post(url,null,function(data){
		alert(data.json);//结果!
	}); 
});
//启动发放收益线程
$("#startSendProfitThread").click(function(){
	var url = "${pageContext.request.contextPath}/Product/Admin/startThread!startSendProfitThread.action";
	$.post(url,null,function(data){
		alert(data.json);//结果!
	}); 
});
$("#meowCurrencyReport").click(function(){
	var url = "${pageContext.request.contextPath}/Product/Admin/startThread!meowCurrencyReport.action";
	$.post(url,null,function(data){
		alert(data.json);//结果!
	}); 
});

//VIP等级制度计算
$("#sendVIPGradeThread").click(function(){
	var url = "${pageContext.request.contextPath}/Product/Admin/startThread!sendInviteEarnThread.action";
	$.post(url,null,function(data){
		alert(data.json);//结果!
	}); 
});

//启动发放邀请好友投资奖励
$("#startFinishCouponThread").click(function(){
	var url = "${pageContext.request.contextPath}/Product/Admin/startThread!startFinishCouponThread.action";
	$.post(url,null,function(data){
		alert(data.json);//结果!
	}); 
});
//启动扫描过期投资券线程
$("#startFinishInterestCouponThread").click(function(){
	var url = "${pageContext.request.contextPath}/Product/Admin/startThread!startFinishInterestCouponThread.action";
	$.post(url,null,function(data){
		alert(data.json);//结果!
	}); 
});
$("#startUpdateUsersInfoThread").click(function(){
	var url = "${pageContext.request.contextPath}/Product/Admin/startThread!startUpdateUsersInfoThread.action";
	$.post(url,null,function(data){
		alert(data.json);//结果!
	}); 
});

$("#autoSendRatesThread").click(function(){
	var url = "${pageContext.request.contextPath}/Product/Admin/startThread!startAutoSendRatesThread.action";
	$.post(url,null,function(data){
		alert(data.json);//结果!
	}); 
});

$("#coinPurseRatesThread").click(function(){
	var url = "${pageContext.request.contextPath}/Product/Admin/startThread!startSendCoinPurseRatesThread.action";
	$.post(url,null,function(data){
		alert(data.json);//结果!
	}); 
});

$("#autoShiftTohread").click(function(){
	var url = "${pageContext.request.contextPath}/Product/Admin/startThread!startAutoShiftTohread.action";
	$.post(url,null,function(data){
		alert(data.json);//结果!
	}); 
});

$("#sendFreshmanMessegeThread").click(function(){
	var url = "${pageContext.request.contextPath}/Product/Admin/startThread!startSendFreshmanMessegeThread.action";
	$.post(url,null,function(data){
		alert(data.json);//结果!
	}); 
});
$("#lianLianCzThread").click(function(){
	var url = "${pageContext.request.contextPath}/Product/Admin/startThread!startLianLianCzThread.action";
	$.post(url,null,function(data){
		alert(data.json);//结果!
	}); 
});
$("#sendMcoinThread").click(function(){
	var url = "${pageContext.request.contextPath}/Product/Admin/startThread!sendMcoinThread.action";
	$.post(url,null,function(data){
		console.log(data);//结果!
	}); 
});
$("#startEveryDayStatsOperate").click(function(){
	var url="${pageContext.request.contextPath}/Product/Admin/startThread!startEveryDayStatsOperate.action";
	$.post(url,null,function(data){
		alert(data.json);//结果!
		window.location.reload();
	}); 
});
$("#startEveryDayChannelOperate").click(function(){
	var url="${pageContext.request.contextPath}/Product/Admin/startThread!startEveryDayChannelOperate.action";
	$.post(url,null,function(data){
		alert(data.json);//结果!
		window.location.reload();
	}); 
});
$("#updateQdtjPlatform").click(function(){
	var url="${pageContext.request.contextPath}/Product/Admin/startThread!updateQdtjPlatform.action";
	$.post(url,null,function(data){
		alert(data.json);//结果!
		window.location.reload();
	}); 
});
$("#startEveryDayIphoneOperate").click(function(){
	var url="${pageContext.request.contextPath}/Product/Admin/startThread!startEveryDayIphoneOperate.action";
	$.post(url,null,function(data){
		alert(data.json);//结果! 
		window.location.reload();
	}); 
});
$("#autoReleaseProductOperate").click(function(){
	var url="${pageContext.request.contextPath}/Product/Admin/startThread!autoReleaseProductOperate.action";
	$.post(url,null,function(data){
		alert(data.json);//结果!
		window.location.reload();
	});
});
//启动提现查询接口 
  $("#startTxQueryThread").click(function(){
	var url = "${pageContext.request.contextPath}/Product/Admin/startThread!startTxQueryThread.action";
	$.post(url,null,function(data){
		alert(data.json);//结果!
	}); 
});  
//固定线程入口
  $("#FixedOrderThread").click(function(){
      var url = "${pageContext.request.contextPath}/Product/Admin/startThread!FixedOrderThread.action";
      $.post(url,null,function(data){
          alert(data.json);//结果!
      });
  });
  
//扫描异常充值用户
  $("#ScanExcpCzUsersThread").click(function(){
      var url = "${pageContext.request.contextPath}/Product/Admin/startThread!ScanExcpCzUsersThread.action";
      $.post(url,null,function(data){
          alert(data.json);//结果!
      });
  });

// 未投资用户发送短信
  $("#SendsmsThread").click(function(){
		var url = "${pageContext.request.contextPath}/Product/Admin/startThread!SendsmsThread.action";
		$.post(url,null,function(data){
			alert(data.json);//结果!
		}); 
	});  

//  土豪星球奖励共享发放线程 
  $("#SendRichThread").click(function(){
		var url = "${pageContext.request.contextPath}/Product/Admin/startThread!SendRichThread.action";
		$.post(url,null,function(data){
			alert(data.json);//结果!
		}); 
	});  
	
//土豪星球奖励共享发放线程   
  $("#RepairUsersMoney").click(function(){
		var url = "${pageContext.request.contextPath}/Product/Admin/startThread!RepairUsersMoney.action";
		$.post(url,null,function(data){
			alert(data.json);//结果!
		}); 
	});  
	
	// 新手活动 每日最高奖励发放线程 
  $("#TopDayInveste").click(function(){
		var url = "${pageContext.request.contextPath}/Product/Admin/startThread!TopDayInveste.action";
		$.post(url,null,function(data){
			alert(data.json);//结果!
		}); 
	});  
	
	//返款用户奖励线程
	$("#AutoSendCoupon").click(function(){
		var url = "${pageContext.request.contextPath}/Product/Admin/startThread!AutoSendCoupon.action";
		$.post(url,null,function(data){
			alert(data.json); //结果
		})
	})
	
	//Gdp新华金典日 收益加成奖励
	$("#SendGdpThread").click(function(){
		var url = "${pageContext.request.contextPath}/Product/Admin/startThread!sendGdpThread.action";
		$.post(url,null,function(data){
			alert(data.json);//结果!
		}); 
	});  
	
	//发放奖励线程
	$("#sendRewardMoney").click(function(){
		var url = "${pageContext.request.contextPath}/Product/Admin/startThread!sendRewardMoney.action";
		$.post(url,null,function(data){
			alert(data.json);//结果!
		}); 
	});  
	
	//充值达标送理财券线程
	$("#sendCzCoupon").click(function(){
		var url = "${pageContext.request.contextPath}/Product/Admin/startThread!sendCzCoupon.action";
		$.post(url,null,function(data){
			alert(data.json);//结果!
		}); 
	});  
  
/* //新华金典网-1月28日早上8点
$("#MessageSendThreadEight").click(function(){
	var url = "${pageContext.request.contextPath}/Product/Admin/messageSend!MessageSendThreadEight.action";
	$.post(url,null,function(data){
		alert(data.json);//结果!
	}); 
}); 

//新华金典网-2月3日早上10点
$("#MessageSendThreadTen").click(function(){
	var url = "${pageContext.request.contextPath}/Product/Admin/messageSend!MessageSendThreadTen.action";
	$.post(url,null,function(data){
		alert(data.json);//结果!
	}); 
}); */

// var randomStr = randomString(16);
//  var requestStr="";
//  $("#startTxQueryThread").click(function(){
//  if(requestStr==randomStr){
// 		console.log("提现请勿重复提交");						
// 		return false;
// 	}
//  requestStr = randomStr;
//  var url = "${pageContext.request.contextPath}/Product/Admin/startThread!startTxQueryThread.action";
//  var su="";
//  $.ajax({	
// 		url:url,
// 		type:"post",
// 		beforeSend:function(XMLHttpRequest){
			
// 			$("#psi_tip").text("正在操作,请稍等...");
// 			var dialog = art.dialog({id: 'dialog_goToWithdraw',title: false,lock:true,fixed:true,content:document.getElementById("psi_load")});
// 		},
// 		success:function(data){
// 			su=data;
// 		},
// 		complete:function(XMLHttpRequest,textStatus){
// 			requestStr = "";
// 			randomStr = randomString(16);
// 			art.dialog({id: 'dialog_goToWithdraw'}).close();
// 			/*  alert(su.status); */
// 			if("ok"==su.status){
// 				alert("执行完毕");
// 			}else{
// 				alert(su.json);
// 			};
// 		}
// 	});

//  }); 

//查询宝付接口的充值记录;
$("#queryBFCzRecord").click(function(){
	//alert($("#czOrderId").val());
	var url = "${pageContext.request.contextPath}/Product/Admin/function!queryPay.action";
	$.post(url,"orderid="+$("#czOrderId").val(),function(data){
		//alert(data.json);//结果!
		$("#ybResult").text(data.json);
	}); 
});
//查询易宝的提现记录;
$("#queryYbTxRecord").click(function(){
	var url = "${pageContext.request.contextPath}/Product/Admin/function!withdrawQuery.action";
	$.post(url,"orderid="+$("#txOrderId").val(),function(data){
		//alert(data.json);//结果!
		$("#ybResult").text(data.json);
	}); 
});
//查询用户的绑卡记录
$("#queryBindList").click(function(){
	var url = "${pageContext.request.contextPath}/Product/Admin/function!bindList.action";
	$.post(url,"usersId="+$("#usersId").val(),function(data){
		//alert(data.json);//结果!
		$("#ybResult").text(data.json);
	}); 
});
$("#cleanMCoin").click(function(){
	var url="${pageContext.request.contextPath}/Product/Admin/mCoinDayDetail!startCelanAllMcoin.action";
	$.post(url,null,function(data){
		alert(data.json);//结果!
		window.location.reload()
	}); 
	
	
});



$(function(){
	
	$.ajax({
		type : "post",
		async : false,
		url : "${pageContext.request.contextPath}/Product/Admin/mCoinDayDetail!getMCoinCleanState.action",
		success : function(data) {
			if(data.json=="1"){
				$("#cleanMCoin").val("关闭一年清空喵币");

			}
			else{
				
				$("#cleanMCoin").val("开启一年清空喵币");
			}
			
		}
	});
})
</script>
</body>
</html>