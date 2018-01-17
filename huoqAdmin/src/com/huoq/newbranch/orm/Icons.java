package com.huoq.newbranch.orm;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by zf on 2017/6/6.
 */
public class Icons implements Serializable {

    private static final long serialVersionUID = -3422162884734214706L;
    private Long id;                    // 主键
    private String name;                // 图标名
    private Long seq;                   // 顺序号
    private char module;                // 图标模块
    private char type;                  // 是否H5或原生，0:H5，1：原生
    private char isLogin;               // 是否需要登录，0:不需要， 1：需要
    private String H5URL;               // H5地址
    private String iconURL;             // 图标URL
    private char isClick;               // 是否可以点击，0：不可以，1，可以
    private char status;                // 状态 0：停用，1：启用
    private char isDelete;              // 是否删除,1:已删除，0：未删除
    private String note;                // 备注
    private String creator;             // 创建人
    private Date createTime;            // 添加时间
    private String modifier;            // 更新人
    private Date updateTime;            // 更新时间
    private String iconMsg;             // 不可点击图标提示信息

    public Icons() {
    }

    public Icons(Long id, String name, Long seq, char module, char type, char isLogin, String h5URL, String iconURL,
                 char isClick, char status, char isDelete, String note, String creator, Date createTime, String modifier, Date updateTime, String iconMsg) {
        this.id = id;
        this.name = name;
        this.seq = seq;
        this.module = module;
        this.type = type;
        this.isLogin = isLogin;
        H5URL = h5URL;
        this.iconURL = iconURL;
        this.isClick = isClick;
        this.status = status;
        this.isDelete = isDelete;
        this.note = note;
        this.creator = creator;
        this.createTime = createTime;
        this.modifier = modifier;
        this.updateTime = updateTime;
        this.iconMsg = iconMsg;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSeq() {
        return seq;
    }

    public void setSeq(Long seq) {
        this.seq = seq;
    }

    public char getModule() {
        return module;
    }

    public void setModule(char module) {
        this.module = module;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public char getIsLogin() {
        return isLogin;
    }

    public void setIsLogin(char isLogin) {
        this.isLogin = isLogin;
    }

    public String getH5URL() {
        return H5URL;
    }

    public void setH5URL(String h5URL) {
        H5URL = h5URL;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public char getIsClick() {
        return isClick;
    }

    public void setIsClick(char isClick) {
        this.isClick = isClick;
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }

    public char getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(char isDelete) {
        this.isDelete = isDelete;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getIconMsg() {
        return iconMsg;
    }

    public void setIconMsg(String iconMsg) {
        this.iconMsg = iconMsg;
    }
}
