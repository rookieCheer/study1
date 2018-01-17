package com.huoq.orm;

import java.io.Serializable;
import java.util.Date;

import com.huoq.common.util.QwyUtil;
/**
 * bananer图片
 * @author 覃文勇
 * 2015年7月3日 10:26:52
 */
public class Banner implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String title;//'标题'
	private String imgURL;//图片地址
	private String status;//状态0使用中1待使用
	private String type;//类型0网站1手机 2喵商城
	private Date insertTime;//插入时间
	private Date updateTime;//修改时间
	private String description;//描述
	private String pt;//平台
	private String noticeId;//公告ID
	private String hdUrl;//活动Url
	private Notice notice;//公告
	private Integer sort;//排序; 数字小的 在前面;
	private String channel;//用于标示在哪个安卓渠道的包上面显示，为空则在所有的包上面显示
	private String noticeUrl;//公告地址
	public String getPt() {
		if(!QwyUtil.isNullAndEmpty(type)&&type.equals("0")){
			return "网站";
		}else if(!QwyUtil.isNullAndEmpty(type)&&type.equals("1")){
			return "手机";
		}else if(!QwyUtil.isNullAndEmpty(type)&&type.equals("2")){
			return "喵商城";
		}else{
			return "其他";
		}
		
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getImgURL() {
		return imgURL;
	}
	public void setImgURL(String imgURL) {
		this.imgURL = imgURL;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getInsertTime() {
		return insertTime;
	}
	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getNoticeId() {
		return noticeId;
	}
	public void setNoticeId(String noticeId) {
		this.noticeId = noticeId;
	}
	public Notice getNotice() {
		return notice;
	}
	public void setNotice(Notice notice) {
		this.notice = notice;
	}
	public String getHdUrl() {
		return hdUrl;
	}
	public void setHdUrl(String hdUrl) {
		this.hdUrl = hdUrl;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getNoticeUrl() {
		return noticeUrl;
	}
	public void setNoticeUrl(String noticeUrl) {
		this.noticeUrl = noticeUrl;
	}

	
}
