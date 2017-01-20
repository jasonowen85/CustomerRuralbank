package com.netease.nim.uikit.session.extension;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by Think on 2016/8/1.
 */

public class WorkorderAttachment extends CustomAttachment {
    private String orderid = "";
    private String title = "";

    protected int type;


    public WorkorderAttachment() {
        super(CustomAttachmentType.Order);
    }

    public WorkorderAttachment(String str) {
        this();
    }

    @Override
    protected void parseData(JSONObject data) {
        title = data.getString("title");
        orderid = data.getString("orderid");
    }

    @Override
    public JSONObject packData() {
        JSONObject data = new JSONObject();
        data.put("title", title);
        data.put("orderid", orderid);
        return data;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}