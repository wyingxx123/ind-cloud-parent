package com.dfc.ind.service.external;

import com.dfc.ind.entity.external.ExternalUserEntity;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 描述: 内外部客户信息对照表 服务类
 * </p>
 *
 * @author wubt
 * @date 2020/11/26
 * @copyright 武汉数慧享智能科技有限公司
 */
public interface IExternalUserService extends IService<ExternalUserEntity> {
    /**
     * 描述：根基商户号查用户
     *
     * @param merchantId
     * @return com.dfc.ind.entity.ExternalUserEntity
     * @author wubt
     * @date 2020-11-26
     */
    ExternalUserEntity getUser(Long merchantId);

    /**
     * 分页查询
     *
     * @param startPage
     * @param entity
     * @return
     */
    IPage<ExternalUserEntity> pageList(Page startPage, ExternalUserEntity entity);

    /**
     * 根据外部商户号查询明细
     *
     * @param externalId
     * @return
     */
    ExternalUserEntity getByExternalId(String externalId);
}
