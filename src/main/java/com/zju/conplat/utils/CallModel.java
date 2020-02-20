package com.zju.conplat.utils;

import org.dmg.pmml.FieldName;
import org.dmg.pmml.PMML;
import org.jpmml.evaluator.*;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * use PMML call XGBOOST   被service层的Call Xgboost调用
 * @author civeng
 */
public class CallModel {
    /**
     * 从输入流中得到模型并返回                 被下面的Evaluator方法调用
     * @param inputStream 模型输入流
     * @return pmml
     */
    public static PMML getPMMLModel(InputStream inputStream){
        PMML pmml = new PMML();
        //unmarshal数据编出 误差e1:XML错误   e2:JAVA XML bind错误
        try{
            pmml=org.jpmml.model.PMMLUtil.unmarshal(inputStream);
        } catch(SAXException e1){
            e1.printStackTrace();
        } catch(JAXBException e2){
            e2.printStackTrace();
        } finally{
            try{
                inputStream.close();
            } catch (IOException e){
                e.printStackTrace();
            }
            return pmml;
        }
    }

    /**
     * 把PMML转化为Evaluator（直接把PMML文件路径写在代码里了）     被service层调用，作为参数传入modelPrediction方法中
     * @return 评估器
     */
    public static Evaluator loadPmmlAndGetEvaluator(){
        //获取模型的pmml文件路径,调用方法一获取模型
        InputStream inputStream= null;
        try {
            inputStream = new FileInputStream("D:\\Code\\Python\\xgboost\\XGBRegressor.pmml");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        PMML pmml=getPMMLModel(inputStream);

        //从pmml模型转化为Evaluator对象的步骤  这个newInstance是使用类加载机制，不指定具体要实例化的类，接口编程
        ModelEvaluatorFactory modelEvaluatorFactory=ModelEvaluatorFactory.newInstance();
        Evaluator evaluator=modelEvaluatorFactory.newModelEvaluator(pmml);
        pmml=null;
        return evaluator;
    }

    /**
     * 【输入】的参数放入Map中，【输出】预测结果的Map               被service层调用
     * @param evaluator 评估器
     * @param paramData 参数
     * @return 结果map
     */
    public static Map<String, Object> modelPrediction(Evaluator evaluator, Map<String, Object> paramData){
        if(evaluator==null||paramData==null){
            System.out.println("------传入对象evaluator或dataMap为空，无法预测---------");
            return null;
        }

        //获得模型的输入域列表  根据Name得到value，一起装进‘输入Map’
        List<InputField> inputFields=evaluator.getInputFields();
        Map<FieldName, FieldValue> arguments=new LinkedHashMap<>();
        //遍历输入域
        for(InputField inputField:inputFields){
            FieldName inputFieldName=inputField.getName();
            //从获取到的模型参数名 在paramData中根据Name得到参数值   arguments
            Object paramValue=paramData.get(inputFieldName.getValue());
            //转换为FieldValue对象
            FieldValue fieldValue=inputField.prepare(paramValue);
            //装入模型输入域Map
            arguments.put(inputFieldName,fieldValue);
        }

        //预测结果  转换格式（写入resultMap）输出
        Map<FieldName,?> results=evaluator.evaluate(arguments);
        List<TargetField> targetFields=evaluator.getTargetFields();

        Map<String, Object> resultMap=new HashMap<>();

        for(TargetField targetField:targetFields){
            FieldName targetFieldName=targetField.getName();
            Object targetFieldValue=results.get(targetFieldName);
            //如果目标值是可计算的(这是个接口，应该是待实现的)
            if(targetFieldValue instanceof  Computable){
                Computable computable=(Computable) targetFieldValue;
                resultMap.put(targetFieldName.getValue(),computable.getResult());
            }else{
                resultMap.put(targetFieldName.getValue(),targetFieldValue);
            }
        }
        return resultMap;
    }
}
