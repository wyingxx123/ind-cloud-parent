package com.dfc.ind.entity.dataapi;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dfc.ind.common.core.annotation.Excel;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 描述: 数据服务信息
 * </p>
 *
 * @author huff
 * @date 2024-03-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("data_api_info")
@ApiModel(value="DataApiInfoEntity对象", description="数据服务信息")
public class DataApiInfoEntity implements Serializable {

    private static final long serialVersionUID=1L;
  

    @ApiModelProperty(value = "服务识别码")
    @MppMultiId
    @Excel(name = "服务识别码")
    private String serviceId;
  

    @ApiModelProperty(value = "应用识别码")
    @Excel(name = "应用识别码")
    @MppMultiId
    private String appId;
  

    @ApiModelProperty(value = "API识别码")
    @Excel(name = "API识别码")
    private String apiId;
  

    @ApiModelProperty(value = "API版本号")
    @Excel(name = "API版本号")
    private String apiVersion;
  

    @ApiModelProperty(value = "外部访问路径")
    @Excel(name = "外部访问路径")
    private String apiPath;
  

    @ApiModelProperty(value = "微服务注册服务名")
    @Excel(name = "微服务注册服务名")
    private String apiName;
  

    @ApiModelProperty(value = "API调用类型")
    @Excel(name = "API调用类型")
    private String apiCallType;
  

    @ApiModelProperty(value = "API调用次数上限")
    @Excel(name = "API调用次数上限")
    private Integer apiCallCountLimit;
  

    @ApiModelProperty(value = "资源识别码")
    @Excel(name = "资源识别码")
    private String resourceId;
  

    @ApiModelProperty(value = "服务类型")
    @Excel(name = "服务类型")
    private String serviceType;
  

    @ApiModelProperty(value = "服务状态")
    @Excel(name = "服务状态")
    private String serviceStatus;
  

    @ApiModelProperty(value = "服务名称")
    @Excel(name = "服务名称")
    private String serviceName;
  

    @ApiModelProperty(value = "服务资源")
    @Excel(name = "服务资源")
    private String serviceResource;
  

    @ApiModelProperty(value = "服务参数")
    @Excel(name = "服务参数")
    private String serviceParament;
  

    @ApiModelProperty(value = "服务描述")
    @Excel(name = "服务描述")
    private String serviceDesc;

    @ApiModelProperty(value = "字段持久化标记")
    @Excel(name = "字段持久化标记 1已持久化")
    private String columnStatus;

    @ApiModelProperty(value = "服务备注")
    @Excel(name = "服务备注")
    private String serviceNote;
  

    @ApiModelProperty(value = "数据源识别码")
    @Excel(name = "数据源识别码")
    private String sourceId;
  

    @ApiModelProperty(value = "数据源名称")
    @Excel(name = "数据源名称")
    private String sourceName;
  

    @ApiModelProperty(value = "数据源适配器类型")
    @Excel(name = "数据源适配器类型")
    private String sourceAdapterType;
  

    @ApiModelProperty(value = "数据源参数加密密钥")
    @Excel(name = "数据源参数加密密钥")
    private String sourceSecretKey;
  

    @ApiModelProperty(value = "数据源参数")
    @Excel(name = "数据源参数")
    private String sourcePara;
  

    @ApiModelProperty(value = "注册时间")
    @Excel(name = "注册时间")
    private Date authRegTime;
  

    @ApiModelProperty(value = "授权开始日期")
    @Excel(name = "授权开始日期")
    private Date authStartDate;
  

    @ApiModelProperty(value = "授权修改时间")
    @Excel(name = "授权修改时间")
    private Date authUpdTime;
  

    @ApiModelProperty(value = "授权结束日期")
    @Excel(name = "授权结束日期")
    private Date authEndDate;
  

    @ApiModelProperty(value = "API描述")
    @Excel(name = "API描述")
    private String description;
  

    @ApiModelProperty(value = "API备注")
    @Excel(name = "API备注")
    private String note;
  

    @ApiModelProperty(value = "操作人")
    @Excel(name = "操作人")
    private String operator;
  

    @ApiModelProperty(value = "注册日期")
    @Excel(name = "注册日期")
    private Date regDate;
  

    @ApiModelProperty(value = "操作日期")
    @Excel(name = "操作日期")
    private Date opDate;
  

    @ApiModelProperty(value = "操作时间")
    @Excel(name = "操作时间")
    private Date opTime;
  

    @ApiModelProperty(value = "服务识别号")
    @Excel(name = "服务识别号")
    private String serverId;
  

    @ApiModelProperty(value = "数据来源代码")
    @Excel(name = "数据来源代码")
    private String dataSrcCd;
  

    @ApiModelProperty(value = "删除标志")
    @Excel(name = "删除标志")
    private String delFlg;


}
