package com.dfc.ind.entity.dataapi;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.dfc.ind.common.core.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 描述: 资源目录信息
 * </p>
 *
 * @author huff
 * @date 2024-03-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("resource_dir_info")
@ApiModel(value="ResourceDirInfoEntity对象", description="资源目录信息")
public class ResourceDirInfoEntity implements Serializable {

    private static final long serialVersionUID=1L;
  

    @ApiModelProperty(value = "资源识别码")
    @Excel(name = "资源识别码")
    private String resourceId;
  

    @ApiModelProperty(value = "应用识别码")
    @Excel(name = "应用识别码")
    private String appId;
  

    @ApiModelProperty(value = "上级资源识别码")
    @Excel(name = "上级资源识别码")
    private String upResourceId;
  

    @ApiModelProperty(value = "工作空间")
    @Excel(name = "工作空间")
    private String workspace;
  

    @ApiModelProperty(value = "资源序号")
    @Excel(name = "资源序号")
    private Integer seq;
  

    @ApiModelProperty(value = "资源名称")
    @Excel(name = "资源名称")
    private String resourceName;
  

    @ApiModelProperty(value = "挂载路径")
    @Excel(name = "挂载路径")
    private String mountPath;
  

    @ApiModelProperty(value = "资源类型")
    @Excel(name = "资源类型")
    private String resourceType;
  

    @ApiModelProperty(value = "资源状态")
    @Excel(name = "资源状态")
    private String resourceStatus;
  

    @ApiModelProperty(value = "资源参数")
    @Excel(name = "资源参数")
    private String resourcePara;
  

    @ApiModelProperty(value = "资源图标")
    @Excel(name = "资源图标")
    private String resourceIcon;
  

    @ApiModelProperty(value = "是否自动展开")
    @Excel(name = "是否自动展开")
    private String isUnfold;
  

    @ApiModelProperty(value = "是否根资源")
    @Excel(name = "是否根资源")
    private String isRoot;
  

    @ApiModelProperty(value = "资源层级")
    @Excel(name = "资源层级")
    private Integer level;
  

    @ApiModelProperty(value = "资源描述")
    @Excel(name = "资源描述")
    private String description;
  

    @ApiModelProperty(value = "资源备注")
    @Excel(name = "资源备注")
    private String note;
  

    @ApiModelProperty(value = "操作人")
    @Excel(name = "操作人")
    private String operator;
  

    @ApiModelProperty(value = "注册日期")
    @Excel(name = "注册日期")
    private Date regDate;
  

    @ApiModelProperty(value = "操作日期")
    @Excel(name = "操作日期")
    private Date opDate;
  

    @ApiModelProperty(value = "操作时间")
    @Excel(name = "操作时间")
    private Date opTime;
  

    @ApiModelProperty(value = "服务识别号")
    @Excel(name = "服务识别号")
    private String serverId;
  

    @ApiModelProperty(value = "数据来源代码")
    @Excel(name = "数据来源代码")
    private String dataSrcCd;
  

    @ApiModelProperty(value = "删除标志")
    @Excel(name = "删除标志")
    private String delFlg;


}
