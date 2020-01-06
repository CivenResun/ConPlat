package com.zju.conplat.utils;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * http测试基类     用于测试，请忽略
 * @author civeng
 */
public class BaseHttpClient {
    String query01="http_requests_total";
    String query02="sum(machine_memory_bytes{kubernetes_io_hostname=~\"^node01\"})";
    String query03="http_requests_total{code=\"200\"}";
    String url="http://193.112.155.109:30003/api/v1/query?query="+query01;

    /**
     * 得到Prometheus的监控数据（JSON格式）
     */
    public String getRes() {
        System.out.println("200 status code:");
        HttpGet get = new HttpGet(url);
        CloseableHttpClient httpClient = HttpClients.custom().build();
        try {
            CloseableHttpResponse response = httpClient.execute(get);
            //Json 格式数据？  但是测试得到的只是请求头，怎么得到请求体待解决-------------------
            System.out.println("before");
            System.out.println(response.getEntity());
            System.out.println("after");
            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 原生方法构建JSON
     */
    public JSONObject getJSON(){
        JSONObject jsObj=new JSONObject();
        JSONObject jsObj01=new JSONObject();

        try {
            jsObj.put("key1","value1");

            jsObj01.put("_name_","request");
            jsObj01.put("code",200);
            jsObj01.put("instance","node01");
            jsObj.put("result",jsObj01);


            JSONArray jsonArray=new JSONArray();
            jsonArray.put(1578.251);
            jsonArray.put("356");

            jsObj.put("value",jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsObj;
    }
}
