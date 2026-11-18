package cn.zachqie.wrench.decisiontree;

import com.alibaba.fastjson2.JSONObject;
import cn.zachqie.wrench.decisiontree.valobj.DecisionTreeActionVO;
import cn.zachqie.wrench.decisiontree.valobj.DecisionTreeNodeVO;

/**
 * @ClassName IDecisionTreeNodeLogic
 * @Desc 决策树节点逻辑
 * @Author qiezhichao
 * @Date 2025/1/9 14:17
 */
public interface IDecisionTreeNodeLogic {

    /**
     * @desc 决策树节点逻辑
     * @author qiezhichao
     * @date 2025/1/9 15:44
     * @param userId 用户id
     * @param inputFactor 输入因子
     * @param currentDecisionTreeNodeVO 当前节点
     * @param preTreeNodeAction 上一个节点动作
     * @return DecisionTreeActionVO 结果
     */
    public DecisionTreeActionVO logic(String userId,
                                      JSONObject inputFactor,
                                      DecisionTreeNodeVO currentDecisionTreeNodeVO,
                                      DecisionTreeActionVO preTreeNodeAction);
}
