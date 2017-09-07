package com.xtao.wechat.listener;

import com.xtao.wechat.WX;
import com.xtao.wechat.callback.NewMessageCallback;
import com.xtao.wechat.constant.ApiUrl;
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

    // TODO: 2017/9/7 下一步需要发送并更新key
    private JSONObject synckey = null;
    private NewMessageCallback messageCallback = null;

    public MessageListener(JSONObject object, NewMessageCallback callback) {
        this.synckey = object;
        this.messageCallback = callback;
    }

    @Override
    public void run() {
        while (!exit) {
            try {
                switch (syncCheck()) {
                    case -1:
                        messageCallback.onError();
                        break;
                    case 0:
                        break;
                    case 1:
                        messageCallback.onReceive(null);
                        break;
                    case 2:
                        messageCallback.onChat();
                        break;
                    default:
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

        System.out.println(response);

        JSONObject result = JSONObject.fromObject(response.substring(response.indexOf('{')));
        if (!result.getString("retcode").equals("0"))
            return -1;
        else if (result.getString("selector").equals("0"))
            return 0;
        else if (result.getString("selector").equals("2"))
            return 1;
        else if (result.getString("selector").equals("7"))
            return 2;
        return -1;
    }

    private String getSyncKeyStr() {
        StringBuilder sb = new StringBuilder();
        JSONArray array = synckey.getJSONArray("List");
        for (int i = 0; i < array.size(); ++ i)
            sb.append("%7C").append(array.getJSONObject(i).getInt("Key")).append("_").append(array.getJSONObject(i).getInt("Val"));
        return sb.toString().substring(3);
    }

    /*
    window.synccheck={retcode:"1102",selector:"0"}
    1102 应该为登录信息实现失效
    retcode:
    0 正常
    1100 失败/退出微信
selector:
    0 正常
    2 新的消息
    7 进入/离开聊天界面
     */

}
