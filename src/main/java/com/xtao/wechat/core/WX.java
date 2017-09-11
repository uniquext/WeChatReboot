package com.xtao.wechat.core;

import com.xtao.wechat.callback.NewMessageCallback;
import com.xtao.wechat.callback.ScanQRCodeCallback;
import com.xtao.wechat.constant.ApiUrl;
import com.xtao.wechat.listener.MessageListener;
import com.xtao.wechat.listener.QRCodeStatusListener;
import com.xtao.wechat.model.Msg;
import com.xtao.wechat.model.User;
import com.xtao.wechat.util.HttpRequest;
import net.sf.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * 　 　　   へ　　　 　／|
 * 　　    /＼7　　　 ∠＿/
 * 　     /　│　　 ／　／
 * 　    │　Z ＿,＜　／　　   /`ヽ
 * 　    │　　　 　　ヽ　    /　　〉
 * 　     Y　　　　　   `　  /　　/
 * 　    ｲ●　､　●　　⊂⊃〈　　/
 * 　    ()　 へ　　　　|　＼〈
 * 　　    >ｰ ､_　 ィ　 │ ／／      去吧！
 * 　     / へ　　 /　ﾉ＜| ＼＼        比卡丘~
 * 　     ヽ_ﾉ　　(_／　 │／／           消灭代码BUG
 * 　　    7　　　　　　　|／
 * 　　    ＞―r￣￣`ｰ―＿
 * ━━━━━━感觉萌萌哒━━━━━━
 *
 * @author penghaitao
 * @date 2017/9/5  19:33
 * @description
 */
public class WX {

    public long time;

    public String uuid = null;
    public String sKey = null;
    public String wxSid = null;
    public String wxUin = null;
    public String passTicket = null;

    private User user = null;
    private JSONObject SyncKey = null;
    private String redirectUri = null;

    public static WX getInstance() {
        return WXHolder.single;
    }

    /**
     * 登录
     */
    public void login() {
        System.out.println("##########################################");
        System.out.println("### 这里是微信智障机器人");
        getUUID();
        getQRCode();
        qrCodeStatusListener.start();
    }

    private WX() {}

    /**
     * 获取UUID绘制登录二维码
     */
    private void getUUID() {
        String result = HttpRequest.get(ApiUrl.SESSION_UUID.getReference() + System.currentTimeMillis());
        JSONObject jsonObject = JSONObject.fromObject("{" + result + "}");
        if (jsonObject.getInt("window.QRLogin.code") == 200){
            uuid =  jsonObject.getString("window.QRLogin.uuid");
        }
    }

    /**
     * 获取登录二维码并保存到本地
     */
    private void getQRCode() {
        String path;
        try {
            path = new File("QRCode.png").getCanonicalPath();
        } catch (IOException e) {
            throw new IllegalStateException("二维码保存失败");
        }
        HttpRequest.get(ApiUrl.VERIFY_QR_CODE.getReference() + uuid, path);
        System.out.println("### 二维码保存成功！请打开图片扫码登录");
    }



    /**
     * 初始化微信
     */
    private void init() {
        setGlobalParams(redirectUri);
        getInfo();
        refreshNotify();
        if (!messageListener.isAlive()) {
            messageListener.setSyncKey(SyncKey);
            messageListener.start();
        }
    }

    /**
     * 设置全局参数
     * @param redirect_uri  重定向地址
     */
    private void setGlobalParams(String redirect_uri) {
        String xml = HttpRequest.get(redirect_uri + "&fun=new&version=v2&lang=zh_CN");
        if (xml != null) {
            sKey = xml.substring(xml.indexOf("<skey>") + "<skey>".length(), xml.indexOf("</skey>"));
            wxSid = xml.substring(xml.indexOf("<wxsid>") + "<wxsid>".length(), xml.indexOf("</wxsid>"));
            wxUin = xml.substring(xml.indexOf("<wxuin>") + "<wxuin>".length(), xml.indexOf("</wxuin>"));
            passTicket = xml.substring(xml.indexOf("<pass_ticket>") + "<pass_ticket>".length(), xml.indexOf("</pass_ticket>"));
        } else {
            throw new IllegalStateException("参数解析错误");
        }
    }

    /**
     * 获取最近联系人(不全)、用户信息、订阅号消息
     * 此处只筛选用户信息
     */
    private void getInfo() {
        String url = ApiUrl.INFO.getUrl() + "?r=" + (~System.currentTimeMillis()) + "&pass_ticket=" + passTicket;
        JSONObject param = new JSONObject();
        JSONObject BaseRequest = new JSONObject();
        BaseRequest.put("Uin", wxUin);
        BaseRequest.put("Sid", wxSid);
        BaseRequest.put("Skey", sKey);
        BaseRequest.put("DeviceID", "e" + String.valueOf(Math.random()).substring(2));
        param.put("BaseRequest", BaseRequest);
        String result = HttpRequest.post(url, param);
        JSONObject jsonResult = JSONObject.fromObject(result);
        JSONObject user = jsonResult.getJSONObject("User");
        this.SyncKey = jsonResult.getJSONObject("SyncKey");
        try {
            this.user = new User(user);
            System.out.println("### 欢迎您 " + this.user.getNickName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 刷新消息状态
     */
    private void refreshNotify() {
        final String url = ApiUrl.REFRESH_NOTIFY.getUrl() + "?lang=zh_CN&pass_ticket=" + WX.getInstance().passTicket;
        JSONObject param = new JSONObject();
        JSONObject BaseRequest = new JSONObject();
        BaseRequest.put("Uin", WX.getInstance().wxUin);
        BaseRequest.put("Sid", WX.getInstance().wxSid);
        BaseRequest.put("Skey", WX.getInstance().sKey);
        BaseRequest.put("DeviceID", "e" + String.valueOf(Math.random()).substring(2));
        param.put("BaseRequest", BaseRequest);
        param.put("Code", 3);
        param.put("FromUserName", user.getUserName());
        param.put("ToUserName", user.getUserName());
        param.put("ClientMsgId", System.currentTimeMillis());
        HttpRequest.post(url, param);
    }

    private void sendMsg(String to, String content) {
        JSONObject param = new JSONObject();
        String url = ApiUrl.SEND_MESSAGE.getUrl() + "?lang=zh_CN&pass_ticket=" + passTicket;
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(0, 13) + String.valueOf(Math.random()).substring(2, 6);

        JSONObject BaseRequest = new JSONObject();
        BaseRequest.put("Uin", WX.getInstance().wxUin);
        BaseRequest.put("Sid", WX.getInstance().wxSid);
        BaseRequest.put("Skey", WX.getInstance().sKey);
        BaseRequest.put("DeviceID", "e" + String.valueOf(Math.random()).substring(2));

        JSONObject Msg = new JSONObject();
        Msg.put("Type", 1);
        Msg.put("Content", content);
        Msg.put("FromUserName", user.getUserName());
        Msg.put("ToUserName", to);
        Msg.put("LocalID", timestamp);
        Msg.put("ClientMsgId", timestamp);

        param.put("BaseRequest", BaseRequest);
        param.put("Msg", Msg);
        param.put("Scene", 0);
        HttpRequest.post(url, param);

    }

    /**
     * 二维码扫描结果轮询
     */
    private QRCodeStatusListener qrCodeStatusListener = new QRCodeStatusListener(new ScanQRCodeCallback() {

        public void onWait(String userAvatar) {
            System.out.println("### 扫码成功，等待确认...");
        }

        public void onTimeout() {
            System.out.println("### 登陆超时");
        }

        public void onSuccess(String uri) {
            System.out.println("### 已确认，正在登录...");
            redirectUri = uri;
            init();
        }

        public void onError() {
            getQRCode();
        }

    });

    /**
     * 消息监听
     */
    private MessageListener messageListener = new MessageListener(new NewMessageCallback() {
        public void onReceive(Msg msg) {
            System.out.println("### " + msg.getFrom());
            System.out.println("### " + msg.getContent());
            if (msg.getFrom().contains("@@")) {
                //  群消息，做@判断，@对象为群昵称
                System.out.println("### 群消息");
            } else if (msg.getFrom().contains("@")) {
                System.out.println("### 私聊消息");
                sendMsg(msg.getFrom(), msg.getContent());
            }
        }

        public void onChat() {
            // TODO: 2017/9/7 进入/退出聊天界面
        }

        public void onError(int code) {
            System.out.println("### 系统异常退出" + code);
            init();
        }
    });


    private static class WXHolder {
        private static WX single = new WX();
    }
}
