package com.dfc.ind.controller.station.sys;

import com.dfc.ind.common.core.utils.poi.ExcelUtil;
import com.dfc.ind.common.core.web.controller.BaseController;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.common.log.annotation.Log;
import com.dfc.ind.common.log.enums.BusinessType;
import com.dfc.ind.entity.sys.SysOperLog;
import com.dfc.ind.service.sys.ISysOperLogService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 操作日志记录
 * 
 * @author admin
 */
@RestController
@RequestMapping("/operlog")
public class SysOperlogController extends BaseController
{
    @Autowired
    private ISysOperLogService operLogService;

    @PreAuthorize("@ss.hasPermi('system:operlog:list')")
    @GetMapping("/list")
    public JsonResults list(SysOperLog operLog)
    {
        return JsonResults.success(operLogService.page(startPage(),new QueryWrapper<SysOperLog>().lambda().setEntity(operLog)

                .orderByDesc(SysOperLog::getOperTime)));
    }

    @Log(title = "操作日志", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:operlog:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysOperLog operLog) throws IOException
    {
        List<SysOperLog> list = operLogService.selectOperLogList(operLog);
        ExcelUtil<SysOperLog> util = new ExcelUtil<SysOperLog>(SysOperLog.class);
        util.exportExcel(response, list, "操作日志");
    }

    @PreAuthorize("@ss.hasPermi('system:operlog:remove')")
    @DeleteMapping("/{operIds}")
    public JsonResults remove(@PathVariable Long[] operIds)
    {
        return toAjax(operLogService.deleteOperLogByIds(operIds));
    }

    @PreAuthorize("@ss.hasPermi('system:operlog:remove')")
    @Log(title = "操作日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public JsonResults clean()
    {
        operLogService.cleanOperLog();
        return JsonResults.success();
    }

    @PostMapping
    public JsonResults add(@RequestBody SysOperLog operLog)
    {
        return toAjax(operLogService.insertOperlog(operLog));
    }
}
