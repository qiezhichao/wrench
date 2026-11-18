package cn.zachqie.wrench.statemachine.impl;

import cn.zachqie.wrench.statemachine.AbstractTransition;
import cn.zachqie.wrench.statemachine.valobj.StateEventVO;
import cn.zachqie.wrench.statemachine.valobj.StateVO;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName DoNothingTransition
 * @Author qiezhichao
 * @Date 2025/1/21 14:00
 */
@Slf4j
public class DoNothingTransition extends AbstractTransition {

    public DoNothingTransition(String eventCode, StateVO currentState, StateVO nextState) {
        super(eventCode, currentState, nextState);
    }

    @Override
    protected boolean doExecute(StateEventVO event) {
        log.info("do nothing...");
        return true;
    }
}
