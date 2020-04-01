package com.zju.conplat.vo;

import cn.hutool.core.date.DateTime;
import lombok.Data;


/**
 * @author: 罗政
 * @date: 2020/03/30
 **/
@Data
public class Server {

    private String Cpu;
    private String Mem;
    private String Disk;
    private DateTime dateTime;
}
