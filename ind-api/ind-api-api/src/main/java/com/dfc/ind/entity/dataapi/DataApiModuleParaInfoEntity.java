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
 * 描述: 模块参数信息
 * </p>
 *
 * @author huff
 * @date 2024-03-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("data_api_module_para_info")
@ApiModel(value="DataApiModuleParaInfoEntity对象", description="模块参数信息")
public class DataApiModuleParaInfoEntity implements Serializable {

    private static final long serialVersionUID=1L;
  

    @ApiModelProperty(value = "参数编码")
    @Excel(name = "参数编码")
    private String paraNo;
  

    @ApiModelProperty(value = "参数组编码")
    @Excel(name = "参数组编码")
    private String paraGroupNo;
  

    @ApiModelProperty(value = "参数名称")
    @Excel(name = "参数名称")
    private String paraName;
  

    @ApiModelProperty(value = "参数状态")
    @Excel(name = "参数状态")
    private String paraStatus;
  

    @ApiModelProperty(value = "参数类型")
    @Excel(name = "参数类型")
    private String paraType;
  

    @ApiModelProperty(value = "参数参考值")
    @Excel(name = "参数参考值")
    private String paraReferValue;
  

    @ApiModelProperty(value = "参数值")
    @Excel(name = "参数值")
    private String paraValue;
  

    @ApiModelProperty(value = "参数组名称")
    @Excel(name = "参数组名称")
    private String paraGroupName;
  

    @ApiModelProperty(value = "上级参数编码")
    @Excel(name = "上级参数编码")
    private String parentParaNo;
  

    @ApiModelProperty(value = "参数层级")
    @Excel(name = "参数层级")
    private Integer paraLevel;
  

    @ApiModelProperty(value = "参数描述")
    @Excel(name = "参数描述")
    private String description;
  

    @ApiModelProperty(value = "参数备注")
    @Excel(name = "参数备注")
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
  

    @ApiModelProperty(value = "到期时间")
    @Excel(name = "到期时间")
    private Date expireTime;
  

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
