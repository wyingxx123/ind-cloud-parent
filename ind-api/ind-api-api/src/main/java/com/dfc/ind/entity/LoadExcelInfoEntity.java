package com.dfc.ind.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dfc.ind.common.core.annotation.Excel;

import com.dfc.ind.vo.PubDictDataVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 描述: 导入模板信息
 * </p>
 *
 * @author huff
 * @date 2024-07-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("c_load_excel_info")
@ApiModel(value="LoadExcelInfoEntity对象", description="导入模板信息")
public class LoadExcelInfoEntity implements Serializable {

    private static final long serialVersionUID=1L;
  

    @ApiModelProperty(value = "模板编号")
    @Excel(name = "模板编号")
    @NotNull(message ="模板编号不能为空" )
    @MppMultiId
    private String templateNo;
  

    @ApiModelProperty(value = "商户识别号")
    @Excel(name = "商户识别号")
    @MppMultiId
    private Long merchantId;
  

    @ApiModelProperty(value = "模板状态")
    @Excel(name = "模板状态")
    private String status;
  

    @ApiModelProperty(value = "模板名称")
    @Excel(name = "模板名称")
    private String name;
  

    @ApiModelProperty(value = "模板格式字典类型")
    @Excel(name = "模板格式字典类型")
    private String formatDictType;
  

    @ApiModelProperty(value = "模板数据格式字典类型")
    @Excel(name = "模板数据格式字典类型")
    private String dataDictType;
  

    @ApiModelProperty(value = "模板参数字典类型")
    @Excel(name = "模板参数字典类型")
    private String paraDictType;
  

    @ApiModelProperty(value = "模板资源")
    @Excel(name = "模板资源")
    private String resource;
  

    @ApiModelProperty(value = "模板参数")
    @Excel(name = "模板参数")
    private String parament;
  

    @ApiModelProperty(value = "所属主管")
    @Excel(name = "所属主管")
    private String owner;
  

    @ApiModelProperty(value = "所属车间编号")
    @Excel(name = "所属车间编号")
    private String factoryNo;
  

    @ApiModelProperty(value = "所属生产线编号")
    @Excel(name = "所属生产线编号")
    private String lineNo;
  

    @ApiModelProperty(value = "所属工位编号")
    @Excel(name = "所属工位编号")
    private String stastionNo;
  

    @ApiModelProperty(value = "所属班组编号")
    @Excel(name = "所属班组编号")
    private String teamNo;
  

    @ApiModelProperty(value = "所属岗位编号")
    @Excel(name = "所属岗位编号")
    private String postNo;
  

    @ApiModelProperty(value = "所属用户名称")
    @Excel(name = "所属用户名称")
    private String userName;
  

    @ApiModelProperty(value = "模板说明")
    @Excel(name = "模板说明")
    private String description;
  

    @ApiModelProperty(value = "备注")
    @Excel(name = "备注")
    private String note;

    @ApiModelProperty(value = "excel路径")
    @Excel(name = "excel路径")
    private String excelUrl;

    @ApiModelProperty(value = "logo路径")
    @Excel(name = "logo路径")
    private String logoUrl;
  

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

    @TableField(exist = false)
    private List<PubDictDataVo>  formatDictTypeList;

    @TableField(exist = false)
    private List<PubDictDataVo>  dataDictTypeList;

    @TableField(exist = false)

    private List<PubDictDataVo>  paraDictTypeList;

    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @ApiModelProperty(value = "查询日期")
    private String clearStartDate;

    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @ApiModelProperty(value = "查询结束日期")
    private String clearEndDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "应用id")
    @NotNull(message ="应用id不能为空" )
    private String appId;

    @TableField(exist = false)
    @ApiModelProperty(value = "指定sheet页导入")
    private Integer sheetAt;


}
