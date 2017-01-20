package com.grgbanking.ruralbank.main.event;


import com.grgbanking.ruralbank.common.bean.workOrder;

/**
 * Created by LiuPeng on 2016/9/20.
 */
public class EventLatLng {
    public final workOrder mWorkOrder;
    public final String[] arrs;
    public final String voiceUrl;

    public EventLatLng(workOrder mWorkOrder, String[] arrs, String voiceUrl) {
        super();
        this.mWorkOrder = mWorkOrder;
        this.arrs = arrs;
        this.voiceUrl = voiceUrl;
    }
}
