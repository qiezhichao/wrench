package cn.zachqie.wrench.test.decisiontree.logic;

import com.alibaba.fastjson2.JSONObject;
import cn.zachqie.wrench.decisiontree.IDecisionTreeNodeLogic;
import cn.zachqie.wrench.decisiontree.valobj.DecisionTreeActionVO;
import cn.zachqie.wrench.decisiontree.valobj.DecisionTreeNodeVO;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName Logic_6
 * @Author qiezhichao
 * @Date 2025/1/15 10:50
 */
@Slf4j
public class Login_End implements IDecisionTreeNodeLogic {

    @Override
    public DecisionTreeActionVO logic(String userId, JSONObject inputFactor, DecisionTreeNodeVO currentDecisionTreeNodeVO, DecisionTreeActionVO preTreeNodeAction) {
        log.info("决策树节点【{}】-Login_End【开始执行】userId:{}, inputFactor:{}, ", currentDecisionTreeNodeVO.getTreeNodeKey(), userId, inputFactor);
//        log.info("决策树节点-Login_End【do something...】");
        log.info("决策树节点【{}】-Login_End【结束执行】userId:{}, inputFactor:{}", currentDecisionTreeNodeVO.getTreeNodeKey(), userId, inputFactor);
        return DecisionTreeActionVO.builder()
                .currentDecisionTreeNode(currentDecisionTreeNodeVO.getTreeNodeKey())
                .decisionResultData(null)
                .build();
    }
}
