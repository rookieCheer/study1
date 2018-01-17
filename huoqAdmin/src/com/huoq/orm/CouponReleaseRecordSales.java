package com.huoq.orm;

/**
 * 投资卷发放规则
 *
 * Created by 陈冬明 on 19/09/2017.
 */
public class CouponReleaseRecordSales extends CouponReleaseRecord {

    private String productName; // 产品名称
    private Product product; // 对应的产品

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
