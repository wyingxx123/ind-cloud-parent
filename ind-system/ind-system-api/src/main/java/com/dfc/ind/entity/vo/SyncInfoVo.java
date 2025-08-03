package com.dfc.ind.entity.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 描述: 菜单同步控制辅助类
 * </p>
 *
 * @author wudj 伍达将
 * @date 2021/2/25
 * @copyright 武汉数慧享智能科技有限公司
 */
@Data
@Accessors(chain = true)
public class SyncInfoVo {
    /**
     * 菜单资源id
     */
    private Long menuId;

    /**
     * 同步类型，00同步到所有商户，01同步到指定商户
     */
    private String syncType;

    /**
     * 同步类型，00同步到所有商户，01同步到指定商户
     */
    private List<String> syncMerchant;
}
