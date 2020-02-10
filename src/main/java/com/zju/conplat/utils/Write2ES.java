package com.zju.conplat.utils;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.alibaba.fastjson.serializer.SerializerFeature.*;
import static com.alibaba.fastjson.serializer.SerializerFeature.WriteNullListAsEmpty;

/**
 * List写入到ElasticSearch中
 * @author civeng
 */
public class Write2ES {
    private final static String ES_HOST=null;
    private final static String ES_PORT=null;
    /**
     *  使用日志框架slf4j，接口的工厂自动寻找恰当的实现，返回一个实例。
     */
    private final static Logger logger = LoggerFactory.getLogger(Write2ES.class);


    /**
     * list转换为JSON字符串形式，使用HttpSend发送到ES
     */
    public static String run(Object list, String saveUrl) {
        String returnValue = JSON.toJSONString(list, WriteMapNullValue,
                WriteNullNumberAsZero, WriteNullStringAsEmpty, WriteNullListAsEmpty);
        logger.info("Writing [" + saveUrl + "] data to ES");
        HttpSend.sendPost("http://" + ES_HOST + ":" + ES_PORT + "/" + saveUrl, returnValue);

        if (returnValue.length()!=0) {
            logger.info("Written [" + saveUrl + "] data done");
        }else{
            logger.info("Written [" + saveUrl + "] data Failed!");
        }

        return returnValue;
    }
}
