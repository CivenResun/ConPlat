package com.zju.conplat.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;

/**
 * 获取Prometheus监控系统采集的数据   有改动请在author后添加相关信息
 * 多组数据时方法类型暂时写为void，其实可以传入想要的String类型PromQL查询语句做参数，返回Map或String。
 * 或者改成一个静态方法？ 然后读取url建立HTTP连接也可以抽出来写成一个单独的方法？
 * @author civeng  1.0版本
 */
public class GetPromData {

    /**
     * 只有一组 “时间戳+采集值”时调用这个方法
     * @param promQL 查询语句
     * @return 查询结果
     */
    public static String getOneSetData(String promQL){
        CloseableHttpClient httpClient=HttpClients.createDefault();
        URIBuilder uri=null;
        HttpGet get=null;
        try {
            uri=new URIBuilder("http://193.112.155.109:30003/api/v1/query");
            uri.addParameter("query",promQL);
            get=new HttpGet(uri.build());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject=null;
        CloseableHttpResponse response=null;
        String queryResult=null;
        try {
            response = httpClient.execute(get);
            String resStr= EntityUtils.toString(response.getEntity(),"UTF-8");
            System.out.println("result-------:"+resStr);
            jsonObject=JSONObject.parseObject(resStr);
            System.out.println(jsonObject);
            //根据Prometheus返回的JSON数据结构解析   metric是一组标签  value是采集的样本（在resultType是vector的情况下只有一组/还可能使matrix）
            JSONArray result=jsonObject.getJSONObject("data").getJSONArray("result");

            JSONArray value=result.getJSONObject(0).getJSONArray("value");
            queryResult=value.getString(1);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 释放连接
            if (null != response) {
                try {
                    response.close();
                    httpClient.close();
                } catch (IOException e) {
                    System.err.println("释放连接出错");
                    e.printStackTrace();
                }
            }
        }
        return queryResult;

    }


    /**
     * 有多组结果调用此方法
     * @param promQL 查询语句
     */
    public static void getMultiData(String promQL){
        //以下两个为参考  实际在uri构建中使用promQL
        String param1Value="sum(rate(container_cpu_usage_seconds_total{image!=\"\",name=~\"^k8s_.*\",kubernetes_io_hostname=~\"^node01\"}[1m])) by (pod_name)";
        String param2Value="http_requests_total";
        //HTTP客户端连接工具
        CloseableHttpClient httpClient= HttpClients.createDefault();
        //参数里有特殊字符，不能直接写成String（会报Illegal Character错误），用URIBuilder构造。
        URIBuilder uri=null;
        HttpGet get =null;
        try {
            /*  一对参数，使用addParameter(param: ,value:)这个方法添加参数。
             *  若多对参数，使用第二种方法（但其实在这里没有这种情况）：uri.addParameters(List<NameValuePair>)
             */
            uri=new URIBuilder("http://193.112.155.109:30003/api/v1/query");
            uri.addParameter("query",promQL);
            //uri此时是http://193.112.155.109:30003/api/v1/query?query=http_requests_total
            get=new HttpGet(uri.build());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject=null;
        CloseableHttpResponse response=null;
        try {
            // 执行请求
            response = httpClient.execute(get);
            String resStr= EntityUtils.toString(response.getEntity(),"UTF-8");
            System.out.println("result-------:"+resStr);
            jsonObject=JSONObject.parseObject(resStr);
            System.out.println(jsonObject);
            //根据Prometheus返回的JSON数据结构解析   metric是一组标签  value是采集的样本（在resultType是vector的情况下只有一组/还可能使matrix）
            JSONArray result=jsonObject.getJSONObject("data").getJSONArray("result");
            JSONObject metric=null;
            JSONArray value=null;
            for(int i=0;i<result.size();i++){
                //时间序列由一组标签确定，这里取指标名"__name__"和实例名"instance"
                metric=result.getJSONObject(i).getJSONObject("metric");
                System.out.println("\nMetric_Name:"+metric.get("__name__")+"  Instance:"+metric.get("instance"));
                //时间戳+样本值   其中时间戳可以用别的数据类型取（考虑存储方式？）  以上这些数据都可以用Map保存
                value=result.getJSONObject(i).getJSONArray("value");
                System.out.println("timestamp:"+value.getString(0)+"  requestTotal:"+value.getString(1));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 释放连接
            if (null != response) {
                try {
                    response.close();
                    httpClient.close();
                } catch (IOException e) {
                    System.err.println("释放连接出错");
                    e.printStackTrace();
                }
            }
        }
    }


}
