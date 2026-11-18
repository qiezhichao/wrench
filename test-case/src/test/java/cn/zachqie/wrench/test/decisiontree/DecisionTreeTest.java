package cn.zachqie.wrench.test.decisiontree;

import cn.zachqie.wrench.test.decisiontree.logic.Logic_1;
import cn.zachqie.wrench.test.decisiontree.logic.Login_End;
import cn.zachqie.wrench.decisiontree.DefaultDecisionTreeFactory;
import cn.zachqie.wrench.decisiontree.IDecisionTreeEngine;
import cn.zachqie.wrench.decisiontree.IDecisionTreeNodeLineLogic;
import cn.zachqie.wrench.decisiontree.IDecisionTreeNodeLogic;
import cn.zachqie.wrench.decisiontree.impl.RandomDecisionTreeNodeLineLogic;
import cn.zachqie.wrench.decisiontree.valobj.DecisionTreeNodeLineVO;
import cn.zachqie.wrench.decisiontree.valobj.DecisionTreeNodeVO;
import cn.zachqie.wrench.decisiontree.valobj.DecisionTreeVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName DecisionTreeTest
 * @Author qiezhichao
 * @Date 2025/1/20 15:29
 */
@Slf4j
public class DecisionTreeTest {

    @Test
    public void test() {
        List<DecisionTreeNodeLineVO> decisionTreeNodeLineVOs = new ArrayList<>();
        decisionTreeNodeLineVOs.add(DecisionTreeNodeLineVO.builder()
                .treeId("treeId_1")
                .key("line_2")
                .fromTreeNode("tree_node_1")
                .toTreeNode("tree_node_2")
                .ruleCondition("$node_result > 500")
                .build());
        decisionTreeNodeLineVOs.add(DecisionTreeNodeLineVO.builder()
                .treeId("treeId_1")
                .key("line_3")
                .fromTreeNode("tree_node_1")
                .toTreeNode("tree_node_3")
                .ruleCondition("$node_result <= 500")
                .build());
        decisionTreeNodeLineVOs.add(DecisionTreeNodeLineVO.builder()
                .treeId("treeId_1")
                .fromTreeNode("tree_node_2")
                .toTreeNode("tree_node_4")
                .key("line_4")
                .ruleCondition("$node_result > 500")
                .build());
        decisionTreeNodeLineVOs.add(DecisionTreeNodeLineVO.builder()
                .treeId("treeId_1")
                .fromTreeNode("tree_node_2")
                .toTreeNode("tree_node_5")
                .key("line_5")
                .ruleCondition("$node_result <= 500")
                .build());
        decisionTreeNodeLineVOs.add(DecisionTreeNodeLineVO.builder()
                .treeId("treeId_1")
                .fromTreeNode("tree_node_3")
                .toTreeNode("tree_node_6")
                .key("line_6")
                .ruleCondition("$node_result > 500")
                .build());

        Map<String, List<DecisionTreeNodeLineVO>> decisionTreeNodeLineMap = new HashMap<>();
        decisionTreeNodeLineVOs.forEach(decisionTreeNodeLineVO -> decisionTreeNodeLineMap
                .computeIfAbsent(decisionTreeNodeLineVO.getFromTreeNode(), k -> new ArrayList<>())
                .add(decisionTreeNodeLineVO));

        List<DecisionTreeNodeVO> decisionTreeNodeVOs = new ArrayList<>();
        decisionTreeNodeVOs.add(DecisionTreeNodeVO.builder()
                .treeId("treeId_1")
                .treeNodeKey("tree_node_1")
                .treeNodeDesc("tree_node_1 desc")
                .treeNodeLines(decisionTreeNodeLineMap.get("tree_node_1"))
                .treeNodeResultKey("$node_result")
                .build());
        decisionTreeNodeVOs.add(DecisionTreeNodeVO.builder()
                .treeId("treeId_1")
                .treeNodeKey("tree_node_2")
                .treeNodeDesc("tree_node_2 desc")
                .treeNodeLines(decisionTreeNodeLineMap.get("tree_node_2"))
                .treeNodeResultKey("$node_result")
                .build());
        decisionTreeNodeVOs.add(DecisionTreeNodeVO.builder()
                .treeId("treeId_1")
                .treeNodeKey("tree_node_3")
                .treeNodeDesc("tree_node_3 desc")
                .treeNodeLines(decisionTreeNodeLineMap.get("tree_node_3"))
                .treeNodeResultKey("$node_result")
                .build());
        decisionTreeNodeVOs.add(DecisionTreeNodeVO.builder()
                .treeId("treeId_1")
                .treeNodeKey("tree_node_4")
                .treeNodeDesc("tree_node_4 desc")
                .treeNodeLines(decisionTreeNodeLineMap.get("tree_node_4"))
                .treeNodeResultKey("$node_result")
                .build());
        decisionTreeNodeVOs.add(DecisionTreeNodeVO.builder()
                .treeId("treeId_1")
                .treeNodeKey("tree_node_5")
                .treeNodeDesc("tree_node_5 desc")
                .treeNodeLines(decisionTreeNodeLineMap.get("tree_node_5"))
                .treeNodeResultKey("$node_result")
                .build());
        decisionTreeNodeVOs.add(DecisionTreeNodeVO.builder()
                .treeId("treeId_1")
                .treeNodeKey("tree_node_6")
                .treeNodeDesc("tree_node_6 desc")
                .treeNodeLines(decisionTreeNodeLineMap.get("tree_node_6"))
                .treeNodeResultKey("$node_result")
                .build());
        decisionTreeNodeVOs.add(DecisionTreeNodeVO.builder()
                .treeId("treeId_1")
                .treeNodeKey("tree_node_end")
                .treeNodeDesc("tree_node_end desc")
                .treeNodeLines(decisionTreeNodeLineMap.get("tree_node_end"))
                .treeNodeResultKey("$node_result")
                .build());
        Map<String, DecisionTreeNodeVO> decisionTreeNodeMap = decisionTreeNodeVOs.stream()
                .collect(Collectors.toMap(DecisionTreeNodeVO::getTreeNodeKey, decisionTreeNodeVO -> decisionTreeNodeVO));


        DecisionTreeVO decisionTreeVO = DecisionTreeVO.builder()
                .treeId("treeId_1")
                .treeName("决策树1")
                .treeDesc("决策树1描述")
                .treeRootNode("tree_node_1")
                .treeNodeMap(decisionTreeNodeMap)
                .build();

        Map<String, IDecisionTreeNodeLogic> decisionTreeNodeLogicMap = new HashMap<>();
        IDecisionTreeNodeLogic logic_1 = new Logic_1();
        IDecisionTreeNodeLogic logic_end = new Login_End();
        decisionTreeNodeLogicMap.put("tree_node_1", logic_1);
        decisionTreeNodeLogicMap.put("tree_node_2", logic_1);
        decisionTreeNodeLogicMap.put("tree_node_3", logic_1);
        decisionTreeNodeLogicMap.put("tree_node_4", logic_1);
        decisionTreeNodeLogicMap.put("tree_node_5", logic_1);
        decisionTreeNodeLogicMap.put("tree_node_6", logic_1);
        decisionTreeNodeLogicMap.put("tree_node_end", logic_end);

        Map<String, IDecisionTreeNodeLineLogic> decisionTreeNodeLineLogicMap = new HashMap<>();
        IDecisionTreeNodeLineLogic lineLogic_1 = new RandomDecisionTreeNodeLineLogic();
        decisionTreeNodeLineLogicMap.put("line_2", lineLogic_1);
        decisionTreeNodeLineLogicMap.put("line_3", lineLogic_1);
        decisionTreeNodeLineLogicMap.put("line_4", lineLogic_1);
        decisionTreeNodeLineLogicMap.put("line_5", lineLogic_1);
        decisionTreeNodeLineLogicMap.put("line_6", lineLogic_1);

        DefaultDecisionTreeFactory defaultDecisionTreeFactory = new DefaultDecisionTreeFactory(decisionTreeNodeLogicMap, decisionTreeNodeLineLogicMap);
        IDecisionTreeEngine decisionTreeEngine = defaultDecisionTreeFactory.buildDecisionTreeEngine(decisionTreeVO);

        for (int i = 0; i < 1; i++) {
            decisionTreeEngine.process("user001", null);
        }
    }
}
