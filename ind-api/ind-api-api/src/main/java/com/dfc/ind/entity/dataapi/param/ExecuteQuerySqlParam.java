package com.dfc.ind.entity.dataapi.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 执行查询sql参数
 * @author huff
 */
@Data
public class ExecuteQuerySqlParam extends BasePageQueryParam {

    /**
     * 数据源id
     */
    @NotBlank
    private String dataSourceId;

    /**
     * 数据库
     */
    @NotBlank
    private String schemaName;

    /**
     * 执行sql
     */
    @NotBlank
    private String sql;

    private String paramJson;



    /**
     * 是否分页
     */
    private Boolean isPage = true;

}
