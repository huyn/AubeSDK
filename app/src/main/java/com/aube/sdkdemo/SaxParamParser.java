package com.aube.sdkdemo;

import com.bokecc.sdk.mobile.util.Md5Encrypt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by huyaonan on 16/7/8.
 */
public class SaxParamParser {

    /** CC视频 **/
    public static final String CC_API_KEY = "l7l7PX9hVOwkxx7gKg8GeAeFBebx8Tau";
    public static final String CC_USERID = "E778404B771671CE";

    public static HashMap<String, String> doTHQS(HashMap<String, String> params) {
        String str = System.currentTimeMillis()/1000 + "";
        ArrayList<String> list = new ArrayList<String>();

        for (Map.Entry<String, String> item : params.entrySet()) {
            list.add(item.getKey());
        }

        Collections.sort(list);

        int listSize = list.size();
        HashMap<String, String> map = new HashMap<String, String>();
        for(int i=0;i<listSize;i++){
            map.put(list.get(i), params.get(list.get(i)));
        }

        String sign = "";
        try{
            sign = getMD5Str(map, str);
        }catch (Exception e) {
        }

        params.put("time", str);
        params.put("hash", sign);

        return params;
    }

    public static String getMD5Str(Map<String, String> params, String time) throws IOException {
        StringBuilder orgin = new StringBuilder();
        for(Map.Entry<String, String> entry:params.entrySet()){
            orgin.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        orgin.append("time=").append(time).append("&").append("salt=").append(CC_API_KEY);
        String query=orgin.toString();

//		MessageDigest md5 = getMd5MessageDigest();
//		byte[] bytes = md5.digest(query.getBytes(Constant.CHARSET_UTF8));
//
//		// 第四步：把二进制转化为大写的十六进制
//		StringBuilder sign = new StringBuilder();
//		for (int i = 0; i < bytes.length; i++) {
//			String hex = Integer.toHexString(bytes[i] & 0xFF);
//			if (hex.length() == 1) {
//				sign.append("0");
//			}
//			sign.append(hex.toUpperCase());
//		}
//
//		return sign.toString().toUpperCase();
        return Md5Encrypt.md5(query);
    }

}
