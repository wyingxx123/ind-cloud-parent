package com.dfc.ind.entity.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 审批信息封装-前端用
 * </p>
 *
 * @author ylyan
 * @since 2020-03-25
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "EveApprovalInfoVO对象-前端用", description = "审批信息封装")
public class EveApprovalInfoVO {

    @ApiModelProperty(value = "认证id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long authId;

    @ApiModelProperty(value = "审批状态：00-未审批，01-审批通过，02-不通过")
    private String approvalStatus;

    @ApiModelProperty(value = "审批意见")
    private String opinion;

    @ApiModelProperty(value = "商户id")
    private Long merchatid;
}
