package com.dfc.ind.controller.station.sys;

import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.core.web.controller.BaseController;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.common.log.annotation.Log;
import com.dfc.ind.common.log.enums.BusinessType;
import com.dfc.ind.common.security.utils.SecurityUtils;

import com.dfc.ind.entity.sys.SysClientDetails;
import com.dfc.ind.service.sys.ISysClientDetailsService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 终端配置 信息操作处理
 *
 * @author admin
 */
@RestController
@RequestMapping("/client")
public class SysClientDetailsController extends BaseController
{
    @Autowired
    private ISysClientDetailsService sysClientDetailsService;

    /**
     * 查询终端配置列表
     */
    @PreAuthorize("@ss.hasPermi('system:client:list')")
    @GetMapping("/list")
    public JsonResults list(SysClientDetails sysClientDetails)
    {
        return JsonResults.success(sysClientDetailsService.page(startPage(),new QueryWrapper<SysClientDetails>().setEntity(sysClientDetails)));

    }

    /**
     * 获取终端配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:client:query')")
    @GetMapping(value = "/{clientId}")
    public JsonResults getInfo(@PathVariable("clientId") String clientId)
    {
        return JsonResults.success(sysClientDetailsService.selectSysClientDetailsById(clientId));
    }

    /**
     * 新增终端配置
     */
    @PreAuthorize("@ss.hasPermi('system:client:add')")
    @Log(title = "终端配置", businessType = BusinessType.INSERT)
    @PostMapping
    public JsonResults add(@RequestBody SysClientDetails sysClientDetails)
    {
        String clientId = sysClientDetails.getClientId();
        if (StringUtils.isNotNull(sysClientDetailsService.selectSysClientDetailsById(clientId)))
        {
            return JsonResults.error("新增终端'" + clientId + "'失败，编号已存在");
        }
        sysClientDetails.setClientSecret(SecurityUtils.encryptPassword(sysClientDetails.getClientSecret()));
        return toAjax(sysClientDetailsService.insertSysClientDetails(sysClientDetails));
    }

    /**
     * 修改终端配置
     */
    @PreAuthorize("@ss.hasPermi('system:client:edit')")
    @Log(title = "终端配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public JsonResults edit(@RequestBody SysClientDetails sysClientDetails)
    {
        sysClientDetails.setClientSecret(SecurityUtils.encryptPassword(sysClientDetails.getClientSecret()));
        return toAjax(sysClientDetailsService.updateSysClientDetails(sysClientDetails));
    }

    /**
     * 删除终端配置
     */
    @PreAuthorize("@ss.hasPermi('system:client:remove')")
    @Log(title = "终端配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{clientIds}")
    public JsonResults remove(@PathVariable String[] clientIds)
    {
        return toAjax(sysClientDetailsService.deleteSysClientDetailsByIds(clientIds));
    }
}
