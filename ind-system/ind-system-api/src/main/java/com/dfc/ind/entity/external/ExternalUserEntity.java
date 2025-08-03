package com.dfc.ind.entity.external;

import com.dfc.ind.common.core.web.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 描述: 内外部客户信息对照表
 * </p>
 *
 * @author wubt
 * @date 2020/11/26
 * @copyright 武汉数慧享智能科技有限公司
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("external_user")
@ApiModel(value = "ExternalUserEntity对象", description = "内外部客户信息对照表")
public class ExternalUserEntity extends BaseEntity {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 登录账号
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    private String delFlag;

    /**
     * 商户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * ip
     */
    private String ip;

    /**
     * 帐号状态（0正常 1停用）
     */
    private String status;

    /**
     * 启用日期
     **/
    private Date startDate;

    /**
     * 停止日期
     **/
    private Date stopDate;

    /**
     * 外部商户ID
     */
    private String externalId;

    /**
     * 外部商户名
     */
    private String externalName;

    /**
     * 数据源标识
     */
    private String dataSrc;

    /**
     * 联系人
     */
    private String contact;

    /**
     * 地址
     */
    private String address;

    /**
     * 联系方式
     */
    private String phone;

    @Override
    public String getRemark() {
        return remark;
    }

    @Override
    public void setRemark(String remark) {
        this.remark = remark;
    }
}
