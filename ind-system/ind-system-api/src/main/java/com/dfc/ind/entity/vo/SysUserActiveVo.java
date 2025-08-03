package com.dfc.ind.entity.vo;

import lombok.Data;

/**
 * @author
 * @program: shfactory
 * @description: 用户活跃度实体类
 * @create: 2022-12-17 19-02
 **/
@Data
public class SysUserActiveVo {
    /**
     * @Author:
     * @Description: 商户名称
     */
    private String merchantName;

    /**
     * @Author:
     * @Description: 商户Id
     */
    private String merchantId;

    /**
     * @Author:
     * @Description: 营业执照
     */
    private String credentialsNo;

    /**
     * @Author:
     * @Description: 商户状态
     */
    private String status;

    /**
     * @Author:
     * @Description: 所属机构
     */
    private String mechanism;

    /**
     * @Author:
     * @Description: 操作人数
     */
    private Integer operNum;

    /**
     * @Author:
     * @Description: 商户人数
     */
    private Integer userNum;

    /**
     * @Author:
     * @Description: 商户操作去重复人数
     */
    private Integer operUserNum;

    /**
     * @Author:
     * @Description: 开始日期
     */
    private String startTime;

    /**
     * @Author:
     * @Description: 结束日期
     */
    private String endTime;

    /**
     * @Author:
     * @Description: 用户活跃量
     */
    private double userActiveAmount;

    /**
     * @Author:
     * @Description: 用户活跃数
     */
    private double userNumberActive;

    /**
     * @Author:
     * @Description: 用户活跃度
     */
    private double userActive;

    /**
     * @Author:
     * @Description: 操作时间
     */
    private String operTime;
}
