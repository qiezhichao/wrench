package cn.zachqie.wrench.decisiontree.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName DecisionTreeNodeLineVO
 * @Desc 决策树节点线
 * @Author qiezhichao
 * @Date 2025/1/9 13:54
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DecisionTreeNodeLineVO {

    /** 决策树ID */
    private String treeId;

    /** 节点线key */
    private String key;

    /** from节点 */
    private String fromTreeNode;

    /** to节点 */
    private String toTreeNode;

    /** 规则条件 */
    private String ruleCondition;
}
