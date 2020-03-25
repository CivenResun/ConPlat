package com.zju.conplat.bean;

/**
 * Pod监控信息
 * @author civeng
 */
public class Pod {
    private String cpuUsed;
    private String memUsed;
    private String cpuTotal;
    private String memTotal;
    private String networkIO;

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

    public String getNetworkIO() {
        return networkIO;
    }

    public void setNetworkIO(String networkIO) {
        this.networkIO = networkIO;
    }
}
