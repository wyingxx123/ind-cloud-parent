package com.dfc.ind.entity.sys;

import com.dfc.ind.common.core.annotation.Excel;
import com.dfc.ind.common.core.web.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 描述: app访问记录
 * </p>
 *
 * @author dingw
 * @date 2020-09-28
 * @copyright 武汉数慧享智能科技有限公司
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_app_record")
@ApiModel(value = "SysAppRecordEntity对象", description = "app访问记录")
public class SysAppRecordEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "记录id")
    @TableId(value = "record_id", type = IdType.AUTO)
    @Excel(name = "记录id")
    private Integer recordId;


    @ApiModelProperty(value = "用户id")
    @Excel(name = "用户id")
    private Long userId;


    @ApiModelProperty(value = "菜单权限标识")
    @Excel(name = "菜单权限标识")
    private String menuPerms;


    @ApiModelProperty(value = "是否收藏(00=收藏,01=未收藏)")
    @Excel(name = "是否收藏(00=收藏,01=未收藏)")
    private String isCollection;


    @ApiModelProperty(value = "商户id")
    @Excel(name = "商户id")
    private String merchantId;

    @ApiModelProperty(value = "是否删除：0-代表存在，1-代表删除")
    @Excel(name = "是否删除：0-代表存在，1-代表删除")
    private String delFlg;

    @ApiModelProperty(value = "路由地址")
    @TableField(exist = false)
    private String path;

    @ApiModelProperty(value = "菜单名称")
    @TableField(exist = false)
    private String menuName;

    @ApiModelProperty(value = "菜单图标")
    @TableField(exist = false)
    private String icon;
}
