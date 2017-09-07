package com.xtao.wechat;

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
