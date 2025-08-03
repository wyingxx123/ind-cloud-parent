package com.dfc.ind.entity.sys;

import com.dfc.ind.common.core.web.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 描述: app端菜单权限表
 * </p>
 *
 * @author zhaoyq 赵亚强
 * @date 2020/8/25
 * @copyright 武汉数慧享智能科技有限公司
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_app_menu")
@ApiModel(value = "SysAppMenuEntity对象", description = "app端菜单权限表")
public class SysAppMenuEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "菜单ID")
    @TableId(value = "menu_id", type = IdType.AUTO)
    private Long menuId;


    @ApiModelProperty(value = "菜单名称")
    private String menuName;


    @ApiModelProperty(value = "显示顺序")
    private Integer orderNum;


    @ApiModelProperty(value = "路由地址")
    private String path;


    @ApiModelProperty(value = "组件路径")
    private String component;

    @ApiModelProperty(value = "商户ID")
    private Long merchantId;

    @ApiModelProperty(value = "是否为外链（0是 1否）")
    private String isFrame;


    @ApiModelProperty(value = "菜单类型（M目录 C菜单 F按钮）")
    private String menuType;


    @ApiModelProperty(value = "菜单状态（0显示 1隐藏）")
    private String visible;


    @ApiModelProperty(value = "权限标识")
    private String perms;


    @ApiModelProperty(value = "菜单图标")
    private String icon;


}
