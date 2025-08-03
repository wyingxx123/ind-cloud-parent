package com.dfc.ind.entity.merchant;

import com.dfc.ind.common.core.annotation.Excel;
import com.dfc.ind.common.core.web.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 中台认证信息表
 * </p>
 *
 * @author ylyan
 * @copyright 武汉数慧享智能科技有限公司
 * @since 2020-04-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("c_eve_auth_info")
@ApiModel(value = "EveAuthInfoEntity对象", description = "中台认证信息表")
public class EveAuthInfoEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "认证id")
    @TableId(value = "auth_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    @Excel(name = "认证id")
    private Long authId;

    /*@ApiModelProperty(value = "商户id")
    @Excel(name = "商户id")
    private String merchanId;*/


    @ApiModelProperty(value = "用户id")
    @JsonSerialize(using = ToStringSerializer.class)
    @Excel(name = "用户id")
    private Long userId;

    @ApiModelProperty(value = "认证类型：1-实名认证，2-商户认证")
    @Excel(name = "认证类型：1-实名认证，2-商户认证")
    private String authType;

    @ApiModelProperty(value = "申请实体类型：1-个人，2-企业")
    @Excel(name = "申请类型：1-个人，2-企业")
    private String applyType;

    @ApiModelProperty(value = "商户名称（企业名称）")
    @Excel(name = "商户名称（企业名称）")
    private String merchantName;

    @ApiModelProperty(value = "法人名称")
    @Excel(name = "法人名称")
    private String legalName;

    @ApiModelProperty(value = "身份证统一社会信用代码/注册号/组织机构代码")
    @Excel(name = "身份证统一社会信用代码/注册号/组织机构代码")
    private String credentialsNo;

    @ApiModelProperty(value = "证件地址")
    @Excel(name = "证件地址")
    private String credentialsAddress;

    @ApiModelProperty(value = "审批状态：0-未审批，1-审批通过，2-不通过")
    @Excel(name = "审批状态：0-未审批，1-审批通过，2-不通过")
    private String approvalStatus;

    @ApiModelProperty(value = "邮箱")
    @Excel(name = "邮箱")
    private String email;

    @ApiModelProperty(value = "电话")
    @Excel(name = "电话")
    private String telephone;

    @ApiModelProperty(value = "地址")
    @Excel(name = "地址")
    private String address;

    @ApiModelProperty(value = "删除标志（0代表存在 2代表删除）")
    @Excel(name = "删除标志")
    private String delFlg;

    @ApiModelProperty(value = "所属机构名称")
    private String mechanism;

    @ApiModelProperty(value = "所属机构名称编号")
    private String serialNumber;

    @ApiModelProperty(value = "法人身份证正面")
    private String idCard;

    @ApiModelProperty(value = "法人身份证反面")
    private String idCardtails;
}
