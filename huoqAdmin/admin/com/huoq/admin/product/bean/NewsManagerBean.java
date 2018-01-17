package com.huoq.admin.product.bean;

import com.huoq.admin.product.dao.NewsManagerDAO;
import com.huoq.orm.News;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class NewsManagerBean {

	@Resource
	NewsManagerDAO dao;


	/**
	 * 保存新闻
	 * @param news
	 * @return
	 */
	public String saveNews(News news){
		news.setStatus("0");//新闻状态:“0”可用
		news.setInsertTime(new Date());
		return dao.save(news);
	}
	
	/**
	 * 修改新闻
	 * @param news
	 * @return
	 */
	public boolean updateNotice(News news) throws Exception {
		News n = findById(news.getId());
		news.setInsertTime(n.getInsertTime());
		news.setImgUrl(n.getImgUrl());
		news.setNote(n.getNote());
		news.setType(n.getType());
		news.setUpdateTime(new Date());
		dao.update(news);
		return true;
	}


	/**
	 * 展示新闻列表
	 */
	public List<News> findNewsList(){
		StringBuffer buffer=new StringBuffer();
		buffer.append(" FROM News n ");
		buffer.append(" ORDER BY n.insertTime DESC, n.status ");
		return dao.LoadAll(buffer.toString(),null);
	}


	/**
	 * 通过id查询
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public News findById(String id) throws Exception{
		return (News) dao.findById(new News(),id);
	}



}
