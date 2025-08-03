package com.dfc.ind.mapper.sys;

import com.dfc.ind.entity.vo.SysUserActiveVo;
import org.mapstruct.Mapper;
import java.util.List;

/**
 * @author
 * @program: shfactory
 * @description
 * @create: 2022-12-17 18-43
 **/
@Mapper
public interface SysUserActiveMapper {

    /**
     * @Author:
     * @Description: 根据时间区间，查询商户的活跃人数
     */
    List<SysUserActiveVo> selectStatisticsUserActive(SysUserActiveVo entity);
}
