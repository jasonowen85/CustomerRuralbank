package com.netease.nim.uikit.session.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grgbanking.ruralbank.R;
import com.grgbanking.ruralbank.common.util.PermissionUtils;
import com.netease.nim.uikit.common.fragment.TFragment;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.session.SessionCustomization;
import com.netease.nim.uikit.session.actions.BaseAction;
import com.netease.nim.uikit.session.actions.ImageAction;
import com.netease.nim.uikit.session.actions.VideoAction;
import com.netease.nim.uikit.session.actions.WorkorderAction;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nim.uikit.session.module.Container;
import com.netease.nim.uikit.session.module.ModuleProxy;
import com.netease.nim.uikit.session.module.input.InputPanel;
import com.netease.nim.uikit.session.module.list.MessageListPanel;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.MessageReceipt;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天界面基类
 * <p/>
 * Created by huangjun on 2015/2/1.
 */
public class MessageFragment extends TFragment implements ModuleProxy {

    private View rootView;

    private SessionCustomization customization;

    protected static final String TAG = "MessageActivity";

    // 聊天对象
    protected String sessionId; // p2p对方Account或者群id

    protected SessionTypeEnum sessionType;

    // modules
    protected InputPanel inputPanel;
    protected  VideoAction videoAction;
    protected MessageListPanel messageListPanel;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        parseIntent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.nim_message_fragment, container, false);
        return rootView;
    }

    /**
     * ***************************** life cycle *******************************
     */

    @Override
    public void onPause() {
        super.onPause();

        NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE,
                SessionTypeEnum.None);
        inputPanel.onPause();
        messageListPanel.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        messageListPanel.onResume();
        NIMClient.getService(MsgService.class).setChattingAccount(sessionId, sessionType);
        getActivity().setVolumeControlStream(AudioManager.STREAM_VOICE_CALL); // 默认使用听筒播放
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        messageListPanel.onDestroy();
        registerObservers(false);
    }

    public boolean onBackPressed() {
        if (inputPanel.collapse(true)) {
            return true;
        }

        if (messageListPanel.onBackPressed()) {
            return true;
        }
        return false;
    }

    public void refreshMessageList() {
        messageListPanel.refreshMessageList();
    }

    private void parseIntent() {
        sessionId = getArguments().getString(Extras.EXTRA_ACCOUNT);
        sessionType = (SessionTypeEnum) getArguments().getSerializable(Extras.EXTRA_TYPE);

        customization = (SessionCustomization) getArguments().getSerializable(Extras.EXTRA_CUSTOMIZATION);
        Container container = new Container(getActivity(), sessionId, sessionType, this);

        if (messageListPanel == null) {
            messageListPanel = new MessageListPanel(container, rootView, false, false);
        } else {
            messageListPanel.reload(container, null);
        }

        if (inputPanel == null) {
            inputPanel = new InputPanel(container, rootView, getActionList());
            inputPanel.setCustomization(customization);
            inputPanel.setFatherFragment(this);
        } else {
            inputPanel.reload(container, customization);
        }

        registerObservers(true);

        if (customization != null) {
            messageListPanel.setChattingBackground(customization.backgroundUri, customization.backgroundColor);
        }
    }

    /**
     * ************************* 消息收发 **********************************
     */
    // 是否允许发送消息
    protected boolean isAllowSendMessage(final IMMessage message) {
        return true;
    }

    /**
     * ****************** 观察者 **********************
     */

    private void registerObservers(boolean register) {
        MsgServiceObserve service = NIMClient.getService(MsgServiceObserve.class);
        service.observeReceiveMessage(incomingMessageObserver, register);
        service.observeMessageReceipt(messageReceiptObserver, register);
    }

    /**
     * 消息接收观察者
     */
    Observer<List<IMMessage>> incomingMessageObserver = new Observer<List<IMMessage>>() {
        @Override
        public void onEvent(List<IMMessage> messages) {
            if (messages == null || messages.isEmpty()) {
                return;
            }

            messageListPanel.onIncomingMessage(messages);
            sendMsgReceipt(); // 发送已读回执
        }
    };

    private Observer<List<MessageReceipt>> messageReceiptObserver = new Observer<List<MessageReceipt>>() {
        @Override
        public void onEvent(List<MessageReceipt> messageReceipts) {
            receiveReceipt();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        LogUtil.i(TAG, "权限结果回调。。。  权限名字= " + permissions[0].toString());
        switch(requestCode) {
            case PermissionUtils.CODE_RECORD_AUDIO:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getActivity(), PermissionUtils.PERMISSION_WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED) {
                        //重新申请 sd 录音权限
                        PermissionUtils.confirmFragmentPermission(this, permissions, PermissionUtils.CODE_RECORD_AUDIO, getString(R.string.recordAudio));
                    }
                }
                break;

            case PermissionUtils.CODE_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //开始调用摄像头
                    LogUtil.i(TAG, "摄像头权限申请 成功。。。  ");
                    videoAction.requestPermissionCarame(true);
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {//第一次点击拒绝授权
                    PermissionUtils.confirmFragmentPermission(this, permissions, PermissionUtils.CODE_CAMERA, getString(R.string.camera));
                }
                break;
            case PermissionUtils.CODE_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //回调 执行true  调用摄像头功能；
                    videoAction.requestPermissionSDcard(true);
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {//第一次点击拒绝授权
                    PermissionUtils.confirmFragmentPermission(this, permissions, PermissionUtils.CODE_READ_EXTERNAL_STORAGE, getString(R.string.readSDcard));
                }
                break;
        }


    }

    /**
     * ********************** implements ModuleProxy *********************
     */
    @Override
    public boolean sendMessage(IMMessage message) {
        if (!isAllowSendMessage(message)) {
            return false;
        }

        // send message to server and save to db
        NIMClient.getService(MsgService.class).sendMessage(message, false);

        messageListPanel.onMsgSend(message);

        return true;
    }

    @Override
    public void onInputPanelExpand() {
        messageListPanel.scrollToBottom();
    }

    @Override
    public void shouldCollapseInputPanel() {
        inputPanel.collapse(false);
    }

    @Override
    public boolean isLongClickEnabled() {
        return !inputPanel.isRecording();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        inputPanel.onActivityResult(requestCode, resultCode, data);
        messageListPanel.onActivityResult(requestCode, resultCode, data);
    }

    // 操作面板集合
    protected List<BaseAction> getActionList() {
        List<BaseAction> actions = new ArrayList<>();
        actions.add(new ImageAction());
        videoAction = new VideoAction(this);
        actions.add(videoAction);
        actions.add(new WorkorderAction(getActivity()));

        if (customization != null && customization.actions != null) {
            actions.addAll(customization.actions);
        }
        return actions;
    }

    /**
     * 发送已读回执
     */
    private void sendMsgReceipt() {
        messageListPanel.sendReceipt();
    }

    /**
     * 收到已读回执
     */
    public void receiveReceipt() {
        messageListPanel.receiveReceipt();
    }
}
