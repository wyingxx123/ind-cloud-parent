package com.dfc.ind.entity.dataapi;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.dfc.ind.common.core.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 描述: 数据源注册信息
 * </p>
 *
 * @author huff
 * @date 2024-03-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("data_api_source_info")
@ApiModel(value="DataSourceInfoEntity对象", description="数据源注册信息")
public class DataSourceInfoEntity implements Serializable {

    private static final long serialVersionUID=1L;
  

    @ApiModelProperty(value = "数据源识别码")
    @Excel(name = "数据源识别码")
    private String sourceId;
  

    @ApiModelProperty(value = "应用识别码")
    @Excel(name = "应用识别码")
    private String appId;
  

    @ApiModelProperty(value = "数据源名称")
    @Excel(name = "数据源名称")
    private String sourceName;
  

    @ApiModelProperty(value = "数据源挂载路径")
    @Excel(name = "数据源挂载路径")
    private String sourceMountPath;
  

    @ApiModelProperty(value = "数据源适配器类型")
    @Excel(name = "数据源适配器类型")
    private String sourceAdapterType;
  

    @ApiModelProperty(value = "数据源状态")
    @Excel(name = "数据源状态")
    private String sourceStatus;
  

    @ApiModelProperty(value = "数据源参数加密密钥")
    @Excel(name = "数据源参数加密密钥")
    private String sourceSecretKey;
  

    @ApiModelProperty(value = "是否自动发布")
    @Excel(name = "是否自动发布")
    private String isAutoPublish;
  

    @ApiModelProperty(value = "数据源描述")
    @Excel(name = "数据源描述")
    private String description;
  

    @ApiModelProperty(value = "数据源备注")
    @Excel(name = "数据源备注")
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
