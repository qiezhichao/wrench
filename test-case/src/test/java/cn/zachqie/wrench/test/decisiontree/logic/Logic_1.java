package cn.zachqie.wrench.test.decisiontree.logic;

import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Maps;
import cn.zachqie.wrench.decisiontree.IDecisionTreeNodeLogic;
import cn.zachqie.wrench.decisiontree.valobj.DecisionTreeActionVO;
import cn.zachqie.wrench.decisiontree.valobj.DecisionTreeNodeVO;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @ClassName Logic_1
 * @Author qiezhichao
 * @Date 2025/1/15 10:50
 */
@Slf4j
public class Logic_1 implements IDecisionTreeNodeLogic {

    @Override
    public DecisionTreeActionVO logic(String userId,
                                      JSONObject inputFactor,
                                      DecisionTreeNodeVO currentDecisionTreeNodeVO,
                                      DecisionTreeActionVO preTreeNodeAction) {

        log.info("决策树节点【{}】-Logic_1【开始执行】userId:{}, inputFactor:{}", currentDecisionTreeNodeVO.getTreeNodeKey(), userId, inputFactor);

//        log.info("决策树节点-Logic_1【do something...】");

        String treeNodeResultKey = currentDecisionTreeNodeVO.getTreeNodeResultKey();
        Map<String, Object> processDataMap = Maps.newHashMap();
        processDataMap.put(treeNodeResultKey, RandomUtil.getRandom(1000));

        log.info("决策树节点【{}】-Logic_1【结束执行】userId:{}, inputFactor:{}", currentDecisionTreeNodeVO.getTreeNodeKey(), userId, inputFactor);
        return DecisionTreeActionVO.builder()
                .currentDecisionTreeNode(currentDecisionTreeNodeVO.getTreeNodeKey())
                .processDataMap(processDataMap)
                .build();
    }
}
