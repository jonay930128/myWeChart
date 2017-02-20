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

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by wrx on 2016/12/9.
 */
@Controller
@RequestMapping("weChartShare")
public class WeChartShareController {
    private static final String TOKEN = "xxx";              // 看心情随意填写的，要与微信公共号的配置相同
    private static final String APPID = "xxx";               // 公共号的信息
    private static final String APPSECRET = "xxx";          // 公共号的信息
    private static final String OPENID = "xxx";              //关注者的安全唯一id
    private static final String TEMPLATE_ID = "xxx";        //发送模板消息的模板id

    /**
     * 微信验证url接口，注意：接收get请求。
     * @param signature 	微信加密签名
     * @param timestamp   	时间戳
     * @param nonce        随机数
     * @param echostr     随机字符串
     * @return
     */
    @RequestMapping(value = "send",method = {RequestMethod.GET})
    @ResponseBody
    public String doGet(String signature,String timestamp,String nonce,String echostr){
        String[] arr = {TOKEN,timestamp,nonce};
        //排序
        Arrays.sort(arr);
        //拼接字符串
        StringBuffer stringBuffer = new StringBuffer();
        for (String str : arr){
            stringBuffer.append(str);
        }
        //sha1加密
        String sha1 = WeChartUtils.getSha1(stringBuffer.toString());
        // 若比较结果相等，原样放回echostr,随机字符串
        if (sha1.equals(signature)){
            return echostr;
        }else {
            return null;
        }

    }

    /**
     * 接收普通用户向公共号发送的消息
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "send",method = {RequestMethod.POST})
    @ResponseBody
    public String doPost(HttpServletRequest request)throws Exception{
        // 接收微信发过来的xml格式的数据包，把xml转换成map处理。
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
            message = WeChartUtils.initText(toUserName, fromUserName, str);
        }
        return message;
    }

    /**
     * 给公共号创建菜单，在浏览器地址栏直接访问即可
     * @throws Exception
     */
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

        String tokenTemp = HttpRequest.sendGet("https://api.weixin.qq.com/cgi-bin/token", "grant_type=client_credential&appid=" + APPID + "&secret=" + APPSECRET + "");
        String access_token = mapper.readValue(tokenTemp, LinkedHashMap.class).get("access_token").toString();

        HttpRequest.sendPost(" https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + access_token + "", json);
    }

    /**
     * 发送模板消息,在浏览器地址栏直接访问即可
     * @throws Exception
     */
    @RequestMapping("template")
    @ResponseStatus(HttpStatus.OK)
    public void template() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        //获得access_token每天最多请求2000次，每个access_token有效期是两个小时。
        String tokenTemp = HttpRequest.sendGet("https://api.weixin.qq.com/cgi-bin/token","grant_type=client_credential&appid="+APPID+"&secret="+APPSECRET+"");
        String access_token = mapper.readValue(tokenTemp, LinkedHashMap.class).get("access_token").toString();
        //将微信模板信息转成json数据
        String json = mapper.writeValueAsString(MBDataUtil.toMBData(OPENID,TEMPLATE_ID));

        //发送模板信息
        HttpRequest.sendPost("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+access_token+"",json);
    }

}
