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
 * 描述:
 * </p>
 *
 * @author nwy
 * @date 2020-04-27
 * @copyright 武汉数慧享智能科技有限公司
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("c_msg_mail_list")
@ApiModel(value = "CMsgMailListEntity对象", description = "")
public class CMsgMailListEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "消息主键")
    private Long mailId;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "类型")
    private String type;


    @ApiModelProperty(value = "使用人")
    private String userName;


    @ApiModelProperty(value = "阅读时间")
    private Date readTime;


    @ApiModelProperty(value = "是否阅读(0-未读,1-已读)")
    private String isRead;


}
