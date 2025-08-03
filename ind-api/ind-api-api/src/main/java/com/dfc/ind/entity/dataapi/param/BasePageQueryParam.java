package com.dfc.ind.entity.dataapi.param;

import com.google.common.base.CaseFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 分页查询基础参数，分页参数类应该继承此类
 * @author zhouzhenhui
 */
@Component
public class BasePageQueryParam {

    /**
     * 最大分页大小
     */
    private Integer maxPageSize = 10000;

    /**
     * 升序
     */
    public static final String ASC = "ASC";

    /**
     * 降序
     */
    public static final String DESC = "DESC";

    /**
     * 默认分页大小
     */
    private static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 最大分页大小
     */
    private static int MAX_PAGE_SIZE = 10000;

    /**
     * 默认页码
     */
    private static final int DEFAULT_PAGE_INDEX = 1;

    /**
     * 页码，从1开始
     */
    private int pageIndex = DEFAULT_PAGE_INDEX;

    /**
     * 页数
     */
    private int pageSize = DEFAULT_PAGE_SIZE;

    /**
     * 排序字段
     */
    private String orderBy;

    /**
     * 排序方式
     */
    private String orderDirection;

    public int getPageIndex() {
        if (pageIndex < DEFAULT_PAGE_INDEX) {
            return DEFAULT_PAGE_INDEX;
        }
        return pageIndex;
    }

    public BasePageQueryParam setPageIndex(int pageIndex) {
        if (pageIndex < DEFAULT_PAGE_INDEX) {
            pageIndex = DEFAULT_PAGE_INDEX;
        }
        this.pageIndex = pageIndex;
        return this;
    }

    public int getPageSize() {
        if (pageSize < DEFAULT_PAGE_INDEX) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        if (pageSize > MAX_PAGE_SIZE) {
            pageSize = MAX_PAGE_SIZE;
        }
        return pageSize;
    }

    public BasePageQueryParam setPageSize(int pageSize) {
        if (pageSize < DEFAULT_PAGE_INDEX) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        this.pageSize = pageSize;
        return this;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public BasePageQueryParam setOrderBy(String orderBy) {
        this.orderBy = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, orderBy);
        return this;
    }

    public String getOrderDirection() {
        return orderDirection;
    }

    public BasePageQueryParam setOrderDirection(String orderDirection) {
        if (ASC.equalsIgnoreCase(orderDirection) || DESC.equalsIgnoreCase(orderDirection)) {
            this.orderDirection = orderDirection;
        }
        return this;
    }

    @Value("${maxPageSize:10000}")
    private void setMaxPageSize(Integer maxPageSize) {
        this.maxPageSize = maxPageSize;
        MAX_PAGE_SIZE = maxPageSize;
    }
}
