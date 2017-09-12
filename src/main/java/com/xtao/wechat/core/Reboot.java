package com.xtao.wechat.core;

import com.xtao.wechat.util.HttpRequest;
import net.sf.json.JSONObject;

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
 * @date 2017/9/11  22:01
 * @description
 */
public class Reboot {

    private final String ApiUrl = "http://www.tuling123.com/openapi/api";
    private final String ApiKey = "09f0714d86484853b72dd807f031dd90";

    public static Reboot getInstance() {
        return RebootHolder.single;
    }

    public String talk(String user, String msg){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key", ApiKey);
        jsonObject.put("info", msg);
        jsonObject.put("userid", user);
        return answer(HttpRequest.post(ApiUrl, jsonObject));
    }

    private Reboot() {}

    private String answer(String result) {
        JSONObject jsonObject;
        try {
            jsonObject = JSONObject.fromObject(result);
            switch (jsonObject.getInt("code")) {
                case 100000:    //  文本类
                    return jsonObject.getString("text");
                case 200000:    // 链接类
                    return jsonObject.getString("text") + jsonObject.getString("url");
                case 302000:    //  新闻类
                    return jsonObject.getString("text") + jsonObject.getString("list");
                case 308000:    //  菜谱类
                    return jsonObject.getString("text") + jsonObject.getString("list");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "你在说什么呀~";
    }

    private static class RebootHolder {
        private static Reboot single = new Reboot();
    }

}
