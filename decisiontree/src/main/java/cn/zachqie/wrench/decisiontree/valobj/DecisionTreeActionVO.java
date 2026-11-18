package cn.zachqie.wrench.decisiontree.valobj;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @ClassName DecisionTreeActionVO
 * @Desc 决策树动作
 * @Author qiezhichao
 * @Date 2025/1/9 14:15
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DecisionTreeActionVO {

    /** 当前决策树节点 */
    private String currentDecisionTreeNode;
    /** 决策结果数据 */
    private AbstractDecisionResultDataVO decisionResultData;

    /** 过程数据，节点逻辑执行完后的数据，可以往下传递给节点线逻辑 */
    private Map<String, Object> processDataMap;

    /** 决策路径节点 */
    private List<String> decisionPathNodes;
    public DecisionTreeActionVO appendDecisionPathNodes(DecisionTreeActionVO preDecisionTreeAction) {
        if (Objects.isNull(preDecisionTreeAction) || CollectionUtils.isEmpty(preDecisionTreeAction.getDecisionPathNodes())) {
            decisionPathNodes = Lists.newArrayList();
        } else {
            // 拷贝而非共享引用：每个 action 保留截至当前节点的路径快照，避免历史 action 路径被后续节点污染
            decisionPathNodes = Lists.newArrayList(preDecisionTreeAction.getDecisionPathNodes());
        }
        decisionPathNodes.add(currentDecisionTreeNode);
        return this;
    }

    public String getCurrentDecisionPath() {
        if (CollectionUtils.isEmpty(decisionPathNodes)) {
            return null;
        }
        return String.join(" -> ", decisionPathNodes);
    }
}
