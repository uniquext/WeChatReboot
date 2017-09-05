package com.xtao.wechat.util;

import net.sf.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
 *
 * @author penghaitao
 * @date 2017/9/5  15:19
 */
public class HttpRequest {

    public static String get(String path) {
        try {
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if(connection.getResponseCode() == 200){
                return new String(readInputStream(connection.getInputStream()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String get(String path, Map<String, String> params) {
        return get(path + getParams(params));
    }

    /**
     * 获取文件
     * @param path  url
     * @param filePath  文件路径，默认保存当前工程根目录
     * @return 文件
     */
    public static File get(String path, String filePath) {
        try {
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if(connection.getResponseCode() == 200){
                File imageFile = new File(filePath);
                FileOutputStream outStream = new FileOutputStream(imageFile);
                outStream.write(readInputStream(connection.getInputStream()));
                outStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String post(String path, JSONObject jsonObject) {
        try {
            //创建连接
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
//            connection.setInstanceFollowRedirects(false);  重定向处理
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(jsonObject.toString());
            out.flush();
            out.close();
            return new String(readInputStream(connection.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 参数合成
     * @param params    参数
     * @return  参数字符串
     */
    private static String getParams(Map<String, String> params) {
        if (params == null)
            return "";
        StringBuilder sb = new StringBuilder();
        for (String key: params.keySet())
            sb.append("&").append(key).append("=").append(params.get(key));
        if (sb.length() > 0)
            sb.setCharAt(0, '?');
        return sb.toString();
    }


    /**
     * 将InputStreamReader转化成byte[]
     * @param inStream URL响应结果
     * @return 转化结果
     */
    private static byte[] readInputStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while( (len=inStream.read(buffer)) != -1 ){
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }

}
