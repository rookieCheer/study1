package com.huoq.assetSide.bean;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.huoq.assetSide.dao.ProjectBorrowerInfoDao;
import com.huoq.orm.ProjectBorrowerInfo;
import com.huoq.util.MyLogger;

/**
 * 标的所属借款人
 */
@Service
public class ProjectBorrowerInfoBean {

    private static MyLogger        log = MyLogger.getLogger(ProjectBorrowerInfoBean.class);
    @Resource
    private ProjectBorrowerInfoDao projectBorrowerInfoDao;

    /**
     * 根据标的主键获取借款人信息
     * 
     * @param productId 标的主键
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<ProjectBorrowerInfo> getProjectBorrowerInfoByProductId(String productId) {
        String hql = "from ProjectBorrowerInfo b where b.productId=?";
        return projectBorrowerInfoDao.LoadAll(hql, new Object[] { productId });
    }

    public void saveProjectBorrowerInfo(ProjectBorrowerInfo projectBorrowerInfo) {
        this.projectBorrowerInfoDao.save(projectBorrowerInfo);
    }
}
