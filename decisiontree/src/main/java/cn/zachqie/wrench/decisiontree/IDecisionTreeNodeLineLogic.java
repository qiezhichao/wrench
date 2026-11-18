package cn.zachqie.wrench.decisiontree;

import com.alibaba.fastjson2.JSONObject;
import cn.zachqie.wrench.decisiontree.valobj.DecisionTreeActionVO;
import cn.zachqie.wrench.decisiontree.valobj.DecisionTreeNodeLineVO;

/**
 * @ClassName IDecisionTreeNodeLineLogic
 * @Desc 决策树节点线逻辑
 * @Author qiezhichao
 * @Date 2025/1/15 15:21
 */
public interface IDecisionTreeNodeLineLogic {

    /**
     * @desc 是否满足条件
     * @author qiezhichao
     * @date 2025/1/15 15:22
     * @param userId 用户id
     * @param inputFactor 输入因子
     * @param treeNodeLine 节点连线
     * @param fromNodeDecisionTreeActionVO from节点行为
     * @return
     */
    public Boolean isMatchCondition(String userId,
                                    JSONObject inputFactor,
                                    DecisionTreeNodeLineVO treeNodeLine,
                                    DecisionTreeActionVO fromNodeDecisionTreeActionVO);
}
