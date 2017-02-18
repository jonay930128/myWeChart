package com.makenv.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.makenv.utils.HttpRequest;
import com.makenv.utils.MBDataUtil;
import com.makenv.utils.WeChartUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

/**
 * Created by Administrator on 2016/12/9.
 */
@Controller
@RequestMapping("weChartShare")
public class WeChartShareController {
    private static final String token = "demo";
//    private static final String appID = "wxfc166b6179859236";
    private static final String appID = "wx9c4410622c03bae1";
//    private static final String appSecret = "32655efa5edbfaaa1e07bbefbaa7468d";
    private static final String appSecret = "a517a3144e942de698efc52d667f9005";
//    private static final String openId = "od4KJwwfRrV8_IIIXIKMdi1P9ZiI";
    private static final String openId = "o8yVCv8aHHbhCIiSrrILqJ0R24lY";
    @RequestMapping(value = "send",method = {RequestMethod.GET})
    @ResponseBody
    public String doGet(HttpServletRequest request){
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");

        String[] arr = {token,timestamp,nonce};
        //排序
        Arrays.sort(arr);
        //拼接字符串
        StringBuffer stringBuffer = new StringBuffer();
        for (String str : arr){
            stringBuffer.append(str);
        }
        //sha1加密
        String sha1 = WeChartUtils.getSha1(stringBuffer.toString());

        if (sha1.equals(signature)){
            return echostr;
        }else {
            return null;
        }

    }

    @RequestMapping(value = "send",method = {RequestMethod.POST})
    @ResponseBody
    public String doPost(HttpServletRequest request)throws Exception{
        Map<String,String> map = WeChartUtils.xmlToMap(request);
        String fromUserName = map.get("FromUserName");      //其实是用户的openID;
        String toUserName = map.get("ToUserName");
        String msgType = map.get("MsgType");
        String content = map.get("Content");

        String message = null;
        if ("event".equals(msgType)) {
            String eventType = map.get("Event");
            if ("subscribe".equals(eventType)) {
                String str = "欢迎您的关注。";
                message = WeChartUtils.initText(toUserName, fromUserName, str);
            }
        }else if ("text".equals(msgType)){
            String str = "你发送的是："+content;
            System.out.println(fromUserName);
            message = WeChartUtils.initText(toUserName, fromUserName, str);
        }
        return message;
    }

    @RequestMapping("menu")
    @ResponseStatus(HttpStatus.OK)
    public void menu() throws Exception {
        Map<String, List<Map<String, String>>> map = new LinkedHashMap<>();
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> map1 = new LinkedHashMap<>();
        map1.put("type", "click");
        map1.put("name", "今日歌曲");
        map1.put("key", "V1001_TODAY_MUSIC");
        list.add(map1);
        Map<String, String> map2 = new LinkedHashMap<>();
        map2.put("type", "click");
        map2.put("name", "歌手简介");
        map2.put("key", "V1001_TODAY_SINGER");
        list.add(map2);
        Map<String, String> map3 = new LinkedHashMap<>();
        map3.put("type", "click");
        map3.put("name", "测试菜单");
        map3.put("key", "V1001_TODAY_MUNU");
        list.add(map3);
        map.put("button", list);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(map);

        String tokenTemp = HttpRequest.sendGet("https://api.weixin.qq.com/cgi-bin/token", "grant_type=client_credential&appid=" + appID + "&secret=" + appSecret + "");
        String access_token = mapper.readValue(tokenTemp, LinkedHashMap.class).get("access_token").toString();

        HttpRequest.sendPost(" https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + access_token + "", json);
    }

    @RequestMapping("muban")
    @ResponseStatus(HttpStatus.OK)
    public void muban() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        //获得access_token每天最多请求2000次，每个access_token有效期是两个小时。
        String tokenTemp = HttpRequest.sendGet("https://api.weixin.qq.com/cgi-bin/token","grant_type=client_credential&appid="+appID+"&secret="+appSecret+"");
        String access_token = mapper.readValue(tokenTemp, LinkedHashMap.class).get("access_token").toString();
        //将微信模板信息转成json数据
        String json = mapper.writeValueAsString(MBDataUtil.toMBData(openId));

        //发送模板信息
        HttpRequest.sendPost("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+access_token+"",json);
    }

}
