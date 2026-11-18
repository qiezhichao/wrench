package cn.zachqie.wrench.decisiontree.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @ClassName DecisionTreeVO
 * @Desc 决策树VO
 * @Author qiezhichao
 * @Date 2025/1/9 13:52
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DecisionTreeVO {

    /** 决策树ID */
    private String treeId;

    /** 决策树名称 */
    private String treeName;

    /** 决策树描述 */
    private String treeDesc;

    /** 决策树根节点 */
    private String treeRootNode;

    /** 决策树节点 */
    private Map<String, DecisionTreeNodeVO> treeNodeMap;
}
