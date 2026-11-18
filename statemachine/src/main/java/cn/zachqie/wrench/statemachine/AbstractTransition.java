package cn.zachqie.wrench.statemachine;

import cn.zachqie.wrench.statemachine.valobj.StateEventVO;
import cn.zachqie.wrench.statemachine.valobj.StateVO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public abstract class AbstractTransition {

    protected String eventCode;
    private StateVO currentState;
    protected StateVO nextState;

    public AbstractTransition(String eventCode, StateVO currentState, StateVO nextState) {
        this.eventCode = eventCode;
        this.currentState = currentState;
        this.nextState = nextState;
    }

    public final StateVO execute(StateEventVO event) {
        this.beforeExecute(event);
        if (this.doExecute(event)) {
            this.afterExecute(event);
            return this.nextState;
        } else {
            return this.currentState;
        }
    }

    protected void beforeExecute(StateEventVO event) {}

    protected void afterExecute(StateEventVO event) {}

    protected abstract boolean doExecute(StateEventVO event);
}
