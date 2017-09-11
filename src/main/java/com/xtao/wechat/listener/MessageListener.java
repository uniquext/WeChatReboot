package com.xtao.wechat.listener;

import com.xtao.wechat.core.WX;
import com.xtao.wechat.callback.NewMessageCallback;
import com.xtao.wechat.constant.ApiUrl;
import com.xtao.wechat.model.Msg;
import com.xtao.wechat.util.HttpRequest;
import net.sf.json.JSONArray;
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
 * @date 2017/9/6  23:48
 * @description
 */
public class MessageListener extends Thread {

    private volatile boolean exit = false;

    private volatile JSONObject synckey = null;
    private NewMessageCallback messageCallback = null;

    public MessageListener(NewMessageCallback callback) {
        this.messageCallback = callback;
    }

    public void setSyncKey(JSONObject object) {
        this.synckey = object;
    }

    @Override
    public void run() {
        while (!exit) {
            try {
                int code = syncCheck();
                switch (code) {
                    case 0:
                        break;
                    case 1:
                        JSONObject response = JSONObject.fromObject(getNewMsg());
                        synckey = response.getJSONObject("SyncCheckKey");
                        JSONArray msgList = response.getJSONArray("AddMsgList");
                        for (int i = 0; i < msgList.size(); ++ i) {
                            String from = msgList.getJSONObject(i).getString("FromUserName");
                            String to = msgList.getJSONObject(i).getString("ToUserName");
                            String content = msgList.getJSONObject(i).getString("Content");
                            messageCallback.onReceive(new Msg(from, content));
                        }
                        break;
                    case 2:
                        messageCallback.onChat();
                        break;
                    default:
                        messageCallback.onError(code);
                        break;
                }
                Thread.sleep(20 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 消息检查返回码 格式 window.synccheck={retcode:"0",selector:"0"}
     * retcode = 0 正常；retcode = 1002 cookie失效
     * selector = 0 正常； selector = 2 新消息； selector = 7 进入/退出聊天界面
     * @return 转义消息码
     */
    private int syncCheck() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("r", String.valueOf(System.currentTimeMillis()));
        params.put("skey", WX.getInstance().sKey);
        params.put("sid", WX.getInstance().wxSid);
        params.put("uin", WX.getInstance().wxUin);
        params.put("deviceid", "e" + String.valueOf(Math.random()).substring(2));
        params.put("synckey", getSyncKeyStr());
        params.put("_", String.valueOf(++ WX.getInstance().time));
        String response = HttpRequest.get(ApiUrl.MessageCheck.getUrl(), params);
        JSONObject result = JSONObject.fromObject(response.substring(response.indexOf('{')));
        if (!result.getString("retcode").equals("0"))
            return Integer.valueOf(result.getString("retcode"));
        else if (result.getString("selector").equals("0"))
            return 0;
        else if (result.getString("selector").equals("2"))
            return 1;
        else if (result.getString("selector").equals("7"))
            return 2;
        return -1;
    }

    /**
     * 拼接同步码
     * @return 拼接的SyncKey字符串
     */
    private String getSyncKeyStr() {
        StringBuilder sb = new StringBuilder();
        JSONArray array = synckey.getJSONArray("List");
        for (int i = 0; i < array.size(); ++ i)
            sb.append("%7C").append(array.getJSONObject(i).getInt("Key")).append("_").append(array.getJSONObject(i).getInt("Val"));
        return sb.toString().substring(3);
    }

    /**
     * 获取新消息
     */
    private String getNewMsg() {
        String url = ApiUrl.NEW_MESSAGE.getUrl() +
                "?sid=" + WX.getInstance().wxSid +
                "&skey=" + WX.getInstance().sKey +
                "&pass_ticket=" + WX.getInstance().passTicket;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Uin", WX.getInstance().wxUin);
        jsonObject.put("Sid", WX.getInstance().wxSid);
        jsonObject.put("Skey", WX.getInstance().sKey);
        jsonObject.put("DeviceID", "e" + String.valueOf(Math.random()).substring(2));

        JSONObject param = new JSONObject();
        param.put("BaseRequest", jsonObject);
        param.put("SyncKey", synckey);
        param.put("rr", String.valueOf(~System.currentTimeMillis()));
        return HttpRequest.post(url, param);
    }
}
