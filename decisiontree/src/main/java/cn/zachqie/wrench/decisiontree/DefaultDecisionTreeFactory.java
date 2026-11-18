package cn.zachqie.wrench.decisiontree;


import cn.zachqie.wrench.decisiontree.impl.DefaultDecisionTreeEngine;
import cn.zachqie.wrench.decisiontree.valobj.DecisionTreeVO;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @ClassName DefaultDecisionTreeFactory
 * @Desc 默认决策树工厂
 * @Author qiezhichao
 * @Date 2025/1/9 14:46
 */
@Slf4j
public class DefaultDecisionTreeFactory {

    private final Map<String, IDecisionTreeNodeLogic> decisionTreeNodeLogicMap;
    private final Map<String, IDecisionTreeNodeLineLogic> decisionTreeNodeLineLogicMap;

    public DefaultDecisionTreeFactory(Map<String, IDecisionTreeNodeLogic> decisionTreeNodeLogicMap,
                                      Map<String, IDecisionTreeNodeLineLogic> decisionTreeNodeLineLogicMap) {
        this.decisionTreeNodeLogicMap = decisionTreeNodeLogicMap;
        this.decisionTreeNodeLineLogicMap = decisionTreeNodeLineLogicMap;
    }

    /**
     * @desc 创建决策树引擎
     * @author qiezhichao
     * @date 2025/1/9 14:51
     * @param decisionTreeVO 决策树
     * @return IDecisionTreeEngine 决策树引擎
     */
    public IDecisionTreeEngine buildDecisionTreeEngine(DecisionTreeVO decisionTreeVO) {
        return new DefaultDecisionTreeEngine(decisionTreeNodeLogicMap, decisionTreeNodeLineLogicMap, decisionTreeVO);
    }
}
