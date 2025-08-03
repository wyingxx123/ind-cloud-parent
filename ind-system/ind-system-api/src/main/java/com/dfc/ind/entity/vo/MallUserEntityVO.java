package com.dfc.ind.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 描述: 用户信息表
 * </p>
 *
 * @author ylyan
 * @date 2020-04-23
 * @copyright 武汉数慧享智能科技有限公司
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "MallUserVO对象-前端用", description = "用户注册信息封装")
public class MallUserEntityVO {

    @ApiModelProperty(value = "登录账号名称")
    private String userName;

    @ApiModelProperty(value = "登录账号密码")
    private String password;

    @ApiModelProperty(value = "手机号码")
    private String phoneNumber;

    @ApiModelProperty(value = "二维码信息")
    private String twoCode;
}
