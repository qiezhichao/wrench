package cn.zachqie.wrench.decisiontree.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName DecisionTreeNodeVO
 * @Desc 决策树节点
 * @Author qiezhichao
 * @Date 2025/1/9 13:53
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DecisionTreeNodeVO {

    /** 决策树ID */
    private String treeId;

    /** 节点key */
    private String treeNodeKey;

    /** 节点描述 */
    private String treeNodeDesc;

    /** 节点结果对应的key, 用来放入 DecisionTreeActionVO 的 processDataMap 中 */
    private String treeNodeResultKey;

    /** 节点连线 */
    private List<DecisionTreeNodeLineVO> treeNodeLines;
}
