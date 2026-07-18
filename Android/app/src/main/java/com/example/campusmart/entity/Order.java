package com.example.campusmart.entity;

import java.math.BigDecimal;
import java.util.Date;

public class Order {
    private Long orderID;
    private Long goodID;
    private String goodTitle;
    private String goodPictureURL;
    private Long buyerID;
    private Long sellerID;
    private BigDecimal amount;
    private String status;
    private Date payTimeout;
    private Date createTime;

    public Long getOrderID() {
        return orderID;
    }

    public void setOrderID(Long orderID) {
        this.orderID = orderID;
    }

    public Long getGoodID() {
        return goodID;
    }

    public void setGoodID(Long goodID) {
        this.goodID = goodID;
    }

    public String getGoodTitle() {
        return goodTitle;
    }

    public void setGoodTitle(String goodTitle) {
        this.goodTitle = goodTitle;
    }

    public String getGoodPictureURL() {
        return goodPictureURL;
    }

    public void setGoodPictureURL(String goodPictureURL) {
        this.goodPictureURL = goodPictureURL;
    }

    public Long getBuyerID() {
        return buyerID;
    }

    public void setBuyerID(Long buyerID) {
        this.buyerID = buyerID;
    }

    public Long getSellerID() {
        return sellerID;
    }

    public void setSellerID(Long sellerID) {
        this.sellerID = sellerID;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getPayTimeout() {
        return payTimeout;
    }

    public void setPayTimeout(Date payTimeout) {
        this.payTimeout = payTimeout;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
