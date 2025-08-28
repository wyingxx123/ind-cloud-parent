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
 * 描述: API引擎信息
 * </p>
 *
 * @author huff
 * @date 2024-03-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("data_api_engine_info")
@ApiModel(value="DataApiEngineInfoEntity对象", description="API引擎信息")
public class DataApiEngineInfoEntity implements Serializable {

    private static final long serialVersionUID=1L;
  

    @ApiModelProperty(value = "引擎编号")
    @Excel(name = "引擎编号")
    private String engineNo;


    @ApiModelProperty(value = "引擎编号")
    @Excel(name = "引擎编号")
    private String appId;

    @ApiModelProperty(value = "引擎类型")
    @Excel(name = "引擎类型")
    private String engineType;
  

    @ApiModelProperty(value = "引擎状态")
    @Excel(name = "引擎状态")
    private String engineStatus;
  

    @ApiModelProperty(value = "引擎名称")
    @Excel(name = "引擎名称")
    private String engineName;
  

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
  

    @ApiModelProperty(value = "数据库编号")
    @Excel(name = "数据库编号")
    private String databaseNo;
  

    @ApiModelProperty(value = "引擎参数组来源")
    @Excel(name = "引擎参数组来源")
    private String engineSource;
  

    @ApiModelProperty(value = "引擎资源")
    @Excel(name = "引擎资源")
    private String engineResource;
  

    @ApiModelProperty(value = "关联引擎数")
    @Excel(name = "关联引擎数")
    private Integer engineResourceNum;
  

    @ApiModelProperty(value = "上游引擎编号")
    @Excel(name = "上游引擎编号")
    private String preEngineNo;
  

    @ApiModelProperty(value = "下游引擎编号")
    @Excel(name = "下游引擎编号")
    private String nextEngineNo;
  

    @ApiModelProperty(value = "引擎流编号")
    @Excel(name = "引擎流编号")
    private String engineFlowNo;
  

    @ApiModelProperty(value = "引擎备注")
    @Excel(name = "引擎备注")
    private String engineDesc;
  

    @ApiModelProperty(value = "引擎注册时间")
    @Excel(name = "引擎注册时间")
    private String engineNote;
  

    @ApiModelProperty(value = "引擎开始日期")
    @Excel(name = "引擎开始日期")
    private Date authRegTime;
  

    @ApiModelProperty(value = "引擎修改时间")
    @Excel(name = "引擎修改时间")
    private Date authStartDate;
  

    @ApiModelProperty(value = "引擎结束日期")
    @Excel(name = "引擎结束日期")
    private Date authUpdTime;
  

    @ApiModelProperty(value = "劳动强度系数")
    @Excel(name = "劳动强度系数")
    private Date authEndDate;
  

    @ApiModelProperty(value = "定额说明")
    @Excel(name = "定额说明")
    private String description;
  

    @ApiModelProperty(value = "备注")
    @Excel(name = "备注")
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
