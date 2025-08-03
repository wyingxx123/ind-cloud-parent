package com.dfc.ind.entity.station;

import com.dfc.ind.common.core.annotation.Excel;
import com.dfc.ind.common.core.web.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * <p>
 * 描述: 工位用户关联表
 * </p>
 *
 * @author zhaoyq 赵亚强
 * @date 2020/9/2
 * @copyright 武汉数慧享智能科技有限公司
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("c_station_related_user")
@ApiModel(value = "StationRelatedUserEntity对象", description = "工位用户关联表")
public class StationRelatedUserEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "工位号")
    @Excel(name = "工位号")
    @TableId(value = "station_no")
    private String stationNo;

    @ApiModelProperty(value = "工位名称")
    @Excel(name = "工位名称")
    private String stationName;

    @ApiModelProperty(value = "工位类型")
    @Excel(name = "工位类型")
    private String stationType;

    @ApiModelProperty(value = "用户id")
    @Excel(name = "用户id")
    private String userId;

    @ApiModelProperty(value = "角色id")
    @Excel(name = "角色id")
    private String roleId;

    @ApiModelProperty(value = "状态")
    @Excel(name = "状态")
    private String states;

    @ApiModelProperty(value = "二维码编号")
    @Excel(name = "二维码编号")
    private String codeNo;

    @ApiModelProperty(value = "二维码地址")
    @Excel(name = "二维码地址")
    private String codeUrl;

    @ApiModelProperty(value = "商户id")
    @Excel(name = "商户id")
    private String merchantId;

    @ApiModelProperty(value = "商户名")
    @Excel(name = "商户名")
    private String merchantName;

    @ApiModelProperty(value = "数据来源")
    @Excel(name = "数据来源")
    private String dataSrc;

    @ApiModelProperty(value = "删除标识")
    @Excel(name = "删除标识")
    private String delFlg;

    @ApiModelProperty("描述")
    @TableField(exist = false)
    private String desc;

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
    
    @ApiModelProperty("二维码类型")
    @TableField(exist = false)
    private String codeType;

    @ApiModelProperty("显示类型 00默认工单显示 01批次显示")
    private String displayType;
}
