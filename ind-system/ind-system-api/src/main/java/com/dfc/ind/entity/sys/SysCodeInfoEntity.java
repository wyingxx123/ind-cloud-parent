package com.dfc.ind.entity.sys;

import com.dfc.ind.common.core.annotation.Excel;
import com.dfc.ind.common.core.web.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 描述: 二维码配置信息
 * </p>
 *
 * @author dingw
 * @date 2020-08-25
 * @copyright 武汉数慧享智能科技有限公司
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_code_info")
@ApiModel(value = "SysCodeInfoEntity对象", description = "二维码配置信息")
public class SysCodeInfoEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "二维码编号")
    @TableId(value = "code_no")
    @Excel(name = "二维码编号")
    private String codeNo;


    @ApiModelProperty(value = "二维码类型")
    @Excel(name = "二维码类型")
    private String codeType;


    @ApiModelProperty(value = "二维码状态(00使用 01停止)")
    @Excel(name = "二维码状态(00使用 01停止)")
    private String codeStatus;


    @ApiModelProperty(value = "使用角色范围")
    @Excel(name = "使用角色范围")
    private String codeRole;


    @ApiModelProperty(value = "使用人范围")
    @Excel(name = "使用人范围")
    private String codeUser;


    @ApiModelProperty(value = "菜单列表")
    @Excel(name = "菜单列表")
    private String codeMenu;


    @ApiModelProperty(value = "二维码url")
    @Excel(name = "二维码url")
    private String codeUrl;


    @ApiModelProperty(value = "二维码有效期")
    @Excel(name = "二维码有效期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date effectiveTime;


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
