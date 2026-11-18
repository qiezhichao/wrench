package cn.zachqie.wrench.statemachine.valobj;


import com.google.common.collect.Lists;
import cn.zachqie.wrench.statemachine.AbstractTransition;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

@Getter
public class StateVO {

    private final String stateCode;
    private Integer order = 99;
    private List<AbstractTransition> transitions;


    public StateVO(String stateCode) {
        this.stateCode = stateCode;
    }

    public StateVO(String stateCode, Integer order) {
        this.stateCode = stateCode;
        this.order = order;
    }

    public StateVO(String stateCode, List<AbstractTransition> transitions) {
        this.stateCode = stateCode;
        this.transitions = CollectionUtils.isEmpty(transitions) ? Lists.newArrayList() : transitions;
    }

    public StateVO addTransition(AbstractTransition transition) {
        if (transition == null) {
            return this;
        }

        if (CollectionUtils.isEmpty(transitions)) {
            this.transitions = Lists.newArrayList();
        }

        transitions.add(transition);
        return this;
    }
}
