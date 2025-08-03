package com.dfc.ind.entity.dataapi.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "LoadApiDataToRedisParam", description = "api数据加载缓存")
public class LoadApiDataToRedisParam {
    @ApiModelProperty(value = "应用id")
    @NotNull
    private String appId;

    @ApiModelProperty(value = "指定数据库")
    @NotNull
    private String schema;

    @ApiModelProperty(value = "指定表")
    private String tableName;

    @ApiModelProperty(value = "额外条件 and xx=xx")
    private String andSql;

    @ApiModelProperty(value = "过期时间")
    private Long expireDays;
}
