package cn.zachqie.wrench.decisiontree.impl;


import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import cn.zachqie.wrench.decisiontree.IDecisionTreeNodeLineLogic;
import cn.zachqie.wrench.decisiontree.IDecisionTreeNodeLogic;
import cn.zachqie.wrench.decisiontree.valobj.DecisionTreeActionVO;
import cn.zachqie.wrench.decisiontree.valobj.DecisionTreeNodeVO;
import cn.zachqie.wrench.decisiontree.valobj.DecisionTreeVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @ClassName DefaultDecisionTreeEngine
 * @Desc 决策树引擎
 * @Author qiezhichao
 * @Date 2025/1/9 13:46
 */
@Slf4j
public class DefaultDecisionTreeEngine extends AbstractDecisionTreeEngine {
    public DefaultDecisionTreeEngine(Map<String, IDecisionTreeNodeLogic> decisionTreeNodeLogicMap,
                                     Map<String, IDecisionTreeNodeLineLogic> decisionTreeNodeLineLogicMap,
                                     DecisionTreeVO decisionTreeVO) {
        super(decisionTreeNodeLogicMap, decisionTreeNodeLineLogicMap, decisionTreeVO);
    }

    @Override
    public List<DecisionTreeActionVO> process(String userId, JSONObject inputFactor) {
        // 获取决策树基础信息
        String treeId = decisionTreeVO.getTreeId();
        String treeName = decisionTreeVO.getTreeName();
        String treeRootNode = decisionTreeVO.getTreeRootNode();
        Map<String, DecisionTreeNodeVO> treeNodeMap = decisionTreeVO.getTreeNodeMap();

        log.info("**********************决策树引擎【treeId:{} treeName:{}】开始执行**********************", treeId, treeName);

        // 获取根节点，根节点记录了第一个要执行的决策逻辑
        InnerTreeNode currentNode = InnerTreeNode.builder().currentTreeNode(treeRootNode).build();
        // 初始化根节点处理结果
        DecisionTreeActionVO currentTreeNodeAction = DecisionTreeActionVO.builder()
                .currentDecisionTreeNode(treeRootNode)
                .decisionResultData(null)
                .decisionPathNodes(Lists.newArrayList(treeRootNode))
                .build();

        DecisionTreeActionVO preTreeNodeAction = null;
        List<DecisionTreeActionVO> resultDecisionTreeActionVOs = Lists.newArrayList();
        do {
            try {
                DecisionTreeNodeVO currentDecisionTreeNodeVO = treeNodeMap.get(currentNode.getCurrentTreeNode());
//                if (Objects.isNull(currentDecisionTreeNodeVO)) {
//                    if (currentNode.isEnd()) {
//                        currentDecisionTreeNodeVO = DecisionTreeNodeVO.buildEndNode();
//                    } else {
//                        log.info("决策树引擎【treeId:{} treeName:{}】当前节点【{}】未找到", treeId, treeName, currentNode.getCurrentTreeNode());
//                        break;
//                    }
//                }
                if (Objects.isNull(currentDecisionTreeNodeVO)) {
                    log.info("决策树引擎【treeId:{} treeName:{}】当前节点【{}】未找到", treeId, treeName, currentNode.getCurrentTreeNode());
                    break;
                }

                IDecisionTreeNodeLogic decisionTreeNodeLogic = decisionTreeNodeLogicMap.get(currentDecisionTreeNodeVO.getTreeNodeKey());

                // 执行当前决策树节点逻辑
                currentTreeNodeAction = decisionTreeNodeLogic.logic(userId, inputFactor, currentDecisionTreeNodeVO, preTreeNodeAction);
                currentTreeNodeAction.appendDecisionPathNodes(preTreeNodeAction);

                // 当前节点处理动作放入结果集
                resultDecisionTreeActionVOs.add(currentTreeNodeAction);

                // 获取下一个决策节点
//                InnerTreeNode nextNode = currentNode.isEnd() ?
//                        null :
//                        this.getFirstMatchNextNode(userId, inputFactor, currentDecisionTreeNodeVO, currentTreeNodeAction);
                InnerTreeNode nextNode = this.getFirstMatchNextNode(userId, inputFactor, currentDecisionTreeNodeVO, currentTreeNodeAction);
                log.info("决策树引擎【treeId:{} treeName:{}】currentTreeNode:{}, nextNode:{}, decisionPath:{}",
                        treeId,
                        treeName,
                        currentNode.getCurrentTreeNode(),
                        Objects.isNull(nextNode) ? null : nextNode.getCurrentTreeNode(),
                        currentTreeNodeAction.getCurrentDecisionPath());

                // 为下一次循环准备数据
                currentNode = nextNode;
                preTreeNodeAction = currentTreeNodeAction;
            } catch (Exception e) {
                log.error("决策树引擎【treeId:{} treeName:{}】currentTreeNode:{} 执行异常, 决策树执行结束",
                        treeId, treeName, currentNode.getCurrentTreeNode(), e);
                currentNode = null;
            }
        } while (currentNode != null);

        // 如果结果集为空，说明循环一次都没有执行，则将默认的根节点处理结果放入结果集
        if (CollectionUtils.isEmpty(resultDecisionTreeActionVOs)) {
            resultDecisionTreeActionVOs.add(currentTreeNodeAction);
        }

        log.info("======================决策树引擎【treeId:{} treeName:{}】执行完毕, 决策路径:{}======================",
                treeId, treeName, currentTreeNodeAction.getCurrentDecisionPath());

        return resultDecisionTreeActionVOs;
    }

    @Override
    public DecisionTreeActionVO process4LastNodeResult(String userId, JSONObject inputFactor) {
        List<DecisionTreeActionVO> decisionTreeActionVOs = this.process(userId, inputFactor);
        return decisionTreeActionVOs.get(decisionTreeActionVOs.size() - 1);
    }
}
