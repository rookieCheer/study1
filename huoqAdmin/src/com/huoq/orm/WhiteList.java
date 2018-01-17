package com.huoq.orm;

import org.apache.log4j.Logger;

import java.util.Date;

/**
 * @author 李瑞丽
 * @Date: 2017/12/26 19:41
 */
@SuppressWarnings("serial")
public class WhiteList implements java.io.Serializable {
    private static Logger log = Logger.getLogger(WhiteList.class);
    private static final long serialVersionUID = 1L;
    private Integer id;                          // 逻辑id,自增+1,起始值为1000
    private String source;                       // 平台
    private Integer iType ;                      // 类型(1:IP;2:手机号)
    private String values;                       // 类型值
    private Integer status  ;                    // 状态（0:启用;1:停用）
    private Date dtCreate;                       // 创建时间
    private Date dtModify;                       // 修改时间

   //full  constructor
    public WhiteList(Integer id, String source, Integer iType, String values, Integer status, Date dtCreate, Date dtModify) {
        this.id = id;
        this.source = source;
        this.iType = iType;
        this.values = values;
        this.status = status;
        this.dtCreate = dtCreate;
        this.dtModify = dtModify;
    }

    public static Logger getLog() {
        return log;
    }

    public static void setLog(Logger log) {
        WhiteList.log = log;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Integer getiType() {
        return iType;
    }

    public void setiType(Integer iType) {
        this.iType = iType;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getDtCreate() {
        return dtCreate;
    }

    public void setDtCreate(Date dtCreate) {
        this.dtCreate = dtCreate;
    }

    public Date getDtModify() {
        return dtModify;
    }

    public void setDtModify(Date dtModify) {
        this.dtModify = dtModify;
    }
}
