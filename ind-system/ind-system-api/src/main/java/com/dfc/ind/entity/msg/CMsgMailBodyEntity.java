package com.dfc.ind.entity.msg;

import com.dfc.ind.common.core.web.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 描述: 消息内容主表
 * </p>
 *
 * @author nwy
 * @date 2020-04-27
 * @copyright 武汉数慧享智能科技有限公司
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("c_msg_mail_body")
@ApiModel(value = "CMsgMailBodyEntity对象", description = "消息内容主表")
public class CMsgMailBodyEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "标题")
    @TableId(value = "mail_id", type = IdType.AUTO)
    private Long mailId;

    @ApiModelProperty(value = "标题")
    private String title;


    @ApiModelProperty(value = "内容")
    private String content;


    @ApiModelProperty(value = "类型(01-系统类,02-审批类,03-订单类,04-其他)")
    private String type;


    @ApiModelProperty(value = "是否发送短信")
    private String isMsgRemind;


    @ApiModelProperty(value = "是否定时发送")
    private String mailTiming;


    @ApiModelProperty(value = "是否发送")
    private String isSend;


    @ApiModelProperty(value = "发送时间")
    private Date sendDate;


    @ApiModelProperty(value = "删除人")
    private String deleteUid;


    @ApiModelProperty(value = "删除时间")
    private Date deleteDate;


    @ApiModelProperty(value = "是否删除")
    private String isDelete;


    @ApiModelProperty(value = "使用人")
    private String userName;
}
