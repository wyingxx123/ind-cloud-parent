package com.dfc.ind.service.code;


import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.entity.code.TwoDimensionalCodeEntity;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 二维码信息 服务类
 * </p>
 *
 * @author dingw
 * @since 2020-09-16
 */
public interface ITwoDimensionalCodeService extends IService<TwoDimensionalCodeEntity> {
    /**
     * 新增二维码信息
     *
     * @param entity
     * @return
     */
    JsonResults saveCode(TwoDimensionalCodeEntity entity);

    /**
     * 修改二维码信息
     *
     * @param entity
     * @return
     */
    JsonResults updateCode(TwoDimensionalCodeEntity entity);

    /**
     * 生成二维码url
     *
     * @param codeNo
     * @return
     */
    JsonResults createCode(String codeNo);

    /**
     * app扫码访问
     *
     * @param codeUrl
     * @param userId
     * @param merchantId
     * @return
     */
    JsonResults codeAccess(String codeUrl,Long userId,String merchantId);

    /**
     * 分页条件查询
     *
     * @param startPage
     * @param entity
     * @return
     */
    IPage page(Page startPage, TwoDimensionalCodeEntity entity);

}
