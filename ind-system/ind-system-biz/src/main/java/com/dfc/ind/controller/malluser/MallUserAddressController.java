package com.dfc.ind.controller.malluser;

import com.dfc.ind.common.core.utils.DateUtils;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.core.utils.poi.ExcelUtil;
import com.dfc.ind.common.core.web.controller.BaseController;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.common.security.utils.SecurityUtils;
import com.dfc.ind.entity.malluser.MallUserAddressEntity;
import com.dfc.ind.service.malluser.IMallUserAddressService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 描述: 本类为用户地址表的前端控制器
 * </p>
 *
 * @author ylyan
 * @date 2020-04-23
 * @copyright 武汉数慧享智能科技有限公司
 */
@Api(tags = "用户地址表接口")
@RestController
@RequestMapping("/mall/address")
public class MallUserAddressController extends BaseController {

    @Autowired
    private IMallUserAddressService service;

    /**
     * 用户地址表 新增
     *
     * @param entity
     */
    @PostMapping
    @ApiOperation(value = "新增")
    public JsonResults add(@RequestBody MallUserAddressEntity entity) {
        entity.setCreateTime(DateUtils.getNowDate());
        entity.setCreateBy(SecurityUtils.getUserName());
        service.save(entity);
        return JsonResults.success(entity);
    }

    /**
     * 用户地址表 删除
     *
     * @param id
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除")
    @ApiImplicitParam(name = "id", value = "地址id", required = true, dataType = "Long")
    public JsonResults del(@PathVariable Long id) {
        return toResult(service.removeById(id));
    }

    /**
     * 用户地址表 修改
     *
     * @param entity
     */
    @PutMapping
    @ApiOperation(value = "修改")
    public JsonResults edit(@RequestBody MallUserAddressEntity entity) {
        entity.setUpdateTime(DateUtils.getNowDate());
        entity.setUpdateBy(SecurityUtils.getUserName());
        service.updateById(entity);
        return JsonResults.success();
    }

    /**
     * 用户地址表 明细
     *
     * @param id
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "明细")
    @ApiImplicitParam(name = "id", value = "地址id", required = true, dataType = "Long")
    public JsonResults getById(@PathVariable Long id) {
        MallUserAddressEntity entity = service.getById(id);
        if (StringUtils.isNotNull(entity)) {
            return JsonResults.success(entity);
        }
        return JsonResults.success("地址id不存在!");
    }

    /**
     * 用户地址表  查询全部
     *
     * @param entity 对象
     */
    @GetMapping
    @ApiOperation(value = "查询全部")
    public JsonResults listAll(MallUserAddressEntity entity) {
        return JsonResults.success(service.list(new QueryWrapper<MallUserAddressEntity>().setEntity(entity)));
    }

    /**
     * 用户地址表 分页查询
     *
     * @param entity        对象
     * @param pageNum       页码
     * @param pageSize      页数
     * @param orderByColumn 排序字段
     * @param isAsc         是否升序 true false
     */
    @GetMapping("/list")
    @ApiOperation(value = "查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页数", required = true),
            @ApiImplicitParam(name = "orderByColumn", value = "排序字段"),
            @ApiImplicitParam(name = "isAsc", value = "是否升序 true false"),
    })
    public JsonResults list(MallUserAddressEntity entity, int pageNum, int pageSize, String orderByColumn, String isAsc) {
        return JsonResults.success(service.page(startPage(), new QueryWrapper<MallUserAddressEntity>().setEntity(entity)));
    }

    /**
     * 导出用户地址表
     *
     * @param entity 对象
     */
    @ApiOperation(value = "导出")
    @PostMapping("/export")
    public void export(HttpServletResponse response, MallUserAddressEntity entity) throws IOException
    {
        List<MallUserAddressEntity> list = service.list(new QueryWrapper<MallUserAddressEntity>().setEntity(entity));
        ExcelUtil<MallUserAddressEntity> util = new ExcelUtil<MallUserAddressEntity>(MallUserAddressEntity.class);
        util.exportExcel(response, list, "用户地址");
    }

}
