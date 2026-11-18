package cn.zachqie.wrench.decisiontree.impl;


import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import cn.zachqie.wrench.decisiontree.IDecisionTreeEngine;
import cn.zachqie.wrench.decisiontree.IDecisionTreeNodeLineLogic;
import cn.zachqie.wrench.decisiontree.IDecisionTreeNodeLogic;
import cn.zachqie.wrench.decisiontree.valobj.DecisionTreeActionVO;
import cn.zachqie.wrench.decisiontree.valobj.DecisionTreeNodeLineVO;
import cn.zachqie.wrench.decisiontree.valobj.DecisionTreeNodeVO;
import cn.zachqie.wrench.decisiontree.valobj.DecisionTreeVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @ClassName AbstractDecisionTreeEngine
 * @Desc 决策树引擎抽象类
 * @Author qiezhichao
 * @Date 2025/1/9 13:46
 */
@Slf4j
public abstract class AbstractDecisionTreeEngine implements IDecisionTreeEngine {

    protected final Map<String, IDecisionTreeNodeLogic> decisionTreeNodeLogicMap;
    protected final Map<String, IDecisionTreeNodeLineLogic> decisionTreeNodeLineLogicMap;
    protected final DecisionTreeVO decisionTreeVO;

    public AbstractDecisionTreeEngine(Map<String, IDecisionTreeNodeLogic> decisionTreeNodeLogicMap,
                                      Map<String, IDecisionTreeNodeLineLogic> decisionTreeNodeLineLogicMap,
                                      DecisionTreeVO decisionTreeVO) {
        this.decisionTreeNodeLogicMap = decisionTreeNodeLogicMap;
        this.decisionTreeNodeLineLogicMap = decisionTreeNodeLineLogicMap;
        this.decisionTreeVO = decisionTreeVO;
    }

    /*
     * @desc 获取下一批节点中第一个匹配的节点
     * @author qiezhichao
     * @date 2025/1/16 10:01
     * @param userId 用户id
     * @param inputFactor 输入因子
     * @param currentDecisionTreeNodeVO 当前决策树节点
     * @return InnerTreeNode 下一个节点
     */
    protected InnerTreeNode getFirstMatchNextNode(String userId,
                                                  JSONObject inputFactor,
                                                  DecisionTreeNodeVO currentDecisionTreeNodeVO,
                                                  DecisionTreeActionVO currentTreeNodeAction) {
        List<InnerTreeNode> nextNodes = this.getNextNodes(userId, inputFactor, currentDecisionTreeNodeVO, currentTreeNodeAction);
        return CollectionUtils.isEmpty(nextNodes) ? null : nextNodes.get(0);
    }

    /*
     * @desc 获取下一批节点
     * @author qiezhichao
     * @date 2025/1/16 10:01
     * @param userId 用户id
     * @param inputFactor 输入因子
     * @param currentDecisionTreeNodeVO 当前决策树节点
     * @return InnerTreeNode 下一个节点
     */
    protected List<InnerTreeNode> getNextNodes(String userId,
                                               JSONObject inputFactor,
                                               DecisionTreeNodeVO currentDecisionTreeNodeVO,
                                               DecisionTreeActionVO currentTreeNodeAction) {
        List<InnerTreeNode> nextNodes = Lists.newArrayList();

        List<DecisionTreeNodeLineVO> treeNodeLines = currentDecisionTreeNodeVO.getTreeNodeLines();
        if (CollectionUtils.isEmpty(treeNodeLines)) {
//            // 如果当前节点的节点线为空，则下个节点默认为结束节点
//            nextNodes.add(InnerTreeNode.buildTreeNodeEnd(currentDecisionTreeNodeVO.getTreeNodeKey()));
            return nextNodes;
        }

        // 节点线规则条件匹配
        for (DecisionTreeNodeLineVO treeNodeLine : treeNodeLines) {
            IDecisionTreeNodeLineLogic decisionTreeNodeLineLogic = decisionTreeNodeLineLogicMap.get(treeNodeLine.getKey());
            if (Objects.isNull(decisionTreeNodeLineLogic)) {
                continue;
            }

            if (decisionTreeNodeLineLogic.isMatchCondition(userId, inputFactor, treeNodeLine, currentTreeNodeAction)) {
                nextNodes.add(InnerTreeNode.build(currentDecisionTreeNodeVO.getTreeNodeKey(), treeNodeLine.getToTreeNode()));
            }
        }

//        // 如果所有节点线规则条件匹配都失败，则下个节点默认为结束节点
//        if (CollectionUtils.isEmpty(nextNodes)) {
//            nextNodes.add(InnerTreeNode.buildTreeNodeEnd(currentDecisionTreeNodeVO.getTreeNodeKey()));
//        }
        return nextNodes;
    }

    /**
     * @author qiezhichao
     * @desc 树节点，内部流转使用
     * @date 2025/1/16 09:17
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    protected static class InnerTreeNode {

        private String preTreeNode;
        private String currentTreeNode;
        private boolean isEnd = false;

        public static InnerTreeNode build(String preTreeNode, String currentTreeNode) {
            return InnerTreeNode.builder()
                    .preTreeNode(preTreeNode)
                    .currentTreeNode(currentTreeNode)
                    .build();
        }

        public boolean isEnd() {
            return isEnd;
        }
    }
}
