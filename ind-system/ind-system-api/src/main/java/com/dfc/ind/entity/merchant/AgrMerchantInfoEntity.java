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
 * 商户实体表
 * </p>
 *
 * @author ylyan
 * @copyright 武汉数慧享智能科技有限公司
 * @since 2020-04-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("c_agr_merchant_info")
@ApiModel(value = "AgrMerchantInfoEntity对象", description = "商户实体表")
public class AgrMerchantInfoEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商户id")
    @TableId(value = "merchant_id", type = IdType.INPUT)
    @JsonSerialize(using = ToStringSerializer.class)
    @Excel(name = "商户id")
    private Long merchantId;

    @ApiModelProperty(value = "认证id")
    @JsonSerialize(using = ToStringSerializer.class)
    @Excel(name = "认证id")
    private Long authId;

    @ApiModelProperty(value = "商户名称")
    @Excel(name = "商户名称")
    private String merchantName;

    @ApiModelProperty(value = "身份证统一社会信用代码/注册号/组织机构代码")
    @Excel(name = "身份证统一社会信用代码/注册号/组织机构代码")
    private String credentialsNo;

    @ApiModelProperty(value = "证件地址")
    @Excel(name = "证件地址")
    private String credentialsAddress;

    @ApiModelProperty(value = "法人名称")
    @Excel(name = "法人名称")
    private String legalName;

    @ApiModelProperty(value = "邮箱")
    @Excel(name = "邮箱")
    private String email;

    @ApiModelProperty(value = "电话")
    @Excel(name = "电话")
    private String telephone;

    @ApiModelProperty(value = "用户详细地址")
    @Excel(name = "用户详细地址")
    private String address;

    @ApiModelProperty(value = "账号状态：0正常 ，1停用")
    @Excel(name = "商户状态：0正常 ，1停用")
    private String status;

    @ApiModelProperty(value = "审批状态：0-未审批，1-审批通过，2-不通过")
    @Excel(name = "审批状态：0-未审批，1-审批通过，2-不通过")
    private String approvalStatus;

    @ApiModelProperty(value = "删除标志（0代表存在 2代表删除）")
    @Excel(name = "删除标志")
    private String delFlg;

    @ApiModelProperty(value = "商户排名")
    private Integer isRanking;

    @ApiModelProperty(value = "服务id")
    private Long serverId;

    @ApiModelProperty(value = "授权事项")
    private String empowerJson;


    @ApiModelProperty(value = "所属机构名称")
    private String mechanism;

    @ApiModelProperty(value = "所属机构名称编号")
    private String serialNumber;

    @ApiModelProperty(value = "法人身份证正面")
    private String idCard;

    @ApiModelProperty(value = "法人身份证反面")
    private String idCardtails;

    @ApiModelProperty(value = "法人身份证反面")
    private Long deptId;
}
