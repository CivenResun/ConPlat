package com.zju.conplat.controller;

import com.zju.conplat.service.ICallXgBoost;
import com.zju.conplat.service.IGetFromProm;
import com.zju.conplat.utils.BaseHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * doc中的说明对应utils中的工具类
 * 此Controller中前三个方法获取集群监控信息，根据传入的节点名称nodeName查询对应的Cpu,Mem,FileSystem利用率和使用量
 * 第四个方法输入用户需要部署的Pod的资源，得到VM的分配结果（未完成）
 * 第五个方法调用机器学习模型，根据输入的参数预测Qos
 * @author civeng
 */
@Controller
public class UserController {
    @Resource
    ICallXgBoost callXgBoost;
    @Resource
    IGetFromProm getFromProm;

    /**
     * 根据nodeName得到此节点的Cpu在过去1分钟内的利用率
     */
    @RequestMapping(value="/getCpu",method={RequestMethod.GET})
    @ResponseBody
    public String getCpu(@RequestParam("nodeName") String nodeName){
        String query="sum (rate (container_cpu_usage_seconds_total{id=\"/\",kubernetes_io_hostname=~\"^"+nodeName+"\"}[1m])) / " +
                "sum (machine_cpu_cores{kubernetes_io_hostname=~\"^"+nodeName+"\"}) * 100";
        String ret=getFromProm.getInfoFromProm(query);
        if(ret==null){
            ret="Fail to query";
        }
        return ret;
    }

    /**
     * 根据nodeName得到此节点在过去1分钟内Mem的利用率（内存工作集）
     */
    @RequestMapping(value="/getMem",method={RequestMethod.GET})
    @ResponseBody
    public String getMem(@RequestParam("nodeName") String nodeName){
        String query="sum (container_memory_working_set_bytes{id=\"/\",kubernetes_io_hostname=~\"^$"+nodeName+"$\"}) / sum" +
                " (machine_memory_bytes{kubernetes_io_hostname=~\"^"+nodeName+"\"}) * 100";
        String ret=getFromProm.getInfoFromProm(query);
        if(ret==null){
            ret="Fail to query";
        }
        return ret;
    }

    /**
     * 根据nodeName得到此节点在过去1分钟内文件系统的利用率
     */
    @RequestMapping(value="/getDisk",method={RequestMethod.GET})
    @ResponseBody
    public String getFs(@RequestParam("nodeName") String nodeName){
        String query="sum (container_fs_usage_bytes{device=~\"^/dev/[sv]d[a-z][1-9]$\",id=\"/\",kubernetes_io_hostname=~\"^"+nodeName+"\"}) / " +
                "sum (container_fs_limit_bytes{device=~\"^/dev/[sv]d[a-z][1-9]$\",id=\"/\",kubernetes_io_hostname=~\"^"+nodeName+"\"}) * 100";
        String ret=getFromProm.getInfoFromProm(query);
        if(ret==null){
            ret="Fail to query";
        }
        return ret;
    }

    /**
     * TODO: 未完成
     * @param map Pod的资源消耗map
     * @return 分配的VM规格和数量
     */
    @RequestMapping(value="getVm",method={RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Object getVmDistribution(Map<String,String> map){
        Map<String,String> ret=new HashMap<>();
        return ret;
    }

    /**
     * 调用XgBoost模型，输出预测的Qos （从pmml文件里确定参数类型）
     * @param x1 Api-server_Num
     * @param x2 Api-server_Cpu
     * @param x3 Api-server_Mem
     * @param x4 Node_Num
     * @return Qos(Response Time  RT)响应时间
     */
    @PostMapping("/Qos")
    @ResponseBody
    public Object getPredictQos(@RequestParam float x1,
                                @RequestParam float x2,
                                @RequestParam float x3,
                                @RequestParam float x4){
        Map<String, Object> paramMap=new HashMap<>(8);
        paramMap.put("x1",x1);
        paramMap.put("x2",x2);
        paramMap.put("x3",x3);
        paramMap.put("x4",x4);
        Object qos= callXgBoost.predictQos(paramMap);
        return qos.toString();
    }



}
