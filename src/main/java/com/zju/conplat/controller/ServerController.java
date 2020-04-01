package com.zju.conplat.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zju.conplat.common.WebSocketConsts;
import com.zju.conplat.service.ICallXgBoost;
import com.zju.conplat.service.IGetFromProm;
import com.zju.conplat.vo.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * doc中的说明对应utils中的工具类
 * 此Controller中前两个方法周期性地返回监控数据
 * 第3~5个方法获取集群监控信息，根据传入的节点名称nodeName查询对应的Cpu,Mem,FileSystem利用率
 * 第6个方法输入用户需要部署的Pod的资源，得到VM的分配结果（还需要进一步考虑）
 * 第7个方法调用机器学习模型，根据输入的参数预测Qos
 * @author civeng
 */
@Slf4j
@RestController
@RequestMapping("/server")
public class ServerController {

    @Resource
    ICallXgBoost callXgBoost;
    @Resource
    IGetFromProm getFromProm;
    @Resource
    private SimpMessagingTemplate messagingTemplate;


    @GetMapping(value = "/getDetail")
    @ResponseBody
    public Server getDetail(){
        String nodeName="centos7";

        String cpuQuery="sum (rate (container_cpu_usage_seconds_total{id=\"/\",kubernetes_io_hostname=~\"^"+nodeName+"\"}[15s])) / " +
                "sum (machine_cpu_cores{kubernetes_io_hostname=~\"^"+nodeName+"\"}) * 100";
        String memQuery="sum (container_memory_working_set_bytes{id=\"/\",kubernetes_io_hostname=~\"^"+nodeName+"\"}) / sum" +
                " (machine_memory_bytes{kubernetes_io_hostname=~\"^"+nodeName+"\"}) * 100";
        String diskQuery="sum (container_fs_usage_bytes{device=~\"^/dev/[sv]d[a-z][1-9]$\",id=\"/\",kubernetes_io_hostname=~\"^"+nodeName+"\"}) / " +
                "sum (container_fs_limit_bytes{device=~\"^/dev/[sv]d[a-z][1-9]$\",id=\"/\",kubernetes_io_hostname=~\"^"+nodeName+"\"}) * 100";

        String cpu=getFromProm.getInfoFromProm(cpuQuery);
        String mem=getFromProm.getInfoFromProm(memQuery);
        String disk=getFromProm.getInfoFromProm(diskQuery);


        Server server=new Server();
        server.setCpu(cpu);
        server.setMem(mem);
        server.setDisk(disk);

        server.setDateTime(DateTime.now());

        return server;
    }

    /**
     * 按照标准时间来算，每隔 60s 执行一次
     */
    @Scheduled(cron = "0/15 * * * * ?")
    public void websocket() throws Exception {
        log.info("【推送消息】开始执行：{}", DateUtil.formatDateTime(new Date()));
        // 查询服务器状态
        String nodeName="centos7";

        String cpuQuery="sum (rate (container_cpu_usage_seconds_total{id=\"/\",kubernetes_io_hostname=~\"^"+nodeName+"\"}[15s])) / " +
                "sum (machine_cpu_cores{kubernetes_io_hostname=~\"^"+nodeName+"\"}) * 100";
        String memQuery="sum (container_memory_working_set_bytes{id=\"/\",kubernetes_io_hostname=~\"^"+nodeName+"\"}) / sum" +
                " (machine_memory_bytes{kubernetes_io_hostname=~\"^"+nodeName+"\"}) * 100";
        String diskQuery="sum (container_fs_usage_bytes{device=~\"^/dev/[sv]d[a-z][1-9]$\",id=\"/\",kubernetes_io_hostname=~\"^"+nodeName+"\"}) / " +
                "sum (container_fs_limit_bytes{device=~\"^/dev/[sv]d[a-z][1-9]$\",id=\"/\",kubernetes_io_hostname=~\"^"+nodeName+"\"}) * 100";

        String cpu=getFromProm.getInfoFromProm(cpuQuery);
        String mem=getFromProm.getInfoFromProm(memQuery);
        String disk=getFromProm.getInfoFromProm(diskQuery);


        Server server=new Server();
        server.setCpu(cpu);
        server.setMem(mem);
        server.setDisk(disk);

        server.setDateTime(DateTime.now());

        ObjectMapper mapper=new ObjectMapper();
        String serverInfo=mapper.writeValueAsString(server);

        messagingTemplate.convertAndSend(WebSocketConsts.PUSH_SERVER,server);
        log.info("【推送消息】执行结束：{}", DateUtil.formatDateTime(new Date()));

    }

    /**
     * 根据nodeName得到此节点的Cpu在过去1分钟内的利用率（当前集群状态）
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
        String query="sum (container_memory_working_set_bytes{id=\"/\",kubernetes_io_hostname=~\"^"+nodeName+"\"}) / sum" +
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
     * TODO: 待考虑
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
