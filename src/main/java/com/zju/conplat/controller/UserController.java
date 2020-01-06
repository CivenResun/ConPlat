package com.zju.conplat.controller;

import com.zju.conplat.utils.BaseHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author civeng
 */
@Controller
public class UserController {


    /**
     *
     * todo:指定HashMap大小
     */
    @RequestMapping("/cpu")
    @ResponseBody
    public Map getCpuUsage(){
        Map<String,Double> mp=new HashMap<>();
        for(Pod pod: podList.getItems()){
            String query="";
        }
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
