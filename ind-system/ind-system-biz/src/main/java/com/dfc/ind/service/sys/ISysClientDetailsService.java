package com.dfc.ind.service.sys;

import com.dfc.ind.entity.sys.SysClientDetails;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 终端配置Service接口
 *
 * @author admin
 */
public interface ISysClientDetailsService extends IService<SysClientDetails>
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
     * 批量删除终端配置
     *
     * @param clientIds 需要删除的终端配置ID
     * @return 结果
     */
    public int deleteSysClientDetailsByIds(String[] clientIds);
}
