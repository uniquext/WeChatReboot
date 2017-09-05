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

    static String xml = "<error>\n" +
            "     <ret>0</ret>\n" +
            "     <message/>\n" +
            "     <skey>@crypt_6dea96f0_7a02e532591af65e88de9d60c42c1b6c</skey>\n" +
            "     <wxsid>qIqMzvCh3gVcYVqm</wxsid>\n" +
            "     <wxuin>2822019321</wxuin>\n" +
            "     <pass_ticket>GOTyo3B%2BvuOhccyzgdYElxPKT5YPy1rkij8nKuAWyTpAz1EtnyOmiJ0gksN9PkjJ</pass_ticket>\n" +
            "     <isgrayscale>1</isgrayscale>\n" +
            "     </error>";

    public static void main(String[] args) throws Exception {
        System.setProperty ("jsse.enableSNIExtension", "false");
        WX.getInstance().login();
//        xml.lastIndexOf()
//        System.out.println(xml.substring(xml.indexOf("<skey>") + "<skey>".length(), xml.indexOf("</skey>")));
//        System.out.println(xml.substring(xml.indexOf("<wxsid>") + "<wxsid>".length(), xml.indexOf("</wxsid>")));
//        System.out.println(xml.substring(xml.indexOf("<wxuin>") + "<wxuin>".length(), xml.indexOf("</wxuin>")));
//        System.out.println(xml.substring(xml.indexOf("<pass_ticket>") + "<pass_ticket>".length(), xml.indexOf("</pass_ticket>")));

//        Map<String, String> params = new HashMap<String, String>();
//        params.put("loginicon", "true");
//        params.put("uuid", "1234546");
//        params.put("tip", String.valueOf(1));
//        params.put("r", String.valueOf(~System.currentTimeMillis()));
//        params.put("_", String.valueOf(System.currentTimeMillis()));
//        System.out.println(params.toString());

//        System.out.println();




    }

}
