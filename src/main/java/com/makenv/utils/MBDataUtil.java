package com.makenv.utils;


import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by wrx on 2016/12/3.
 */
public class MBDataUtil {
    public static Map toMBData(String openID,String templateId){
        Map<String,Object> map = new LinkedHashMap<>();     //存储整个模板数据
        Map<String,Object> data = new LinkedHashMap<>();    //存储模板内data数据

        //存储{{first.DATA}}
        Map<String,String> first = new LinkedHashMap<>();
        first.put("value","aa");
        first.put("color","#743A3A");
        data.put("first",first);

        //存储{{keyword1.DATA}}
        Map<String,String> keyword1 = new LinkedHashMap<>();
        keyword1.put("value","bb");
        keyword1.put("color","#FF0000");
        data.put("keyword1",keyword1);

        //存储{{keyword2.DATA}}
        Map<String,String> keyword2 = new LinkedHashMap<>();
        keyword2.put("value","cc");
        keyword2.put("color","#C4C400");
        data.put("keyword2",keyword2);

        //存储{{keyword3.DATA}}
        Map<String,String> keyword3 = new LinkedHashMap<>();
        keyword3.put("value","dd");
        keyword3.put("color","#0000FF");
        data.put("keyword3",keyword3);

        //存储{{keyword4.DATA}}
        Map<String,String> keyword4 = new LinkedHashMap<>();
        keyword4.put("value","ee");
        keyword4.put("color","#008000");
        data.put("keyword4",keyword4);

        //存储{{remark.DATA}}
        Map<String,String> remark = new LinkedHashMap<>();
        remark.put("value","\n以上就是全部信息！");
        remark.put("color","#173177");
        data.put("remark",remark);

        map.put("touser",openID);                   //添加openID
        map.put("template_id",templateId);        //使用模板
        map.put("url","www.baidu.com");       //详情地址,不写的话则没有点击详情这个功能
        map.put("topcolor","#008000");             //头部字体颜色
        map.put("data",data);                       //添加data数据

        return map;
    }
}
