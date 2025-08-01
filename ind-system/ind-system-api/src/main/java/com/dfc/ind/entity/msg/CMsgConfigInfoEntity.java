package com.dfc.ind.entity.msg;

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
 * 描述: 消息配置表
 * </p>
 *
 * @author nwy
 * @date 2020-04-27
 * @copyright 武汉数慧享智能科技有限公司
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("c_msg_config_info")
@ApiModel(value="CMsgConfigInfoEntity对象", description="消息配置表")
public class CMsgConfigInfoEntity extends BaseEntity {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    @ApiModelProperty(value = "类型(01-系统类,02-审批类,03-订单类,04-其他)")
    private String type;


    @ApiModelProperty(value = "使用人(0-所有人)")
    private String userName;


    @ApiModelProperty(value = "是否接收(0接收,2不接收)")
    @TableField("IsSystem")
    private String IsSystem;


    @ApiModelProperty(value = "服务ID")
    private String serverId;


    @ApiModelProperty(value = "需求提出方")
    private String merchantId;

}
