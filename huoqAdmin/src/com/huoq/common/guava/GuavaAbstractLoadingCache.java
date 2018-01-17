/**
 * 
 */
package com.huoq.common.guava;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * 抽象Guava缓存类、缓存模板。 子类需要实现loadData(key)，从数据库或其他数据源中获取数据。
 * 子类调用getValue(key)方法，从缓存中获取数据，并处理不同的异常，比如value为null时的InvalidCacheLoadException异常。
 * 
 * @author gaoshuang
 * @email gaoshvng@163.com
 * @createTime 2017年5月31日 上午11:41:52
 */
public abstract class GuavaAbstractLoadingCache {

	private static Logger LOGGER = Logger.getLogger(GuavaAbstractLoadingCache.class);

	// 用于初始化cache的参数及其缺省值
	private int maximumSize = 300; // 最大缓存条数，子类在构造方法中调用setMaximumSize(int
									// size)来更改
	private int expireAfterWriteDuration = 1; // 数据存在时长，子类在构造方法中调用setExpireAfterWriteDuration(int
												// duration)来更改
	private TimeUnit timeUnit = TimeUnit.MINUTES; // 时间单位（分钟）

	private Date resetTime; // Cache初始化或被重置的时间
	private long highestSize = 0; // 历史最高记录数
	private Date highestTime; // 创造历史记录的时间

	private LoadingCache<Object, Object> cache;

	

	/**
	 * 通过调用getCache().get(key)来获取数据
	 * 
	 * @return cache
	 */
	public LoadingCache<Object, Object> getCache() {
		if (cache == null) { // 使用双重校验锁保证只有一个cache实例
			synchronized (this) {
				if (cache == null) {
					cache = CacheBuilder.newBuilder().maximumSize(maximumSize) // 缓存数据的最大条目，也可以使用.maximumWeight(weight)代替
							.expireAfterWrite(expireAfterWriteDuration, timeUnit) // 数据被创建多久后被移除
							.recordStats() // 启用统计
							.build(new CacheLoader<Object, Object>() {
								@Override
								public Object load(Object key) throws Exception {
									return loadData(key);
								}
							});
					this.resetTime = new Date();
					this.highestTime = new Date();
				}
			}
		}

		return cache;
	}

	/**
	 * 根据key从数据库或其他数据源中获取一个value，并被自动保存到缓存中。连同key一起被加载到缓存中的。
	 * 
	 * @param key
	 * @return
	 */
	protected abstract Object loadData(Object key);

	/**
	 * 从缓存中获取数据（第一次自动调用loadData从外部获取数据），并处理异常
	 * 
	 * @param key
	 * @return Value
	 * @throws ExecutionException
	 */
	public Object getValue(Object key) {
		Object result = null;
		try {
			result = getCache().get(key);
			if (getCache().size() > highestSize) {
				highestSize = getCache().size();
				highestTime = new Date();
			}
		} catch (ExecutionException e) {
			LOGGER.error("获取本地缓存出错！！！" + e.getMessage());
		}
		return result;
	}
	/**
	 * 添加缓存数据
	 * 
	 * @param k
	 * @param v
	 */
	public void put(Object k, Object v) {
		cache.put(k, v);
	}
	
	public long getHighestSize() {
		return highestSize;
	}

	public Date getHighestTime() {
		return highestTime;
	}

	public Date getResetTime() {
		return resetTime;
	}

	public void setResetTime(Date resetTime) {
		this.resetTime = resetTime;
	}

	public int getMaximumSize() {
		return maximumSize;
	}

	public int getExpireAfterWriteDuration() {
		return expireAfterWriteDuration;
	}
	
	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	/**
	 * 设置过期时间单位
	 */
	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}
	/**
	 * 设置最大缓存条数
	 * 
	 * @param maximumSize
	 */
	public void setMaximumSize(int maximumSize) {
		this.maximumSize = maximumSize;
	}

	/**
	 * 设置数据存在时长（分钟）
	 * 
	 * @param expireAfterWriteDuration
	 */
	public void setExpireAfterWriteDuration(int expireAfterWriteDuration) {
		this.expireAfterWriteDuration = expireAfterWriteDuration;
	}

	

}