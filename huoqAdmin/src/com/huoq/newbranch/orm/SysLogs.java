package com.huoq.newbranch.orm;

import java.util.Date;

public class SysLogs {
	private int id;
	private String creator;
	private Date create_time;
	private String modifier;
	private Date update_time;
	private char is_delete;
	private String req_url;
	private String req_type;//请求类型（post、get）
	private String req_app;//请求端（api、admin）
	private String req_params;
	private String user_id;
	private String req_ip;
	private String resp_time;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	public String getModifier() {
		return modifier;
	}
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}
	public Date getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(Date update_time) {
		this.update_time = update_time;
	}
	public char getIs_delete() {
		return is_delete;
	}
	public void setIs_delete(char is_delete) {
		this.is_delete = is_delete;
	}
	public String getReq_url() {
		return req_url;
	}
	public void setReq_url(String req_url) {
		this.req_url = req_url;
	}
	public String getReq_type() {
		return req_type;
	}
	public void setReq_type(String req_type) {
		this.req_type = req_type;
	}
	public String getReq_app() {
		return req_app;
	}
	public void setReq_app(String req_app) {
		this.req_app = req_app;
	}
	public String getReq_params() {
		return req_params;
	}
	public void setReq_params(String req_params) {
		this.req_params = req_params;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getReq_ip() {
		return req_ip;
	}
	public void setReq_ip(String req_ip) {
		this.req_ip = req_ip;
	}
	public String getResp_time() {
		return resp_time;
	}
	public void setResp_time(String resp_time) {
		this.resp_time = resp_time;
	}
	
}
