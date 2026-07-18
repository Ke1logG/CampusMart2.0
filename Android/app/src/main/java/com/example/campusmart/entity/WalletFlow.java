package com.example.campusmart.entity;

import java.math.BigDecimal;
import java.util.Date;

public class WalletFlow {
    private Long flowID;
    private Long userID;
    private String flowType;
    private BigDecimal amount;
    private BigDecimal balance;
    private Long relatedID;
    private String remark;
    private Date createTime;

    public Long getFlowID() {
        return flowID;
    }

    public void setFlowID(Long flowID) {
        this.flowID = flowID;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public String getFlowType() {
        return flowType;
    }

    public void setFlowType(String flowType) {
        this.flowType = flowType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Long getRelatedID() {
        return relatedID;
    }

    public void setRelatedID(Long relatedID) {
        this.relatedID = relatedID;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
