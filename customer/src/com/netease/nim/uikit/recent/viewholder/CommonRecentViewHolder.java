package com.netease.nim.uikit.recent.viewholder;

import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;

public class CommonRecentViewHolder extends RecentViewHolder {

    @Override
    protected String getContent() {
        return descOfMsg();
    }

    protected String descOfMsg() {
        if (recent.getMsgType() == MsgTypeEnum.text) {
            return recent.getContent();
        } else if (recent.getMsgType() == MsgTypeEnum.tip) {
            String digest = null;
            if (getCallback() != null) {
                digest = getCallback().getDigestOfTipMsg(recent);
            }

            if (digest == null) {
                digest = getDefaultDigest(null);
            }

            return digest;
        } else if (recent.getAttachment() != null) {
            String digest = null;
            if (getCallback() != null) {
                digest = getCallback().getDigestOfAttachment(recent.getAttachment());
            }

            if (digest == null) {
                digest = getDefaultDigest(recent.getAttachment());
            }

            return digest;
        }
        return "";
    }

    // SDK本身只记录原始数据，第三方APP可根据自己实际需求，在最近联系人列表上显示缩略消息
    // 以下为一些常见消息类型的示例。
    private String getDefaultDigest(MsgAttachment attachment) {
        switch (recent.getMsgType()) {
            case text:
                return recent.getContent();
            case image:
                return "[图片]";
            case video:
                return "[视频]";
            case audio:
                return "[语音消息]";
            case location:
                return "[位置]";

            case tip:
                return "[通知提醒]";

            default:
                return "[工单]";
        }
    }
}
