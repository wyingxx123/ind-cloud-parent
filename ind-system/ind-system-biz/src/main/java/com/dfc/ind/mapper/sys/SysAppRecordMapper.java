package com.dfc.ind.mapper.sys;

import com.dfc.ind.entity.sys.SysAppRecordEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * app访问记录 Mapper 接口
 * </p>
 *
 * @author dingw
 * @since 2020-09-28
 */
public interface SysAppRecordMapper extends BaseMapper<SysAppRecordEntity> {
    /**
     * 根据用户删除访问菜单记录
     *
     * @param userIds
     * @param perms
     * @return
     */
    int deleteByUserPerms(@Param("userIds") Long[] userIds, @Param("perms") String[] perms);
}
