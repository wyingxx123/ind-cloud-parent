package com.dfc.ind.entity.dataapi.vo;

import lombok.Data;

@Data
public class FiledTypeVo {
    private String tableSchema;
    private String tableName;
    private String columnName;
    private String columnType;
}
