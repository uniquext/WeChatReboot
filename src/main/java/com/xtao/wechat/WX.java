package com.xtao.wechat;

import com.xtao.wechat.constant.ApiUrl;
import com.xtao.wechat.model.User;
import com.xtao.wechat.util.HttpRequest;
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

    private int tip = 0;
    private String uuid = null;
    private String sKey = null;
    private String wxSid = null;
    private String wxUin = null;
    private String passTicket = null;

    private User user = null;

    public static WX getInstance() {
        return WXHolder.single;
    }

    /**
     * 登录
     */
    public void login() {
        getUUID();
        getQRCode();
        thread.start();
    }

    private WX() {}

    /**
     * 获取UUID绘制登录二维码
     */
    private void getUUID() {
        System.setProperty ("jsse.enableSNIExtension", "false");
        String result = HttpRequest.get(ApiUrl.SESSION_UUID.getReference() + System.currentTimeMillis());
        JSONObject jsonObject = JSONObject.fromObject("{" + result + "}");
        if (jsonObject.getInt("window.QRLogin.code") == 200){
            this.uuid =  jsonObject.getString("window.QRLogin.uuid");
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
        HttpRequest.get(ApiUrl.VERIFY_QR_CODE.getReference() + this.uuid, path);
        System.out.println("### 二维码保存成功！请打开图片扫码登录");
    }

    /**
     * 获取扫码结果
     * @return 扫码结果
     */
    private String getScanResult() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("loginicon", "true");
        params.put("uuid", this.uuid);
        params.put("tip", String.valueOf(tip));
        params.put("r", String.valueOf(~System.currentTimeMillis()));
        params.put("_", String.valueOf(System.currentTimeMillis()));
        return HttpRequest.get(ApiUrl.IS_SCAN_QR_CODE.getUrl(), params);
    }

    /**
     * 初始化微信
     * @param redirect_uri  重定向地址
     */
    private void init(String redirect_uri) {
        setGlobalParams(redirect_uri);
        getInfo();
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
        String url = ApiUrl.INFO.getUrl() + "?r=" + (~System.currentTimeMillis()) + "&lang=zh_CN&pass_ticket=" + passTicket;
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
        try {
            this.user = new User(user);
            System.out.println("### 欢迎您 " + this.user.getNickName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 轮询
     */
    private Thread thread = new Thread(new Runnable() {
        public void run() {
            int code;
            JSONObject jsonObject;
            while (true) {
                jsonObject = JSONObject.fromObject("{" + getScanResult() + "}");
                code = jsonObject.getInt("window.code");
                switch (code) {
                    case 200:   //  确认登录
                        System.out.println("### 确认登录");
                        init(jsonObject.getString("window.redirect_uri"));
                        return;
                    case 201:   //  扫描成功
                        System.out.println("### 扫码成功");
                        // TODO: 2017/9/5  此时能获取到用户头像，字段为window.userAvatar，如有需求自行处理
                        try {
                            tip = 1;
                            Thread.sleep(25 * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 408:   //  登陆超时
                        System.out.println("### 登陆超时");
                        tip = 1;
                        break;
                    default:
                        tip = 0;
                        getQRCode();
                }
            }
        }
    });

    private static class WXHolder {
        private static WX single = new WX();
    }
}
