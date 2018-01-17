package com.huoq.orm;

import java.util.Set;
import java.util.TreeSet;

/**
 * 投资卷发放规则
 *
 * Created by 陈冬明 on 19/09/2017.
 */
public class CouponReleaseRule {
    private Integer id;
    private String name;
    private Boolean canDelete;
    private Boolean toAllProduct;
    private Boolean isEnable;
    private Set<CouponReleaseRuleItem> ruleItems = new TreeSet<CouponReleaseRuleItem>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getCanDelete() {
        return canDelete;
    }

    public void setCanDelete(Boolean canDelete) {
        this.canDelete = canDelete;
    }

    public Boolean getToAllProduct() {
        return toAllProduct;
    }

    public void setToAllProduct(Boolean toAllProduct) {
        this.toAllProduct = toAllProduct;
    }

    public Boolean getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(Boolean enable) {
        isEnable = enable;
    }

    public Set<CouponReleaseRuleItem> getRuleItems() {
        return ruleItems;
    }

    public void setRuleItems(Set<CouponReleaseRuleItem> ruleItems) {
        this.ruleItems = ruleItems;
    }
}
