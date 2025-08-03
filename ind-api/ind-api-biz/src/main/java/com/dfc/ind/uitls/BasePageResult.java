package com.dfc.ind.uitls;

import lombok.Data;
import lombok.experimental.Accessors;
import java.util.List;

/**
 * 分页结果
 * @author zhouzhenhui
 * @param <T>
 */
@Data
@Accessors(chain = true)
public class BasePageResult<T> {

    /**
     * 数据记录集合
     */
    private List<T> records;

    /**
     * 总数量
     */
    private long totalCount = 0;

    public BasePageResult(List<T> records, long totalCount) {
        this.records = records;
        this.totalCount = totalCount;
    }

    /**
     * 获取总页数
     * @param pageSize
     * @return
     */
    public long getTotalPageCount(int pageSize) {
        long totalPageCount = totalCount / pageSize;
        if (totalCount % pageSize != 0) {
            totalPageCount += 1;
        }
        return totalPageCount;
    }
}
