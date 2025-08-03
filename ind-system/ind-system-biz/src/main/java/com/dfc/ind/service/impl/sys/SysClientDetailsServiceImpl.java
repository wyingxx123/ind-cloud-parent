package com.dfc.ind.service.impl.sys;

import com.dfc.ind.common.core.constant.CacheConstants;

import com.dfc.ind.entity.sys.SysClientDetails;
import com.dfc.ind.mapper.sys.SysClientDetailsMapper;
import com.dfc.ind.service.sys.ISysClientDetailsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 终端配置Service业务层处理
 *
 * @author admin
 */
@Service
public class SysClientDetailsServiceImpl extends ServiceImpl<SysClientDetailsMapper, SysClientDetails> implements ISysClientDetailsService
{
    /**
     * 查询终端配置
     *
     * @param clientId 终端配置ID
     * @return 终端配置
     */
    @Override
    public SysClientDetails selectSysClientDetailsById(String clientId)
    {
        return baseMapper.selectSysClientDetailsById(clientId);
    }

    /**
     * 查询终端配置列表
     *
     * @param sysClientDetails 终端配置
     * @return 终端配置
     */
    @Override
    public List<SysClientDetails> selectSysClientDetailsList(SysClientDetails sysClientDetails)
    {
        return baseMapper.selectSysClientDetailsList(sysClientDetails);
    }

    /**
     * 新增终端配置
     *
     * @param sysClientDetails 终端配置
     * @return 结果
     */
    @Override
    public int insertSysClientDetails(SysClientDetails sysClientDetails)
    {
        return baseMapper.insertSysClientDetails(sysClientDetails);
    }

    /**
     * 修改终端配置
     *
     * @param sysClientDetails 终端配置
     * @return 结果
     */
    @Override
    @CacheEvict(value = CacheConstants.CLIENT_DETAILS_KEY, key = "#sysClientDetails.clientId")
    public int updateSysClientDetails(SysClientDetails sysClientDetails)
    {
        return baseMapper.updateSysClientDetails(sysClientDetails);
    }

    /**
     * 批量删除终端配置
     *
     * @param clientIds 需要删除的终端配置ID
     * @return 结果
     */
    @Override
    @CacheEvict(value = CacheConstants.CLIENT_DETAILS_KEY, allEntries = true)
    public int deleteSysClientDetailsByIds(String[] clientIds)
    {
        return baseMapper.deleteSysClientDetailsByIds(clientIds);
    }
}
