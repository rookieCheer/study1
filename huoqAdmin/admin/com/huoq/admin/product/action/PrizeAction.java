package com.huoq.admin.product.action;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;
import com.huoq.admin.product.bean.PrizeBean;
import com.huoq.common.action.BaseAction;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.Prize;
import com.huoq.orm.UsersAdmin;


/**
 * 奖品记录表
 * @author 
 */

@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
@Results({ 
	@Result(name = "add", value = "/Product/Admin/operationManager/prizeadd.jsp"),
	@Result(name = "update", value = "/Product/Admin/operationManager/prizeUpdate.jsp")
})
public class PrizeAction extends BaseAction {
	@Resource
	private PrizeBean bean;
	
	private String prizeName;
	private String prizeType;
	private Double winningRate;
	private Prize prize;
	
	
	public String showPrize(){
		return "add";
	}
	public String addPrize(){
		String json="";
		try {
			UsersAdmin admin=(UsersAdmin)getRequest().getSession().getAttribute("usersAdmin");
			if(QwyUtil.isNullAndEmpty(admin)){
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				//管理员没有登录;
				return null;
			}
			if(!QwyUtil.isNullAndEmpty(prize)){
				if(!QwyUtil.isNullAndEmpty(prize.getPrizeName())){
					if(!QwyUtil.isNullAndEmpty(prize.getType())){
						if(!QwyUtil.isNullAndEmpty(prize.getWinningRate())){
							prize.setInsertTime(new Date());
							prize.setStatus("1");
							String id=bean.addHdFlag(prize);
							if(!QwyUtil.isNullAndEmpty(id)){
								request.setAttribute("message", "添加成功");	
							}else{
								request.setAttribute("message", "添加失败");
							}
						}else{
							request.setAttribute("message", "中奖概率不能为空");
						}
					}else{
						request.setAttribute("message", "类型不能为空");
					}
				}else{
					request.setAttribute("message", "奖品名称不能为空");
				}
				
			}else{
				request.setAttribute("message", "添加失败");
			}
			
			
			
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return "add";
	}
	
	/**
	 * 获取奖品
	 * @return
	 */
	public String getPrizeList(){
		String json = "";
		try {
			
			UsersAdmin users = (UsersAdmin) getRequest().getSession()
					.getAttribute("usersAdmin");
			if (QwyUtil.isNullAndEmpty(users)) {
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				//管理员没有登录;
				return null;
			}
			String superName="0EA5D6BC23E8EEC78F62546B9F68BABFA96976B775889BA625DB6D764FD0DBD42A1C0F45F85B0DE8";
			if(!superName.equals(users.getUsername())){
				if(isExistsQX("prize更改", users.getId())){
					getRequest().setAttribute("err", "您没有操作该功能的权限!");
					return "err";
				}
			}
			String prizeName = QwyUtil.isNullAndEmpty(prize)?null:prize.getPrizeName();
			String prizeType = QwyUtil.isNullAndEmpty(prize)?null:prize.getPrizeType();
			getRequest().setAttribute("prizeName", prizeName);
			getRequest().setAttribute("prizeType", prizeType);
			
			List<Prize> list = bean.findPrize(prizeName, prizeType);
			getRequest().setAttribute("list", list);
			return "update";
			
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("err", "查询记录异常");
		}
		
		return "update";
	}
	
	/**
	 * 更改奖品中奖的概率
	 * @return
	 */
	public String modifyPrize(){
		String json="";
		try {
			if(!QwyUtil.isNullAndEmpty(prize)){
					if(bean.modifyPrize(prize.getId(),prize.getWinningRate(),prize.getPayRate())){
						json = QwyUtil.getJSONString("ok","修改成功");
						}else{
							json = QwyUtil.getJSONString("error","修改失败");
						}
			}else{
				json = QwyUtil.getJSONString("error","prize不能为空");
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "prize修改异常");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return null;
	}
	
	
	
	/**
	 * 根据id修改状态
	 * @return
	 */
	public String updateStatuById() {
		String json = "";
		try {
			UsersAdmin users = (UsersAdmin) getRequest().getSession()
					.getAttribute("usersAdmin");
			if (QwyUtil.isNullAndEmpty(users)) {
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				//管理员没有登录;
				return null;
			}
			if(bean.updateStatusById(prize.getId())){
				request.setAttribute("update", "ok");
				json = QwyUtil.getJSONString("ok", "成功");
				QwyUtil.printJSON(getResponse(), json);
				return null;
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
			log.error("操作异常: ",e);
		}
		json = QwyUtil.getJSONString("error", "失败");
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return null;	
	}
	
	

	public Prize getPrize() {
		return prize;
	}

	public void setPrize(Prize prize) {
		this.prize = prize;
	}
	
	
	public String getPrizeName() {
		return prizeName;
	}
	public void setPrizeName(String prizeName) {
		this.prizeName = prizeName;
	}
	public String getPrizeType() {
		return prizeType;
	}
	public void setPrizeType(String prizeType) {
		this.prizeType = prizeType;
	}
	public Double getWinningRate() {
		return winningRate;
	}
	public void setWinningRate(Double winningRate) {
		this.winningRate = winningRate;
	}
	
	
}
