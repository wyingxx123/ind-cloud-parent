package com.dfc.ind.service.sys;

import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.entity.sys.SysAppMenuEntity;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 描述: app端菜单权限服务类
 * </p>
 *
 * @author zhaoyq 赵亚强
 * @date 2020/8/25
 * @copyright 武汉数慧享智能科技有限公司
 */
public interface ISysAppMenuService extends IService<SysAppMenuEntity> {

    /**
     * 获取所有
     *
     * @param entity
     * @return
     */
    JsonResults listAll(SysAppMenuEntity entity);

    /**
     * 新增app菜单权限
     *
     * @param entity
     * @return
     */
    JsonResults add(SysAppMenuEntity entity);

    /**
     * 修改app菜单权限
     *
     * @param entity
     * @return
     */
    JsonResults update(SysAppMenuEntity entity);

    /**
     * 分页查询app菜单权限
     *
     * @param startPage
     * @param entity
     * @return
     */
    JsonResults list(Page startPage, SysAppMenuEntity entity);

    /**
     * 批量删除
     *
     * @param menuIds
     * @return
     */
    JsonResults batchDelete(String menuIds);

    /**
     * 查询菜单
     *
     * @param ids
     * @return
     */
    JsonResults getByIds(String ids);

    /**
     * 查询菜单
     *
     * @param ids
     * @return
     */
    List<SysAppMenuEntity> getByMenu(String ids);
}
