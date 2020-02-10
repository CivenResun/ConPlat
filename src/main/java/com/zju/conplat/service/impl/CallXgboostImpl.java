package com.zju.conplat.service.impl;

import com.zju.conplat.service.CallXgboost;
import com.zju.conplat.utils.CallModel;
import org.jpmml.evaluator.Evaluator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 *
 * @author civeng
 */
@Service
public class CallXgboostImpl implements CallXgboost {

    /**
     * @param paramMap 模型输入
     * @return 预测值（double类型的Qos,取两位）
     */
    @Override
    public Object predictQos(Map paramMap) {
        Evaluator evaluator = CallModel.loadPmmlAndGetEvaluator();
        Map<String, Object> results = CallModel.modelPrediction(evaluator, paramMap);

        Double predictY=(Double)results.get("y");
        BigDecimal bg=new BigDecimal(predictY);
        Double Qos=bg.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
        return Qos;
    }

}
