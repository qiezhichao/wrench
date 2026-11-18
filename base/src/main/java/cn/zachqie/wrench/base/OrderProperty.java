package cn.zachqie.wrench.base;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Desc 通用排序属性
 * @ClassName OrderProperty
 * @Author qiezhichao
 * @Date 2022/11/29 09:19
 */
@Data
@ToString
public class OrderProperty {

    /**
     * 排序属性集合
     */
    private List<Order> orders;

    public List<Order> getOrders() {
        return CollectionUtils.isEmpty(this.orders) ?
                Collections.emptyList() :
                this.orders.stream()
                        .filter(t -> Objects.nonNull(t) && t.checkAvailable())
                        .sorted(Comparator.comparingInt(Order::getOrderPosition))
                        .collect(Collectors.toList());
    }

    public boolean hasOrders() {
        return !CollectionUtils.isEmpty(this.getOrders());
    }

    public OrderProperty orderAsc(String property) {
        return order(property, OrderRule.ASC);
    }

    public OrderProperty orderDesc(String property) {
        return order(property, OrderRule.DESC);
    }

    public OrderProperty order(String property, OrderRule orderRule) {
        if (StringUtils.isBlank(property) || Objects.isNull(orderRule)) {
            return this;
        }

        if (CollectionUtils.isEmpty(this.orders)) {
            this.orders = new ArrayList<>();
        }

        Order order = new Order(property, orderRule, this.orders.size() + 1);
        orders.add(order);
        return this;
    }

    @Getter
    public static class Order {

        /**
         * 排序字段
         */
        private final String orderField;

        /**
         * 排序规则，DESC or ASC
         */
        private final OrderRule orderRule;

        /**
         * 排序顺序，比如要先按ID排序，再按照name排序，则通过这个字段进行处理
         */
        private final int orderPosition;

        public Order(String orderField, OrderRule orderRule, int orderPosition) {
            this.orderField = orderField;
            this.orderRule = orderRule;
            this.orderPosition = orderPosition;
        }

        public boolean checkAvailable() {
            return StringUtils.isNotBlank(this.getOrderField())
                    && Objects.nonNull(this.getOrderRule());
        }

        public boolean isAsc() {
            return OrderRule.ASC.equals(this.orderRule);
        }

        @Override
        public String toString() {
            return "Order{" +
                    "orderField='" + orderField + '\'' +
                    ", orderRule=" + orderRule +
                    ", orderPosition=" + orderPosition +
                    '}';
        }
    }

    public enum OrderRule {
        ASC,
        DESC
    }
}
