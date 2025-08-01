package com.dfc.ind.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 系统访问记录
 * </p>
 *
 * @author nwy
 * @since 2020-03-24
 */
@Data
@Accessors(chain = true)
public class SysLogininforEntity {

    private static final long serialVersionUID = 1L;


    @TableId(value = "info_id", type = IdType.AUTO)
    private Integer infoId;


    private String loginName;


    private String ipaddr;


    private String loginLocation;


    private String browser;


    private String os;


    private String status;


    private String msg;


    private Date loginTime;


}
