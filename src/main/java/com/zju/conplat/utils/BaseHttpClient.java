package com.zju.conplat.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sun.reflect.generics.tree.Tree;

import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * http测试基类     用于测试，请忽略
 * @author civeng
 */
public class BaseHttpClient {

    /**
     * 原生方法构建JSON
     */
    public JSONObject getJSON(){
        JSONObject jsObj=new JSONObject();
        JSONObject jsObj01=new JSONObject();
        try {
            jsObj.put("key1","value1");

            jsObj01.put("_name_","request");
            jsObj01.put("code",200);
            jsObj01.put("instance","node01");
            jsObj.put("result",jsObj01);

            JSONArray jsonArray=new JSONArray();
            jsonArray.put(1578.251);
            jsonArray.put("356");
            jsObj.put("value",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return jsObj;


    }
}
