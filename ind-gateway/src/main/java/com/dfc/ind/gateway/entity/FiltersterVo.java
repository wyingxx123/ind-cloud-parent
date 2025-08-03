package com.dfc.ind.gateway.entity;


import com.dfc.ind.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "c_mov_role", description = "白名单表，角色权限表，角色权限标识表")
public class FiltersterVo {

    @ApiModelProperty(value = "主键")
    private String id;


    @ApiModelProperty(value = "查看表名-- 白名单-- 角色权限表-- 角色权限标识表")
    private String   pathtype;



    @ApiModelProperty(value = "服务路径")
    @Excel(name = "服务路径")
    private String movUrl;

    @ApiModelProperty(value = "角色Id")
    @Excel(name = "角色Id")
    private String roleId;

    @ApiModelProperty(value = "通道类型")
    @Excel(name = "通道类型")
    private String aisleType;

    @ApiModelProperty(value = "菜单名称")
    private String menuName;

    @ApiModelProperty(value = "删除标记")
    private String delFlg;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;


    @ApiModelProperty(value = "创建人")
    private String createBy;


    @ApiModelProperty(value = "修改时间")
    private Date updateTime;


    @ApiModelProperty(value = "修改人")
    private String updateBy;


    /**
     * 分页参数
     * */
    private   int pageNum;


    private  int pageSize;

}
