package com.dfc.ind.service.merchant;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.entity.merchant.EveAuthInfoEntity;
import com.dfc.ind.entity.merchant.EveAuthInfoVO;
import com.dfc.ind.entity.vo.EveApprovalInfoVO;

/**
 * <p>
 * 中台认证信息表 服务类
 * </p>
 *
 * @author ylyan
 * @since 2020-03-28
 */
public interface IEveAuthInfoService extends IService<EveAuthInfoEntity> {

    /**
     * <审批商户申请>
     *
     * @param eveApprovalInfoVO 审批信息封装
     * @return
     * @author ylyan
     * @Date 2020/4/26 15:39
     */
    JsonResults approval(EveApprovalInfoVO eveApprovalInfoVO);

    /**
     * <用户请求成为商户>
     *
     * @param entity 审批信息对象
     * @param userId 用户id
     * @return
     * @author ylyan
     * @Date 2020/4/26 10:37
     */
    JsonResults applyMch(EveAuthInfoVO entity, Long userId);

    /**
     * <分页条件查询>
     *
     * @param startPage 分页对象
     * @param entity    认证信息对象
     * @return
     * @author ylyan
     * @Date 2020/4/26 16:47
     */
    IPage pageList(Page startPage, EveAuthInfoEntity entity);

    JsonResults pageLists(Page startPage, EveAuthInfoEntity entity);

    /**
     * <校验用户申请类型是否唯一>
     *
     * @param entity 认证信息对象
     * @return
     * @author ylyan
     * @Date 2020/4/26 17:13
     */
    String checkAuthTypeUnique(EveAuthInfoEntity entity);

    JsonResults fbapproval(EveAuthInfoVO eveAuthInfoVO);
}
