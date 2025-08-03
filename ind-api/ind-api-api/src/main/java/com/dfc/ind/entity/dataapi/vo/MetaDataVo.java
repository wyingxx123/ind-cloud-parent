package com.dfc.ind.entity.dataapi.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MetaDataVo {

    @ApiModelProperty(value = "是否主键")
    private String isPk;

    @ApiModelProperty(value = "字段名")
    private String name;

    @ApiModelProperty(value = "排序")
    private Integer sort;
}
