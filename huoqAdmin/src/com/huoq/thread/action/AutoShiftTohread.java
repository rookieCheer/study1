package com.huoq.thread.action;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.account.bean.AutoShiftToBean;
import com.huoq.account.bean.CoinPurseFundRecordBean;
import com.huoq.account.bean.CoinpPurseBean;
import com.huoq.account.bean.ShiftToBean;
import com.huoq.common.bean.MyWalletBean;
import com.huoq.common.util.QwyUtil;
import com.huoq.login.bean.RegisterUserBean;
import com.huoq.orm.AutoShiftTo;
import com.huoq.orm.UsersInfo;

/**后台线程自动发放收益<br>
 * @author qwy
 *
 * @createTime 2015-4-28上午9:42:57
 */
@Service
public class AutoShiftTohread implements Runnable {
	private Logger log = Logger.getLogger(AutoShiftTohread.class);
	@Resource
	MyWalletBean walletBean;
	@Resource
	AutoShiftToBean autoShiftToBean;
	@Resource
	RegisterUserBean registerUserBean;
	@Resource
	CoinPurseFundRecordBean cpfrBean;
	@Resource
	CoinpPurseBean coinpPurseBean;
	@Resource
	ShiftToBean shiftToBean;
	private Integer pageSize = 50;
	
	
	@Override
	public void run() {
		
		synchronized (this) {
			/*ApplicationContext context = ApplicationContexts.getContexts();
			//SessionFactory sf = (SessionFactory) context.getBean("sessionFactory");
			PlatformTransactionManager tm = (PlatformTransactionManager) context.getBean("transactionManager");
			TransactionStatus ts = tm.getTransaction(new DefaultTransactionDefinition());*/
			//查询的50个人的ID
			if(new Date().getHours()!=0||new Date().getMinutes()>30){
				return;
			}
			try {
				int currentPage = 0;
				for (;;) {
					String usersIds="";
					currentPage++;
					List<AutoShiftTo> list=autoShiftToBean.findAutoShiftTo(currentPage, pageSize);
					if(QwyUtil.isNullAndEmpty(list)){
						log.info("退出自动转入线程");
						break;
					}
					for (AutoShiftTo autoShiftTo : list) {
						usersIds+=autoShiftTo.getUsersId()+",";
					}
					usersIds=usersIds.substring(0, usersIds.length()-1);
					List<UsersInfo> usersInfos=registerUserBean.findUsersInfosByUsersIds(usersIds);
					Map<String,Object> map=QwyUtil.ListToMap("usersId", list);
					for (UsersInfo usersInfo : usersInfos) {
						AutoShiftTo autoShiftTo=(AutoShiftTo) map.get(usersInfo.getUsersId()+"");
						//walletBean.subLeftMoney(autoShiftTo.getUsersId(), QwyUtil.calcNumber(usersInfo.getLeftMoney(), autoShiftTo.getLeftMoney(), "-",2).doubleValue(), "to",  "零钱包自动转入", "零钱包自动转入");
						Double num=QwyUtil.calcNumber(usersInfo.getLeftMoney(), autoShiftTo.getLeftMoney(), "-",2).doubleValue();
						if(num>0){
							shiftToBean.shift(autoShiftTo.getUsersId(), num);
						}
					}
					/*tm.commit(ts);*/
					list=null;
					
				}
			} catch (Exception e) {
				/*tm.rollback(ts);*/
				log.error("进入修改产品状态的后台线程异常: ",e);
			}
		
		}
	}
	
	public static void main(String[] args) {
		
	}

}
