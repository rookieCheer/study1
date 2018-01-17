package com.huoq.newbranch.config.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.huoq.common.util.PageUtil;
import com.huoq.newbranch.config.dao.SysConfigDao;
import com.huoq.newbranch.config.manager.SysConfigManager;
import com.huoq.newbranch.orm.SysConfigNew;



@Service
public class SysConfigService {
	@Resource 
	private SysConfigManager manager;
	
	public SysConfigNew getSysConfigByCode(String code) {
		return manager.getSysConfigByCode(code);
	}


	public PageUtil loadSysConfig(PageUtil<SysConfigNew> pageUtil) {
		
		return manager.loadSysConfig(pageUtil);
	}


	public SysConfigNew getSysConfigById(Long id) {
		return manager.getSysConfigById(id);
	}

	@Transactional(rollbackFor=Exception.class)
	public void updateConfig(SysConfigNew config) {
		manager.updateConfig(config);;
	}

	@Transactional(rollbackFor=Exception.class)
	public void addConfig(SysConfigNew config) {
		manager.addConfig(config);
	}

	public List<SysConfigNew> getCodeList(String config) {
		 List<SysConfigNew> list = manager.getCodeList(config);
		 return list;
	}
}
