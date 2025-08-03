package com.dfc.ind.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dfc.ind.common.core.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
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
 * 描述: 商户参数值信息
 * </p>
 *
 * @author huff
 * @date 2023-05-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("c_pub_dict_data")
@ApiModel(value="PubDictDataEntity对象", description="商户参数值信息")
public class PubDictDataEntity implements Serializable {

    private static final long serialVersionUID=1L;
  

    @ApiModelProperty(value = "商户字典类型")
    @MppMultiId
    @Excel(name = "商户字典类型")
    private String dictType;

    @MppMultiId
    @ApiModelProperty(value = "商户识别号")
    @Excel(name = "商户识别号")
    private Long merchantId;

    @MppMultiId
    @ApiModelProperty(value = "商户字典代码")
    @Excel(name = "商户字典代码")
    private String dictCode;
  

    @ApiModelProperty(value = "排序序号")
    @Excel(name = "排序序号")
    private Integer sortNo;
  

    @ApiModelProperty(value = "状态 00-正常 01-未启用 02-关闭")
    @Excel(name = "状态 00-正常 01-未启用 02-关闭")
    private String status;
  

    @ApiModelProperty(value = "代码名称")
    @Excel(name = "代码名称")
    private String codeName;
  

    @ApiModelProperty(value = "显示标签")
    @Excel(name = "显示标签")
    private String codeLabel;
  

    @ApiModelProperty(value = "代码值")
    @Excel(name = "代码值")
    private String codeValue;
  

    @ApiModelProperty(value = "图标类")
    @Excel(name = "图标类")
    private String iconClass;
  

    @ApiModelProperty(value = "CSS类")
    @Excel(name = "CSS类")
    private String cssClass;
  

    @ApiModelProperty(value = "提示类")
    @Excel(name = "提示类")
    private String listClass;
  

    @ApiModelProperty(value = "是否缺省值")
    @Excel(name = "是否缺省值")
    private String isDefault;

    @ApiModelProperty(value = "是否缺省值")
    @Excel(name = "是否缺省值")
    private String isDisplay;


    @ApiModelProperty(value = "参数资源")
    @Excel(name = "参数资源")
    private String resource;

    @ApiModelProperty(value = "说明")
    @Excel(name = "说明")
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date opTime;
  

    @ApiModelProperty(value = "服务识别号")
    @Excel(name = "服务识别号")
    private String serverId;
  

    @ApiModelProperty(value = "数据来源代码")
    @Excel(name = "数据来源代码")
    private String dataSrcCd;
  

    @ApiModelProperty(value = "删除标志 0-未删除 1-已删除")
    @Excel(name = "删除标志 0-未删除 1-已删除")
    private String delFlg;
    @ApiModelProperty(value = "上级主题")

    @TableField(exist = false)
    private String subjectUp;


}
