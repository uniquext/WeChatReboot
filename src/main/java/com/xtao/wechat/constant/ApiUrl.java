package com.xtao.wechat.constant;

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
 *
 * @author penghaitao
 * @date 2017/9/5  14:51
 */
public enum ApiUrl {

    SESSION_UUID(
            "https://login.wx.qq.com/jslogin",
            "https://login.wx.qq.com/jslogin?appid=wx782c26e4c19acffb&redirect_uri=https%3A%2F%2Fwx.qq.com%2Fcgi-bin%2Fmmwebwx-bin%2Fwebwxnewloginpage&fun=new&lang=zh_CN&_="
    ),
    VERIFY_QR_CODE(
            "https://login.weixin.qq.com/qrcode",
            "https://login.weixin.qq.com/qrcode/"
    ),
    IS_SCAN_QR_CODE(
            "https://login.weixin.qq.com/cgi-bin/mmwebwx-bin/login",
            "https://login.web.wechat.com/cgi-bin/mmwebwx-bin/login?loginicon=true&uuid=wcbLWO6x2Q==&tip=0&r=-1504615582061&_=1504615582060"
    ),
    INFO(
            "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxinit",
            "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxinit?r=-1381521145&&pass_ticket=GOTyo3B%252BvuOhccyzgdYElxPKT5YPy1rkij8nKuAWyTpAz1EtnyOmiJ0gksN9PkjJ"
    ),
    MessageCheck(
            "https://webpush.wx.qq.com/cgi-bin/mmwebwx-bin/synccheck",
            "https://webpush.wx.qq.com/cgi-bin/mmwebwx-bin/synccheck?r=1504716369087&skey=%40crypt_6dea96f0_b492ecd74fdfc829c1c4fd40aa5ffed1&sid=%2FyBet%2BzwWWwzV8Tf&uin=2822019321&deviceid=e401714103231374&synckey=1_649892050%7C2_649892078%7C3_649892073%7C1000_1504696643&_=1504716346347"
    //       https://webpush.wx.qq.com/cgi-bin/mmwebwx-bin/synccheck?r=1504716476144&skey=%40crypt_6dea96f0_b492ecd74fdfc829c1c4fd40aa5ffed1&sid=%2FyBet%2BzwWWwzV8Tf&uuid=gdssmVhEAA==&deviceid=e401714103231374&synckey=1_649892050%7C2_649892078%7C3_649892073%7C1000_1504696643&_=1504716450050
    );

    private String url;

    private String reference;

    ApiUrl(String url, String reference) {
        this.url = url;
        this.reference = reference;
    }

    public String getUrl() {
        return url;
    }


    public String getReference() {
        return reference;
    }

}
