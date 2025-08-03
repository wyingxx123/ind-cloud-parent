package com.dfc.ind.mapper.sys;

import com.dfc.ind.entity.sys.SysClientDetails;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * 终端配置Mapper接口
 *
 * @author admin
 */
public interface SysClientDetailsMapper extends BaseMapper<SysClientDetails>
{
    /**
     * 查询终端配置
     *
     * @param clientId 终端配置ID
     * @return 终端配置
     */
    public SysClientDetails selectSysClientDetailsById(String clientId);

    /**
     * 查询终端配置列表
     *
     * @param sysClientDetails 终端配置
     * @return 终端配置集合
     */
    public List<SysClientDetails> selectSysClientDetailsList(SysClientDetails sysClientDetails);

    /**
     * 新增终端配置
     *
     * @param sysClientDetails 终端配置
     * @return 结果
     */
    public int insertSysClientDetails(SysClientDetails sysClientDetails);

    /**
     * 修改终端配置
     *
     * @param sysClientDetails 终端配置
     * @return 结果
     */
    public int updateSysClientDetails(SysClientDetails sysClientDetails);

    /**
     * 删除终端配置
     *
     * @param clientId 终端配置ID
     * @return 结果
     */
    public int deleteSysClientDetailsById(String clientId);

    /**
     * 批量删除终端配置
     *
     * @param clientIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteSysClientDetailsByIds(String[] clientIds);
}
