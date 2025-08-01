package com.dfc.ind.entity.dataapi.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ApiColumnVo {
    @ApiModelProperty(value = "字段名")
    private String columnName;

    @ApiModelProperty(value = "字段描述")
    private String columnDesc;



}
