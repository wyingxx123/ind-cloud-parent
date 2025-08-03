package com.dfc.ind.entity.merchant;

import com.dfc.ind.common.core.web.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
@TableName("c_agr_factory_info")
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "AgrFactoryInfoEntity对象", description = "工厂信息表")
public class AgrFactoryInfoEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 工厂唯一
     */
    @TableId
    private Long serverId;

    /**
     * 工厂名称
     */
    private String serverName;

    /**
     * 工厂云ip
     */
    private String factoryIp;

    /**
     * 工厂状态
     */
    private String status;

    /**
     * 端口
     */
    private String port;

}
