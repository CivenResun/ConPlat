package com.zju.conplat.utils;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 通过Client-Java获取集群中节点信息   以nodeName为例，其他可获取的信息也很多
 * 待补充：默认方式创建，除了传入的参数MasterIP外还需要token（应该放在配置文件里？）
 * @author civeng 1.0版本 有改动请添加相关信息
 */
public class GetNodeInfo {
    private List<String> nodeNameList=new ArrayList<>();

    /**
     * 获取到的是node名，在我们的集群中是centos7 node01 node04
     */
    @Test
    public void getNodeNameList(){
        KubernetesClient client=GetClient.getClient("193.112.155.109");
        NodeList nodeList=client.nodes().list();
        //podList也能得到，待扩展
        PodList podList=client.pods().list();

        for(Node node:nodeList.getItems()){
            //
            String nodeName=node.getMetadata().getName();
            if(nodeName!=null){
                nodeNameList.add(nodeName);
            }
            //可测试
            System.out.println(nodeName);
        }
        //实际上在Prometheus中对pod信息的查询也只需要$node$就行
        for(Pod pod:podList.getItems()){
            String podName=pod.getMetadata().getName();
            if(podName!=null){
                System.out.println(podName);
            }
        }

        //返回什么？
    }

}
