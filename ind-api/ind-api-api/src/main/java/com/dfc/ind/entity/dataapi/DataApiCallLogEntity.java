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
 * 描述: 数据服务调用日志
 * </p>
 *
 * @author huff
 * @date 2024-03-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("data_api_call_log")
@ApiModel(value="DataApiCallLogEntity对象", description="数据服务调用日志")
public class DataApiCallLogEntity implements Serializable {

    private static final long serialVersionUID=1L;
  

    @ApiModelProperty(value = "日志识别码")
    @Excel(name = "日志识别码")
    private String logId;
  

    @ApiModelProperty(value = "应用识别码")
    @Excel(name = "应用识别码")
    private String appId;
  

    @ApiModelProperty(value = "数据服务识别码")
    @Excel(name = "数据服务识别码")
    private String serverId;
  

    @ApiModelProperty(value = "数据会话服务识别码")
    @Excel(name = "数据会话服务识别码")
    private String sessionServerId;
  

    @ApiModelProperty(value = "调用次数")
    @Excel(name = "调用次数")
    private Integer callCount;
  

    @ApiModelProperty(value = "调用模式")
    @Excel(name = "调用模式")
    private String callType;
  

    @ApiModelProperty(value = "调用结果状态")
    @Excel(name = "调用结果状态")
    private String resultStatus;
  

    @ApiModelProperty(value = "调用时间")
    @Excel(name = "调用时间")
    private Long spendTime;
  

    @ApiModelProperty(value = "调用开始时间")
    @Excel(name = "调用开始时间")
    private Date callStartTime;
  

    @ApiModelProperty(value = "调用结束时间")
    @Excel(name = "调用结束时间")
    private Date callEndTime;
  

    @ApiModelProperty(value = "调用返回信息")
    @Excel(name = "调用返回信息")
    private String returnMsg;
  

    @ApiModelProperty(value = "主机地址")
    @Excel(name = "主机地址")
    private String requestHost;
  

    @ApiModelProperty(value = "请求资源地址")
    @Excel(name = "请求资源地址")
    private String requestUrl;
  

    @ApiModelProperty(value = "请求资源参数")
    @Excel(name = "请求资源参数")
    private String requestPara;
  

    @ApiModelProperty(value = "日志描述")
    @Excel(name = "日志描述")
    private String description;
  

    @ApiModelProperty(value = "日志备注")
    @Excel(name = "日志备注")
    private String note;
  

    @ApiModelProperty(value = "操作人")
    @Excel(name = "操作人")
    private String operator;
  

    @ApiModelProperty(value = "操作日期")
    @Excel(name = "操作日期")
    private Date opDate;
  

    @ApiModelProperty(value = "操作时间")
    @Excel(name = "操作时间")
    private Date opTime;


}
