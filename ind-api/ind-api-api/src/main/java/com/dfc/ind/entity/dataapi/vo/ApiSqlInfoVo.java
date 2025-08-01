package com.dfc.ind.entity.dataapi.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ApiSqlInfoVo {
    @ApiModelProperty(value = "应用id")
    private String appId;
    @ApiModelProperty(value = "服务id")
    private String serviceId;
    @ApiModelProperty(value = "sql")
    private String engineResource;

}
