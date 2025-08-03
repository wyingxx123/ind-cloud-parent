package com.dfc.ind.entity.code;


import com.dfc.ind.common.core.annotation.Excel;
import com.dfc.ind.common.core.web.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 描述: 二维码信息
 * </p>
 *
 * @author dingw
 * @date 2020-09-16
 * @copyright 武汉数慧享智能科技有限公司
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("two_dimensional_code")
@ApiModel(value="TwoDimensionalCodeEntity对象", description="二维码信息")
public class TwoDimensionalCodeEntity extends BaseEntity {

    private static final long serialVersionUID=1L;


    @ApiModelProperty(value = "二维码编号")
    @TableId(value = "code_no")
    @Excel(name = "二维码编号")
    private String codeNo;


    @ApiModelProperty(value = "二维码关联明细")
    @Excel(name = "二维码关联明细")
    private String codeIds;


    @ApiModelProperty(value = "二维码类型")
    @Excel(name = "二维码类型")
    private String codeType;


    @ApiModelProperty(value = "二维码状态(00使用 01停止)")
    @Excel(name = "二维码状态(00使用 01停止)")
    private String codeStatus;


    @ApiModelProperty(value = "有效期")
    @Excel(name = "有效期")
    private Date effectiveTime;


    @ApiModelProperty(value = "是否启用有效期(00使用,01不使用)")
    @Excel(name = "是否启用有效期(00使用,01不使用)")
    private String isTime;


    @ApiModelProperty(value = "地址")
    @Excel(name = "地址")
    private String address;


    @ApiModelProperty(value = "经度")
    @Excel(name = "经度")
    private BigDecimal longitude;


    @ApiModelProperty(value = "纬度")
    @Excel(name = "纬度")
    private BigDecimal latitude;


    @ApiModelProperty(value = "有效范围距离")
    @Excel(name = "有效范围距离")
    private BigDecimal effectiveRange;


    @ApiModelProperty(value = "是否启动位置限制(00使用,01不使用)")
    @Excel(name = "是否启动位置限制(00使用,01不使用)")
    private String isLocation;


    @ApiModelProperty(value = "安全码")
    @Excel(name = "安全码")
    private String safetyCode;


    @ApiModelProperty(value = "二维码地址")
    @Excel(name = "二维码地址")
    private String codeUrl;


    @ApiModelProperty(value = "商户id")
    @Excel(name = "商户id")
    private String merchantId;


    @ApiModelProperty(value = "商户名称")
    @Excel(name = "商户名称")
    private String merchantName;


    @ApiModelProperty(value = "是否删除：0-代表存在，1-代表删除")
    @Excel(name = "是否删除：0-代表存在，1-代表删除")
    private String delFlg;


}
