package com.xtao.wechat.listener;

import com.xtao.wechat.WX;
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

    private JSONObject synckey = null;

    public MessageListener(JSONObject object) {
        this.synckey = object;
    }

    @Override
    public void run() {
        while (!exit) {
            try {
                System.out.println(syncCheck());
                Thread.sleep(25 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String syncCheck() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("r", String.valueOf(System.currentTimeMillis()));
        params.put("skey", WX.getInstance().sKey);
        params.put("sid", WX.getInstance().wxSid);
        params.put("uin", WX.getInstance().wxUin);
        params.put("deviceid", "e" + String.valueOf(Math.random()).substring(2));
        params.put("synckey", getSyncKeyStr());
        params.put("_", String.valueOf(++ WX.getInstance().time));
        return HttpRequest.get(ApiUrl.MessageCheck.getUrl(), params);
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

//1_649892050|2_649892070|3_649892073|11_649892060|13_649420029|201_1504711998|203_1504710657|1000_1504696643   |1001_1504693293|1004_1504696643
    /**
     *
     * 	r=时间戳（ms）
     skey=xxx
     sid=xxx
     uin=xxx
     deviceid=xxx
     synckey=1_654585659%7C2_654585745%7C3_654585673%7C1000_1467162721
     _=1467184052133
     */
}
