package com.zju.conplat.controller;

import com.zju.conplat.utils.GetClient;
import io.fabric8.kubernetes.api.model.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 通过fabric8获取集群中node，service，pod的列表
 * ResponseBody+Controller 因为前后端分离，所以只返回Json格式数据
 * @author civeng
 */
@RestController
@RequestMapping("/watch")
public class ClusterInfoController {
    private String clusterIp="193.112.155.109";

    /**
     * 用于获得集群中node的名称，以获取后续每个node上的资源使用情况信息
     */
    @RequestMapping(value="/node",method = {RequestMethod.GET})
    public List getNode(@RequestParam(value="masterIp",defaultValue = "193.112.155.109") String masterIp){
        List<String> ret=new ArrayList<>();
        NodeList nodeList= GetClient.getClient(masterIp).nodes().list();
        for(Node node:nodeList.getItems()){
            ret.add(node.getMetadata().getName());
        }
        return ret;
    }

    /**
     * 获取集群中各个service的名称,默认不传入namespace这个参数（即获取所有命名空间的service）
     */
    @RequestMapping(value="/service",method = {RequestMethod.GET})
    public List getService(@RequestParam(value="nameSpace",defaultValue = "all")String nameSpace){
        List<String> ret=new ArrayList<>();
        ServiceList svcList = GetClient.getClient(clusterIp).services().list();
        String all="all";
        if(all.equals(nameSpace)){
            for (Service svc : svcList.getItems()) {
                ret.add(svc.getMetadata().getName());
            }
        }else{
            for (Service svc : svcList.getItems()) {
                if(svc.getMetadata().getNamespace().equals(nameSpace)){
                    ret.add(svc.getMetadata().getName());
                }
            }
        }
        return ret;
    }

    /**
     * 获取集群中各个pod的名称，默认所有
     */
    @RequestMapping(value="/pod",method = {RequestMethod.GET})
    public List getServicePod(@RequestParam(value="nameSpace",defaultValue = "all")String nameSpace){
        List<String> ret=new ArrayList<>();
        PodList podList = GetClient.getClient(clusterIp).pods().list();
        String all="all";
        if(all.equals(nameSpace)){
            for (Pod pod : podList.getItems()) {
                ret.add(pod.getMetadata().getName());
            }
        }else{
            for (Pod pod : podList.getItems()) {
                if(pod.getMetadata().getNamespace().equals(nameSpace)){
                    ret.add(pod.getMetadata().getName());
                }
            }
        }
        return ret;
    }


}
