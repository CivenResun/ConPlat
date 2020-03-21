package com.zju.conplat.service;

/**
 * 从Prometheus系统中获取监控数据
 * @author civeng
 */
public interface IGetFromProm {

    /**
     * 根据PromQL查询语句，从Prometheus系统获取需要的数据
     * @param promQL 查询语句
     * @return 查询结果
     */
    public String getInfoFromProm(String promQL);
}
