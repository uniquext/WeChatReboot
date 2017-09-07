package com.xtao.wechat;

import com.xtao.wechat.callback.NewMessageCallback;
import com.xtao.wechat.callback.ScanQRCodeCallback;
import com.xtao.wechat.constant.ApiUrl;
import com.xtao.wechat.listener.MessageListener;
import com.xtao.wechat.listener.QRCodeStatusListener;
import com.xtao.wechat.model.Msg;
import com.xtao.wechat.model.User;
import com.xtao.wechat.util.HttpRequest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
     * @param redirect_uri  重定向地址
     */
    private void init(String redirect_uri) {
        setGlobalParams(redirect_uri);
        getInfo();
        MessageListener messageListener = new MessageListener(SyncKey, new NewMessageCallback() {
            public void onReceive(Msg msg) {
                System.out.println("### 您有新的未读消息");
            }

            public void onChat() {
                // TODO: 2017/9/7 进入/退出聊天界面
            }

            public void onError() {
                System.out.println("### 系统异常退出");
            }
        });
        messageListener.start();
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
        Random random = new Random();
        random.nextInt();
        String url = ApiUrl.INFO.getUrl() + "?r=" + (~System.currentTimeMillis()) + "&pass_ticket=" + passTicket;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Uin", wxUin);
        jsonObject.put("Sid", wxSid);
        jsonObject.put("Skey", sKey);
        jsonObject.put("DeviceID", "e" + String.valueOf(Math.random()).substring(2));
        JSONObject param = new JSONObject();
        param.put("BaseRequest", jsonObject);
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
     * 二维码扫描结果轮询
     */
    private QRCodeStatusListener qrCodeStatusListener = new QRCodeStatusListener(new ScanQRCodeCallback() {

        public void onWait(String userAvatar) {
            System.out.println("### 扫码成功，等待确认...");
        }

        public void onTimeout() {
            System.out.println("### 登陆超时");
        }

        public void onSuccess(String redirectUri) {
            System.out.println("### 已确认，正在登录...");
            init(redirectUri);
        }

        public void onError() {
            getQRCode();
        }

    });

    private static class WXHolder {
        private static WX single = new WX();
    }
}
