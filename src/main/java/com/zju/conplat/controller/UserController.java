package com.zju.conplat.controller;

import com.zju.conplat.service.CallXgboost;
import com.zju.conplat.utils.BaseHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author civeng
 */
@Controller
public class UserController {
    @Resource
    CallXgboost callXgboost;

    /**
     * todo:指定HashMap大小
     */
    @RequestMapping("/cpu")
    @ResponseBody
    public Map getCpuUsage(){
        Map<String,Double> mp=new HashMap<>();
//        for(Pod pod: podList.getItems()){
//            String query="";
//        }
        return mp;
    }

    /**
     * 调用Xgboost模型，输出预测的Qos （从pmml文件里确定参数类型）
     * @param x1 Api-server Num
     * @param x2 Api-server Cpu
     * @param x3 Api-server Mem
     * @param x4 Node_Num
     * @return Qos(Response Time  RT)
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
        Object Qos= callXgboost.predictQos(paramMap);
        return Qos.toString();
    }

    /**
     * 测试方法，没有意义
     */
    @RequestMapping("/testJSON")
    @ResponseBody
    public String testJson(){
        BaseHttpClient baseHttpClient=new BaseHttpClient();
        JSONObject obj= baseHttpClient.getJSON();
        String conVer=obj.toString();
        try {
            JSONObject objTmp=new JSONObject(conVer);
            System.out.println(objTmp.getJSONArray("value"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }

}
