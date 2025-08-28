package com.dfc.ind.controller.dataapi;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dfc.ind.common.core.utils.DateUtils;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.core.utils.poi.ExcelUtil;
import com.dfc.ind.common.core.web.controller.BaseController;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.utils.SecurityUtils;
import com.dfc.ind.entity.dataapi.DataApiCallLogEntity;
import com.dfc.ind.service.dataapi.IDataApiCallLogService;
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
 * 描述: 本类为数据服务调用日志的前端控制器
 * </p>
 *
 * @author huff
 * @date 2024-03-22
 */
@Api(tags = "数据服务调用日志接口")
@RestController
@RequestMapping("/dataApiCallLog")
public class DataApiCallLogController extends BaseController {

    @Autowired
    private IDataApiCallLogService service;

    /**
     * 数据服务调用日志 新增
     *
     * @param entity
     */
    @PostMapping
    @ApiOperation(value = "新增")
    public JsonResults add(@RequestBody DataApiCallLogEntity entity) {

        service.save(entity);
        return JsonResults.success(entity);
    }

    /**
     * 数据服务调用日志 删除
     *
     * @param id
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除")
    @ApiImplicitParam(name = "id", value = "日志识别码", required = true, dataType = "Long")
    public JsonResults del(@PathVariable Long id) {
        return toResult(service.removeById(id));
    }

    /**
     * 数据服务调用日志 修改
     *
     * @param entity
     */
    @PutMapping
    @ApiOperation(value = "修改")
    public JsonResults edit(@RequestBody DataApiCallLogEntity entity) {

        service.updateById(entity);
        return JsonResults.success();
    }

    /**
     * 数据服务调用日志 明细
     *
     * @param id
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "明细")
    @ApiImplicitParam(name = "id", value = "日志识别码", required = true, dataType = "Long")
    public JsonResults getById(@PathVariable Long id) {
        DataApiCallLogEntity entity = service.getById(id);
        if (StringUtils.isNotNull(entity)) {
            return JsonResults.success(entity);
        }
        return JsonResults.success("日志识别码不存在!");
    }

    /**
     * 数据服务调用日志  查询全部
     *
     * @param entity 对象
     */
    @GetMapping
    @ApiOperation(value = "查询全部")
    public JsonResults listAll(DataApiCallLogEntity entity) {
        return JsonResults.success(service.list(new QueryWrapper<DataApiCallLogEntity>().setEntity(entity)));
    }

    /**
     * 数据服务调用日志 分页查询
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
    public JsonResults list(DataApiCallLogEntity entity, int pageNum, int pageSize, String orderByColumn, String isAsc) {
        return JsonResults.success(service.page(startPage(), new QueryWrapper<DataApiCallLogEntity>().setEntity(entity)));
    }

    /**
     * 导出数据服务调用日志
     *
     * @param entity 对象
     */
    @ApiOperation(value = "导出")
    @GetMapping("/export")
    public void export(HttpServletResponse response, DataApiCallLogEntity entity) throws Exception {
        List<DataApiCallLogEntity> list = service.list(new QueryWrapper<DataApiCallLogEntity>().setEntity(entity));
        ExcelUtil<DataApiCallLogEntity> util = new ExcelUtil<>(DataApiCallLogEntity.class);
        util.exportExcel(response, list, "数据服务调用日志");
    }

}
