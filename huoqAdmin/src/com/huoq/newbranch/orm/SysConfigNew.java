package com.huoq.newbranch.orm;

import java.io.Serializable;
import java.sql.Timestamp;

public class SysConfigNew  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 453451L;
	// Fields
	private Long id;//逻辑id,自增+1
	private String code;//编码
	private String name;//名称
	private String note;//描述
	private String type;//0:web端注册; 1:Android移动端; 2:IOS移动端; 3:微信注册
	private String state;//0：停用，1：启用
	private String isDelete;//是否删除,0:已删除，1：未删除
	private String creator;//创建人
	private Timestamp createTime;//添加时间
	private String modifier;//更新人
	private  Timestamp  updateTime;//修改时间
	
	// Constructors
	
	/** full constructor */
	public SysConfigNew(Long id, String code, String name, String note, String type, String state, String isDelete,
			String creator, Timestamp createTime, String modifier, Timestamp updateTime) {
		super();
		this.id = id;
		this.code = code;
		this.name = name;
		this.note = note;
		this.type = type;
		this.state = state;
		this.isDelete = isDelete;
		this.creator = creator;
		this.createTime = createTime;
		this.modifier = modifier;
		this.updateTime = updateTime;
	}
	/** default constructor */
	public SysConfigNew() {
		super();
	}
	
	// Property accessors
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getIsDelete() {
		return isDelete;
	}
	public void setIsDelete(String isDelete) {
		this.isDelete = isDelete;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public String getModifier() {
		return modifier;
	}
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}
	public Timestamp getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}
	
	
	@Override
	public String toString() {
		return "SysConfig [id=" + id + ", code=" + code + ", name=" + name + ", note=" + note + ", type=" + type
				+ ", state=" + state + ", isDelete=" + isDelete + ", creator=" + creator + ", createTime=" + createTime
				+ ", modifier=" + modifier + ", updateTime=" + updateTime + "]";
	}
	
	
}
