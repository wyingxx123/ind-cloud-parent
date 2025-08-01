package com.dfc.ind.entity.dataapi;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.dfc.ind.common.core.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 描述: api字段配置表
 * </p>
 *
 * @author huff
 * @date 2024-04-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("data_api_column_info")
@ApiModel(value="DataApiColumnInfoEntity对象", description="api字段配置表")
public class DataApiColumnInfoEntity implements Serializable {

    private static final long serialVersionUID=1L;
  

    @ApiModelProperty(value = "字段名")
    @MppMultiId
    @Excel(name = "字段名")
    private String columnNo;

    @MppMultiId
    @ApiModelProperty(value = "应用编号")
    @Excel(name = "应用编号")
    private String appId;
  

    @ApiModelProperty(value = "字段描述")
    @Excel(name = "字段描述")
    private String columnDesc;
  

    @ApiModelProperty(value = "创建时间")
    @Excel(name = "创建时间")
    private Date createTime;
  

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    private String createBy;


    @ApiModelProperty(value = "更新时间")
    @Excel(name = "更新时间")
    private Date updateTime;
  

    @ApiModelProperty(value = "更新人")
    @Excel(name = "更新人")
    private String updateBy;


}
