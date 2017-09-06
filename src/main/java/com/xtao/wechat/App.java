package com.xtao.wechat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
