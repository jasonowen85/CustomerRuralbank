package com.grgbanking.ruralbank.common.bean;

/**
 * Created by Think on 2016/8/11.
 */
public class tracking {
    private String supplierSendUserId;
    private String sendSupName;
    private String supplierAcceptUserId;
    private String supplierAcceptphone;
    private String acceptSupName;
    private String createTime;
    private String bankUserId;
    private String bankName;
    private String bankPhone;
    private String contactId;
    private String express;
    private String state;
    private String content;
    private String courierNum;
    private String signAddress;

    private String coordinates;

    public String getCoordinates() {
        return coordinates;
    }

    public tracking setCoordinates(String coordinates) {
        this.coordinates = coordinates;
        return this;
    }

    public String getSignAddress() {
        return signAddress;
    }

    public void setSignAddress(String signAddress) {
        this.signAddress = signAddress;
    }

    public String getSupplierSendUserId() {
        return supplierSendUserId;
    }

    public void setSupplierSendUserId(String supplierSendUserId) {
        this.supplierSendUserId = supplierSendUserId;
    }

    public String getBankPhone() {
        return bankPhone;
    }

    public void setBankPhone(String bankPhone) {
        this.bankPhone = bankPhone;
    }

    public String getSupplierAcceptphone() {
        return supplierAcceptphone;
    }

    public void setSupplierAcceptphone(String supplierAcceptphone) {
        this.supplierAcceptphone = supplierAcceptphone;
    }


    public String getSendSupName() {
        return sendSupName;
    }

    public void setSendSupName(String sendSupName) {
        this.sendSupName = sendSupName;
    }

    public String getSupplierAcceptUserId() {
        return supplierAcceptUserId;
    }

    public void setSupplierAcceptUserId(String supplierAcceptUserId) {
        this.supplierAcceptUserId = supplierAcceptUserId;
    }

    public String getAcceptSupName() {
        return acceptSupName;
    }

    public void setAcceptSupName(String acceptSupName) {
        this.acceptSupName = acceptSupName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getBankUserId() {
        return bankUserId;
    }

    public void setBankUserId(String bankUserId) {
        this.bankUserId = bankUserId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getExpress() {
        return express;
    }

    public void setExpress(String express) {
        this.express = express;
    }

    public String getCourierNum() {
        return courierNum;
    }

    public void setCourierNum(String courierNum) {
        this.courierNum = courierNum;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


}
