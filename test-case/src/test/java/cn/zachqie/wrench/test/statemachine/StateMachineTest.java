package cn.zachqie.wrench.test.statemachine;


import cn.zachqie.wrench.statemachine.valobj.StateEventVO;
import cn.zachqie.wrench.test.statemachine.impl.TestStateMachine;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class StateMachineTest {

    @Test
    public void test(){
        TestStateMachine testStateMachine = new TestStateMachine();
        testStateMachine.execute("CREATING", new StateEventVO("SAVE")).ifPresent(t -> System.out.println(t.getStateCode()));
    }
}
