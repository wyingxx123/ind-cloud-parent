package com.dfc.ind.entity.dataapi.vo;

import com.dfc.ind.common.core.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * api保存 数据缓存传输对象
 * @author huff
 */
@Data
public class ApiInfoCatchDTO implements Serializable {


    @ApiModelProperty(value = "接口信息编号")
    @Excel(name = "接口信息编号")
    private String apiCode;

    @ApiModelProperty(value = "应用id")
    @Excel(name = "应用id")
    private String appId;

    @Excel(name = "api名称")
    @ApiModelProperty(value = "api名称")
    private String name;

    @Excel(name = "api类型")
    @ApiModelProperty(value = "api类型")
    private String type;

    @Excel(name = "api描述")
    @ApiModelProperty(value = "api描述")
    private String apiDesc;

    @ApiModelProperty(value = "api路径")
    @Excel(name = "api路径")
    private String path;

    @ApiModelProperty(value = "版本号")
    @Excel(name = "版本号")
    private Integer version;

    @Excel(name = "数据源id")
    @ApiModelProperty(value = "数据源id")
    private String dataSourceId;

    @Excel(name = "sql语句")
    @ApiModelProperty(value = "sql语句")
    private String sqlJob;


    @ApiModelProperty(value = "sql类型:query,update,delete")
    @Excel(name = "sql类型")
    private String sqlType;


    @ApiModelProperty(value = "发布有效开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "发布有效开始时间" ,dateFormat = "yyyy-MM-dd")
    private Date pubEffectiveStartDate;


    @ApiModelProperty(value = "发布有效结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "发布有效结束时间" ,dateFormat = "yyyy-MM-dd")
    private Date pubEffectiveEndDate;
    /**
     * 分析形式(0:有效期;1:次数限制;)
     */
    @Excel(name = "分析形式")
    private String shareType;
    /**
     * 调用次数上限
     */
    @Excel(name = "调用次数上限")
    private Integer shareCountLimit;

    /**
     * 请求协议(http; https;)
     */
    @Excel(name = "请求协议")
    @ApiModelProperty(value = "请求协议")
    private String requestProtocol;

    /**
     * 请求方式(GET; POST; PUT; DELETE; HEAD; CONNECT; OPTIONS; TRACE; PATCH;)
     */
    @Excel(name = "请求方式")
    @ApiModelProperty(value = "请求方式")
    private String requestMethod;

    /**
     * 返回类型(JSON; CSV; TXT;)
     */
    @Excel(name = "返回类型")
    @ApiModelProperty(value = "返回类型")
    private String responseType;


    /**
     * 请求参数(json字符串)
     */
    @Excel(name = "请求参数")
    @ApiModelProperty(value = "请求参数")
    private String requestParam;

    /**
     * 返回参数(json字符串)
     */
    @Excel(name = "返回参数")
    @ApiModelProperty(value = "返回参数")
    private String responseParam;



}
