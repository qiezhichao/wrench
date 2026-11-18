package cn.zachqie.wrench.decisiontree;



import com.alibaba.fastjson2.JSONObject;
import cn.zachqie.wrench.decisiontree.valobj.DecisionTreeActionVO;

import java.util.List;

/**
 * @ClassName IDecisionTreeEngine
 * @Desc 决策树引擎
 * @Author qiezhichao
 * @Date 2025/1/9 13:44
 */
public interface IDecisionTreeEngine {

    /**
     * @desc 决策树执行
     * @author qiezhichao
     * @date 2025/1/9 13:49
     * @param userId 用户id
     * @param inputFactor 输入因子
     * @return List<DecisionTreeActionVO> 决策树每一步的决策动作
     */
    public List<DecisionTreeActionVO> process(String userId, JSONObject inputFactor);

    /**
     * @desc 决策树执行,返回最后一个节点的结果
     * @author qiezhichao
     * @date 2025/1/16 14:05
     * @param userId 用户id
     * @param inputFactor 输入因子
     * @return  DecisionTreeActionVO 最后一个节点的结果
     */
    public DecisionTreeActionVO process4LastNodeResult(String userId, JSONObject inputFactor);
}
