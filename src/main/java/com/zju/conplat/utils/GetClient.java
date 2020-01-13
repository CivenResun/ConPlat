package com.zju.conplat.utils;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

import java.util.HashMap;
import java.util.Map;

/**
 * 根据MasterIP创建与Kubernetes集群交互的Client
 * 待补充：默认方式创建，除了传入的参数MasterIP外还需要token（应该放在配置文件里？）
 * @author civeng   1.0版本   有改动请添加相关信息
 */
public class GetClient {
    /**
     * 每个key都是String类型的MasterIp，对应一个Client对象
     */
    private static Map<String,KubernetesClient> clientMap=new HashMap<>();
    /**
     * 从Service Account中找到的token  authentication
     */
    private static String token="eyJhbGciOiJSUzI1NiIsImtpZCI6IiJ9.eyJpc3MiOiJrdWJlcm5ldGVzL3N" +
            "lcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJrdWJlLXN5c3R" +
            "lbSIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VjcmV0Lm5hbWUiOiJkYXNoYm9hcmQtYWRtaW4tdG9rZW4tdGpmdjci" +
            "LCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC5uYW1lIjoiZGFzaGJvYXJkLWFkbWluIiwia3ViZXJ" +
            "uZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQudWlkIjoiMjhhZjNkMGUtMWIwYy00MzZjLWI2OWEtZTFmNDhkZDU" +
            "4NDZkIiwic3ViIjoic3lzdGVtOnNlcnZpY2VhY2NvdW50Omt1YmUtc3lzdGVtOmRhc2hib2FyZC1hZG1pbiJ9.mL-pNEaEFxW0AxjLz-" +
            "qqBU9Mvsx7_WmMV3G7jegHFj-1i8ao67AC7gTAuiSqrHCUdhBkaCyswrYLpC2SAcMCZNHo9md37cuU5ZQ2iDDVeVxD7KHX41fEX49kSROvgCH" +
            "U41pdESIj50lvQx87ND2nQPnD0gzI1QRB9c4CNjCAFc1x3XJDYeJL5AE5Pp7RNmXo2ab-aPKMQfIkSlPW7H7z1xIkoxmfrJ9rcx22eOt5_0m2hM" +
            "PgGcBqVxOdIHrHwvOJeu4QHjpxbo10j9m5-rNB5Xd8cuSeYzpu6ys8082ufyKGieykdmchY1Pkr_T0RIMnHE-giZvCmGd3Gq3axY3sew";

    /**
     * 静态工厂方法
     */
    public static KubernetesClient getClient(String masterIp){
        if(!clientMap.containsKey(masterIp)){
            synchronized (GetClient.class){
                //跳create()
                if(!clientMap.containsKey(masterIp)) {
                    KubernetesClient client = createClient(masterIp);
                    clientMap.put(masterIp, client);
                    return client;
                }
            }
        }
        return clientMap.get(masterIp);
    }

    /**
     * @param masterIp 通过config创建client，token认证？ 授权是默认的，之后就可以操作了
     * @return 一个默认的Client
     */
    public static KubernetesClient createClient(String masterIp){
        Config config=new ConfigBuilder()
//                .withUsername() 会用到吗？ 不设置暂时没出现问题
                .withOauthToken(token)
                .withTrustCerts(true)
                .withMasterUrl("https://"+masterIp+":6443/")
                .build();
        return new DefaultKubernetesClient(config);
    }
}
