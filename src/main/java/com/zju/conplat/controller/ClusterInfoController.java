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
 * 通过fabric8获取集群中node，service，pod的列表及相关信息（其实可以获得很多信息，但本项目只用到部分信息）
 * ResponseBody+Controller 因为前后端分离，所以只返回Json格式数据
 * @author civeng
 */
@RestController
@RequestMapping("/watch")
public class ClusterInfoController {
    private String clusterIp="193.112.155.109";

    /**
     * 用于获得集群中node的信息，以获取后续每个node上的资源使用情况信息
     * 包括：1.name 2.创建时间 3.虚拟网段地址(flannel或calico)
     */
    @RequestMapping(value="/node",method = {RequestMethod.GET})
    public List getNode(@RequestParam(value="masterIp",defaultValue = "193.112.155.109") String masterIp){
        List<List> ret=new ArrayList<>();
        NodeList nodeList= GetClient.getClient(masterIp).nodes().list();
        for(Node node:nodeList.getItems()){
            List<String> cur=new ArrayList<>();
            cur.add(node.getMetadata().getName());
            cur.add(node.getMetadata().getCreationTimestamp());
            cur.add(node.getSpec().getPodCIDR());

            ret.add(cur);
        }
        return ret;
    }

    /**
     * 获取集群中各个service的信息,默认不传入namespace这个参数（即获取所有命名空间的service）
     * 包括： 1.name 2.创建时间 3.ClusterIP
     */
    @RequestMapping(value="/service",method = {RequestMethod.GET})
    public List getService(@RequestParam(value="nameSpace",defaultValue = "all")String nameSpace){
        List<List> ret=new ArrayList<>();
        ServiceList svcList = GetClient.getClient(clusterIp).services().list();
        String all="all";
        if(all.equals(nameSpace)){
            for (Service svc : svcList.getItems()) {
                List<String> cur=new ArrayList<>();
                cur.add(svc.getMetadata().getName());
                cur.add(svc.getMetadata().getCreationTimestamp());
                cur.add(svc.getSpec().getClusterIP());
                ret.add(cur);
            }
        }else{
            for (Service svc : svcList.getItems()) {
                if(svc.getMetadata().getNamespace().equals(nameSpace)){
                    List<String> cur=new ArrayList<>();
                    cur.add(svc.getMetadata().getName());
                    cur.add(svc.getMetadata().getCreationTimestamp());
                    cur.add(svc.getSpec().getClusterIP());
                    ret.add(cur);
                }
            }
        }
        return ret;
    }

    /**
     * 获取集群中各个pod的信息，默认所有
     * 包括: 1.name 2.创建时间 3.宿主机IP 4.PodIP
     */
    @RequestMapping(value="/pod",method = {RequestMethod.GET})
    public List getServicePod(@RequestParam(value="nameSpace",defaultValue = "all")String nameSpace){
        List<List> ret=new ArrayList<>();
        PodList podList = GetClient.getClient(clusterIp).pods().list();
        String all="all";
        if(all.equals(nameSpace)){
            for (Pod pod : podList.getItems()) {
                List<String> cur=new ArrayList<>();
                cur.add(pod.getMetadata().getName());
                cur.add(pod.getMetadata().getCreationTimestamp());
                cur.add(pod.getStatus().getHostIP());
                cur.add(pod.getStatus().getPodIP());
                ret.add(cur);
            }
        }else{
            for (Pod pod : podList.getItems()) {
                if(pod.getMetadata().getNamespace().equals(nameSpace)){
                    List<String> cur=new ArrayList<>();
                    cur.add(pod.getMetadata().getName());
                    cur.add(pod.getMetadata().getCreationTimestamp());
                    cur.add(pod.getStatus().getHostIP());
                    cur.add(pod.getStatus().getPodIP());
                    ret.add(cur);
                }
            }
        }
        return ret;
    }


}
