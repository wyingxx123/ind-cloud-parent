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
 * 描述: 数据应用系统信息
 * </p>
 *
 * @author huff
 * @date 2024-03-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("data_api_app_info")
@ApiModel(value="DataApiAppInfoEntity对象", description="数据应用系统信息")
public class DataApiAppInfoEntity implements Serializable {

    private static final long serialVersionUID=1L;
  

    @ApiModelProperty(value = "应用识别码")
    @Excel(name = "应用识别码")
    private String appId;
  

    @ApiModelProperty(value = "应用名称")
    @Excel(name = "应用名称")
    private String appName;
  

    @ApiModelProperty(value = "应用外部访问根路径")
    @Excel(name = "应用外部访问根路径")
    private String appPath;
  

    @ApiModelProperty(value = "应用类型")
    @Excel(name = "应用类型")
    private String appType;
  

    @ApiModelProperty(value = "应用状态")
    @Excel(name = "应用状态")
    private String appStatus;
  

    @ApiModelProperty(value = "应用识别密钥")
    @Excel(name = "应用识别密钥")
    private String secretKey;
  

    @ApiModelProperty(value = "应用会话有效期-秒")
    @Excel(name = "应用会话有效期-秒")
    private Integer tokenExpiration;
  

    @ApiModelProperty(value = "应用是否开放IP白名单")
    @Excel(name = "应用是否开放IP白名单")
    private String openIpWhiteList;
  

    @ApiModelProperty(value = "应用IP白名单")
    @Excel(name = "应用IP白名单")
    private String ipWhiteList;
  

    @ApiModelProperty(value = "邮件地址")
    @Excel(name = "邮件地址")
    private String email;
  

    @ApiModelProperty(value = "注册时间")
    @Excel(name = "注册时间")
    private Date authRegTime;
  

    @ApiModelProperty(value = "授权开始日期")
    @Excel(name = "授权开始日期")
    private Date authStartDate;
  

    @ApiModelProperty(value = "授权修改时间")
    @Excel(name = "授权修改时间")
    private Date authUpdTime;
  

    @ApiModelProperty(value = "授权结束日期")
    @Excel(name = "授权结束日期")
    private Date authEndDate;
  

    @ApiModelProperty(value = "应用系统描述")
    @Excel(name = "应用系统描述")
    private String description;
  

    @ApiModelProperty(value = "应用系统备注")
    @Excel(name = "应用系统备注")
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
