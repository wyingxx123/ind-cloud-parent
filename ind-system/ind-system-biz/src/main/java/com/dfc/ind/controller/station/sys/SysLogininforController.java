package com.dfc.ind.controller.station.sys;

import com.dfc.ind.common.core.constant.Constants;
import com.dfc.ind.common.core.utils.ServletUtils;
import com.dfc.ind.common.core.utils.ip.IpUtils;
import com.dfc.ind.common.core.utils.poi.ExcelUtil;
import com.dfc.ind.common.core.web.controller.BaseController;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.common.log.annotation.Log;
import com.dfc.ind.common.log.enums.BusinessType;
import com.dfc.ind.entity.sys.SysLogininfor;
import com.dfc.ind.service.sys.ISysLogininforService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 系统访问记录
 * 
 * @author admin
 */
@RestController
@RequestMapping("/logininfor")
public class SysLogininforController extends BaseController
{
    @Autowired
    private ISysLogininforService logininforService;

    @PreAuthorize("@ss.hasPermi('system:logininfor:list')")
    @GetMapping("/list")
    public JsonResults list(SysLogininfor logininfor)
    {
        return JsonResults.success(logininforService.page(startPage(),new QueryWrapper<SysLogininfor>().setEntity(logininfor).lambda().orderByDesc(SysLogininfor::getAccessTime)));

    }

    @Log(title = "登陆日志", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:logininfor:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysLogininfor logininfor) throws IOException
    {
        List<SysLogininfor> list = logininforService.selectLogininforList(logininfor);
        ExcelUtil<SysLogininfor> util = new ExcelUtil<SysLogininfor>(SysLogininfor.class);
        util.exportExcel(response, list, "登陆日志");
    }

    @PreAuthorize("@ss.hasPermi('system:logininfor:remove')")
    @Log(title = "登陆日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{infoIds}")
    public JsonResults remove(@PathVariable Long[] infoIds)
    {
        return toAjax(logininforService.deleteLogininforByIds(infoIds));
    }

    @PreAuthorize("@ss.hasPermi('system:logininfor:remove')")
    @Log(title = "登陆日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/clean")
    public JsonResults clean()
    {
        logininforService.cleanLogininfor();
        return JsonResults.success();
    }

    @PostMapping
    public JsonResults add(@RequestParam("username") String username, @RequestParam("status") String status,
            @RequestParam("message") String message)
    {
        String ip = IpUtils.getIpAddr(ServletUtils.getRequest());

        // 封装对象
        SysLogininfor logininfor = new SysLogininfor();
        logininfor.setUserName(username);
        logininfor.setIpaddr(ip);
        logininfor.setMsg(message);
        // 日志状态
        if (Constants.LOGIN_SUCCESS.equals(status) || Constants.LOGOUT.equals(status))
        {
            logininfor.setStatus("0");
        }
        else if (Constants.LOGIN_FAIL.equals(status))
        {
            logininfor.setStatus("1");
        }
        return toAjax(logininforService.insertLogininfor(logininfor));
    }
}
