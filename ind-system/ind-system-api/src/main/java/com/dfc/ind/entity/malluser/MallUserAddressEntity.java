package com.dfc.ind.entity.malluser;


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
 * 描述: 用户地址表
 * </p>
 *
 * @author ylyan
 * @date 2020-04-23
 * @copyright 武汉数慧享智能科技有限公司
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("mall_user_address")
@ApiModel(value = "MallUserAddressEntity对象", description = "用户地址表")
public class MallUserAddressEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "地址id")
    @TableId(value = "adr_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    @Excel(name = "地址id")
    private Long adrId;


    @ApiModelProperty(value = "用户id")
    @JsonSerialize(using = ToStringSerializer.class)
    @Excel(name = "用户id")
    private Long userId;


    @ApiModelProperty(value = "真实姓名")
    @Excel(name = "真实姓名")
    private String realName;


    @ApiModelProperty(value = "手机号码")
    @Excel(name = "手机号码")
    private String phoneNumber;


    @ApiModelProperty(value = "收货人详细地址")
    @Excel(name = "收货人详细地址")
    private String detail;


    @ApiModelProperty(value = "邮编")
    @Excel(name = "邮编")
    private String postCode;


    @ApiModelProperty(value = "是否默认：1代表是，0代表否")
    @Excel(name = "是否默认：1代表是，0代表否")
    private String isDefault;
}
