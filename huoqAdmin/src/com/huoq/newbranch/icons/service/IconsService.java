package com.huoq.newbranch.icons.service;

import com.huoq.common.util.PageUtil;
import com.huoq.newbranch.icons.manager.IconsManager;
import com.huoq.newbranch.orm.Icons;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by zf on 2017/6/7.
 */
@Service
public class IconsService {

    @Resource
    private IconsManager iconsManager;

    /**
     * 根据条件加载图标，根据分页(只读)
     * @param pageUtil
     * @param icons
     * @return
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public PageUtil<Icons> getIconsByCondition(PageUtil<Icons> pageUtil, Icons icons) {
        return iconsManager.getIconsByCondition(pageUtil, icons);
    }

    /**
     * 查询所有图标d(只读)
     * @return 返回图标集合
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public List<Icons> getIcons() {
        return iconsManager.getIcons("Icons");
    }

    /**
     * 根据id查询图标（只读）
     * @param id 图标id
     * @return 返回图标实体
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public Icons findIconById(Long id) {
        return iconsManager.findIconById(id);
    }

    /**
     * 保存图标
     * @param icons 要保存的图标
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String saveIcons(Icons icons) {
        return iconsManager.saveIcons(icons);
    }

    /**
     * 更新图标信息
     * @param icons  图标
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateIcons(Icons icons) {
        iconsManager.updateIcons(icons);
    }

    /**
     * 获取最大序列号(只读)
     * @return
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public Long getMaxSeq() {
        return iconsManager.getMaxSeq();
    }
}
