# JSON数据格式
直接以Prometheus的JSON为例：
![PromJSON](https://img-blog.csdnimg.cn/20200108162834824.PNG?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3NwYXJyb3cxMjEzOA==,size_16,color_FFFFFF,t_70)
JSON的**主要数据结构**只有两种：Object和Array。
操作JSON数据可以用JSON官方的库，Google的GSON，Ali的FastJSON，下面代码中以及我在项目中用的都是FastJSON。
构建JSON在这里就不赘述了，put键值对或Map都可以。
解析JSON：
比如上图中"status":"success" 就是一个**JSON对象**，是一个键值对，键必须是String类型，value可以是任何类型（即Object）。"data":{  xx  } 也是对象，key是"data", value是{}包起的内容，即一个JSON对象（对象Object和JSON对象JSONObject是不同的概念）。

```java
//把HttpEntity的数据先转换为字符串再转换为JSON对象
String resStr= EntityUtils.toString(response.getEntity(),"UTF-8");
JSONObject jsonObject=JSONObject.parseObject(resStr);
//由key得到value（Object类型，可转换为String等）
jsonObject.get("status");
//得到的是JSONObject
jsonObject.getJSONObject("data")
```

"result":[ xx ]是一个**JSON数组**，value可以是一维数组也可以是二维数组。在Prometheus的返回数据中，resultType键值对注明了result的类型，"vector"是一维数组，"matrix"是二维数组。

```java
//提取出JSONArray
JSONArray result=jsonObject.getJSONObject("data").getJSONArray("result");
//数组元素的获取用索引index  
for(int i=0;i<result.size();i++){
                //时间序列由一组标签确定，这里取指标名"__name__"和实例名"instance"打印
                metric=result.getJSONObject(i).getJSONObject("metric");
                System.out.println("\nMetric_Name:"+metric.get("__name__")+"  Instance:"+metric.get("instance"));
                //时间戳+样本值   其中时间戳可以用别的数据类型取（考虑存储方式？）  以上这些数据都可以用Map保存
                value=result.getJSONObject(i).getJSONArray("value");
                System.out.println("timestamp:"+value.getString(0)+"  requestTotal:"+value.getString(1));
            }
```

<br/>

# Prometheus数据简介
主要是metric中，一组标识时间序列的标签+时间序列的采样值。
"metric":{\<label name>:\<label value>,...}  
\+  "value/values(对应matrix的resultType)":[timestamp,"value"] 
数据类型的转换根据需要来确定，比如时间戳可以解析成字符串类型。

--待补充：PromQL查询语句


<br/>

#  HTTP请求与响应
通过HTTP接口就可以访问Prometheus监控系统，获取监控采集的数据。
我用的HTTP建立连接的工具包是这个

```java
<!--        httpClient包，在Apache HttpComponents项目中-->
        <dependency>
            <groupId>org.apache.httpcomponents</groupId>
            <artifactId>httpclient</artifactId>
            <version>4.5.10</version>
        </dependency>
```
其中有一些使用细节，比如统一资源标识符URI的构建，将HttpEntity用UTF-8编码方式转换为字符串再解析为JSON对象，我都写在注释里了。

```java
		String paramValue="http_requests_total";
//HTTP客户端连接工具
        CloseableHttpClient httpClient=HttpClients.createDefault();
        //参数里有特殊字符，不能直接写成String（会报Illegal Character错误），用URIBuilder构造。
        URIBuilder uri=null;
        HttpGet get =null;
        try {
            //一对参数，使用addParameter(param: ,value:)这个方法添加参数。
            //若多对参数，使用第二种方法（但其实在这里没有这种情况）：uri.addParameters(List<NameValuePair>);
            //这里的ip，port换成你的Prometheus的ip+port。paramValue要自己定义，比如http_request_total
            uri=new URIBuilder("http://ip:port/api/v1/query");
            uri.addParameter("query",paramValue);
            //uri此时是http://ip:port/api/v1/query?query=http_requests_total
            get=new HttpGet(uri.build());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject=null;
        CloseableHttpResponse response=null;
        try {
            // 执行请求并接收+转换  得到jsonObject就可以解析了。
            response = httpClient.execute(get);
            String resStr= EntityUtils.toString(response.getEntity(),"UTF-8");
            jsonObject=JSONObject.parseObject(resStr);
```

统一资源定位符URL(locator)可以看作是统一资源标识符URI（唯一标识一个资源）的一种实现，上述代码中的HttpGet需要传入uri参数。