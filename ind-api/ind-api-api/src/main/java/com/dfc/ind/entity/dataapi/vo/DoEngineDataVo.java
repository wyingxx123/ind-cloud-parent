package com.dfc.ind.entity.dataapi.vo;

import com.dfc.ind.entity.dataapi.param.ExecuteQuerySqlParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * @author huff
 */
@Data
public class DoEngineDataVo {
    @ApiModelProperty(value = "应用id")
    private String engineNo;

    @ApiModelProperty(value = "顺序号")
    private Integer seq;

    @ApiModelProperty(value = "sql")
    private String sql;

    @ApiModelProperty(value = "sqlType")
    private String sqlType;

    Map<String, Object> engineAttr;
  private   ExecuteQuerySqlParam querySqlParam;


}
