package cn.zachqie.wrench.base;

import lombok.Data;
import lombok.ToString;

import java.util.Objects;

/**
 * @Desc 通用分页查询属性
 * @ClassName PageQuery
 * @Author qiezhichao
 * @Date 2022/11/28 15:30
 */
@Data
@ToString
public class PageProperty {
    private static final Integer DEFAULT_PAGE_NUM = 1;
    private static final Integer DEFAULT_PAGE_SIZE = 100;
    private static final Integer DEFAULT_MAX_PAGE_SIZE = 1000;

    private Integer pageNum;
    private Integer pageSize;
//    private Boolean notLimitPageSize = false;

    public PageProperty() {
        this(DEFAULT_PAGE_NUM, DEFAULT_PAGE_SIZE);
    }

    public PageProperty(Integer pageNum, Integer pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public Integer getPageNum() {
        return Objects.isNull(this.pageNum) || this.pageNum < 1 ?
                DEFAULT_PAGE_NUM : this.pageNum;
    }

    public Integer getPageSize() {
        if (Objects.isNull(this.pageSize) || this.pageSize < 1) {
            return DEFAULT_PAGE_SIZE;
        }

//        if (Boolean.TRUE.equals(notLimitPageSize)) {
//            return this.pageSize;
//        }

        return this.pageSize > DEFAULT_MAX_PAGE_SIZE ? DEFAULT_MAX_PAGE_SIZE : this.pageSize;
    }
}
