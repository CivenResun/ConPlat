package com.zju.conplat.service.impl;

import com.zju.conplat.service.IGetFromProm;
import com.zju.conplat.utils.GetPromData;
import org.springframework.stereotype.Service;

/**
 * 获取Prometheus数据，实现类
 * @author civeng
 */
@Service
public class GetFromPromImpl implements IGetFromProm {

    /**
     * 根据PromQL查询语句，从Prometheus系统获取需要的数据
     *
     * @param promQL 查询语句
     * @return 查询结果
     */
    @Override
    public String getInfoFromProm(String promQL) {
        String result=GetPromData.getOneSetData(promQL);
        return result;
    }
}
