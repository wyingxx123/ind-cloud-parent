package com.dfc.ind.controller.station.sys;

import com.dfc.ind.common.core.web.controller.BaseController;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.entity.sys.SysAppMenuEntity;
import com.dfc.ind.service.sys.ISysAppMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 描述: app菜单权限前端控制器
 * </p>
 *
 * @author zhaoyq 赵亚强
 * @date 2020/8/26
 * @copyright 武汉数慧享智能科技有限公司
 */
@Api(tags = "app菜单权限表接口")
@RestController
@RequestMapping("/app/menu")
public class SysAppMenuController extends BaseController {

    @Autowired
    private ISysAppMenuService service;

    @PostMapping
    @ApiOperation(value = "新增")
    public JsonResults add(SysAppMenuEntity entity) {
        return service.add(entity);
    }

    @PutMapping
    @ApiOperation(value = "修改")
    public JsonResults edit(SysAppMenuEntity entity) {
        return service.update(entity);
    }

    @DeleteMapping
    @ApiOperation(value = "删除")
    @ApiImplicitParam(name = "menuId", value = "菜单权限编号", required = true, dataType = "Long")
    public JsonResults delete(Long menuId) {
        if (service.removeById(menuId)) {
            return JsonResults.success("删除成功");
        }
        return JsonResults.success("删除失败");
    }

    @GetMapping("/list")
    @ApiOperation(value = "分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页数", required = true),
            @ApiImplicitParam(name = "orderByColumn", value = "排序字段"),
            @ApiImplicitParam(name = "isAsc", value = "是否升序 true false")
    })
    public JsonResults list(SysAppMenuEntity entity, int pageSize, int pageNum, String orderByColumn, String isAsc) {
        return service.list(startPage(), entity);
    }

    @GetMapping
    @ApiOperation(value = "查询所有")
    public JsonResults listAll(SysAppMenuEntity entity) {
        return service.listAll(entity);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "明细")
    @ApiImplicitParam(name = "menuId", value = "菜单权限编号", required = true, dataType = "Long")
    public JsonResults getOneById(@PathVariable Long menuId) {
        return JsonResults.success(service.getById(menuId));
    }

    @DeleteMapping("batchDelete")
    @ApiOperation(value = "批量删除")
    public JsonResults batchDelete(String menuIds) {
        return service.batchDelete(menuIds);
    }
}
