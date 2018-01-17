/**
 * 
 */
package com.huoq.common.util;

import org.apache.log4j.Logger;

import com.huoq.newbranch.constants.Constants;

/**
 * @author 覃文勇
 * 2016年1月9日下午2:45:32
 *	自定义锁
 */
public class MySynchronized {
	//通过缓存形式
	private static MyRedis redis=new MyRedis();
	private static final int TIME_OUT = 60;  
	static Logger log = Logger.getLogger(MyRedis.class);
	public static void lock(String lockKey){
		 tryLock(lockKey);
	}
	/** 
     * 锁在给定的等待时间内空闲，则获取锁成功 返回true， 否则返回false 
     * @author 覃文勇 
     * @param lockKey  key
     * @param timeout 超时时间 
     * @return 
     */  
    private static boolean tryLock(String lockKey) {
    	try {
    		int timeout=TIME_OUT*1000;
    		while (timeout>0) {
    			String flag=redis.get(lockKey);
       		if(QwyUtil.isNullAndEmpty(flag)||flag.equals("0")){
            		//设置锁名并设置超时时间
        			redis.setex(lockKey,TIME_OUT,"1");
        			return Boolean.TRUE;
        		}else{
        			Thread.sleep(300);
        		}
        		timeout-=300;
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
			log.info("自定义锁", e);
		}
		return Boolean.FALSE;  
    }
    /** 
     * 释放锁 
     * @param lockKey 锁 
     */  
    public static void unLock(String lockKey) {  
    	try {
    		redis.setex(lockKey,TIME_OUT,"0");
    	} catch (Exception e) {
			log.error("操作异常: ",e);
			unLock(lockKey);
			SMSUtil.sendYzmCZ_SALE(Constants.SMS_REDISERROR_PHONE, "redis请重启");//("15090793102", null,"redis请重启");
			log.info("自定义锁", e);
			try {
				Thread.sleep(10*60*1000);
			} catch (InterruptedException e1) {
				log.error("操作异常",e1);
			}
		}
    }
    
    public static void main(String[] args) {
		
	}
	
}
