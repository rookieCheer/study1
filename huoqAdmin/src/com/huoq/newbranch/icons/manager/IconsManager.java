package com.huoq.newbranch.icons.manager;

import com.huoq.common.util.PageUtil;
import com.huoq.newbranch.icons.dao.IconsDao;
import com.huoq.newbranch.orm.Icons;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by zf on 2017/6/16.
 */
@Service
public class IconsManager {
    @Resource
    private IconsDao iconsDao;

    /**
     * 根据条件加载图标，根据分页(只读)
     * @param pageUtil
     * @param icons
     * @return
     */
    public PageUtil<Icons> getIconsByCondition(PageUtil<Icons> pageUtil, Icons icons) {
        return iconsDao.getIconsByCondition(pageUtil, icons);
    }

    /**
     * 查询所有图标d(只读)
     * @return 返回图标集合
     */
    public List<Icons> getIcons(String str) {
        return iconsDao.LoadAll(str);
    }

    /**
     * 根据id查询图标（只读）
     * @param id 图标id
     * @return 返回图标实体
     */
    public Icons findIconById(Long id) {
        return (Icons) iconsDao.findById(new Icons(), id);
    }

    /**
     * 保存图标
     * @param icons 要保存的图标
     * @return
     */
    public String saveIcons(Icons icons) {
        return iconsDao.save(icons);
    }

    /**
     * 更新图标信息
     * @param icons  图标
     */
    public void updateIcons(Icons icons) {
        iconsDao.update(icons);
    }

    /**
     * 获取最大序列号(只读)
     * @return
     */
    public Long getMaxSeq() {
        return iconsDao.getMaxSeq();
    }
}
