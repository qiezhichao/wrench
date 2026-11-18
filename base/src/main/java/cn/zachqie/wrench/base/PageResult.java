package cn.zachqie.wrench.base;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @Desc 分页查询统一结果
 * @ClassName PageResult
 * @Author qiezhichao
 * @Date 2022/11/28 13:34
 */
@Data
@ToString
public class PageResult<T> implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -1235940793424203229L;

    private int pageNum;
    private int pageSize;
    private long totalRows;
    private long totalPages;
    private boolean isFirstPage;
    private boolean isLastPage;
    private boolean hasPreviousPage;
    private boolean hasNextPage;

    private List<T> list;

    private boolean isEmptyPageResult;

    private PageResult() { }

    private PageResult(int pageNum, int pageSize, long totalRows, List<T> list) {
        if (pageSize <= 0) {
            this.pageNum = 0;
            this.pageSize = 0;
            this.totalRows = 0;
            this.totalPages = 0;
            this.isFirstPage = false;
            this.isLastPage = false;
            this.hasPreviousPage = false;
            this.hasNextPage = false;
            this.list = Collections.emptyList();
            this.isEmptyPageResult = true;
            return;
        }

        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalRows = totalRows;
        this.list = list;

        this.totalPages = totalRows / pageSize + ((totalRows % pageSize) == 0 ? 0 : 1);
        this.isFirstPage = this.pageNum == 1;
        this.isLastPage = this.pageNum == this.totalPages;
        this.hasPreviousPage = this.pageNum > 1 && this.pageNum <= this.totalPages;
        this.hasNextPage = this.pageNum >= 1 && this.pageNum < this.totalPages;
        this.isEmptyPageResult = false;
    }

    public static <T> PageResult<T> buildPageResult(PageProperty pageProperty, long totalRows, List<T> list) {
        return new PageResult<>(pageProperty.getPageNum(), pageProperty.getPageSize(), totalRows, list);
    }

    public static <T> PageResult<T> buildPageResult(int pageNum, int pageSize, long totalRows, List<T> list) {
        return new PageResult<>(pageNum, pageSize, totalRows, list);
    }

    public static <T> PageResult<T> buildPageResult(PageResult<?>  source, long totalRows, List<T> list) {
        return PageResult.buildPageResult(source.getPageNum(), source.getPageSize(), totalRows, list);
    }

    public static <T> PageResult<T> buildPageResult(PageResult<?>  source, List<T> list) {
        return PageResult.buildPageResult(source.getPageNum(), source.getPageSize(), source.getTotalRows(), list);
    }

    public static <T> PageResult<T> emptyPageResult() {
        return emptyPageResult(0, 0);
    }

    public static <T> PageResult<T> emptyPageResult(int pageNum, int pageSize) {
        return PageResult.buildPageResult(pageNum, pageSize, 0, Collections.emptyList());
    }

    public static <T> PageResult<T> emptyPageResult(PageProperty pageProperty) {
        return PageResult.emptyPageResult(pageProperty.getPageNum(), pageProperty.getPageSize());
    }
}
