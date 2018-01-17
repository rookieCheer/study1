package com.huoq.test.action;

import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import com.opensymphony.xwork2.ActionSupport;

@ParentPackage("struts-default")
@Namespace("/Product")
@Results({ @Result(name = "newFile", value = "/NewFile1.jsp")
})
public class MyDemoAction extends ActionSupport{
	
	public String getJ(){
		return "newFile";
	}
}
