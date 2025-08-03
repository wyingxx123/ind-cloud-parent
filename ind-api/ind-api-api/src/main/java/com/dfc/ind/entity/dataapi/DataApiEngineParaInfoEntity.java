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
 * 描述: API引擎参数信息
 * </p>
 *
 * @author huff
 * @date 2024-03-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("data_api_engine_para_info")
@ApiModel(value="DataApiEngineParaInfoEntity对象", description="API引擎参数信息")
public class DataApiEngineParaInfoEntity implements Serializable {

    private static final long serialVersionUID=1L;
  

    @ApiModelProperty(value = "引擎编号")
    @Excel(name = "引擎编号")
    private String engineNo;
  

    @ApiModelProperty(value = "引擎参数编码")
    @Excel(name = "引擎参数编码")
    private String engineParaNo;
  

    @ApiModelProperty(value = "引擎参数组编码")
    @Excel(name = "引擎参数组编码")
    private String engineParaGroupNo;
  

    @ApiModelProperty(value = "引擎参数类型")
    @Excel(name = "引擎参数类型")
    private String engineParaType;
  

    @ApiModelProperty(value = "引擎输入类型")
    @Excel(name = "引擎输入类型")
    private String engineParaInType;
  

    @ApiModelProperty(value = "引擎输出类型")
    @Excel(name = "引擎输出类型")
    private String engineParaOutType;
  

    @ApiModelProperty(value = "引擎参数名称")
    @Excel(name = "引擎参数名称")
    private String engineParaName;
  

    @ApiModelProperty(value = "引擎参数状态")
    @Excel(name = "引擎参数状态")
    private String engineParaStatus;
  

    @ApiModelProperty(value = "引擎参数输出信息")
    @Excel(name = "引擎参数输出信息")
    private String engineParaOut;
  

    @ApiModelProperty(value = "引擎参数输出资源")
    @Excel(name = "引擎参数输出资源")
    private String engineParaOutRe;
  

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
