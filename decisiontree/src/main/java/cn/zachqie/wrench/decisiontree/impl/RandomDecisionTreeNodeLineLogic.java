package cn.zachqie.wrench.decisiontree.impl;

import cn.zachqie.wrench.decisiontree.IDecisionTreeNodeLineLogic;
import cn.zachqie.wrench.decisiontree.valobj.DecisionTreeActionVO;
import cn.zachqie.wrench.decisiontree.valobj.DecisionTreeNodeLineVO;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;

/**
 * @ClassName RandomDecisionTreeNodeLineLogic
 * @Author qiezhichao
 * @Date 2025/1/17 10:08
 */
@Slf4j
public class RandomDecisionTreeNodeLineLogic implements IDecisionTreeNodeLineLogic {

    @Override
    public Boolean isMatchCondition(String userId,
                                    JSONObject inputFactor,
                                    DecisionTreeNodeLineVO treeNodeLine,
                                    DecisionTreeActionVO fromNodeDecisionTreeActionVO) {

        log.info("决策树连线【{}】-【开始匹配】userId:{}, ruleCondition:{}, processMap:{}",
                treeNodeLine.getKey(), userId, treeNodeLine.getRuleCondition(), fromNodeDecisionTreeActionVO.getProcessDataMap());

        Boolean result = RandomUtils.nextInt() % 2 == 0;

        log.info("决策树连线【{}】-【结束匹配】userId:{}, result:{}", treeNodeLine.getKey(), userId, result);
        return result;
    }
}
