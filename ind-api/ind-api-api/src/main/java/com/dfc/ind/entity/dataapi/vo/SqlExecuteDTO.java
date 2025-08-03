package com.dfc.ind.entity.dataapi.vo;

import lombok.Data;
import java.io.Serializable;

/**
 * sql执行信息数据传输对象
 * @author huff
 */
@Data
public class SqlExecuteDTO implements Serializable {

    /**
     * 数据库
     */
    String schema;

    /**
     * 执行sql
     */
    String sql;

}
