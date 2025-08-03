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
 * 描述: 数据源参数信息
 * </p>
 *
 * @author huff
 * @date 2024-03-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("data_api_source_para_info")
@ApiModel(value="DataSourceParaInfoEntity对象", description="数据源参数信息")
public class DataSourceParaInfoEntity implements Serializable {

    private static final long serialVersionUID=1L;
  

    @ApiModelProperty(value = "数据源参数识别码")
    @Excel(name = "数据源参数识别码")
    private String paraId;
  

    @ApiModelProperty(value = "应用识别码")
    @Excel(name = "应用识别码")
    private String appId;
  

    @ApiModelProperty(value = "环境类型")
    @Excel(name = "环境类型")
    private String envType;
  

    @ApiModelProperty(value = "参数组编码")
    @Excel(name = "参数组编码")
    private String paraGroupNo;
  

    @ApiModelProperty(value = "数据源识别码")
    @Excel(name = "数据源识别码")
    private String sourceId;
  

    @ApiModelProperty(value = "数据源参数名称")
    @Excel(name = "数据源参数名称")
    private String paraName;
  

    @ApiModelProperty(value = "参数序号")
    @Excel(name = "参数序号")
    private Integer seq;
  

    @ApiModelProperty(value = "数据源参数状态")
    @Excel(name = "数据源参数状态")
    private String paraStatus;
  

    @ApiModelProperty(value = "数据源参数值")
    @Excel(name = "数据源参数值")
    private String paraValue;
  

    @ApiModelProperty(value = "是否引用其他参数")
    @Excel(name = "是否引用其他参数")
    private String isRefer;
  

    @ApiModelProperty(value = "是否加密参数")
    @Excel(name = "是否加密参数")
    private String isSecret;
  

    @ApiModelProperty(value = "引用其他参数组编码")
    @Excel(name = "引用其他参数组编码")
    private String referGroupNo;
  

    @ApiModelProperty(value = "数据源参数描述")
    @Excel(name = "数据源参数描述")
    private String description;
  

    @ApiModelProperty(value = "数据源参数备注")
    @Excel(name = "数据源参数备注")
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
