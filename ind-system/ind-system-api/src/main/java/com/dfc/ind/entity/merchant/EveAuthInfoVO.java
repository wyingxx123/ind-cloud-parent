package com.dfc.ind.entity.merchant;

import com.dfc.ind.common.core.annotation.Excel;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <中台认证信息-前端用>
 *
 * @author ylyan
 * @return
 * @Date 2020/4/26 10:28
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "EveAuthInfoVO对象-前端用", description = "认证信息封装")
public class EveAuthInfoVO {

    @ApiModelProperty(value = "认证类型：1-实名认证，2-商户认证")
    private String authType;

    @ApiModelProperty(value = "申请实体类型：1-个人，2-企业")
    private String applyType;

    @ApiModelProperty(value = "商户名称（企业名称）")
    private String merchantName;

    @ApiModelProperty(value = "法人名称")
    private String legalName;

    @ApiModelProperty(value = "证件号码:身份证统一社会信用代码/注册号/组织机构代码")
    private String credentialsNo;

    @ApiModelProperty(value = "证件地址")
    private String credentialsAddress;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "电话")
    private String telephone;

    @ApiModelProperty(value = "地址")
    private String address;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "用户id", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "商户id")
    private String merchantId;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "认证id")
    private Long authId;

    @ApiModelProperty(value = "审批状态")
    private String approvalStatus;

    @ApiModelProperty(value = "所属机构名称编号")
    private String serialNumber;

    @ApiModelProperty(value = "所属机构名称")
    @Excel(name = "所属机构")
    private String mechanism;

    @ApiModelProperty(value = "法人身份证正面")
    private String idCard;

    @ApiModelProperty(value = "法人身份证反面")
    private String idCardtails;

    private String createBy;


    private Date createTime;

    private String updateBy;

    private Date updateTime;


}
