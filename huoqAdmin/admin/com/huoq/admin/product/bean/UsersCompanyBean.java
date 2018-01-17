package com.huoq.admin.product.bean;

import com.huoq.common.dao.ObjectDAO;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.BlackList;
import com.huoq.orm.UsersCompany;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 企业用户记录
 * Created by yks on 2016/10/24.
 */
@Service
public class UsersCompanyBean {

    @Resource(name = "objectDAO")
    private ObjectDAO dao;
    private Logger log = Logger.getLogger(UsersCompanyBean.class);


    /**
     * 显示企业用户名单列表
     *
     * @param pageUtil   分页对象
     * @param username   企业用户
     * @param insertTime
     * @return 企业用户名单分页对象
     */
    public PageUtil<UsersCompany> loadUsersCompany(PageUtil pageUtil, String username, String insertTime) {
        try {
            ArrayList<Object> params = new ArrayList<>();
            StringBuilder hql = new StringBuilder();
            hql.append("FROM UsersCompany uc WHERE 1=1 ");
            //企业用户
            if (!QwyUtil.isNullAndEmpty(username)) {
                hql.append(" AND uc.username = ? ");
                params.add(DESEncrypt.jiaMiUsername(username.trim()));
                //params.add(username);
            }
            //按创建时间查询
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                String[] times = QwyUtil.splitTime(insertTime);
                if (times.length == 1) {
                    hql.append("AND uc.insertTime >= ?");
                    params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[0] + " 00:00:00"));
                    hql.append("AND uc.insertTime <= ?");
                    params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[0] + " 23:59:59"));
                } else {
                    hql.append("AND uc.insertTime >= ?");
                    params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[0] + " 00:00:00"));
                    hql.append("AND uc.insertTime <= ?");
                    params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[1] + " 23:59:59"));
                }
            }
            hql.append("ORDER BY uc.insertTime DESC, uc.id ASC");
            pageUtil = dao.getPage(pageUtil, hql.toString(), params.toArray());
        } catch (Exception e) {
            log.error("操作失败", e.getCause());
            return null;
        }
        return pageUtil;
    }

    /**
     * 添加企业用户（唯一用户）
     *
     * @param usersCompany
     */
    public Map<String,String> addUsersCompany(UsersCompany usersCompany) {
        Map<String,String> resultMap = new HashMap<>();
        try {
            String jiamiStr = DESEncrypt.jiaMiUsername(usersCompany.getUsername());
            if (!isUsernameExist(jiamiStr)) {
                usersCompany.setUsername(jiamiStr);
                usersCompany.setPhone(jiamiStr);
                usersCompany.setInsertTime(new Date());
                usersCompany.setUserType(0L);
                usersCompany.setUserStatus("0");
                String newId = dao.saveAndReturnId(usersCompany);
                resultMap.put("ok","操作成功！企业用户id=" + newId);
                return resultMap;
            } else {
                resultMap.put("error","操作失败！该用户已存在！");
                return resultMap;
            }
        } catch (Exception e) {
            log.error("添加企业用户出错:", e);
            resultMap.put("error","操作失败！");
            return resultMap;
        }
    }

    /**
     * 企业用户是否存在
     *
     * @param username
     * @return
     * @throws Exception
     */
    private boolean isUsernameExist(String username) throws Exception {
        boolean result = false;
        StringBuilder hql = new StringBuilder();
        hql.append(" FROM UsersCompany uc WHERE 1=1 ");
        hql.append(" AND uc.username = ? ");
        hql.append(" ORDER BY uc.insertTime DESC ");
        List<UsersCompany> usersCompanies = dao.LoadAll(hql.toString(), new Object[]{username});
        if (usersCompanies != null && usersCompanies.size() > 0) {
            result = true;
        }
        return result;
    }

}
