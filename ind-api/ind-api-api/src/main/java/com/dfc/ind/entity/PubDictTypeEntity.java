package com.dfc.ind.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dfc.ind.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 描述: 商户参数类型信息
 * </p>
 *
 * @author huff
 * @date 2024-03-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("c_pub_dict_type")
@ApiModel(value="PubDictTypeEntity对象", description="商户参数类型信息")
public class PubDictTypeEntity implements Serializable {

    private static final long serialVersionUID=1L;
  

    @ApiModelProperty(value = "商户字典类型识别号")
      @TableId(value = "dict_id", type = IdType.AUTO)
    @Excel(name = "商户字典类型识别号")
    private String dictId;
  

    @ApiModelProperty(value = "商户识别号")
    @Excel(name = "商户识别号")
    private Long merchantId;
  

    @ApiModelProperty(value = "排序序号")
    @Excel(name = "排序序号")
    private Integer sortNo;
  

    @ApiModelProperty(value = "商户字典类型")
    @Excel(name = "商户字典类型")
    private String dictType;
  

    @ApiModelProperty(value = "主题层级")
    @Excel(name = "主题层级")
    private String subjectLevelCode;
  

    @ApiModelProperty(value = "主题")
    @Excel(name = "主题")
    private String subject;
  

    @ApiModelProperty(value = "上级主题")
    @Excel(name = "上级主题")
    private String subjectUp;
  

    @ApiModelProperty(value = "上级主题")
    @Excel(name = "上级主题")
    private String subjectUpList;
  

    @ApiModelProperty(value = "状态")
    @Excel(name = "状态")
    private String status;
  

    @ApiModelProperty(value = "商户字典名称")
    @Excel(name = "商户字典名称")
    private String dictName;
  

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
