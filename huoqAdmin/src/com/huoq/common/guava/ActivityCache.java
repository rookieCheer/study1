/**
 * 
 */
package com.huoq.common.guava;



import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;


/**
 * @author gaoshuang
 * @param <K>
 * @email gaoshvng@163.com
 * @createTime 2017年5月31日 上午11:48:15
 */
public class ActivityCache extends GuavaAbstractLoadingCache {
	
	private static Logger LOGGER = Logger.getLogger(ActivityCache.class);

	private static ActivityCache activityCache = null;

	public static synchronized ActivityCache getInstance() {
		if (activityCache == null) {
			activityCache = new ActivityCache();
			activityCache.setTimeUnit(TimeUnit.DAYS);
			//设置6天缓存，6天以外的继续落数据
			activityCache.setExpireAfterWriteDuration(6);
		}
		return activityCache;
	}

	// 未查询到的记录登记未第一次查询 默认初始值未0
	protected Integer loadData(Object key) {
		return 0;
	}
	
	public  String getkey(String deviceId) {
		return "huoq" + deviceId.hashCode();
	}

}
