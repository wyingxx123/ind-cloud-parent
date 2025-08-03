package com.dfc.ind.service.sys;


import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.entity.sys.SysCodeInfoEntity;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 二维码配置信息 服务类
 * </p>
 *
 * @author dingw
 * @since 2020-08-25
 */
public interface ISysCodeInfoService extends IService<SysCodeInfoEntity> {
    /**
     * 新增二维码配置信息
     *
     * @param entity
     * @return
     */
    JsonResults saveCode(SysCodeInfoEntity entity);

    /**
     * 修改二维码配置信息
     *
     * @param entity
     * @return
     */
    JsonResults updateCode(SysCodeInfoEntity entity);

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
    IPage pageList(Page startPage, SysCodeInfoEntity entity);

    /**
     * 条件查询全部
     *
     * @param entity
     * @return
     */
    List<SysCodeInfoEntity> list(SysCodeInfoEntity entity);
}
