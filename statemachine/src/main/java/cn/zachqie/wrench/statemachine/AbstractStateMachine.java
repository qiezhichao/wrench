package cn.zachqie.wrench.statemachine;


import cn.zachqie.wrench.base.Assert;
import cn.zachqie.wrench.statemachine.valobj.StateEventVO;
import cn.zachqie.wrench.statemachine.valobj.StateVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public abstract class AbstractStateMachine {

    private List<StateVO> allStates;

    /**
     * @desc 状态机执行
     * @author qiezhichao
     * @date 2025/1/21 14:02
     * @param stateCode 状态码
     * @param event 事件
     * @return Optional<StateVO> 下一个状态
     */
    public Optional<StateVO> execute(String stateCode, StateEventVO event) {
        StateVO state = this.getState(stateCode);
        for (AbstractTransition transition : state.getTransitions()) {
            if (Objects.nonNull(event) && StringUtils.equalsIgnoreCase(event.getEventCode(), transition.getEventCode())) {
                return Optional.ofNullable(transition.execute(event));
            }
        }
        return Optional.empty();
    }

    /**
     * @desc 状态机执行，异常抛出
     * @author qiezhichao
     * @date 2025/1/21 14:02
     * @param stateCode 状态码
     * @param event 事件
     * @return Optional<StateVO> 下一个状态
     */
    public Optional<StateVO> executeWithException(String stateCode, StateEventVO event) {
        Optional<StateVO> stateOptional = this.execute(stateCode, event);
        if (!stateOptional.isPresent()) {
            throw new RuntimeException("类型转换错误");
        }

        // 如果执行前后状态相同，则需要进行判断
        if (stateOptional.get().getStateCode().equalsIgnoreCase(stateCode) && !event.isSupportNoChange()) {
            throw new RuntimeException("类型转换受限制");
        }

        return stateOptional;
    }

    /*
     * @desc 根据状态码获取状态对象
     * @author qiezhichao
     * @date 2025/1/21 14:18
     * @param stateCode 状态码
     * @return StateVO 状态对象
     */
    protected final StateVO getState(String stateCode) {
        if (CollectionUtils.isEmpty(allStates)) {
            this.allStates = declareAllStates();
        }
        Assert.notEmpty(allStates, "状态类型错误");

        for (StateVO state : allStates) {
            if (StringUtils.equalsIgnoreCase(state.getStateCode(), stateCode)) {
                return state;
            }
        }

        throw new RuntimeException("状态错误");
    }

    /**
     * @desc 定义所有的状态
     * @author qiezhichao
     * @date 2025/1/21 14:01
     */
    public abstract List<StateVO> declareAllStates();
}
