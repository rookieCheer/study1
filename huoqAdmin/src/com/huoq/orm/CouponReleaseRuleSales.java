package com.huoq.orm;

import java.util.Set;
import java.util.TreeSet;

/**
 * 销售投资卷发放规则
 * Created by 陈冬明 on 19/09/2017.
 */
public class CouponReleaseRuleSales extends CouponReleaseRule {
    private Set<Product> products = new TreeSet<Product>();

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }
}
