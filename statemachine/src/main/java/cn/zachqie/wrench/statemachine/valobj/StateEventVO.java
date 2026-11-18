package cn.zachqie.wrench.statemachine.valobj;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Data
public class StateEventVO {

    private String eventCode;
    private String command;
    private String billCode;
    private String billType;
    private Map<String, Object> attributes;
    /** 是否支持不改变，比如状态从 待支付 到 待支付 */
    private boolean isSupportNoChange = true;

    public StateEventVO(String eventCode) {
        this.eventCode = eventCode;
        this.attributes = new HashMap<>();
    }

    public StateEventVO(String eventCode, String billCode, String billType) {
        this.eventCode = eventCode;
        this.billCode = billCode;
        this.billType = billType;
        this.attributes = new HashMap<>();
    }

    public StateEventVO(String eventCode, Map<String, Object> attributes) {
        this.eventCode = eventCode;
        this.attributes = Optional.ofNullable(attributes).orElse(new HashMap<>());
    }

    public StateEventVO(String eventCode, Map<String, Object> attributes, boolean isSupportNoChange) {
        this.eventCode = eventCode;
        this.attributes = Optional.ofNullable(attributes).orElse(new HashMap<>());
        this.isSupportNoChange = isSupportNoChange;
    }
}
