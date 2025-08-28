package com.dfc.ind.controller.dataapi;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dfc.ind.common.core.utils.DateUtils;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.core.utils.poi.ExcelUtil;
import com.dfc.ind.common.core.web.controller.BaseController;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.utils.SecurityUtils;
import com.dfc.ind.entity.dataapi.DataSourceParaInfoEntity;
import com.dfc.ind.service.dataapi.IDataSourceParaInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 * 描述: 本类为数据源参数信息的前端控制器
 * </p>
 *
 * @author huff
 * @date 2024-03-22
 */
@Api(tags = "数据源参数信息接口")
@RestController
@RequestMapping("/dataSourceParaInfo")
public class DataSourceParaInfoController extends BaseController {

    @Autowired
    private IDataSourceParaInfoService service;

    /**
     * 数据源参数信息 新增
     *
     * @param entity
     */
    @PostMapping
    @ApiOperation(value = "新增")
    public JsonResults add(@RequestBody DataSourceParaInfoEntity entity) {

        service.save(entity);
        return JsonResults.success(entity);
    }

    /**
     * 数据源参数信息 删除
     *
     * @param id
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除")
    @ApiImplicitParam(name = "id", value = "数据源参数识别码", required = true, dataType = "Long")
    public JsonResults del(@PathVariable Long id) {
        return toResult(service.removeById(id));
    }

    /**
     * 数据源参数信息 修改
     *
     * @param entity
     */
    @PutMapping
    @ApiOperation(value = "修改")
    public JsonResults edit(@RequestBody DataSourceParaInfoEntity entity) {
        service.updateById(entity);
        return JsonResults.success();
    }

    /**
     * 数据源参数信息 明细
     *
     * @param id
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "明细")
    @ApiImplicitParam(name = "id", value = "数据源参数识别码", required = true, dataType = "Long")
    public JsonResults getById(@PathVariable Long id) {
        DataSourceParaInfoEntity entity = service.getById(id);
        if (StringUtils.isNotNull(entity)) {
            return JsonResults.success(entity);
        }
        return JsonResults.success("数据源参数识别码不存在!");
    }

    /**
     * 数据源参数信息  查询全部
     *
     * @param entity 对象
     */
    @GetMapping
    @ApiOperation(value = "查询全部")
    public JsonResults listAll(DataSourceParaInfoEntity entity) {
        return JsonResults.success(service.list(new QueryWrapper<DataSourceParaInfoEntity>().setEntity(entity)));
    }

    /**
     * 数据源参数信息 分页查询
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
    public JsonResults list(DataSourceParaInfoEntity entity, int pageNum, int pageSize, String orderByColumn, String isAsc) {
        return JsonResults.success(service.page(startPage(), new QueryWrapper<DataSourceParaInfoEntity>().setEntity(entity)));
    }

    /**
     * 导出数据源参数信息
     *
     * @param entity 对象
     */
    @ApiOperation(value = "导出")
    @GetMapping("/export")
    public void export(HttpServletResponse response, DataSourceParaInfoEntity entity) throws Exception {
        List<DataSourceParaInfoEntity> list = service.list(new QueryWrapper<DataSourceParaInfoEntity>().setEntity(entity));
        ExcelUtil<DataSourceParaInfoEntity> util = new ExcelUtil<>(DataSourceParaInfoEntity.class);
        util.exportExcel(response, list, "数据源参数信息");
    }

}
