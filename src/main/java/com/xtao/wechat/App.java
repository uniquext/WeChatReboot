package com.xtao.wechat;

import com.xtao.wechat.core.WX;

/**
 * Hello world!
 *
 */
public class App 
{

    public static void main(String[] args) throws Exception {
        System.setProperty ("jsse.enableSNIExtension", "false");
        WX.getInstance().login();
    }

}
