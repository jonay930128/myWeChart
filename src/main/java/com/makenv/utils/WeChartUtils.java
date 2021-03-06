package com.makenv.utils;

import com.makenv.model.TextMessage;
import com.thoughtworks.xstream.XStream;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wrx on 2016/12/9.
 */
public class WeChartUtils {
    /**
     * sha1加密
     * @param str
     * @return
     */
    public static String getSha1(String str){
        if (str == null || str.length() == 0){
            return null;
        }
        char hexDigite[] = {'0','1','2','3','4','5','6','7','8','9','a',
                'b','c','d','e','f'};

        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes("UTF-8"));

            byte[] md = mdTemp.digest();
            int j = md.length;
            char buf[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++){
                byte byte0 = md[i];
                buf[k++] = hexDigite[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigite[byte0 & 0xf];
            }
            return new String(buf);
        }catch (Exception e){
            return null;
        }
    }


    /**
     * xml转成map
     * @param request
     * @return
     * @throws Exception
     */
    public static Map<String,String> xmlToMap(HttpServletRequest request) throws Exception {
        Map<String,String> map = new LinkedHashMap<>();

        SAXReader reader = new SAXReader();

        InputStream ins = request.getInputStream();
        Document doc = reader.read(ins);
        // 得到根节点元素
        Element root = doc.getRootElement();
        // 得到所有元素list
        List<Element> list = root.elements();
        // 将所有元素封装map
        for (Element e : list){
            map.put(e.getName(),e.getText());
        }

        ins.close();
        return map;
    }

    /**
     * 把数据封装成对象
     * @param toUserName
     * @param fromUserName
     * @param content
     * @return
     */
    public static String initText(String toUserName,String fromUserName,String content){
        TextMessage textMessage = new TextMessage();
        textMessage.setFromUserName(toUserName);    // 公共号把消息发送给用户时，fromUserName 和 toUserName调换一下
        textMessage.setToUserName(fromUserName);    // 公共号把消息发送给用户时，fromUserName 和 toUserName调换一下
        textMessage.setMsgType("text");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        textMessage.setCreateTime(sdf.format(new Date()));
        textMessage.setContent(content);
        // 将对象转换成xml
        return textMessageToXml(textMessage);
    }

    /**
     * 将对象转换成xml格式
     * @param textMessage
     * @return
     */
    public static String textMessageToXml(TextMessage textMessage){
        XStream xStream = new XStream();
        xStream.alias("xml",textMessage.getClass());
        return xStream.toXML(textMessage);
    }
}
