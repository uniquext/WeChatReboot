package com.xtao.wechat.listener;

import com.xtao.wechat.core.WX;
import com.xtao.wechat.callback.ScanQRCodeCallback;
import com.xtao.wechat.constant.ApiUrl;
import com.xtao.wechat.util.HttpRequest;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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
 * @date 2017/9/6  22:49
 * @description
 */
public class QRCodeStatusListener extends Thread {

    private volatile boolean exit = false;

    private int tip;
    private ScanQRCodeCallback scanQRCodeCallback = null;

    public QRCodeStatusListener(ScanQRCodeCallback codeCallback) {
        this.scanQRCodeCallback = codeCallback;
    }

    public void close() {
        this.exit = true;
    }

    @Override
    public void run() {
        JSONObject jsonObject;
        while (!exit) {
            jsonObject = JSONObject.fromObject("{" + getScanResult() + "}");
            switch (jsonObject.getInt("window.code")) {
                case 200:   //  确认登录
                    scanQRCodeCallback.onSuccess(jsonObject.getString("window.redirect_uri"));
                    return;
                case 201:   //  扫描成功
                    try {
                        tip = 1;
                        // TODO: 2017/9/5  此时能获取到用户头像，字段为window.userAvatar，如有需求自行处理
                        scanQRCodeCallback.onWait(jsonObject.getString("window.userAvatar"));
                        Thread.sleep(8 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case 408:   //  登陆超时
                    tip = 1;
                    scanQRCodeCallback.onTimeout();
                    break;
                default:
                    tip = 0;
                    scanQRCodeCallback.onError();
                    break;
            }
        }
    }

    /**
     * 获取扫码结果
     * @return 扫码结果
     */
    private String getScanResult() {
        WX.getInstance().time = System.currentTimeMillis();
        Map<String, String> params = new HashMap<String, String>();
        params.put("loginicon", "true");
        params.put("uuid", WX.getInstance().uuid);
        params.put("tip", String.valueOf(tip));
        params.put("r", String.valueOf(~WX.getInstance().time));
        params.put("_", String.valueOf(WX.getInstance().time));
        return HttpRequest.get(ApiUrl.IS_SCAN_QR_CODE.getUrl(), params);
    }
}
