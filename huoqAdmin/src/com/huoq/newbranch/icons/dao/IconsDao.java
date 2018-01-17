package com.huoq.newbranch.icons.dao;

import com.huoq.common.dao.ObjectDAO;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.newbranch.icons.action.IconsAction;
import com.huoq.newbranch.orm.Icons;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.stereotype.Service;

/**
 * Created by zf on 2017/6/6.
 */
@Service
public class IconsDao extends ObjectDAO {

    private static Logger log = Logger.getLogger(IconsAction.class);

    /**
     * 获取序列号
     * @return
     */
    public Long getMaxSeq() {
        String hql = "SELECT max(seq) FROM Icons";
        Query query = getSession().createQuery(hql);
        Object maxSeq = query.uniqueResult();
        return (Long) maxSeq;
    }

    /**
     * 根据条件查询图标
     * @param pageUtil
     * @param icons
     * @return
     */
    public PageUtil<Icons> getIconsByCondition(PageUtil<Icons> pageUtil, Icons icons) {
        try {
            String module = "";
            StringBuffer buff = new StringBuffer();
            buff.append("FROM Icons icon ");
            buff.append("WHERE icon.isDelete = '0' ");
            if (!QwyUtil.isNullAndEmpty(icons) && 'a' != icons.getModule()) {
                buff.append("AND icon.module = '" + icons.getModule() + "' ");
            }
            buff.append("ORDER BY icon.seq ASC ");
            return (PageUtil<Icons>)getPage(pageUtil, buff.toString(), null);
        } catch (Exception e) {
            log.error("操作异常: ",e);
        }
        return null;
    }
}
