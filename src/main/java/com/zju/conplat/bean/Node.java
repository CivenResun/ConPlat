package com.zju.conplat.bean;

import java.io.Serializable;

/**
 *Node节点的监控信息
 * @author civeng
 */
public class Node implements Serializable {
    private String cpuUsed;
    private String memUsed;
    private String cpuTotal;
    private String memTotal;
    private String fsUsed;
    private String fsTotal;


    public String getCpuUsed() {
        return cpuUsed;
    }

    public void setCpuUsed(String cpuUsed) {
        this.cpuUsed = cpuUsed;
    }

    public String getMemUsed() {
        return memUsed;
    }

    public void setMemUsed(String memUsed) {
        this.memUsed = memUsed;
    }

    public String getCpuTotal() {
        return cpuTotal;
    }

    public void setCpuTotal(String cpuTotal) {
        this.cpuTotal = cpuTotal;
    }

    public String getMemTotal() {
        return memTotal;
    }

    public void setMemTotal(String memTotal) {
        this.memTotal = memTotal;
    }

    public String getFsUsed() {
        return fsUsed;
    }

    public void setFsUsed(String fsUsed) {
        this.fsUsed = fsUsed;
    }

    public String getFsTotal() {
        return fsTotal;
    }

    public void setFsTotal(String fsTotal) {
        this.fsTotal = fsTotal;
    }
}
