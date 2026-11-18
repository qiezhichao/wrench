package cn.zachqie.wrench.test.statemachine.impl;


import com.google.common.collect.Lists;
import cn.zachqie.wrench.statemachine.AbstractStateMachine;
import cn.zachqie.wrench.statemachine.impl.DoNothingTransition;
import cn.zachqie.wrench.statemachine.valobj.StateVO;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class TestStateMachine extends AbstractStateMachine {

    @Override
    public List<StateVO> declareAllStates() {
        // 定义状态
        StateVO creating = new StateVO("CREATING");
        StateVO created = new StateVO("CREATED");

        // 定义状态流转出发逻辑
        creating.addTransition(new DoNothingTransition("SAVE", creating, created));

        List<StateVO> list = Lists.newArrayList();
        list.add(creating);
        list.add(created);
        return list;
    }
}
