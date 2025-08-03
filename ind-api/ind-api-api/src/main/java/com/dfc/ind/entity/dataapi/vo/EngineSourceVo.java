package com.dfc.ind.entity.dataapi.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author huff
 */
@Data
public class EngineSourceVo {
    @ApiModelProperty(value = "应用id")
    private String engineNo;

    @ApiModelProperty(value = "顺序号")
    private Integer seq;

    @ApiModelProperty(value = "sql")
    private String sql;


}
