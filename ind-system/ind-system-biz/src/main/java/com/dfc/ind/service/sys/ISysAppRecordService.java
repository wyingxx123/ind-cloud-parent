package com.dfc.ind.service.sys;

import com.dfc.ind.entity.sys.SysAppRecordEntity;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * app访问记录 服务类
 * </p>
 *
 * @author dingw
 * @since 2020-09-28
 */
public interface ISysAppRecordService extends IService<SysAppRecordEntity> {
    /**
     * 新增修改记录
     *
     * @param entity
     * @return
     */
    Boolean saveUpdate(SysAppRecordEntity entity);

    /**
     * 分页查询
     *
     * @param startPage
     * @param entity
     * @return
     */
    IPage pageList(Page startPage, SysAppRecordEntity entity);

    /**
     * 查询全部
     *
     * @param entity
     * @return
     */
    List<SysAppRecordEntity> listAll(SysAppRecordEntity entity);
}
