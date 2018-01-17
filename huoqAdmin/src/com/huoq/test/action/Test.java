/**
 * 
 */
package com.huoq.test.action;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

/**
 * @author 覃文勇 2015年9月22日下午3:08:02
 */
public class Test {

	private static Logger log = Logger.getLogger(Test.class);
	
	public static void main(String[] args) {
		ExecutorService executor = Executors.newCachedThreadPool();
		Task task = new Task();
		Future<Integer> result = executor.submit(task);
		// executor.shutdown();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			log.error("操作异常",e1);
		}

		log.info("主线程在执行任务");

		try {
			log.info("task运行结果" + result.get());
		} catch (InterruptedException e) {
			log.error("操作异常: ",e);
		} catch (ExecutionException e) {
			log.error("操作异常: ",e);
		}

		log.info("所有任务执行完毕");

	}
}

class Task implements Callable<Integer> {
	private static Logger log = Logger.getLogger(Task.class);
	@Override
	public Integer call() throws Exception {
		log.info("子线程在进行计算");
		Thread.sleep(3000);
		int sum = 0;
		for (int i = 0; i < 100; i++)
			sum += i;
		return sum;
	}
}
